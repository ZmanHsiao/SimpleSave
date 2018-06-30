package com.example.simplesave;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {


    public TransactionFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_transactions, container, false);
        TextView trans = (TextView)view.findViewById(R.id.transactions);

        Spinner dropdown = (Spinner) view.findViewById(R.id.dayDropdown);
        String[] items = new String[MainActivity.time];
        for (int i = 0; i < items.length; i++) {
            items[i] = Integer.toString(i+1);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        String text = "";
        for (int i = 0; i < Main2Activity.name.length; i++) {
            for (int j = 0; j < Main2Activity.name[i].size(); j++) {
                text += (i + 1) + " - " + Main2Activity.name[i].get(j) + "     $" + Main2Activity.price[i].get(j) + "\n";
            }
        }
        trans.setText(text);
        return view;
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




}
