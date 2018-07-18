package com.example.simplesave;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context mCtx;
    private List <Transaction> transList;

    public TransactionAdapter(Context mCtx, List<Transaction> transList) {
        this.mCtx = mCtx;
        this.transList = transList;
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.cards_layout, null);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction t = transList.get(position);
        holder.name.setText(t.getName());
        holder.date.setText(t.getTimestamp().toDate().toString());
        holder.category.setText(t.getCategory());
        holder.price.setText("$" + t.getPrice());

        if (t.getCategory().equals("Utilities")) {
            holder.imageView.setImageResource(R.drawable.utilities);
            holder.category.setBackgroundColor(Color.parseColor("#ffe266"));
        } else if (t.getCategory().equals("Entertainment")) {
            holder.imageView.setImageResource(R.drawable.game);
            holder.category.setBackgroundColor(Color.parseColor("#53cffc"));
        } else if (t.getCategory().equals("Transportation")) {
            holder.imageView.setImageResource(R.drawable.car);
            holder.category.setBackgroundColor(Color.parseColor("#4aad63"));
        } else if (t.getCategory().equals("Tuition")) {
            holder.imageView.setImageResource(R.drawable.tuition);
            holder.category.setBackgroundColor(Color.parseColor("#f77662"));
        } else if (t.getCategory().equals("Food")) {
            holder.imageView.setImageResource(R.drawable.food);
            holder.category.setBackgroundColor(Color.parseColor("#a17cff"));
        } else if (t.getCategory().equals("Rent")) {
            holder.imageView.setImageResource(R.drawable.rent);
            holder.category.setBackgroundColor(Color.parseColor("#ff7a32"));
        } else {
            holder.imageView.setImageResource(R.drawable.naruto);
            holder.category.setBackgroundColor(Color.parseColor("#4aad63"));
        }
    }

    @Override
    public int getItemCount() {
        return transList.size();
    }


    class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, category, price;
        ImageView imageView;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            category = itemView.findViewById(R.id.category);
            price = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

}
