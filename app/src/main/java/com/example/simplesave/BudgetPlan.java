package com.example.simplesave;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BudgetPlan implements Serializable {

    private float budget;
    private float remBudget;
    //timestamps are for firestore
    transient private Timestamp startDate;
    transient private Timestamp endDate;
    private HashMap<String, Budget> categories;
    // hashmap of arraylist, keys are the days as Integers, values are Lists of Transactions for that day
    transient private ArrayList<Transaction> transactions;

    //CONSTRUCTORS
    public BudgetPlan() {
        budget = 0;
        remBudget = 0;
        setStartDate(new Timestamp(new Date()));
        setEndDate(new Timestamp(new Date()));
        generateDefaultCategories();
        transactions = new ArrayList<Transaction>();
    }


    //PUBLIC

    public void addMoney(float value) {
        budget += value;
        remBudget += value;
    }

    public void addTransaction(Transaction transaction){
        transactions.add(transaction);
    }

    public void addTransaction(String category, String name, float price) {
        budget -= price;
        remBudget -= price;
        Transaction transaction = new Transaction(category, name, price);
        transactions.add(transaction);
    }

    public void addTransaction(String category, String name, float price, Timestamp time){
        budget -= price;
        remBudget -= price;
        Transaction transaction = new Transaction(category, name, price, time);
        transactions.add(transaction);
    }

    @Exclude
    public float getBalancePerDay() {
        return getRemainingBalance() / getDaysLeft();
    }

    public float getBudget() {
        return budget;
    }

    public HashMap<String, Budget> getCategories() {
        return categories;
    }

    @Exclude
    public String[] getCategoriesArray() {
        String[] cats = new String[categories.size()];
        int i = 0;
        for (String key : categories.keySet()) {
            cats[i] = key;
            ++i;
        }
        return cats;
    }

    @Exclude
    public HashMap<String, ArrayList<Transaction>> getCategoryTransactionMap() {
        HashMap<String, ArrayList<Transaction>> m = new HashMap<String, ArrayList<Transaction>>();
        for (String s : categories.keySet()) {
            m.put(s, new ArrayList<Transaction>());
        }
        for (Transaction t : transactions) {
            m.get(t.getCategory()).add(t);
        }
        return m;
    }

    @Exclude
    public int getDaysLeft() {
       return AppLibrary.getDaysDif(AppLibrary.getToday(), endDate);
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    @Exclude
    public String getEndDateString() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(endDate.toDate());
    }

    @Exclude
    public float getInitBalance() {
        float sum = 0;
        for (String key : categories.keySet()) {
            sum += ((Budget) categories.get(key)).getInitBalance();
        }
        return sum;
    }

    public float getRemBudget() {
        return remBudget;
    }

    @Exclude
    public float getRemainingBalance() {
        float sum = 0;
        for (String key : categories.keySet()) {
            sum += ((Budget) categories.get(key)).getRemainingBalance();
        }
        return sum;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    @Exclude
    public String getStartDateString() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(startDate.toDate());
    }

    @Exclude
    public int getTotalDays() {
        return AppLibrary.getDaysDif(startDate, endDate);
    }

    @Exclude
    public float getTotalPriceByCategory(String cat) {
        float total = 0;
        for (Transaction t : transactions) {
            if (t.getCategory().equals(cat)) {
                total += t.getPrice();
            }
        }
        return total;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    @Exclude
    public HashMap<Date, Transaction> getTransactionsMap(){
        HashMap<Date, Transaction> m = new HashMap<Date, Transaction>();
        for(Transaction t : transactions){
            m.put(t.getTimestamp().toDate(), t);
        }
        return m;
    }

    public List<Transaction>  getDayTransactions(Date day) {
        List<Transaction> list = new ArrayList<>();
        for (Transaction t: getTransactions()) {
            if (t.getTimestamp().toDate().equals(day)) {
                list.add(t);
            }
        }
        return list;
    }

    public void nextDay() {
        Calendar c = Calendar.getInstance();
//        c.setTime(currentDate);
        c.add(Calendar.DATE, 1); //minus number would decrement the days
//        currentDate = c.getTime();
    }

    public void resetTransactions() {
        ArrayList<Transaction> oldTransactions = transactions;
        transactions = new ArrayList<Transaction>();
        for (Transaction t : oldTransactions) {
            if (getDaysLeft() >= 0) {
                transactions.add(t);
            }
        }
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public void setCategories(HashMap<String, Budget> categories) {
        this.categories = categories;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = AppLibrary.getTimestampWithoutTime(endDate);
    }

    public void setRemBudget(float remBudget) {
        this.remBudget = remBudget;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = AppLibrary.getTimestampWithoutTime(startDate);
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    //PRIVATE

    private void generateDefaultCategories() {
        this.categories = new HashMap<String, Budget>();
        this.categories.put("Tuition", new Budget());
        this.categories.put("Rent", new Budget());
        this.categories.put("Utilities", new Budget());
        this.categories.put("Transportation", new Budget());
        this.categories.put("Food", new Budget());
        this.categories.put("Entertainment", new Budget());
    }

}
