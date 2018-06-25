package com.example.simplesave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddTransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
    }

    public void addTrans(View view) {
        int current = MainActivity.time - TransactionsActivity.remTime;
        int itemPrice = Integer.parseInt(((EditText) findViewById(R.id.price)).getText().toString());
        DailyActivity.dailyRem -= itemPrice;
        TransactionsActivity.remBudget -= itemPrice;
        TransactionsActivity.name[current].add(((EditText) findViewById(R.id.name)).getText().toString());
        TransactionsActivity.price[current].add(itemPrice);
        Intent intent = new Intent(this, TransactionsActivity.class);
        startActivity(intent);
    }
}
