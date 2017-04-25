package com.example.stark.formulizer.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Stark on 03-03-2017.
 */

public class CustomerModel {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("_UId")
    @Expose
    private String userId;
    @Expose
    @SerializedName("Name")
    private String name;
    @Expose
    @SerializedName("Contact")
    private String contact;
    @SerializedName("Formulas")
    private List<FormulaModel> formulas = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FormulaModel> getCustomerFormulas() {
        return formulas;
    }

    public void setCustomerFormulas(List<FormulaModel> formulas) {
        this.formulas = formulas;
    }

    public int getFormulasCount(){
        if(formulas!=null){return formulas.size();} else return 0;
    }

}

