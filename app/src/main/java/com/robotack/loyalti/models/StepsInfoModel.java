package com.robotack.loyalti.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StepsInfoModel {


    @SerializedName("errorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("descriptionCode")
    @Expose
    private String descriptionCode;
    @SerializedName("stepsCount")
    @Expose
    private String stepsCount;
    @SerializedName("points")
    @Expose
    private String points;

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

    public String getStepsCount() {
        return stepsCount;
    }

    public void setStepsCount(String stepsCount) {
        this.stepsCount = stepsCount;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }


}
