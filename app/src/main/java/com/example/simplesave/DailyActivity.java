package com.example.simplesave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DailyActivity extends AppCompatActivity {

    public static int dailyAve = TransactionsActivity.remBudget / TransactionsActivity.remTime;
    public static int dailyRem = TransactionsActivity.remBudget / TransactionsActivity.remTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        TextView balance = (TextView) findViewById(R.id.remBalance);
        TextView average = (TextView) findViewById(R.id.average);
        TextView remDays = (TextView) findViewById(R.id.remDays);
        TextView dailyLimit = (TextView) findViewById(R.id.dailyLimit);
        TextView transactions = (TextView) findViewById(R.id.transactions);

        balance.setText("$" + TransactionsActivity.remBudget);
        average.setText("$" + dailyAve);
        remDays.setText(TransactionsActivity.remTime + " days");
        dailyLimit.setText("$" + dailyRem);

        String text = "";
        int current = MainActivity.time - TransactionsActivity.remTime;
        for (int i = 0; i < TransactionsActivity.name[current].size(); i++) {
            text += TransactionsActivity.name[current].get(i) + "   " + TransactionsActivity.price[current].get(i) + "\n";
        }
        transactions.setText(text);
    }

    public void add(View view) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        startActivity(intent);
    }

    public void nextDay(View view) {
        TransactionsActivity.remTime--;
        dailyAve = TransactionsActivity.remBudget / TransactionsActivity.remTime;
        dailyRem = TransactionsActivity.remBudget / TransactionsActivity.remTime;
        Intent intent = new Intent(this, DailyActivity.class);
        startActivity(intent);
    }
}
