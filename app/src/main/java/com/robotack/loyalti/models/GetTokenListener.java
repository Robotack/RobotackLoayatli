package com.robotack.loyalti.models;

import android.os.Parcelable;

import java.io.Serializable;

public interface GetTokenListener extends Parcelable {
    String getToken();
}
