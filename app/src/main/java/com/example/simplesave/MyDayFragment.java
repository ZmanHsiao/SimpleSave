package com.example.simplesave;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MyDayFragment extends Fragment {

//    public static int dailyAve = TransactionsActivity.remBudget / TransactionsActivity.remTime;
//    public static int dailyRem = TransactionsActivity.remBudget / TransactionsActivity.remTime;

    TextView balance;
    TextView average;
    TextView remDays;
    TextView dailyLimit;
    TextView transactions;

    public MyDayFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_daily, container, false);
        balance = (TextView) view.findViewById(R.id.remBalance);
        average = (TextView) view.findViewById(R.id.average);
        remDays = (TextView) view.findViewById(R.id.remDays);
        dailyLimit = (TextView) view.findViewById(R.id.dailyLimit);
        transactions = (TextView) view.findViewById(R.id.transactions);
        setDisplay(view);
        return view;
    }

    public void setDisplay(View view) {
//        balance.setText("$" + TransactionsActivity.remBudget);
//        average.setText("$" + dailyAve);
//        remDays.setText(TransactionsActivity.remTime + " days");
//        dailyLimit.setText("$" + dailyRem);
//
//
//
//        String text = "";
//        int current = MainActivity.time - TransactionsActivity.remTime;
//        for (int i = 0; i < TransactionsActivity.name[current].size(); i++) {
//            text += TransactionsActivity.name[current].get(i) + "   " + TransactionsActivity.price[current].get(i) + "\n";
//        }
//        transactions.setText(text);

        Button button = (Button) view.findViewById(R.id.increase);
        button.setOnClickListener(addMoneyListener);
    }

    public void addTrans(View view) {
//        Intent intent = new Intent(this, AddTransactionActivity.class);
//        startActivity(intent);
    }

    public void nextDay(View view) {
//        TransactionsActivity.remTime--;
//        dailyAve = TransactionsActivity.remBudget / TransactionsActivity.remTime;
//        dailyRem = TransactionsActivity.remBudget / TransactionsActivity.remTime;
//        average.setText(dailyAve);
//        dailyLimit.setText(dailyRem);
//        remDays.setText(TransactionsActivity.remTime);
    }
    private View.OnClickListener addMoneyListener = new View.OnClickListener() {
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View mView = getLayoutInflater().inflate(R.layout.add_money_dialog, null);
            final EditText value = (EditText) mView.findViewById(R.id.add);
            Button button = (Button) mView.findViewById(R.id.addMoney);

            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int val = Integer.parseInt(value.getText().toString());
                    Toast.makeText( getContext(), "Added Money!", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setView(mView);
            AlertDialog dialog = builder.create();
            builder.show();
        }
    };


}
