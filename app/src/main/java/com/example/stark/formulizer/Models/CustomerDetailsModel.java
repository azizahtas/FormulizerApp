package com.example.stark.formulizer.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by aziz_ on 24-04-2017.
 */

public class CustomerDetailsModel {
    @SerializedName("_id")
    @Expose
    private String id;
    @Expose
    @SerializedName("Name")
    private String name;
    @Expose
    @SerializedName("Contact")
    private String contact;
    @Expose
    @SerializedName("Formulas")
    private List<String> formulas;

    public CustomerDetailsModel() {
        this.id = null;
        this.name = "";
        this.contact = "";
    }

    public CustomerDetailsModel(String id, String name, String contact, List<String> formulas) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.formulas = formulas;
    }

    public List<String> getFormulas() {
        return formulas;
    }

    public void setFormulas(List<String> formulas) {
        this.formulas = formulas;
    }

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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }









}
