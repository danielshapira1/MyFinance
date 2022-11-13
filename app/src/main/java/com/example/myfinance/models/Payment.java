package com.example.myfinance.models;

import com.example.myfinance.Util;

public class Payment {
    String id;
    String cost;
    String date;
    String dateFormatted;
    String category;
    String description;
    String uid;
    String uEmail;

    public Payment() {
    }


    public Payment(String cost, String date, String category, String description, String uid, String uEmail) {
        this.cost = cost;

        this.date = Util.fixDateFormat(date);
        this.dateFormatted = Util.fixDateFormatTolexicographicOrder(this.date);

        this.category = category;
        this.description = description;
        this.uid = uid;
        this.uEmail = uEmail;
        

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getDateFormatted() {
        return dateFormatted;
    }

    public void setDateFormatted(String dateFormatted) {
        this.dateFormatted = dateFormatted;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "cost='" + cost + '\'' +
                ", date='" + date + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", uid='" + uid + '\'' +
                ", uEmail='" + uEmail + '\'' +
                '}';
    }
}
