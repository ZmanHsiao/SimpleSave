package com.example.simplesave;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Budget implements Serializable {

    private float initBalance;
    private float remainingBalance;

    public Budget(){
        this(0);
    }

    public Budget(float balance){
        this(balance, balance);
    }

    public Budget(float initBalance, float remainingBalance){
        this.initBalance = initBalance;
        this.remainingBalance = remainingBalance;
    }


    //getters

    public float getInitBalance() {
        return initBalance;
    }

    public float getRemainingBalance() {
        return remainingBalance;
    }

    //setters

    public void setInitBalance(float initBalance) {
        this.initBalance = initBalance;
    }

    public void setRemainingBalance(float remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public void subtractRemainingBalance(float dif){
        this.remainingBalance -= dif;
    }

}