package com.example.stark.formulizer.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Stark on 04-03-2017.
 */

public class PagedData<T> {
    @SerializedName("docs")
    private List<T> docs = null;
    @SerializedName("total")
    private Integer total;
    @SerializedName("limit")
    private Integer limit;
    @SerializedName("page")
    private Integer page;
    @SerializedName("pages")
    private Integer pages;

    public List<T> getDocs() {
        return docs;
    }

    public void setDocs(List<T> docs) {
        this.docs = docs;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }
}
