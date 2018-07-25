package com.example.simplesave;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class StatsFragment extends Fragment {

    private BudgetPlan budgetplan;
    private LineChart spendingsGraph;
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
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        budgetplan = MainActivity.budgetplan;

        spendingsGraph = (LineChart) view.findViewById(R.id.spendingsGraph);
        categoriesGraph = (BarChart) view.findViewById(R.id.categoriesGraph);
        displaySpendingsGraph();
        displayCategoriesGraph();

        return view;
    }

    private void displaySpendingsGraph(){
        setChartStylings(spendingsGraph);

        //set data
        List<Entry> entries = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTime(budgetplan.getStartDate().toDate());
        ArrayList<String> dates = new ArrayList<>();
        Timestamp today = AppLibrary.getTimestampWithoutTime(new Timestamp(new Date()));
        for(int i = 0; i < AppLibrary.getDaysDif(today, budgetplan.getStartDate()); ++i){
            dates.add(AppLibrary.timestampToDateString(new Timestamp(c.getTime())));
            float dayTotal = 0;
            for(Transaction t : budgetplan.getTransactions()){
                if(AppLibrary.isDateEqual(new Timestamp(c.getTime()), t.getTimestamp())) {
                    dayTotal += t.getPrice();
                }
            }
            entries.add(new Entry(i, dayTotal));
            c.add(Calendar.DATE, 1);
        }
        LineDataSet dataSet = new LineDataSet(entries, "Spendings");
        LineData lineData = new LineData(dataSet);
        spendingsGraph.setData(lineData);

        //styles
        XAxis xAxis = spendingsGraph.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setLabelCount(5);

        ArrayList<String> test = new ArrayList<>();

        YAxis leftYAxis = spendingsGraph.getAxisLeft();

        float dailyAvg = budgetplan.getDailyAve(today);
        if(leftYAxis.getAxisMaximum() < dailyAvg * 1.5){
            leftYAxis.setAxisMaximum((float) (dailyAvg * 1.5));
        }
        float dailyAve = budgetplan.getDailyAve(today);
        leftYAxis.addLimitLine(new LimitLine(dailyAve, "daily average: $" + AppLibrary.getDollarFormat(dailyAve)));

        spendingsGraph.zoomToCenter(dataSet.getEntryCount() / 5f, 1);
        spendingsGraph.centerViewTo(dataSet.getEntryCount(), dataSet.getYMax() / 2, YAxis.AxisDependency.LEFT);

        spendingsGraph.invalidate();

    }

    private void displayCategoriesGraph(){
        setChartStylings(categoriesGraph);

        //set data and labels
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
        }
        BarDataSet dataSet = new BarDataSet(entries, "Spendings");
        categoriesGraph.setData(new BarData(dataSet));
        categoriesGraph.getXAxis().setValueFormatter(new IndexAxisValueFormatter(categories));

        //styles
        categoriesGraph.zoomToCenter(dataSet.getEntryCount() / 4f, 1);
        categoriesGraph.centerViewTo(0, dataSet.getYMax() / 2, YAxis.AxisDependency.LEFT);
    }

    private void setChartStylings(BarLineChartBase chart){
        chart.setNoDataText("no Transactions yet!");
        chart.getDescription().setEnabled(false);
        chart.animateXY(1000, 1000);
        chart.setDragEnabled(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);

        YAxis leftYAxis = chart.getAxisLeft();
        leftYAxis.setGranularity(1);
        leftYAxis.setAxisMinimum(0);

        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setDrawLabels(false);
        rightYAxis.setDrawGridLines(false);
    }
}

