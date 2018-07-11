package com.example.simplesave;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private String category;
    private String name;
    private Date date;
    private int day;
    private float price;

    //constructors
    public Transaction() {
        this.category = null;
        this.name = null;
        this.day = 0;
        this.price = 0;
    }

    public Transaction(String name, float price, int day) {
        this.name = name;
        this.price = price;
        this.day = day;
    }

    public Transaction(String category, String name, float price) {
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public Transaction(String category, String name, int time, float price) {
        this.category = category;
        this.name = name;
        this.day = time;
        this.price = price;
    }

    //getters
    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public Date getDate(){
        return date;
    }

    public int getDay() {
        return day;
    }

    public float getPrice() {
        return price;
    }

    //setters
    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDay(int time) {
        this.day = time;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}