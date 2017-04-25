package com.example.stark.formulizer.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Stark on 04-03-2017.
 */

public class TinterModel {
    @SerializedName("Tinter")
    @Expose
    private String tinter;
    @SerializedName("Qty")
    @Expose
    private Double qty;
    @SerializedName("_id")
    @Expose
    private String id;

    public TinterModel(String tinter, Double qty, String id) {
        this.tinter = tinter;
        this.qty = qty;
        this.id = id;
    }
    public TinterModel(){
        this.id = null;
        this.tinter = "";
        this.qty = 0d;
    }

    public String getTinter() {
        return tinter;
    }

    public void setTinter(String tinter) {
        this.tinter = tinter;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
