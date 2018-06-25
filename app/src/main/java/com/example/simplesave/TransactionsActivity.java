package com.example.simplesave;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    public static int remBudget = MainActivity.budget;
    public static int remTime = MainActivity.time;
    public static List<String>[] name = new ArrayList[MainActivity.time];
    public static List<Integer>[] price = new ArrayList[MainActivity.time];
    public static boolean first = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        TextView trans = (TextView) findViewById(R.id.transactions);
        if (first) {
            for (int i = 0; i < name.length; i++) {
                name[i] = new ArrayList<>();
                price[i] = new ArrayList<>();
            }
            first = false;
        }

        String text = "";
        for (int i = 0; i < name.length; i++) {
            for (int j = 0; j < name[i].size(); j++) {
                text += (i + 1) + " - " + name[i].get(j) + "     $" + price[i].get(j) + "\n";
            }
        }
        trans.setText(text);
    }

    public void toDay(View view) {
        Intent intent = new Intent(this, DailyActivity.class);
        startActivity(intent);
    }
}
