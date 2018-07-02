package com.example.simplesave;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {

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
//
//    public HashMap<String, Object> getFirestoreMap(){
//        HashMap<String, Object> firestoreMap = new HashMap<String, Object>();
//        firestoreMap.put("email", email);
//        firestoreMap.put("budgetPlan", budgetPlan.getFirestoreMap());
//        return firestoreMap;
//    }

}