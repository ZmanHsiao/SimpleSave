package com.example.simplesave;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class EditBudgetPlanFragment extends Fragment {

    private View view;
    private EditText editBudgetPlan;
    private EditText editDays;
    private Button submit;

    public EditBudgetPlanFragment() {
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
        view = inflater.inflate(R.layout.fragment_edit_budget_plan, container, false);
        editBudgetPlan = (EditText) view.findViewById(R.id.editBudget);
        editDays = (EditText) view.findViewById(R.id.editDays);
        submit = (Button) view.findViewById(R.id.submit);
        submit.setOnClickListener(submitListener);

        return view;
    }

    private View.OnClickListener submitListener = new View.OnClickListener() {
        public void onClick(View view) {
            float balanceSpent = Main2Activity.budgetplan.getBudget() - Main2Activity.budgetplan.getRemainingBalance();
            float newBudget = Float.parseFloat(editBudgetPlan.getText().toString());
            Main2Activity.budgetplan.setRemBudget(newBudget - balanceSpent);
            Main2Activity.budgetplan.setBudget(newBudget);
            int oldDays = Main2Activity.budgetplan.getDaysLeft();
            int days = Integer.parseInt(editDays.getText().toString());
            Main2Activity.budgetplan.setDaysLeft(Main2Activity.budgetplan.getDaysLeft() + (days - oldDays));
            Main2Activity.budgetplan.setTotalDays(days);
            Main2Activity.budgetplan.resetTransactions();
        }
    };
}
