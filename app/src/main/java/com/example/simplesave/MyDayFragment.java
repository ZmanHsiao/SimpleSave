package com.example.simplesave;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MyDayFragment extends Fragment {

    TextView balance;
    TextView average;
    TextView currentDate;
    TextView dailyLimit;
    TextView transactions;
    ProgressBar progressBar;
    Button nextDay;
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
//        balance = (TextView) view.findViewById(R.id.remBalance);
//        average = (TextView) view.findViewById(R.id.average);
//        currentDate = (TextView) view.findViewById(R.id.remDays);
        dailyLimit = (TextView) view.findViewById(R.id.dailyLimit);
        transactions = (TextView) view.findViewById(R.id.transactions);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        nextDay = (Button) view.findViewById(R.id.nextDay);
        budgetplan = MainActivity.budgetplan;
        date = AppLibrary.getTimestampWithoutTime(new Timestamp(new Date()));
        setDisplay();
        setListeners(view);
        return view;
    }

    public void setDisplay() {
        dailyAve = budgetplan.getBudget();
        for(Transaction t : budgetplan.getTransactions()){
            if(AppLibrary.isDateEqual(t.getTimestamp(), date)){
                dailyAve -= t.getPrice();
            }
        }
        dailyAve /= AppLibrary.getDaysDif(budgetplan.getEndDate(), date);
        double dailyRem = dailyAve;
        for(Transaction t : budgetplan.getDayTransactions(date)){
            dailyRem -= t.getPrice();
        }
//        balance.setText("$" + budgetplan.getRemBudget());
//        average.setText("$" + Math.round(dailyAve * 100.0) / 100.0);
//        currentDate.setText(AppLibrary.timestampToDateString(date));
        nextDay.setText(AppLibrary.timestampToDateString(date));
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
        nextDay.setOnClickListener(changeDateListener);
        Button addMoney = (Button) view.findViewById(R.id.increase);
        addMoney.setOnClickListener(addMoneyListener);
        Button addTrans = (Button) view.findViewById(R.id.addTrans);
        addTrans.setOnClickListener(addTransListener);
        Button viewTrans = (Button) view.findViewById(R.id.viewTrans);
        viewTrans.setOnClickListener(viewDailyTrans);
    }



    private View.OnClickListener addTransListener = new View.OnClickListener() {
        Timestamp transactionDate = new Timestamp(new Date());
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View mView = getLayoutInflater().inflate(R.layout.add_transaction_dialog, null);
            final EditText price = (EditText) mView.findViewById(R.id.price);
            final EditText title = (EditText) mView.findViewById(R.id.title);
            Button timestampButton = (Button) mView.findViewById(R.id.timestampButton);
            Button addButton = (Button) mView.findViewById(R.id.addTrans);
            builder.setView(mView);
            builder.create();
            final AlertDialog display = builder.show();

            final Spinner dropdown = (Spinner) mView.findViewById(R.id.categoryDropdown);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,
                                                budgetplan.getCategories().toArray(new String[0]));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown.setAdapter(adapter);

            timestampButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    View mView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
                    final DatePicker datePicker = (DatePicker) mView.findViewById(R.id.datePicker);
                    datePicker.setMinDate(budgetplan.getStartDate().toDate().getTime());
                    datePicker.setMaxDate(budgetplan.getEndDate().toDate().getTime());
                    final TimePicker timePicker = (TimePicker) mView.findViewById(R.id.timePicker);
                    Button doneButton = (Button) mView.findViewById(R.id.doneButton);
                    builder.setView(mView);
                    builder.create();
                    final AlertDialog display = builder.show();

                    doneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar c = Calendar.getInstance();
                            c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            transactionDate = new Timestamp(c.getTime());
                            display.dismiss();
                        }
                    });
                }
            });

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String category = dropdown.getSelectedItem().toString();
                    String name = title.getText().toString();
                    float val = Float.valueOf(price.getText().toString());
                    budgetplan.addTransaction(category, name, val, transactionDate);
                    display.dismiss();
                    setDisplay();
                    AppLibrary.pushUser(MainActivity.user);
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
                    Toast.makeText(getContext(), "Added Money!", Toast.LENGTH_SHORT).show();
                    display.dismiss();
                    setDisplay();
                }
            });
        }
    };

    private View.OnClickListener changeDateListener = new View.OnClickListener() {
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
                    date = AppLibrary.getTimestampWithoutTime(new Timestamp(c.getTime()));
                    display.dismiss();
                    setDisplay();
                    AppLibrary.pushUser(MainActivity.user);
                }
            });
        }
    };

    private View.OnClickListener viewDailyTrans = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            List<Transaction> transList = new ArrayList<>();
            for(Transaction t : budgetplan.getTransactions()){
                if(AppLibrary.isDateEqual(t.getTimestamp(), date)){
                    transList.add(t);
                }
            }

            if (transList.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialog_view_day_trans, null);
                builder.setView(mView);
                builder.create();

                TransactionAdapter adapter = new TransactionAdapter(getContext(), transList, true);
                RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recylcerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
                final AlertDialog display = builder.show();

                Button back = (Button) mView.findViewById(R.id.back);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        display.dismiss();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "No Transactions Today!", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
