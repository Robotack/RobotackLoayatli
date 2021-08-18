package com.robotack.loyatli.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RedeemModel implements Serializable {

    @SerializedName("points")
    @Expose
    private String points;

    @SerializedName("account")
    @Expose
    private String account;

    @SerializedName("cashback")
    @Expose
    private String cashback;


    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCashback() {
        return cashback;
    }

    public void setCashback(String cashback) {
        this.cashback = cashback;
    }
}
