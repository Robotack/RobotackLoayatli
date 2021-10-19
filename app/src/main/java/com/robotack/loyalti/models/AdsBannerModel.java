package com.robotack.loyalti.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdsBannerModel {

    public class Datum {

        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("label")
        @Expose
        private String label;
        @SerializedName("link")
        @Expose
        private String link;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

    }


        @SerializedName("errorCode")
        @Expose
        private Integer errorCode;
        @SerializedName("descriptionCode")
        @Expose
        private String descriptionCode;
        @SerializedName("data")
        @Expose
        private List<Datum> data = null;


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

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }
}
