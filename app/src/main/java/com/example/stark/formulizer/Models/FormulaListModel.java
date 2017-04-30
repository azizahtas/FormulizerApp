package com.example.stark.formulizer.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by aziz_ on 29-04-2017.
 */

public class FormulaListModel implements Serializable{
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("Name")
    @Expose
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormulaListModel(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public FormulaListModel() {
        this.id = null;
        this.name = "";
    }
}
