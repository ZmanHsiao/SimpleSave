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

    private User u;
    private BudgetPlan budgetplan;

    private EditText editBudget;
    private Timestamp startDate;
    private Timestamp endDate;
    private TextView startDateText;
    private TextView endDateText;
    private Button startDateButton;
    private Button endDateButton;
    private Button submitButton;

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
        editBudget = (EditText) view.findViewById(R.id.budget);
        startDateText = (TextView) view.findViewById(R.id.startDateText);
        endDateText = (TextView) view.findViewById(R.id.endDateText);
        startDateButton = (Button) view.findViewById(R.id.startDateButton);
        endDateButton = (Button) view.findViewById(R.id.endDateButton);
        submitButton = (Button) view.findViewById(R.id.submit);
        startDate = budgetplan.getStartDate();
        endDate = budgetplan.getEndDate();
        editBudget.setText(String.valueOf(budgetplan.getBudget()));
        setDisplay();

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                float budg = Float.valueOf(editBudget.getText().toString());
                budgetplan.setBudget(budg);
                budgetplan.setStartDate(startDate);
                budgetplan.setEndDate(endDate);
                budgetplan.resetTransactions();
                AppLibrary.pushUser(u);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent = AppLibrary.serializeUser(u, intent);
                startActivity(intent);
            }
        });
        return view;
    }

    public void setDisplay() {
        startDateText.setText("Start Date: " + AppLibrary.timestampToDateString(startDate));
        endDateText.setText("End Date: " + AppLibrary.timestampToDateString(endDate));
    }

    // close keyboard
    public void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
