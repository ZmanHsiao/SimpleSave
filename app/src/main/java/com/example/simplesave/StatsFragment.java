package com.example.simplesave;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Timestamp;
import java.util.Date;


public class StatsFragment extends Fragment {

    private BudgetPlan budgetplan;
    GraphView spendingsOverTime;
    GraphView categoriesGraph;

    public StatsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        budgetplan = MainActivity.budgetplan;

        spendingsOverTime = (GraphView) view.findViewById(R.id.spendingsOverTime);
        categoriesGraph = (GraphView) view.findViewById(R.id.categoriesGraph);
        displaySpendingsGraph();

        return view;
    }

    private void displaySpendingsGraph(){
        DataPoint[] points = new DataPoint[budgetplan.getTransactions().size()];
        int i = 0;
        float prevPrice = 0;
        for(Transaction t : budgetplan.getTransactions()){
            System.out.println("time: " + t.getTimestamp().toDate());
            points[i] = new DataPoint(t.getTimestamp().toDate(), t.getPrice() + prevPrice);
            ++i;
            prevPrice += t.getPrice();
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        spendingsOverTime.addSeries(series);

        spendingsOverTime.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        spendingsOverTime.getGridLabelRenderer().setNumHorizontalLabels(3);

        spendingsOverTime.getViewport().setMinX(budgetplan.getStartDate().toDate().getTime());
        spendingsOverTime.getViewport().setXAxisBoundsManual(true);

        spendingsOverTime.getViewport().setMinY(0);
        spendingsOverTime.getViewport().setMaxY(budgetplan.getBudget());
        spendingsOverTime.getViewport().setYAxisBoundsManual(true);

        spendingsOverTime.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        spendingsOverTime.getViewport().setScalableY(true); // enables vertical zooming and scrolling

        spendingsOverTime.getGridLabelRenderer().setHumanRounding(false);
    }

    private void displayCategoriesGraph(){
        String[] categories = (String[]) budgetplan.getCategories().toArray();
        DataPoint[] points = new DataPoint[categories.length];
    }
}

