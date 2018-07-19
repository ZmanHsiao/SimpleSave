package com.example.simplesave;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;


public class InputBudgetFragment extends Fragment {

    User u;
    BudgetPlan budgetplan;
    private Timestamp startDate;
    private Timestamp endDate;
    Button startDateButton;
    Button endDateButton;
    Button submitButton;

    public InputBudgetFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_budget, container, false);
        if(getActivity() instanceof SigninActivity){
            Bundle arguments = getArguments();
            u = ((User) arguments.get("user"));
            budgetplan = u.getBudgetPlan();
        }
        else{
            u = MainActivity.user;
            budgetplan = u.getBudgetPlan();
        }
        final EditText budget = (EditText) view.findViewById(R.id.budget);
        budget.setText(String.valueOf(budgetplan.getBudget()));
        final TextView startDateText = (TextView) view.findViewById(R.id.startDateText);
        startDateText.setText("Start Date: " + AppLibrary.timestampToDateString(budgetplan.getStartDate()));
        final TextView endDateText = (TextView) view.findViewById(R.id.endDateText);
        endDateText.setText("End Date: " + AppLibrary.timestampToDateString(budgetplan.getEndDate()));
        startDateButton = (Button) view.findViewById(R.id.startDateButton);
        endDateButton = (Button) view.findViewById(R.id.endDateButton);
        submitButton = (Button) view.findViewById(R.id.submit);
        startDate = new Timestamp(new Date());
        endDate = new Timestamp(new Date());

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialog_calendar_layout, null);
                final CalendarView calendar = (CalendarView) mView.findViewById((R.id.calendar));
                calendar.setDate(startDate.toDate().getTime());
                calendar.setMaxDate(new Date().getTime());
                builder.setView(mView);
                builder.create();
                final AlertDialog display = builder.show();
                calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, dayOfMonth);
                        startDate = new Timestamp(c.getTime());
                        display.dismiss();
                        startDateText.setText("Start Date: " + AppLibrary.timestampToDateString(startDate));
                    }
                });
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialog_calendar_layout, null);
                final CalendarView calendar = (CalendarView) mView.findViewById((R.id.calendar));
                calendar.setDate(endDate.toDate().getTime());
                calendar.setMinDate(new Date().getTime());
                builder.setView(mView);
                builder.create();
                final AlertDialog display = builder.show();
                calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, dayOfMonth);
                        endDate = new Timestamp(c.getTime());
                        display.dismiss();
                        endDateText.setText("End Date: " + AppLibrary.timestampToDateString(endDate));
                    }
                });
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float budg = Float.valueOf(budget.getText().toString());
                budgetplan.setBudget(budg);
                budgetplan.setStartDate(startDate);
                budgetplan.setEndDate(endDate);
                AppLibrary.pushUser(u);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent = AppLibrary.serializeUser(u, intent);
                startActivity(intent);
            }
        });
        return view;
    }

    // close keyboard
    public void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
