package com.example.stark.formulizer.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Stark on 04-03-2017.
 */

public class PagedGeneralResponseModel<T> {
    @SerializedName("success")
    private boolean success;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private PagedData<T> data;

    public PagedGeneralResponseModel(boolean success, String msg, PagedData<T> data) {
        this.success = success;
        this.msg = msg;
        this.data = data;
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

    public PagedData<T> getData() {
        return data;
    }

    public void setData(PagedData<T> data) {
        this.data = data;
    }
}
