package com.example.simplesave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CheckDayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_day);

        Intent intent = getIntent();
        int day = intent.getIntExtra("selected", 0) - 1;

        String text = "";
        for (int i = 0; i < TransactionsActivity.name[day].size(); i++) {
            text += TransactionsActivity.name[day].get(i) + "   " + TransactionsActivity.price[day].get(i) + "\n";
        }
        TextView transactions = (TextView) findViewById(R.id.transactions);
        TextView selectedDay = (TextView) findViewById(R.id.selectedDay);
        selectedDay.setText("Day " + (day+1));
        transactions.setText(text);
    }
}
