package com.example.simplesave;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


public class InputBudgetFragment extends Fragment {

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
        final EditText budget = (EditText) view.findViewById(R.id.budget);
        final EditText time = (EditText) view.findViewById(R.id.time);
        Button button = (Button) view.findViewById(R.id.create);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float budg = Float.valueOf(budget.getText().toString());
                int days = Integer.parseInt(time.getText().toString());
                Main2Activity.budget = Integer.parseInt(budget.getText().toString());
                Main2Activity.time = Integer.parseInt(time.getText().toString());
                Main2Activity.remBudget = Main2Activity.budget;
                Main2Activity.remTime = Main2Activity.time;
                Main2Activity.name = new ArrayList[Main2Activity.time];
                Main2Activity.price = new ArrayList[Main2Activity.time];
                for (int i = 0; i < Main2Activity.name.length; i++) {
                    Main2Activity.name[i] = new ArrayList<>();
                    Main2Activity.price[i] = new ArrayList<>();
                }

                Main2Activity.budgetplan.setBudget(budg);
                Main2Activity.budgetplan.setTotalDays(days);
                Main2Activity.budgetplan.setRemBudget(budg);
                Main2Activity.budgetplan.setDaysLeft(days);
                //update user on Firestore DB
                //UpdateTestUser.updateTestUser(Main2Activity.user);

                Fragment nextFrag = new MyDayFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.area, nextFrag)
                        .addToBackStack(null)
                        .commit();
                hideKeyboardFrom(getContext(), view);
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
