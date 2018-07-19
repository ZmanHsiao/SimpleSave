package com.example.simplesave;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;


public class MyDayFragment extends Fragment {

    TextView balance;
    TextView average;
    TextView currentDate;
    TextView dailyLimit;
    TextView transactions;
    ProgressBar progressBar;
    private BudgetPlan budgetplan;
    private double dailyAve;
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
        budgetplan = MainActivity.budgetplan;
        date = new Timestamp(new Date());
        setDisplay();
        setListeners(view);
        return view;
    }

    public void setDisplay() {
//this code will make daily ave change based on previous spendings
        dailyAve = budgetplan.getBudget();
        for(Transaction t : budgetplan.getTransactions()){
            if(t.getTimestamp().getSeconds() < date.getSeconds()){
                dailyAve -= t.getPrice();
            }
        }
        dailyAve /= budgetplan.getDaysLeft();
        //dailyAve = budgetplan.getBudget() / budgetplan.getTotalDays();
        double dailyRem = dailyAve;
        for(Transaction t : budgetplan.getDayTransactions(date)){
            dailyRem -= t.getPrice();
        }
        balance.setText("$" + budgetplan.getRemBudget());
        average.setText("$" + Math.round(dailyAve * 100.0) / 100.0);
        currentDate.setText(AppLibrary.timestampToDateString(date));
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
        animation.setDuration(1000); //0.5 second
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
                                                budgetplan.getCategories().keySet().toArray(new String[0]));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown.setAdapter(adapter);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String category = dropdown.getSelectedItem().toString();
                    String name = title.getText().toString();
                    float val = Float.valueOf(price.getText().toString());
                    budgetplan.addTransaction(category, name, val, date);
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
                    budgetplan.addMoney(val);
                    dailyAve = budgetplan.getRemBudget() / budgetplan.getDaysLeft();
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
            View mView = getLayoutInflater().inflate(R.layout.dialog_calendar_layout, null);
            final CalendarView calendar = (CalendarView) mView.findViewById((R.id.calendar));
            calendar.setDate(date.toDate().getTime());
            calendar.setMinDate(budgetplan.getStartDate().toDate().getTime());
            calendar.setMaxDate(budgetplan.getEndDate().toDate().getTime());
            builder.setView(mView);
            builder.create();
            final AlertDialog display = builder.show();
            calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, dayOfMonth);
                    date = new Timestamp(c.getTime());
                    dailyAve = budgetplan.getRemBudget() / budgetplan.getDaysLeft();
                    display.dismiss();
                    setDisplay();
                    AppLibrary.pushUser(MainActivity.user);
                }
            });
        }
    };



}
