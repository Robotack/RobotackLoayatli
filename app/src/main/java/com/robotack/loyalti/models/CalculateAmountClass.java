package com.robotack.loyalti.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CalculateAmountClass {


    @SerializedName("errorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("descriptionCode")
    @Expose
    private String descriptionCode;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("points")
    @Expose
    private String points;
    @SerializedName("identifierValue")
    @Expose
    private String identifierValue;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescriptionCode() {
        return descriptionCode;
    }

    public void setDescriptionCode(String descriptionCode) {
        this.descriptionCode = descriptionCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

}
