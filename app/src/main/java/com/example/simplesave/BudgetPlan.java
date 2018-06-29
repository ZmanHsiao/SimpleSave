package com.example.simplesave;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

public class BudgetPlan implements Parcelable {

    private float budget;
    private float remBudget;
    private int totalDays;
    private int daysLeft;
    private Bundle categories;
    // arraylist of arraylist, first level index (sorted by day), second level transactions
    private List<List<Transaction>> transactions;

    //constructors

public void fuckbrian(){
    transactions = new ArrayList<List<Transaction>>();
}


    public BudgetPlan(){
        this(0, 0);
        transactions = new ArrayList<List<Transaction>>();
    }

    public BudgetPlan(int days) {
        this(days, days);
    }

    public BudgetPlan(float budget, int totalDays) {
        this.budget = budget;
        this.totalDays = totalDays;
        transactions = new ArrayList<List<Transaction>>();
    }

    public BudgetPlan(int totalDays, int daysLeft) {
        this.totalDays = totalDays;
        this.daysLeft = daysLeft;
        generateDefaultCategories();
        transactions = new ArrayList<List<Transaction>>();
        for (int i = 0; i < totalDays; i++) {
            transactions.add(new ArrayList<Transaction>());
        }
    }

    public BudgetPlan(int totalDays, int daysLeft, Bundle categories) {
        this.totalDays = totalDays;
        this.daysLeft = daysLeft;
        this.categories = categories;
    }

    private void generateDefaultCategories(){
        categories = new Bundle();
        this.categories.putParcelable("Tuition", new Budget());
        this.categories.putParcelable("Rent", new Budget());
        this.categories.putParcelable("Utilies", new Budget());
        this.categories.putParcelable("Transportation", new Budget());
        this.categories.putParcelable("Food", new Budget());
        this.categories.putParcelable("Entertainment", new Budget());
    }


    //getters and setters

    @Exclude
    public float getInitBalance(){
        float sum = 0;
        for(String key : categories.keySet()){
            sum += ((Budget)categories.get(key)).getInitBalance();
        }
        return sum;
    }

    @Exclude
    public float getRemainingBalance(){
        float sum = 0;
        for(String key : categories.keySet()){
            sum += ((Budget)categories.get(key)).getRemainingBalance();
        }
        return sum;
    }

    @Exclude
    public void nextDay() {
        daysLeft--;
        transactions.add(new ArrayList<Transaction>());
    }

    public void addTransaction(String name, float price) {
        int currentDay = totalDays - daysLeft;
        if (currentDay == 0) {
            transactions.add(new ArrayList<Transaction>());
        }
        Transaction transaction = new Transaction(name, price);
        transactions.get(currentDay).add(transaction);
    }

    public float getBudget() { return budget; }

    public void setBudget(float budget) { this.budget = budget; }

    public float getRemBudget() { return remBudget; }

    public void setRemBudget(float remBudget) { this.remBudget = remBudget; }

    public List<List<Transaction>> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<List<Transaction>> transactions) {
        this.transactions = transactions;
    }

    public float getBalancePerDay(){
        return getRemainingBalance() / daysLeft;
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

    public Bundle getCategories() {
        return categories;
    }

    public void setCategories(Bundle categories) {
        this.categories = categories;
    }




    protected BudgetPlan(Parcel in) {
        budget = in.readFloat();
        remBudget = in.readFloat();
        totalDays = in.readInt();
        daysLeft = in.readInt();
        categories = in.readBundle();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(budget);
        dest.writeFloat(remBudget);
        dest.writeInt(totalDays);
        dest.writeInt(daysLeft);
        dest.writeBundle(categories);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BudgetPlan> CREATOR = new Parcelable.Creator<BudgetPlan>() {
        @Override
        public BudgetPlan createFromParcel(Parcel in) {
            return new BudgetPlan(in);
        }

        @Override
        public BudgetPlan[] newArray(int size) {
            return new BudgetPlan[size];
        }
    };
}