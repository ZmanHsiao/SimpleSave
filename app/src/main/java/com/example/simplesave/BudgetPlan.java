package com.example.simplesave;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BudgetPlan implements Serializable{

    final int SECONDS_IN_DAY = 86400;

    private float budget;
    private float remBudget;
    private int totalDays;
    private int daysLeft;
    private Date startDate;
    private Date currentDate;
    private Date endDate;
    private HashMap<String, Budget> categories;
    // hashmap of arraylist, keys are the days as Integers, values are Lists of Transactions for that day
    private ArrayList<Transaction> transactions;

    //constructors

    public void fuckbrian() {
        transactions = new ArrayList<Transaction>();
    }

    public void fuckbrian2() {
        int currentDay = totalDays - daysLeft;
        if (currentDay == 0) {
            //transactions.add(currentDay, new ArrayList<Transaction>());
        }
    }


    public BudgetPlan() {
        this(0);
    }

    public BudgetPlan(int days) {
        this.totalDays = totalDays;
        this.daysLeft = daysLeft;
        generateDefaultCategories();
        transactions = new ArrayList<Transaction>();    }


    public BudgetPlan(int totalDays, int daysLeft, HashMap<String, Budget> categories, ArrayList<Transaction> transactions) {
        this.totalDays = totalDays;
        this.daysLeft = daysLeft;
        this.categories = categories;
        this.transactions = transactions;
        int currentDay = totalDays - daysLeft;
        if (currentDay == 0) {
            //transactions.put(currentDay, new ArrayList<Transaction>());
        }
        transactions = new ArrayList<Transaction>();
    }

    private void generateDefaultCategories() {
        this.categories = new HashMap<String, Budget>();
        this.categories.put("Tuition", new Budget());
        this.categories.put("Rent", new Budget());
        this.categories.put("Utilies", new Budget());
        this.categories.put("Transportation", new Budget());
        this.categories.put("Food", new Budget());
        this.categories.put("Entertainment", new Budget());
    }


    //getters and setters

    @Exclude
    public float getInitBalance() {
        float sum = 0;
        for (String key : categories.keySet()) {
            sum += ((Budget) categories.get(key)).getInitBalance();
        }
        return sum;
    }

    @Exclude
    public float getRemainingBalance() {
        float sum = 0;
        for (String key : categories.keySet()) {
            sum += ((Budget) categories.get(key)).getRemainingBalance();
        }
        return sum;
    }

    @Exclude
    public void nextDay() {
        daysLeft--;
        //transactions.put(totalDays - daysLeft, new ArrayList<Transaction>());
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, 1); //minus number would decrement the days
        currentDate = c.getTime();
    }

    public void addTransaction(String category, String name, float price) {
        budget -= price;
        remBudget -= price;
        Transaction transaction = new Transaction(category, name, getCurrentDay(), price);
        transactions.add(transaction);
    }

    public void addMoney(float value) {
        budget += value;
        remBudget += value;
    }

    public int getCurrentDay() {
        return totalDays - daysLeft;
    }


    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public float getRemBudget() {
        return remBudget;
    }

    public void setRemBudget(float remBudget) {
        this.remBudget = remBudget;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Exclude
    public float getBalancePerDay() {
        return getRemainingBalance() / daysLeft;
    }

    @Exclude
    public String[] getCategoriesArray(){
        String[] cats = new String[categories.size()];
        int i = 0;
        for (String key : categories.keySet()) {
            cats[i] = key;
            ++i;
        }
        return cats;
    }

    @Exclude
    public HashMap<String, ArrayList<Transaction>> getCategoryTransactionMap(){
        HashMap<String, ArrayList<Transaction>> m = new HashMap<String, ArrayList<Transaction>>();
        for(String s : categories.keySet()){
            m.put(s, new ArrayList<Transaction>());
        }
        for(Transaction t : transactions){
            m.get(t.getCategory()).add(t);
        }
        return m;
    }

    public void resetTransactions(){
        ArrayList<Transaction> oldTransactions = transactions;
        transactions = new ArrayList<Transaction>();
        for(Transaction t : oldTransactions){
            if(t.getDate() <= totalDays){
                transactions.add(t);
            }
        }
    }

    public float getTotalPriceByCategory(String cat){
        float total = 0;
        for(Transaction t : transactions){
            if(t.getCategory().equals(cat)){
                total += t.getPrice();
            }
        }
        return total;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @Exclude
    public String getStartDateString(){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(startDate);
    }

    @Exclude
    public String getCurrentDateString(){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(currentDate);
    }

    @Exclude
    public String getEndDateString(){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(endDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }

    public HashMap<String, Budget> getCategories() {
        return categories;
    }

    public void setCategories(HashMap<String, Budget> categories) {
        this.categories = categories;
    }

}
