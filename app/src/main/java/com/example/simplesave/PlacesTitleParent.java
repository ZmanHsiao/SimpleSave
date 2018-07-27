package com.example.simplesave;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;
import java.util.UUID;

public class PlacesTitleParent implements ParentObject {

    private List<Object> mChildrenList;
    private UUID _id;
    private String title;
    private String place_id;

    public PlacesTitleParent(String title, String place_id) {
        this.title = title;
        this.place_id = place_id;
        _id = UUID.randomUUID();
    }

    public String getPlace_id() {
        return place_id;
    }

    @Override
    public List<Object> getChildObjectList() {
        return mChildrenList;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        mChildrenList = list;
    }

    public String getTitle() {
        return title;
    }
}
