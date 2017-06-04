package com.example.stark.formulizer.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stark on 04-03-2017.
 */

public class FormulaModel {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("_UId")
    @Expose
    private String userId;
    @SerializedName("_UName")
    @Expose
    private String userName;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Base")
    @Expose
    private String base;
    @SerializedName("Access")
    @Expose
    private String access;
    @SerializedName("Company")
    @Expose
    private String company;
    @SerializedName("Date")
    @Expose
    private String date;
    @SerializedName("Likes")
    @Expose
    private int likes;
    @SerializedName("Rating")
    @Expose
    private int rating;
    @SerializedName("Desc")
    @Expose
    private String desc;
    @SerializedName("TWeight")
    @Expose
    private double tWeight;
    @SerializedName("VCompany")
    @Expose
    private String vCompany;
    @SerializedName("VModal")
    @Expose
    private String vModal;
    @SerializedName("Formula")
    @Expose
    private List<TinterModel> tinters = null;

    public FormulaModel(){
        this.id = null;
        this.userId = "";
        this.userName = "";
        this.type = "";
        this.name = "";
        this.base = "";
        this.access = "";
        this.company = "";
        this.date = "";
        this.likes = 0;
        this.rating = 0;
        this.desc = "";
        this.tWeight = 0;
        this.vCompany = "";
        this.vModal = "";
        this.tinters = null;
    }
    public FormulaModel(String id, String userId,String userName, String type, String name, String base, String access, String company, String date, int likes, int rating, String desc, int tWeight, String vCompany, String vModal, List<TinterModel> tinters) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.type = type;
        this.name = name;
        this.base = base;
        this.access = access;
        this.company = company;
        this.date = date;
        this.likes = likes;
        this.rating = rating;
        this.desc = desc;
        this.tWeight = tWeight;
        this.vCompany = vCompany;
        this.vModal = vModal;
        this.tinters = tinters;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double gettWeight() {
        return tWeight;
    }

    public void settWeight(double tWeight) {
        this.tWeight = tWeight;
    }

    public String getvCompany() {
        return vCompany;
    }

    public void setvCompany(String vCompany) {
        this.vCompany = vCompany;
    }

    public String getvModal() {
        return vModal;
    }

    public void setvModal(String vModal) {
        this.vModal = vModal;
    }

    public List<TinterModel> getTinters() {
        return tinters;
    }
    public List<TinterModel> getNewTinters() {
        List<TinterModel> newList = new ArrayList<>(tinters.size());
        for (int i =0; i<tinters.size(); i++){
            newList.add(tinters.get(0));
        }
        return newList;
    }
    public void removeAllTinters() {
        tinters.clear();
    }
    public int getTintersCount() {
       return tinters.size();
    }
    public void removeTinterAt(int index){
        tinters.remove(index);
    }
    public int addTinter(TinterModel newTinter){
        tinters.add(newTinter);
        return getTintersCount();
    }


    public void setTinters(List<TinterModel> tinters) {
        this.tinters = tinters;
    }
}
