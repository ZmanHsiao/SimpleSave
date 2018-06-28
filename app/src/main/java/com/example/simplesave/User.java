package com.example.simplesave;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String email;
    private BudgetPlan budgetPlan;

    public User(){
        this.email = "";
        this.budgetPlan = new BudgetPlan();
    }

    public User(String email){
        this.email = email;
        this.budgetPlan = new BudgetPlan();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BudgetPlan getBudgetPlan() {
        return budgetPlan;
    }

    public void setBudgetPlan(BudgetPlan budgetPlan) {
        this.budgetPlan = budgetPlan;
    }

    protected User(Parcel in) {
        email = in.readString();
        budgetPlan = (BudgetPlan) in.readValue(BudgetPlan.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeValue(budgetPlan);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}