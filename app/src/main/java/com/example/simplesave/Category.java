package com.example.simplesave;

public class Category {

    private String name;
    private float initBalance;
    private float remainingBalance;

    public Category(){

    }

    public Category(String name, float balance){
        this.name = name;
        this.initBalance = balance;
        this.remainingBalance = balance;
    }

    public Category(String name, float initBalance, float remainingBalance){
        this.name = name;
        this.initBalance = initBalance;
        this.remainingBalance = remainingBalance;
    }

    //getters

    public String getName() {
        return name;
    }

    public float getTotalBalance() {
        return initBalance;
    }

    public float getRemainingBalance() {
        return remainingBalance;
    }

    //setters

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalBalance(float initBalance) {
        this.initBalance = initBalance;
    }

    public void setRemainingBalance(float remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public void subtractRemainingBalance(float dif){
        this.remainingBalance -= dif;
    }

}
