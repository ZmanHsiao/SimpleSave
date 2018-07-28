package com.example.simplesave;

import android.app.AlertDialog;
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

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.google.firebase.Timestamp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.simplesave.AppLibrary.*;
import static java.lang.Math.abs;


public class StatsFragment extends Fragment {

    int totalDays;
    int daysSoFar;
    private float cumulativeSpending;
    private float constDailyAvg;
    private float averageSpending;
    private float projection;
    private BudgetPlan budgetplan;

    private View view;
    private LinearLayout overviewLayout, projectionLayout, spendingsLayout, categoriesLayout;
    private LineChart projectionGraph, spendingsGraph;
    private BarChart categoriesGraph;

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
        view = inflater.inflate(R.layout.fragment_stats, container, false);

        budgetplan = MainActivity.budgetplan;

        overviewLayout = view.findViewById(R.id.overviewLayout);
        projectionLayout = view.findViewById(R.id.projectionLayout);
        spendingsLayout = view.findViewById(R.id.spendingsLayout);
        categoriesLayout = view.findViewById(R.id.categoriesLayout);
        projectionGraph = view.findViewById(R.id.projectionGraph);
        spendingsGraph = view.findViewById(R.id.spendingsGraph);
        categoriesGraph = view.findViewById(R.id.categoriesGraph);
        setDisplay();

        overviewLayout.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InputBudgetActivity.class);
                intent = serializeUser(MainActivity.user, intent);
                startActivity(intent);
            }
        });

        return view;
    }

    private void setDisplay(){

        //set values and display graphs
        totalDays = getDaysDif(budgetplan.getEndDate(), budgetplan.getStartDate());
        daysSoFar = getDaysDif(getTimestampWithoutTime(new Timestamp(new Date())), budgetplan.getStartDate());
        constDailyAvg = budgetplan.getBudget() / totalDays;

        displayDateGraphs();
        displayCategoriesGraph();

        averageSpending =  cumulativeSpending / daysSoFar;

        displayOverviewText();
        displayProjectionText();
        displaySpendingsText();
    }

    private void displayDateGraphs(){
        //set data for projection and spendings graphs and calculate values
        List<Entry> projectionEntries = new ArrayList<>();
        List<Entry> spendingsEntries = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTime(budgetplan.getStartDate().toDate());
        ArrayList<String> dates = new ArrayList<>();
        Timestamp today = getTimestampWithoutTime(new Timestamp(new Date()));
        cumulativeSpending = 0;
        projection = 0;
        for(int i = 0; i < getDaysDif(today, budgetplan.getStartDate()); ++i){
            dates.add(timestampToDateString(new Timestamp(c.getTime())));
            float dayTotal = 0;
            for(Transaction t : budgetplan.getTransactions()){
                if(isDateEqual(new Timestamp(c.getTime()), t.getTimestamp())) {
                    cumulativeSpending += t.getPrice();
                }
            }
            cumulativeSpending += dayTotal;
            projection += dayTotal - constDailyAvg;
            spendingsEntries.add(new Entry(i, dayTotal));
            projectionEntries.add(new Entry(i, cumulativeSpending - constDailyAvg * (i + 1)));
            c.add(Calendar.DATE, 1);
        }
        //projection data set
        LineDataSet spendingsDataSet = new LineDataSet(projectionEntries, "");
        LineData spendingsLineData = new LineData(spendingsDataSet);
        projectionGraph.setData(spendingsLineData);

        //spendings data set
        LineDataSet projectionDataSet = new LineDataSet(spendingsEntries, "");
        LineData projectionLineData = new LineData(projectionDataSet);
        spendingsGraph.setData(projectionLineData);

        //projection styles
        setChartStylings(projectionGraph);
        XAxis projectionXAxis = projectionGraph.getXAxis();
        YAxis projectionYAxis = projectionGraph.getAxisLeft();

        projectionXAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        projectionXAxis.setLabelCount(5);

        projectionYAxis.addLimitLine(new LimitLine(0, ""));
        if(projectionYAxis.getAxisMaximum() > abs(projectionYAxis.getAxisMinimum())){
            projectionYAxis.setAxisMinimum(-projectionYAxis.getAxisMaximum());
        }
        else{
            projectionYAxis.setAxisMaximum(-projectionYAxis.getAxisMinimum());
        }

        projectionGraph.zoomToCenter(projectionDataSet.getEntryCount() / 14f, 1);
        projectionGraph.centerViewTo(projectionDataSet.getEntryCount(), projectionDataSet.getYMax() / 2, YAxis.AxisDependency.LEFT);

        projectionGraph.invalidate();
        
        //spendings styles
        setChartStylings(spendingsGraph);
        XAxis spendingsXAxis = spendingsGraph.getXAxis();
        YAxis spendingsYAxis = spendingsGraph.getAxisLeft();

        spendingsXAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        spendingsXAxis.setLabelCount(5);

        spendingsYAxis.setAxisMinimum(0);

        float dailyAve = budgetplan.getBudget() / getDaysDif(budgetplan.getEndDate(), budgetplan.getStartDate());
        if(spendingsYAxis.getAxisMaximum() < dailyAve * 1.5){
            spendingsYAxis.setAxisMaximum((float) (dailyAve * 1.5));
        }
        spendingsYAxis.addLimitLine(new LimitLine(dailyAve, "daily average: $" + getDollarFormat(dailyAve)));

        spendingsGraph.zoomToCenter(spendingsDataSet.getEntryCount() / 14f, 1);
        spendingsGraph.centerViewTo(spendingsDataSet.getEntryCount(), spendingsDataSet.getYMax() / 2, YAxis.AxisDependency.LEFT);

        spendingsGraph.invalidate();
    }

    private void displayCategoriesGraph(){
        //set data for categories graph
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> categories = budgetplan.getCategories();
        for(int i = 0; i < categories.size(); ++i){
            float total = 0;
            for(Transaction t : budgetplan.getTransactions()){
                if(t.getCategory().equals(categories.get(i))){
                    total += t.getPrice();
                }
            }
            entries.add(new BarEntry(i, total));
            TextView text = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
            categoriesLayout.addView(text);
            text.setText(categories.get(i) + ": $" + getDollarFormat(total));
        }
        BarDataSet dataSet = new BarDataSet(entries, "Spendings");
        categoriesGraph.setData(new BarData(dataSet));

        //styles
        setChartStylings(categoriesGraph);
        XAxis categoriesXAxis = categoriesGraph.getXAxis();
        YAxis categoriesYAxis = categoriesGraph.getAxisLeft();

        categoriesXAxis.setValueFormatter(new IndexAxisValueFormatter(categories));
        categoriesYAxis.setAxisMinimum(0);
    }
    
    private void displayOverviewText(){
        TextView budgetText = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
        TextView remBudgetText = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
        TextView startDateText = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
        TextView endDateText = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
        TextView totalDaysText = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));

        overviewLayout.addView(remBudgetText);
        overviewLayout.addView(budgetText);
        overviewLayout.addView(startDateText);
        overviewLayout.addView(endDateText);
        overviewLayout.addView(totalDaysText);

        budgetText.setText("Total Budget: $" + getDollarFormat(budgetplan.getBudget()));
        remBudgetText.setText("Remaining Budget: $" + getDollarFormat(budgetplan.getBudget() - cumulativeSpending));
        startDateText.setText("Start Date: " + timestampToDateString(budgetplan.getStartDate()));
        endDateText.setText("End Date: " + timestampToDateString(budgetplan.getEndDate()));
        totalDaysText.setText("Total days: " + totalDays);
    }

    private void displayProjectionText(){
        TextView dynamicProjectionText = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
        TextView constantProjectionText = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
        TextView breakEvenText = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
        projectionLayout.addView(dynamicProjectionText);
        projectionLayout.addView(constantProjectionText);
        projectionLayout.addView(breakEvenText);
        if(projection > 0) {
            dynamicProjectionText.setText("Spend at current rate of $" + getDollarFormat(averageSpending)
                    + "/day\nto LOSE $" + getDollarFormat(-budgetplan.getBudget() - averageSpending * totalDays));
            constantProjectionText.setText("Spend at flat daily average of $" + getDollarFormat(constDailyAvg)
                    + "/day\nto LOSE $" + getDollarFormat(projection));
            breakEvenText.setText("Spend at rate of $" + getDollarFormat(budgetplan.getDailyAve(new Timestamp(new Date())))
                    + "/day to break even");
        }
        else{
            dynamicProjectionText.setText("Spend at current rate of $" + getDollarFormat(averageSpending)
                    + "/day\nto SAVE $" + getDollarFormat(budgetplan.getBudget() - averageSpending * totalDays));
            constantProjectionText.setText("Spending at the flat average of $" + getDollarFormat(constDailyAvg)
                    + "/day\nto SAVE $" + getDollarFormat(-projection));
            breakEvenText.setText("Spend at rate of $" + getDollarFormat(budgetplan.getDailyAve(new Timestamp(new Date())))
                    + "/day to break even");
        }
    }

    private void displaySpendingsText(){
        TextView avgSpendingsText = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
        TextView avgOverspendText = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
        spendingsLayout.addView(avgSpendingsText);
        spendingsLayout.addView(avgOverspendText);
        avgSpendingsText.setText("Average spendings per day: $" + getDollarFormat(averageSpending));
        float overspend = projection / daysSoFar;
        if(overspend < 0){
            avgOverspendText.setText("Average underspendings per day: $" + getDollarFormat(-overspend));
        }
        else{
            avgOverspendText.setText("Average overspendings per day: $" + getDollarFormat(overspend));
        }
    }

    private void setChartStylings(BarLineChartBase chart){
        chart.setNoDataText("no Transactions yet!");
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.animateXY(1000, 1000);
        chart.setDragEnabled(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);
        xAxis.setLabelRotationAngle(-45);

        YAxis leftYAxis = chart.getAxisLeft();
        leftYAxis.setGranularity(1);
        //leftYAxis.setAxisMinimum(0);

        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setDrawLabels(false);
        rightYAxis.setDrawGridLines(false);
    }

}

