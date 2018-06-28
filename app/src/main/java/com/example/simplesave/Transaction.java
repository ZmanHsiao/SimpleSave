package com.example.simplesave;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {
    private String category;
    private String name;
    private int date;
    private float price;

    //constructors
    public Transaction() {
        this.category = null;
        this.name = null;
        this.date = 0;
        this.price = 0;
    }

    public Transaction(String category, String name, float price) {
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public Transaction(String category, String name, int time, float price) {
        this.category = category;
        this.name = name;
        this.date = time;
        this.price = price;
    }

    //getters
    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
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

    public void setDate(int time) {
        this.date = time;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    protected Transaction(Parcel in) {
        category = in.readString();
        name = in.readString();
        date = in.readInt();
        price = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(name);
        dest.writeInt(date);
        dest.writeFloat(price);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}