package com.example.simplesave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddMoneyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
    }

    public void deposit(View view) {
        int increase = Integer.parseInt(((EditText) findViewById(R.id.addMoney)).getText().toString());
        MainActivity.budget += increase;
        TransactionsActivity.remBudget += increase;
        DailyActivity.dailyAve = TransactionsActivity.remBudget / TransactionsActivity.remTime;
        Intent intent = new Intent(this, TransactionsActivity.class);
        startActivity(intent);
    }
}
