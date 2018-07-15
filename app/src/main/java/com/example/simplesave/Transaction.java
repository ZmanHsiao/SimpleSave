package com.example.simplesave;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements Serializable {
    private String category;
    private String name;
    transient private Timestamp timestamp;
    private float price;

    //constructors
    public Transaction() {
        this.category = null;
        this.name = null;
        this.price = 0;
        timestamp = AppLibrary.getTimestampWithoutTime(new Timestamp(new Date()));
    }

    public Transaction(String category, String name, float price) {
        this.category = category;
        this.name = name;
        this.price = price;
        timestamp = AppLibrary.getTimestampWithoutTime(new Timestamp(new Date()));
    }

    public Transaction(String category, String name, float price, Timestamp timestamp) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.timestamp = timestamp;
    }

    //getters
    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public Timestamp getTimestamp(){
        return timestamp;
    }

    @Exclude
    public String getTimestampString() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(timestamp.toDate());
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

    public void setTimestamp(Timestamp timestamp){
        this.timestamp = timestamp;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}