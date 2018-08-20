package com.buststudios.pare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class CategoryFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View view;
    private TableLayout tableLayout;

    public CategoryFragment() {
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
        view = inflater.inflate(R.layout.fragment_category, container, false);
        tableLayout = (TableLayout) view.findViewById(R.id.Table);
        displayCategories();
        return view;
    }

    private void displayCategories(){
        //HashMap<String, ArrayList<Transaction>> m = MainActivity.budgetplan.getCategoryTransactionMap();
        //HashMap<String, ArrayList<Transaction>> m = new HashMap<String, Array>()
        for(String category : MainActivity.budgetplan.getCategories()){
        //for(String category : m.keySet()){
            TableRow tr = new TableRow(getContext());

            TextView categoryTitle = new TextView(getContext());
            categoryTitle.setText(category);
            categoryTitle.setTextSize(getResources().getDimension(R.dimen.textsizeTwo));
            categoryTitle.setPadding(0, 0, 30, 0);
            tr.addView(categoryTitle);

            TextView totalPrice = new TextView(getContext());
            //totalPrice.setText(String.valueOf(MainActivity.budgetplan.getTotalPriceByCategory(category)));
            totalPrice.setTextSize(getResources().getDimension(R.dimen.textsizeTwo));
            tr.addView(totalPrice);

            tr.setPadding(0, 30, 0, 10);

            tableLayout.addView(tr);

            for(Transaction t : MainActivity.budgetplan.getTransactions()){
                if(t.getCategory().equals(category)) {
                    tr = new TableRow(getContext());

                    TextView name = new TextView(getContext());
                    TextView date = new TextView(getContext());
                    TextView price = new TextView(getContext());

                    name.setText(t.getName());
                    date.setText(t.getTimestampString());
                    price.setText(String.valueOf(t.getPrice()));

                    name.setTextSize(getResources().getDimension(R.dimen.textsizeThree));
                    date.setTextSize(getResources().getDimension(R.dimen.textsizeThree));
                    price.setTextSize(getResources().getDimension(R.dimen.textsizeThree));

                    tr.addView(name);
                    tr.addView(date);
                    tr.addView(price);

                    tableLayout.addView(tr);
                }
            }
        }
    }
}
