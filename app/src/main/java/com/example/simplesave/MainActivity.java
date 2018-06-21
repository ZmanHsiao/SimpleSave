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

    String balance;
    String parent;
    String aid;
    String wage;
    String wageFreq;
    String rent;
    String util;
    String food;
    String trans;
    String allowance;
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
        balance = ((EditText) findViewById(R.id.balance)).getText().toString();
        parent = ((EditText) findViewById(R.id.parent)).getText().toString();
        aid = ((EditText) findViewById(R.id.aid)).getText().toString();
        wage = ((EditText) findViewById(R.id.wage)).getText().toString();
        wageFreq = ((EditText) findViewById(R.id.frequency)).getText().toString();
        setContentView(R.layout.expense_finance);
    }

    public void nextExpense(View view) {
        rent = ((EditText) findViewById(R.id.rent)).getText().toString();
        util = ((EditText) findViewById(R.id.utilities)).getText().toString();
        food = ((EditText) findViewById(R.id.food)).getText().toString();
        trans = ((EditText) findViewById(R.id.transportation)).getText().toString();
        allowance = ((EditText) findViewById(R.id.allowance)).getText().toString();

        setContentView(R.layout.select_plan);
    }

    public void nextPlan(View view) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        System.out.println(radioButton.getText());

        setContentView(R.layout.activity_display_info);
        TextView info = (TextView) findViewById(R.id.info);
        info.setText(balance);
    }
}
