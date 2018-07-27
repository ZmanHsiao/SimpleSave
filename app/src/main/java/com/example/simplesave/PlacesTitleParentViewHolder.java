package com.example.simplesave;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

public class PlacesTitleParentViewHolder extends ParentViewHolder{

    public TextView _textView;
    View itemView;

    public PlacesTitleParentViewHolder(View itemView) {
        super(itemView);
        _textView = (TextView) itemView.findViewById(R.id.parentTitle);
        this.itemView = itemView;
    }
}
