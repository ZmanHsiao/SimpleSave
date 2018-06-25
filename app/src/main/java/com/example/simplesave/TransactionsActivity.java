package com.example.simplesave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    public static List<String> name = new ArrayList<>();
    public static List<Integer> price = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        TextView trans = (TextView) findViewById(R.id.transactions);
        String text = "";
        for (int i = 0; i < name.size(); i++) {
            text += name.get(i) + "     $" + price.get(i) + "\n";
        }
        trans.setText(text);
    }

    public void add(View view) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        startActivity(intent);
    }
}
