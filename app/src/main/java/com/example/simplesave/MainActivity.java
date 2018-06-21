package com.example.simplesave;


import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    int balance;
    int parent;
    int aid;
    int wage;
    int wageFreq;
    int rent;
    int util;
    int food;
    int trans;
    int allowance;
    String plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void begin(View view) {
        setContentView(R.layout.income_finance);
    }

    public void nextIncome(View view) {
        balance = Integer.parseInt(((EditText) findViewById(R.id.balance)).getText().toString());
        parent = Integer.parseInt(((EditText) findViewById(R.id.parent)).getText().toString());
        aid = Integer.parseInt(((EditText) findViewById(R.id.aid)).getText().toString());
        wage = Integer.parseInt(((EditText) findViewById(R.id.wage)).getText().toString());
        wageFreq = Integer.parseInt(((EditText) findViewById(R.id.frequency)).getText().toString());
        setContentView(R.layout.expense_finance);
    }

    public void nextExpense(View view) {
        rent = Integer.parseInt(((EditText) findViewById(R.id.rent)).getText().toString());
        util = Integer.parseInt(((EditText) findViewById(R.id.utilities)).getText().toString());
        food = Integer.parseInt(((EditText) findViewById(R.id.food)).getText().toString());
        trans = Integer.parseInt(((EditText) findViewById(R.id.transportation)).getText().toString());
        allowance = Integer.parseInt(((EditText) findViewById(R.id.allowance)).getText().toString());

        setContentView(R.layout.select_plan);
    }

    public void nextPlan(View view) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);

        setContentView(R.layout.activity_display_info);
//        TextView info = (TextView) findViewById(R.id.info);
//        info.setText(Integer.toString(balance));
    }
}
