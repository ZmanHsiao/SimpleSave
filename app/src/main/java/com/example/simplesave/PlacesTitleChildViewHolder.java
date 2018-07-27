package com.example.simplesave;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

public class PlacesTitleChildViewHolder extends ChildViewHolder {

    public TextView name, address, website, phonenum, types;
    public RatingBar rating;
    public ImageView image;
    public Button addTrans;

    public PlacesTitleChildViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        address = (TextView) itemView.findViewById(R.id.address);
        website = (TextView) itemView.findViewById(R.id.website);
        phonenum = (TextView) itemView.findViewById(R.id.phonenum);
        types = (TextView) itemView.findViewById(R.id.types);
        rating = (RatingBar) itemView.findViewById(R.id.rating);
        image = (ImageView) itemView.findViewById(R.id.image);
        addTrans = (Button) itemView.findViewById(R.id.add);
    }
}
