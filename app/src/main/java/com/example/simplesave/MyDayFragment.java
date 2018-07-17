package com.example.simplesave;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class MyDayFragment extends Fragment {

    TextView balance;
    TextView average;
    TextView currentDate;
    TextView dailyLimit;
    TextView transactions;
    ProgressBar progressBar;
    private static double dailyAve = Main2Activity.budgetplan.getRemBudget() / Main2Activity.budgetplan.getDaysLeft();
    private Timestamp date;

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
        currentDate = (TextView) view.findViewById(R.id.remDays);
        dailyLimit = (TextView) view.findViewById(R.id.dailyLimit);
        transactions = (TextView) view.findViewById(R.id.transactions);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        date = new Timestamp(new Date());
        setDisplay();
        setListeners(view);
        return view;
    }

    public void setDisplay() {
        double dailyRem = dailyAve;
        for(Transaction t : Main2Activity.budgetplan.getDayTransactions(date)){
            dailyRem -= t.getPrice();
            System.out.println("GAY: " + date + " " + t.getPrice());
        }
        balance.setText("$" + Main2Activity.budgetplan.getRemBudget());
        average.setText("$" + Math.round(dailyAve * 100.0) / 100.0);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        currentDate.setText(df.format(date.toDate()));
        dailyLimit.setText("$" + Math.round(dailyRem * 100.0) / 100.0);
        if (dailyRem < 0) {
            dailyLimit.setTextColor(Color.parseColor("#ff0000"));
        } else {
            dailyLimit.setTextColor(Color.parseColor("#00e600"));
        }
        setProgressBar(dailyRem);
    }

    private void setProgressBar(double dailyRem) {
        int percentage = (int) (((dailyAve - dailyRem) / dailyAve) * 1000);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", percentage);
        animation.setDuration(750); //0.5 second
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
        if (percentage >= 100) {
            progressBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        }
    }

    public void setListeners(View view) {
        Button addMoney = (Button) view.findViewById(R.id.increase);
        addMoney.setOnClickListener(addMoneyListener);
        Button addTrans = (Button) view.findViewById(R.id.addTrans);
        addTrans.setOnClickListener(addTransListener);
        Button nextDay = (Button) view.findViewById(R.id.nextDay);
        nextDay.setOnClickListener(nextDayListener);
    }



    private View.OnClickListener addTransListener = new View.OnClickListener() {
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View mView = getLayoutInflater().inflate(R.layout.add_transaction_dialog, null);
            final EditText price = (EditText) mView.findViewById(R.id.price);
            final EditText title = (EditText) mView.findViewById(R.id.title);
            Button button = (Button) mView.findViewById(R.id.addTrans);
            builder.setView(mView);
            builder.create();
            final AlertDialog display = builder.show();

            final Spinner dropdown = (Spinner) mView.findViewById(R.id.categoryDropdown);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,
                                                                Main2Activity.budgetplan.getCategoriesArray());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown.setAdapter(adapter);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String category = dropdown.getSelectedItem().toString();
                    String name = title.getText().toString();
                    float val = Float.valueOf(price.getText().toString());
                    Main2Activity.budgetplan.addTransaction(category, name, val, date);
                    display.dismiss();
                    setDisplay();
                }
            });
        }
    };


    private View.OnClickListener addMoneyListener = new View.OnClickListener() {
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View mView = getLayoutInflater().inflate(R.layout.add_money_dialog, null);
            final EditText value = (EditText) mView.findViewById(R.id.add);
            Button button = (Button) mView.findViewById(R.id.addMoney);
            builder.setView(mView);
            builder.create();
            final AlertDialog display = builder.show();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    float val = Float.valueOf(value.getText().toString());
                    Main2Activity.budgetplan.addMoney(val);
                    dailyAve = Main2Activity.budgetplan.getRemBudget() / Main2Activity.budgetplan.getDaysLeft();
                    Toast.makeText(getContext(), "Added Money!", Toast.LENGTH_SHORT).show();
                    display.dismiss();
                    setDisplay();
                }
            });
        }
    };

    private View.OnClickListener nextDayListener = new View.OnClickListener() {
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View mView = getLayoutInflater().inflate(R.layout.dialog_map_layout, null);
            final CalendarView calendar = (CalendarView) mView.findViewById((R.id.calendar));
            calendar.setDate(date.toDate().getTime());
            calendar.setMinDate(Main2Activity.budgetplan.getStartDate().toDate().getTime());
            calendar.setMaxDate(Main2Activity.budgetplan.getEndDate().toDate().getTime());
            builder.setView(mView);
            builder.create();
            final AlertDialog display = builder.show();
            calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, dayOfMonth);
                    date = new Timestamp(c.getTime());
                    dailyAve = Main2Activity.budgetplan.getRemBudget() / Main2Activity.budgetplan.getDaysLeft();
                    display.dismiss();
                    setDisplay();
                    AppLibrary.pushUser(Main2Activity.user);
                }
            });
        }
    };



}
