package com.robotack.loyalti.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CustomerDataModel implements Serializable {


    @SerializedName("identifierType")
    @Expose
    private String identifierType;
    @SerializedName("errorCode")
    @Expose
    private String errorCode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @SerializedName("status")
    @Expose
    private String status;


    @SerializedName("descriptionCode")
    @Expose
    private String descriptionCode;

    public String getDescriptionCode() {
        return descriptionCode;
    }

    public void setDescriptionCode(String descriptionCode) {
        this.descriptionCode = descriptionCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @SerializedName("identifierValue")
    @Expose
    private String identifierValue;
    @SerializedName("currentPoints")
    @Expose
    private String currentPoints;
    @SerializedName("currentPointsValue")
    @Expose
    private String currentPointsValue;
    @SerializedName("accumulatedCashback")
    @Expose
    private String accumulatedCashback;
    @SerializedName("accumulatedPoints")
    @Expose
    private String accumulatedPoints;
    @SerializedName("expiryPoint")
    @Expose
    private String expiryPoint;


    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    public String getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(String currentPoints) {
        this.currentPoints = currentPoints;
    }

    public String getCurrentPointsValue() {
        return currentPointsValue;
    }

    public void setCurrentPointsValue(String currentPointsValue) {
        this.currentPointsValue = currentPointsValue;
    }

    public String getAccumulatedCashback() {
        return accumulatedCashback;
    }

    public void setAccumulatedCashback(String accumulatedCashback) {
        this.accumulatedCashback = accumulatedCashback;
    }

    public String getAccumulatedPoints() {
        return accumulatedPoints;
    }

    public void setAccumulatedPoints(String accumulatedPoints) {
        this.accumulatedPoints = accumulatedPoints;
    }

    public String getExpiryPoint() {
        return expiryPoint;
    }

    public void setExpiryPoint(String expiryPoint) {
        this.expiryPoint = expiryPoint;
    }
}
