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
    GraphView graph;

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

        graph = (GraphView) view.findViewById(R.id.graph);
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
        graph.addSeries(series);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        graph.getViewport().setMinX(budgetplan.getStartDate().toDate().getTime());
        graph.getViewport().setMaxX(budgetplan.getEndDate().toDate().getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(budgetplan.getBudget());
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getGridLabelRenderer().setHumanRounding(false);

        return view;
    }

}

