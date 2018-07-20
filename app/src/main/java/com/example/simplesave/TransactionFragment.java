package com.example.simplesave;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {


    private RecyclerView recyclerView;
    List<Transaction> transList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        String[] categories = MainActivity.budgetplan.getCategories().toArray(new String[0]);
        String[] array = new String[categories.length + 1];
        array[0] = "Transactions";
        for (int i = 1; i < array.length; i++) {
            array[i] = categories[i-1];
        }
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, array);
        spinner.setAdapter(spinAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                String selected = parent.getItemAtPosition(i).toString();
                transList = MainActivity.budgetplan.getTransactions();
                if (!selected.equals("Transactions")) {
                    List<Transaction> trans = new ArrayList<>();
                    for (Transaction t: transList) {
                        if (t.getCategory().equals(selected))
                            trans.add(t);
                    }
                    transList = trans;
                }
                TransactionAdapter adapter = new TransactionAdapter(getContext(), transList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}