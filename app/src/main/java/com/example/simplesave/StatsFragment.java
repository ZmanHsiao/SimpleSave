package com.example.simplesave;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
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
import java.util.List;


public class StatsFragment extends Fragment {

    private BudgetPlan budgetplan;
    private LineChart spendingsGraph;
    private HorizontalBarChart categoriesGraph;

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

        spendingsGraph = (LineChart) view.findViewById(R.id.spendingsGraph);
        categoriesGraph = (HorizontalBarChart) view.findViewById(R.id.categoriesGraph);
        //categoriesGraph = (GraphView) view.findViewById(R.id.categoriesGraph);
        displaySpendingsGraph();
        displayCategoriesGraph();

        return view;
    }

    private void displaySpendingsGraph(){
        List<Entry> entries = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTime(budgetplan.getStartDate().toDate());
        for(int i = 0; i < AppLibrary.getDaysDif(budgetplan.getEndDate(), budgetplan.getStartDate()); ++i){
            float dayTotal = 0;
            for(Transaction t : budgetplan.getTransactions()){
                if(AppLibrary.isDateEqual(new Timestamp(c.getTime()), t.getTimestamp())) {
                    dayTotal += t.getPrice();
                }
            }
            entries.add(new Entry(i, dayTotal));
            c.add(Calendar.DATE, 1);
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineData lineData = new LineData(dataSet);
        spendingsGraph.setData(lineData);
        spendingsGraph.getLegend().setEnabled(false);
    }

    private void displayCategoriesGraph(){
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
        BarDataSet dataSet = new BarDataSet(entries, "Categories");
        categoriesGraph.setData(new BarData(dataSet));
        categoriesGraph.getXAxis().setValueFormatter(new IndexAxisValueFormatter(categories));
    }
}

