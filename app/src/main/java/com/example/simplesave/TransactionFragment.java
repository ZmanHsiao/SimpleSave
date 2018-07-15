package com.example.simplesave;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TransactionFragment extends Fragment {


    private Spinner dropdown;

    public TransactionFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_transactions, container, false);
        TextView trans = (TextView)view.findViewById(R.id.transactions);

        dropdown = (Spinner) view.findViewById(R.id.dayDropdown);
        String[] items = new String[Main2Activity.budgetplan.getTotalDays() - Main2Activity.budgetplan.getDaysLeft() + 1];
        for (int i = 0; i < items.length; i++) {
            items[i] = Integer.toString(i+1);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);

        String text = "";
        for (int i = 0; i < Main2Activity.budgetplan.getTransactions().size(); i++) {
                text += (i + 1) + ") " + Main2Activity.budgetplan.getTransactions().get(i).getName() +
                        "  $" + Main2Activity.budgetplan.getTransactions().get(i).getPrice() + "\n";
        }
        trans.setText(text);

        Button addMoney = (Button) view.findViewById(R.id.specDay);
        addMoney.setOnClickListener(addCheckDayListener);

        return view;
    }

    private View.OnClickListener addCheckDayListener = new View.OnClickListener() {
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View mView = getLayoutInflater().inflate(R.layout.check_day_dialog, null);
            TextView dayCount = (TextView) mView.findViewById((R.id.dayCount));
            TextView transactions = (TextView) mView.findViewById(R.id.dayTransactions);


            int selected = Integer.parseInt(dropdown.getSelectedItem().toString());
            dayCount.setText("Day " + selected);
            String text = "";
            List<Transaction> dayTransactions = Main2Activity.budgetplan.getTransactions();
            for ( int i = 0; i < dayTransactions.size(); i++) {
                //if (dayTransactions.get(i).getDate() == selected - 1) {
                    text += dayTransactions.get(i).getName() + "   $" + dayTransactions.get(i).getPrice() + "\n";
                //}
            }
            transactions.setText(text);


            Button button = (Button) mView.findViewById(R.id.back);
            builder.setView(mView);
            builder.create();
            final AlertDialog display = builder.show();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    display.dismiss();
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
