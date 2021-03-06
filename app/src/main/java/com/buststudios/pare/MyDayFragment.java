package com.buststudios.pare;

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
import com.liulishuo.magicprogresswidget.MagicProgressCircle;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyDayFragment extends Fragment {

    TextView balance;
    TextView average;
    TextView todaySpent;
    TextView dailyLimit;
    TextView transactions;
    TextView noTransText;
    Button nextDay;
    MagicProgressCircle mpc;
    RecyclerView recyclerView;
    private BudgetPlan budgetplan;
    private float dailyAve;
    private Timestamp date;

    public MyDayFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_day, container, false);
        balance = (TextView) view.findViewById(R.id.remBalance);
        average = (TextView) view.findViewById(R.id.average);
        todaySpent = (TextView) view.findViewById(R.id.todaySpent);
        dailyLimit = (TextView) view.findViewById(R.id.dailyLimit);
        transactions = (TextView) view.findViewById(R.id.transactions);
        mpc = (MagicProgressCircle) view.findViewById(R.id.mpc);
        nextDay = (Button) view.findViewById(R.id.nextDay);
        recyclerView = (RecyclerView) view.findViewById(R.id.recylcerView);
        noTransText = (TextView) view.findViewById(R.id.none);
        budgetplan = MainActivity.budgetplan;
        date = AppLibrary.getTimestampWithoutTime(new Timestamp(new Date()));
        setDisplay();
        setListeners(view);
        return view;
    }

    public void setDisplay() {
        List<Transaction> transList = new ArrayList<>();
        dailyAve = budgetplan.getDailyAve(date);
        float dailyRem = dailyAve;
        for(Transaction t : budgetplan.getDayTransactions(date)){
            transList.add(t);
            dailyRem -= t.getPrice();
        }
        balance.setText("Balance: $" + AppLibrary.getDollarFormat(budgetplan.getRemBudget()));
        average.setText("Today Remaining: $" + AppLibrary.getDollarFormat(dailyAve));
        todaySpent.setText("Spent Today: $" + AppLibrary.getDollarFormat(dailyAve - dailyRem));
        nextDay.setText(AppLibrary.timestampToDateString(date));
        dailyLimit.setText("$" + AppLibrary.getDollarFormat((float)(Math.round(dailyRem * 100.0) / 100.0)));
        setProgressBar(dailyRem);
        setDailyTrans(transList);
    }

    private void setDailyTrans(List<Transaction> transList) {
        TransactionAdapter adapter = new TransactionAdapter(getContext(), transList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void setProgressBar(double dailyRem) {
        if (dailyRem < 0) {
            dailyLimit.setTextColor(Color.parseColor("#ff0000"));
        } else {
            dailyLimit.setTextColor(Color.parseColor("#00e600"));
        }
        float percentage = (float) ((dailyAve - dailyRem) / dailyAve);
        mpc.setSmoothPercent(percentage);
    }

    public void setListeners(View view) {
        nextDay.setOnClickListener(changeDateListener);
        Button addMoney = (Button) view.findViewById(R.id.increase);
        addMoney.setOnClickListener(addMoneyListener);
        Button addTrans = (Button) view.findViewById(R.id.addTrans);
        addTrans.setOnClickListener(addTransListener);
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
                    String num = price.getText().toString();
                    if (!name.isEmpty() && !num.isEmpty()) {
                        float val = Float.valueOf(num);
                        budgetplan.addTransaction(category, name, val, transactionDate);
                        display.dismiss();
                        setDisplay();
                        AppLibrary.pushUser(MainActivity.user);
                    } else if (name.isEmpty()) {
                        title.setBackgroundResource(R.drawable.red_border);
                        Toast.makeText(getActivity(), "Not a Valid Input", Toast.LENGTH_SHORT).show();
                    } else {
                        price.setBackgroundResource(R.drawable.red_border);
                        Toast.makeText(getActivity(), "Not a Valid Input", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    };


    private View.OnClickListener addMoneyListener = new View.OnClickListener() {
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final View mView = getLayoutInflater().inflate(R.layout.add_money_dialog, null);
            final EditText value = (EditText) mView.findViewById(R.id.add);
            Button button = (Button) mView.findViewById(R.id.addMoney);
            builder.setView(mView);
            builder.create();
            final AlertDialog display = builder.show();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String text = value.getText().toString();
                    if (!text.isEmpty()) {
                        float val = Float.valueOf(text);
                        budgetplan.addMoney(val);
                        Toast.makeText(getContext(), "Added Money!", Toast.LENGTH_SHORT).show();
                        display.dismiss();
                        setDisplay();
                        AppLibrary.pushUser(MainActivity.user);
                    } else {
                        value.setBackgroundResource(R.drawable.red_border);
                        Toast.makeText(getActivity(), "Not a Valid Input", Toast.LENGTH_SHORT).show();
                    }

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
                }
            });
        }
    };
}
