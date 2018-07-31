package com.example.simplesave;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

import static com.example.simplesave.AppLibrary.getDollarFormat;
import static com.example.simplesave.AppLibrary.timestampToDateString;
import static java.lang.Math.abs;

public class StatsProjectionFragment extends Fragment {

    private View view;
    LinearLayout layout;
    private LineChart projectionChart;

    public StatsProjectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stats_projection, container, false);
        layout = view.findViewById(R.id.mainLayout);

        projectionChart = view.findViewById(R.id.projectionGraph);
        projectionChart.setData(StatsFragment.projectionLineData);

        //styles

        projectionChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                projectionChart.setMarker(new CustomMarkerView(getContext(), projectionChart, StatsFragment.dates));
            }

            @Override
            public void onNothingSelected() {

            }
        });

        StatsFragment.setChartStylings(projectionChart);
        XAxis projectionXAxis = projectionChart.getXAxis();
        YAxis projectionYAxis = projectionChart.getAxisLeft();

        projectionXAxis.setValueFormatter(new IndexAxisValueFormatter(StatsFragment.dates));
        projectionXAxis.setLabelCount(5);

        projectionYAxis.addLimitLine(new LimitLine(0, ""));
        if (projectionYAxis.getAxisMaximum() > abs(projectionYAxis.getAxisMinimum())) {
            projectionYAxis.setAxisMinimum(-projectionYAxis.getAxisMaximum());
        } else {
            projectionYAxis.setAxisMaximum(-projectionYAxis.getAxisMinimum());
        }

        projectionChart.zoomToCenter(StatsFragment.projectionDataSet.getEntryCount() / 14f, 1);
        projectionChart.centerViewTo(StatsFragment.projectionDataSet.getEntryCount(), StatsFragment.projectionDataSet.getYMax() / 2, YAxis.AxisDependency.LEFT);

        projectionChart.invalidate();


        //text

        TextView dynamicProjectionText = (TextView) view.findViewById(R.id.dynamic);
        TextView breakEvenText = (TextView) view.findViewById(R.id.breakeven);
        breakEvenText.setText("Break Even Rate: $" + getDollarFormat(StatsFragment.budgetplan.getDailyAve(new Timestamp(new Date())))
                + "/day");
        if (StatsFragment.projection > 0) {
            dynamicProjectionText.setText("Current Rate ($" + getDollarFormat(StatsFragment.averageSpending)
                    + "/day): $" + getDollarFormat(-StatsFragment.budgetplan.getBudget() - StatsFragment.averageSpending * StatsFragment.totalDays) + " lost");
        } else {
            dynamicProjectionText.setText("Current Rate ($" + getDollarFormat(StatsFragment.averageSpending)
                    + "/day): $" + getDollarFormat(StatsFragment.budgetplan.getBudget() - StatsFragment.averageSpending * StatsFragment.totalDays) + " saved");
        }
        return view;

    }
}
