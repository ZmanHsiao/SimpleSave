package com.buststudios.pare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buststudios.pare.DollarValueFormatter;
import com.buststudios.pare.R;
import com.buststudios.pare.StatsFragment;
import com.buststudios.pare.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

import static com.buststudios.pare.AppLibrary.getDollarFormat;

public class StatsCategoriesFragment extends Fragment {

    private View view;
    LinearLayout layout;
    private BarChart categoriesChart;

    public StatsCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stats_categories, container, false);
        layout = view.findViewById(R.id.mainLayout);

        categoriesChart = view.findViewById(R.id.categoriesGraph);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> categories = StatsFragment.budgetplan.getCategories();
        for(int i = 0; i < categories.size(); ++i){
            float total = 0;
            for(Transaction t : StatsFragment.budgetplan.getTransactions()){
                if(t.getCategory().equals(categories.get(i))){
                    total += t.getPrice();
                }
            }
            entries.add(new BarEntry(i, total));
//            TextView text = new TextView(new ContextThemeWrapper(getActivity(), R.style.StatsText));
//            layout.addView(text);
//            text.setText(categories.get(i) + ": $" + getDollarFormat(total));
        }
        BarDataSet dataSet = new BarDataSet(entries, "Spendings");
        dataSet.setValueFormatter(new DollarValueFormatter());
        categoriesChart.setData(new BarData(dataSet));


        //styles
        StatsFragment.setChartStylings(categoriesChart);
        XAxis categoriesXAxis = categoriesChart.getXAxis();
        YAxis categoriesYAxis = categoriesChart.getAxisLeft();

        categoriesXAxis.setValueFormatter(new IndexAxisValueFormatter(categories));
        categoriesYAxis.setAxisMinimum(0);

        return view;

    }

}
