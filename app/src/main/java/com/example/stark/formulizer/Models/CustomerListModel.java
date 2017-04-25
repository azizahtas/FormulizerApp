package com.example.stark.formulizer.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Stark on 03-03-2017.
 */

public class CustomerListModel {
    @SerializedName("_id")
    @Expose
    private String id;
    @Expose
    @SerializedName("Name")
    private String name;
    @Expose
    @SerializedName("Count")
    private int count;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

