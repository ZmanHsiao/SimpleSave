package com.example.simplesave;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;

public class BudgetPlan implements Parcelable {

    private int totalDays;
    private int daysLeft;
    private Bundle categories;
    private Bundle transactions;

    //constructors

    public BudgetPlan(){
        this(0, 0);
    }

    public BudgetPlan(int days) {
        this(days, days);
    }

    public BudgetPlan(int totalDays, int daysLeft) {
        this.totalDays = totalDays;
        this.daysLeft = daysLeft;
        generateDefaultCategories();
        transactions = new Bundle();
    }

    public BudgetPlan(int totalDays, int daysLeft, Bundle categories) {
        this.totalDays = totalDays;
        this.daysLeft = daysLeft;
        this.categories = categories;
        transactions = new Bundle();
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
        totalDays = in.readInt();
        daysLeft = in.readInt();
        categories = in.readBundle();
        transactions = in.readBundle();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(totalDays);
        dest.writeInt(daysLeft);
        dest.writeBundle(categories);
        dest.writeBundle(transactions);
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