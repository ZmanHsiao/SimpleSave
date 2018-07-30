package com.example.simplesave;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.Timestamp;

import java.util.Date;

import static com.example.simplesave.AppLibrary.getDaysDif;
import static com.example.simplesave.AppLibrary.getDollarFormat;
import static java.lang.Math.abs;

public class StatsSpendingsFragment extends Fragment {
    private View view;
    LinearLayout layout;
    private LineChart spendingsChart;

    public StatsSpendingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stats_spendings, container, false);
        layout = view.findViewById(R.id.mainLayout);

        spendingsChart = view.findViewById(R.id.spendingsGraph);
        spendingsChart.setData(StatsFragment.spendingsLineData);
        
        //styles

        spendingsChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                spendingsChart.setMarker(new CustomMarkerView(getContext(), spendingsChart, StatsFragment.dates));
            }

            @Override
            public void onNothingSelected() {

            }
        });

        StatsFragment.setChartStylings(spendingsChart);
        XAxis spendingsXAxis = spendingsChart.getXAxis();
        YAxis spendingsYAxis = spendingsChart.getAxisLeft();

        spendingsXAxis.setValueFormatter(new IndexAxisValueFormatter(StatsFragment.dates));
        spendingsXAxis.setLabelCount(5);

        spendingsYAxis.setAxisMinimum(0);

        float dailyAve = StatsFragment.budgetplan.getBudget() / getDaysDif(StatsFragment.budgetplan.getEndDate(), StatsFragment.budgetplan.getStartDate());
        if(spendingsYAxis.getAxisMaximum() < dailyAve * 1.5){
            spendingsYAxis.setAxisMaximum((float) (dailyAve * 1.5));
        }
        spendingsYAxis.addLimitLine(new LimitLine(dailyAve, "daily average: $" + getDollarFormat(dailyAve)));

        spendingsChart.zoomToCenter(StatsFragment.spendingsDataSet.getEntryCount() / 14f, 1);
        spendingsChart.centerViewTo(StatsFragment.spendingsDataSet.getEntryCount(), StatsFragment.spendingsDataSet.getYMax() / 2, YAxis.AxisDependency.LEFT);

        spendingsChart.invalidate();


        //text

        TextView avgSpendingsText = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
        TextView avgOverspendText = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
        layout.addView(avgSpendingsText);
        layout.addView(avgOverspendText);
        avgSpendingsText.setText("Average spendings per day: $" + getDollarFormat(StatsFragment.averageSpending));
        float overspend = StatsFragment.projection / StatsFragment.daysSoFar;
        if(overspend < 0){
            avgOverspendText.setText("Average underspendings per day: $" + getDollarFormat(-overspend));
        }
        else{
            avgOverspendText.setText("Average overspendings per day: $" + getDollarFormat(overspend));
        }

       
        return view;

    }
}
