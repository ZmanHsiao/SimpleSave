package com.example.simplesave;


import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void begin(View view) {
        setContentView(R.layout.income_finance);
    }

    public void next(View view) {
        setContentView(R.layout.expense_finance);
    }

    public void selectPlan(View view) {
        setContentView(R.layout.select_plan);
    }
}
