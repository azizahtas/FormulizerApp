package com.example.stark.formulizer.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeneralResponseModel<T> {
    @SerializedName("success")
    private boolean success;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private List<T> data;

    public GeneralResponseModel(boolean success, String msg, List<T> data){
        this.success = success;
        this.data = data;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
