package com.buststudios.pare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.buststudios.pare.AppLibrary.*;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context mCtx;
    private List <Transaction> transList;
    private LayoutInflater inflater;
    private boolean day; // if day is true, use different card layout

    public TransactionAdapter(Context mCtx, List<Transaction> transList) {
        this.mCtx = mCtx;
        this.transList = transList;
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.cards_layout, null);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction t = transList.get(position);
        holder.name.setText(t.getName());
        DateFormat df = new SimpleDateFormat("MMM dd, h:mm a");
        holder.date.setText(df.format(t.getTimestamp().toDate()));
        holder.category.setText(t.getCategory());
        holder.price.setText("$" + AppLibrary.getDollarFormat(t.getPrice()));

        if (t.getCategory().equals("Restaurant")) {
            holder.imageView.setImageResource(R.drawable.ic_restaurant_black_24dp);
            holder.category.setBackgroundTintList(mCtx.getResources().getColorStateList(R.color.restaurant));
        } else if (t.getCategory().equals("Grocery/Drug Store")) {
            holder.imageView.setImageResource(R.drawable.ic_local_grocery_store_black_24dp);
            holder.category.setBackgroundTintList(mCtx.getResources().getColorStateList(R.color.grocery));
        } else if (t.getCategory().equals("Entertainment")) {
            holder.imageView.setImageResource(R.drawable.ic_videogame_asset_black_24dp);
            holder.category.setBackgroundTintList(mCtx.getResources().getColorStateList(R.color.entertainment));
        } else if (t.getCategory().equals("Transportation")) {
            holder.imageView.setImageResource(R.drawable.ic_directions_car_black_24dp);
            holder.category.setBackgroundTintList(mCtx.getResources().getColorStateList(R.color.transportation));
        } else if (t.getCategory().equals("Shopping")) {
            holder.imageView.setImageResource(R.drawable.ic_card_giftcard_black_24dp);
            holder.category.setBackgroundTintList(mCtx.getResources().getColorStateList(R.color.shopping));
        } else if (t.getCategory().equals("Rent/Maintenance")) {
            holder.imageView.setImageResource(R.drawable.ic_home);
            holder.category.setBackgroundTintList(mCtx.getResources().getColorStateList(R.color.rent));
        } else {
            holder.imageView.setImageResource(R.drawable.ic_attach_money_black_24dp);
            holder.category.setBackgroundTintList(mCtx.getResources().getColorStateList(R.color.other));
        }
    }

    @Override
    public int getItemCount() {
        return transList.size();
    }


    class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, category, price;
        ImageView imageView;
        Timestamp transactionDate;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            category = itemView.findViewById(R.id.category);
            price = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.imageView);

            transactionDate = new Timestamp(new Date());
            itemView.setOnClickListener(editTransListener);
        }

        private View.OnClickListener editTransListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Transaction t = transList.get(getAdapterPosition());
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                View mView = inflater.inflate(R.layout.edit_transaction_dialog, null);
                final EditText price = (EditText) mView.findViewById(R.id.price);
                price.setText(AppLibrary.getDollarFormat(t.getPrice()));
                final EditText title = (EditText) mView.findViewById(R.id.title);
                title.setText(t.getName());
                builder.setView(mView);
                builder.create();
                final AlertDialog display = builder.show();

                final Spinner dropdown = (Spinner) mView.findViewById(R.id.categoryDropdown);
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mCtx, android.R.layout.simple_list_item_1,
                        MainActivity.budgetplan.getCategories().toArray(new String[0]));
                dropdown.setAdapter(adapter);

                int pos = adapter.getPosition(t.getCategory());
                dropdown.setSelection(pos);

                Button timestampButton = (Button) mView.findViewById(R.id.timestampButton);
                Button edit = (Button) mView.findViewById(R.id.edit);
                Button delete = (Button) mView.findViewById(R.id.delete);

                transactionDate = t.getTimestamp();
                timestampButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        View mView = inflater.inflate(R.layout.dialog_time_picker, null);

                        final DatePicker datePicker = (DatePicker) mView.findViewById(R.id.datePicker);
                        datePicker.setMinDate(MainActivity.budgetplan.getStartDate().toDate().getTime());
                        datePicker.setMaxDate(MainActivity.budgetplan.getEndDate().toDate().getTime());
                        final TimePicker timePicker = (TimePicker) mView.findViewById(R.id.timePicker);
                        Calendar c = Calendar.getInstance();
                        c.setTime(t.getTimestamp().toDate());
                        datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                        timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                        timePicker.setCurrentMinute(c.get(Calendar.MINUTE));

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

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = price.getText().toString();
                        String name = title.getText().toString();
                        if (!text.isEmpty() && !name.isEmpty()) {
                            String category = dropdown.getSelectedItem().toString();
                            float val = Float.valueOf(price.getText().toString());
                            t.setName(name);
                            t.setCategory(category);
                            t.setPrice(val);
                            t.setTimestamp(transactionDate);
                            notifyDataSetChanged();
                            display.dismiss();
                            AppLibrary.pushUser(MainActivity.user);
                        } else if (text.isEmpty()){
                            price.setBackgroundResource(R.drawable.red_border);
                            Toast.makeText(mCtx, "Not a Valid Input", Toast.LENGTH_SHORT).show();
                        } else {
                            title.setBackgroundResource(R.drawable.red_border);
                            Toast.makeText(mCtx, "Not a Valid Input", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                delete.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        transList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), transList.size());
                        MainActivity.budgetplan.getTransactions().remove(t);
                        display.dismiss();
                        AppLibrary.pushUser(MainActivity.user);
                    }
                });
            }
        };
    }

}
