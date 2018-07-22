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
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;


public class StatsFragment extends Fragment {

    private BudgetPlan budgetplan;
    GraphView spendingsGraph;
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

        spendingsGraph = (GraphView) view.findViewById(R.id.spendingsGraph);
        categoriesGraph = (GraphView) view.findViewById(R.id.categoriesGraph);
        displaySpendingsGraph();
        displayCategoriesGraph();

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
        spendingsGraph.addSeries(series);

        spendingsGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        spendingsGraph.getGridLabelRenderer().setNumHorizontalLabels(3);

        spendingsGraph.getViewport().setMinX(budgetplan.getStartDate().toDate().getTime());
        spendingsGraph.getViewport().setXAxisBoundsManual(true);

        spendingsGraph.getViewport().setMinY(0);
        spendingsGraph.getViewport().setYAxisBoundsManual(true);

        spendingsGraph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        spendingsGraph.getViewport().setScalableY(true); // enables vertical zooming and scrolling

        spendingsGraph.getGridLabelRenderer().setHumanRounding(false);
    }

    private void displayCategoriesGraph(){
        String[] categories = Arrays.copyOf(budgetplan.getCategories().toArray(), budgetplan.getCategories().size(), String[].class);
        DataPoint[] points = new DataPoint[categories.length];
        for(int i = 0; i < categories.length; ++i){
            float total = 0;
            for(Transaction t : budgetplan.getTransactions()){
                if(t.getCategory().equals(categories[i])){
                    total += t.getPrice();
                }
            }
            points[i] = new DataPoint(i, total);
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);
        categoriesGraph.addSeries(series);
    }
}

