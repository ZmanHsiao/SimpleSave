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
    //timestamps are for firestore
    transient private Timestamp startDate;
    transient private Timestamp endDate;
    private ArrayList<String> categories;
    // hashmap of arraylist, keys are the days as Integers, values are Lists of Transactions for that day
    transient private ArrayList<Transaction> largeTransactions;
    transient private ArrayList<Transaction> transactions;

    //CONSTRUCTORS
    public BudgetPlan() {
        budget = 0;
        setStartDate(new Timestamp(new Date()));
        setEndDate(new Timestamp(new Date()));
        generateDefaultCategories();
        largeTransactions = new ArrayList<Transaction>();
        transactions = new ArrayList<Transaction>();
    }


    //PUBLIC

    public void addMoney(float value) {
        budget += value;
    }

    public void addLargeTransaction(Transaction transaction){
        largeTransactions.add(transaction);
    }

    public void addLargeTransaction(String category, String name, float price, Timestamp time){
        Transaction transaction = new Transaction(category, name, price, time);
        largeTransactions.add(transaction);
    }

    public void addTransaction(Transaction transaction){
        for(int i = 0; i < transactions.size(); ++i){
            if(transaction.getTimestamp().getSeconds() < transactions.get(i).getTimestamp().getSeconds()){
                transactions.add(transactions.get(transactions.size() - 1));
                for(int j = transactions.size() - 1; j > i; --j){
                    transactions.set(j, transactions.get(j - 1));
                }
                transactions.set(i, transaction);
                return;
            }
        }
        transactions.add(transaction);
    }

    public void addTransaction(String category, String name, float price, Timestamp time){
        addTransaction(new Transaction(category, name, price, time));
    }

    public float getBudget() {
        return budget;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public float getDailyAve(Timestamp date){
        float dailyAve = budget;
        for(Transaction t : transactions){
            if(AppLibrary.getDaysDif(t.getTimestamp(), date) <= 1){
                dailyAve -= t.getPrice();
            }
        }
        return dailyAve / AppLibrary.getDaysDif(endDate, date);
    }

    public ArrayList<Transaction> getLargeTransactions() {
        return largeTransactions;
    }

    public float getRemBudget() {
        Float remBudget = budget;
        for(Transaction t : transactions){
            remBudget -= t.getPrice();
        }
        return remBudget;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public List<Transaction>  getDayTransactions(Timestamp day) {
        List<Transaction> list = new ArrayList<>();
        for (Transaction t: getTransactions()) {
            if (AppLibrary.isDateEqual(t.getTimestamp(), day)) {
                list.add(t);
            }
        }
        return list;
    }

    //when user changes dates, remove transactions that are out of bounds
    public void resetTransactions() {
        ArrayList<Transaction> oldTransactions = transactions;
        transactions = new ArrayList<Transaction>();
        for (Transaction t : oldTransactions) {
            if (AppLibrary.getDaysDif(this.startDate, AppLibrary.getTimestampWithoutTime(t.getTimestamp())) <= 1 &&
                    AppLibrary.getDaysDif(AppLibrary.getTimestampWithoutTime(t.getTimestamp()), this.endDate) <= 1) {
                transactions.add(t);
            }
        }
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = AppLibrary.getTimestampWithoutTime(endDate);
    }

    public void setLargeTransactions(ArrayList<Transaction> largeTransactions){
        this.largeTransactions = largeTransactions;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = AppLibrary.getTimestampWithoutTime(startDate);
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        if(transactions.size() == 0){
            this.transactions = transactions;
        }
        for(Transaction t: transactions){
            addTransaction(t);
        }
    }

    //PRIVATE

    private void generateDefaultCategories() {
        this.categories = new ArrayList<>();
        this.categories.add("Tuition");
        this.categories.add("Rent");
        this.categories.add("Utilities");
        this.categories.add("Transportation");
        this.categories.add("Food");
        this.categories.add("Entertainment");
    }

}
