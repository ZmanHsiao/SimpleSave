package com.buststudios.gaysony;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.buststudios.gaysony.AppLibrary.*;

public class StatsOverviewFragment extends Fragment {

    private View view;
    LinearLayout layout;
    private Button editBudget;

    public StatsOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stats_overview, container, false);
        layout = view.findViewById(R.id.mainLayout);
        editBudget = view.findViewById(R.id.editBudgetButton);

        editBudget.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InputBudgetActivity.class);
                intent = serializeUser(MainActivity.user, intent);
                startActivity(intent);
            }
        });

        TextView budgetText = (TextView) view.findViewById(R.id.startingBudget);
        TextView remBudgetText = (TextView) view.findViewById(R.id.remBudget);
        TextView startDateText = (TextView) view.findViewById(R.id.startDate);
        TextView endDateText = (TextView) view.findViewById(R.id.endDate);
        TextView totalDaysText = (TextView) view.findViewById(R.id.days);

        budgetText.setText("Starting Budget: $" + getDollarFormat(StatsFragment.budgetplan.getBudget()));
        remBudgetText.setText("Remaining Budget: $" + getDollarFormat(StatsFragment.budgetplan.getBudget() - StatsFragment.cumulativeSpending));
        startDateText.setText("Start Date: " + timestampToDateString(StatsFragment.budgetplan.getStartDate()));
        endDateText.setText("End Date: " + timestampToDateString(StatsFragment.budgetplan.getEndDate()));
        totalDaysText.setText("Total days: " + StatsFragment.totalDays);

        return view;
    }

}
