package com.example.simplesave;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    public static int remBudget = MainActivity.budget;
    public static int remTime = MainActivity.time;
    public static List<String>[] name;
    public static List<Integer>[] price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        TextView trans = (TextView) findViewById(R.id.transactions);
        String text = "";
        for (int i = 0; i < name.length; i++) {
            for (int j = 0; j < name[i].size(); j++) {
                text += (i + 1) + " - " + name[i].get(j) + "     $" + price[i].get(j) + "\n";
            }
        }
        trans.setText(text);

        Spinner dropdown = (Spinner) findViewById(R.id.dayDropdown);
        String[] items = new String[MainActivity.time];
        for (int i = 0; i < items.length; i++) {
            items[i] = Integer.toString(i+1);
            System.out.println(items[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        dropdown.setAdapter(adapter);
    }

    public void toDay(View view) {
        Intent intent = new Intent(this, DailyActivity.class);
        startActivity(intent);
    }

    public void checkDay(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.dayDropdown);
        int selected = Integer.parseInt(spinner.getSelectedItem().toString());
        Intent intent = new Intent(this, CheckDayActivity.class);
        intent.putExtra("selected", selected);
        startActivity(intent);
    }
}
