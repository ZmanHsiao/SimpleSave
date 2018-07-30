package com.example.simplesave;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InputBudgetActivity extends AppCompatActivity {

    private User user;
    private BudgetPlan budgetplan;

    private EditText editBudget;
    private Timestamp startDate;
    private Timestamp endDate;
    private Button startDateButton;
    private Button endDateButton;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_budget);

        user = AppLibrary.deserializeUser(getIntent());
        budgetplan = user.getBudgetPlan();
        editBudget = (EditText) findViewById(R.id.budget);
        startDateButton = (Button) findViewById(R.id.startDateButton);
        endDateButton = (Button) findViewById(R.id.endDateButton);
        submitButton = (Button) findViewById(R.id.submit);
        startDate = budgetplan.getStartDate();
        endDate = budgetplan.getEndDate();
        editBudget.setText(String.valueOf(budgetplan.getBudget()));
        setDisplay();

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InputBudgetActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_calendar_layout, null);
                final CalendarView calendar = (CalendarView) mView.findViewById((R.id.calendar));
                calendar.setDate(startDate.toDate().getTime());
                calendar.setMaxDate(endDate.toDate().getTime());
                builder.setView(mView);
                builder.create();
                final AlertDialog display = builder.show();
                calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, dayOfMonth);
                        startDate = new Timestamp(c.getTime());
                        setDisplay();
                        display.dismiss();
                    }
                });
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InputBudgetActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_calendar_layout, null);
                final CalendarView calendar = (CalendarView) mView.findViewById((R.id.calendar));
                calendar.setDate(endDate.toDate().getTime());
                calendar.setMinDate(startDate.toDate().getTime());
                builder.setView(mView);
                builder.create();
                final AlertDialog display = builder.show();
                calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, dayOfMonth);
                        endDate = new Timestamp(c.getTime());
                        setDisplay();
                        display.dismiss();
                    }
                });
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editBudget.getText().toString();
                if (!text.isEmpty()) {
                    float budg = Float.valueOf(editBudget.getText().toString());
                    budgetplan.setBudget(budg);
                    budgetplan.setStartDate(startDate);
                    budgetplan.setEndDate(endDate);
                    budgetplan.resetTransactions();
                    AppLibrary.pushUser(user);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent = AppLibrary.serializeUser(user, intent);
                    startActivity(intent);
                } else {
                    editBudget.setBackgroundResource(R.drawable.red_border);
                    Toast.makeText(getApplicationContext(), "Not a Valid Input", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void setDisplay() {
        DateFormat df = new SimpleDateFormat("MMM/dd/YYYY");
        startDateButton.setText("Start Date: " + df.format(startDate.toDate()));
        endDateButton.setText("End Date: " + df.format(endDate.toDate()));
    }

}
