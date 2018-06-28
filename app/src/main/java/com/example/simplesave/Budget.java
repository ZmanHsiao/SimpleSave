package com.example.simplesave;

import android.os.Parcel;
import android.os.Parcelable;

public class Budget implements Parcelable {

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


    protected Budget(Parcel in) {
        initBalance = in.readFloat();
        remainingBalance = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(initBalance);
        dest.writeFloat(remainingBalance);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Budget> CREATOR = new Parcelable.Creator<Budget>() {
        @Override
        public Budget createFromParcel(Parcel in) {
            return new Budget(in);
        }

        @Override
        public Budget[] newArray(int size) {
            return new Budget[size];
        }
    };
}