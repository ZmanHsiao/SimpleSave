package com.example.simplesave;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionFragment extends Fragment {


    private RecyclerView recyclerView;
    private BudgetPlan budgetplan;
    private TransactionAdapter transAdapter;
    List<Transaction> transList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        budgetplan = MainActivity.budgetplan;
        recyclerView = (RecyclerView) view.findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(addTransListener);

        String[] categories = budgetplan.getCategories().toArray(new String[0]);
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
                transList = budgetplan.getTransactions();
                transList = Lists.reverse(transList);
                if (!selected.equals("Transactions")) {
                    List<Transaction> trans = new ArrayList<>();
                    for (Transaction t: transList) {
                        if (t.getCategory().equals(selected))
                            trans.add(t);
                    }
                    transList = trans;
                }
                transAdapter = new TransactionAdapter(getContext(), transList);
                recyclerView.setAdapter(transAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return view;
    }

    private View.OnClickListener addTransListener = new View.OnClickListener() {
        Timestamp transactionDate = new Timestamp(new Date());
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View mView = getLayoutInflater().inflate(R.layout.add_transaction_dialog, null);
            final EditText price = (EditText) mView.findViewById(R.id.price);
            final EditText title = (EditText) mView.findViewById(R.id.title);
            Button timestampButton = (Button) mView.findViewById(R.id.timestampButton);
            Button addButton = (Button) mView.findViewById(R.id.addTrans);
            builder.setView(mView);
            builder.create();
            final AlertDialog display = builder.show();

            final Spinner dropdown = (Spinner) mView.findViewById(R.id.categoryDropdown);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,
                    budgetplan.getCategories().toArray(new String[0]));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown.setAdapter(adapter);

            timestampButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    View mView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
                    final DatePicker datePicker = (DatePicker) mView.findViewById(R.id.datePicker);
                    datePicker.setMinDate(budgetplan.getStartDate().toDate().getTime());
                    datePicker.setMaxDate(budgetplan.getEndDate().toDate().getTime());
                    final TimePicker timePicker = (TimePicker) mView.findViewById(R.id.timePicker);
                    Button doneButton = (Button) mView.findViewById(R.id.doneButton);
                    builder.setView(mView);
                    builder.create();
                    final AlertDialog display = builder.show();

                    doneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar c = Calendar.getInstance();
                            c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            transactionDate = new Timestamp(c.getTime());
                            display.dismiss();
                        }
                    });
                }
            });

            final TransactionAdapter transAdapter1 = transAdapter;
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String category = dropdown.getSelectedItem().toString();
                    String name = title.getText().toString();
                    String num = price.getText().toString();
                    if (!name.isEmpty() && !num.isEmpty()) {
                        float val = Float.valueOf(num);
                        budgetplan.addTransaction(category, name, val, transactionDate);
                        display.dismiss();
                        transAdapter.notifyDataSetChanged();
                        AppLibrary.pushUser(MainActivity.user);
                    } else if (name.isEmpty()) {
                        title.setBackgroundResource(R.drawable.red_border);
                        Toast.makeText(getActivity(), "Not a Valid Input", Toast.LENGTH_SHORT).show();
                    } else {
                        price.setBackgroundResource(R.drawable.red_border);
                        Toast.makeText(getActivity(), "Not a Valid Input", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}