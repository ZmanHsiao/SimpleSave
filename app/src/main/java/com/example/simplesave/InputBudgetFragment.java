package com.example.simplesave;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class InputBudgetFragment extends Fragment {

    private Timestamp endDate;
    User u;
    BudgetPlan budgetplan;

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
        View view = inflater.inflate(R.layout.simple_info, container, false);
        if(getActivity() instanceof SigninActivity){
            Bundle arguments = getArguments();
            u = ((User) arguments.get("user"));
            budgetplan = u.getBudgetPlan();
        }
        else{
            u = Main2Activity.user;
            budgetplan = u.getBudgetPlan();
        }
        final EditText budget = (EditText) view.findViewById(R.id.budget);
        budget.setText(String.valueOf(budgetplan.getBudget()));
        final CalendarView calendar = (CalendarView) view.findViewById((R.id.calendar));
        Button button = (Button) view.findViewById(R.id.create);

        endDate = new Timestamp(new Date());
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                endDate = new Timestamp(c.getTime());
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float budg = Float.valueOf(budget.getText().toString());
                budgetplan.setBudget(budg);
                budgetplan.setRemBudget(budg);
                budgetplan.setStartDate(new Timestamp(new Date()));
                budgetplan.setEndDate(endDate);

//                Fragment nextFrag = new MyDayFragment();
////                getActivity().getSupportFragmentManager().beginTransaction()
////                        .replace(R.id.area, nextFrag)
////                        .addToBackStack(null)
////                        .commit();
////                hideKeyboardFrom(getContext(), view);
                AppLibrary.pushUser(u);
                Intent intent = new Intent(getActivity(), Main2Activity.class);
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
