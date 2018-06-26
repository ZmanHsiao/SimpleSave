package com.example.simplesave;

import java.util.ArrayList;
import java.util.Arrays;

public class BudgetPlan {

    final private static Category[] defaultCatsArray = {
        new Category("Tuition", 0),
            new Category("Tuition", 0),
    };
//    private static ArrayList<Category> defaultCategories = new ArrayList<Category>(Arrays.asList(
//            {
//                    new Category("Tuition", 0)
//            }
//    ));


    private int initBalance;
    private int remainingBalance;
    private int totalDays;
    private int daysLeft;
    private ArrayList<Category> categories;

    public BudgetPlan(){

    }

    public BudgetPlan(int balance, int totalDays, int daysLeft) {
        this.initBalance = balance;
        this.remainingBalance = balance;
        this.totalDays = totalDays;
        this.daysLeft = daysLeft;
        categories = new ArrayList<Category>();
    }

    public BudgetPlan(int initBalance, int remainingBalance, int totalDays, int daysLeft) {
        this.initBalance = initBalance;
        this.remainingBalance = remainingBalance;
        this.totalDays = totalDays;
        this.daysLeft = daysLeft;
    }

    public BudgetPlan(int initBalance, int remainingBalance, int totalDays, int daysLeft, ArrayList<Category> categories) {
        this.initBalance = initBalance;
        this.remainingBalance = remainingBalance;
        this.totalDays = totalDays;
        this.daysLeft = daysLeft;
        this.categories = categories;
    }
}
