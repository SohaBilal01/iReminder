package com.example.ireminder;

import com.google.gson.annotations.SerializedName;

public class LocationModel {
    @SerializedName("lat1")
    private float lat1;
    @SerializedName("long1")
    private float long1;
    @SerializedName("lat2")
    private float lat2;
    @SerializedName("long2")
    private float long2;
    @SerializedName("lat3")
    private float lat3;
    @SerializedName("long3")
    private float long3;
    @SerializedName("lat4")
    private float lat4;
    @SerializedName("long4")
    private float long4;
    @SerializedName("lat5")
    private float lat5;
    @SerializedName("long5")
    private float long5;

    public LocationModel(float lat1, float long1, float lat2, float long2, float lat3, float long3, float lat4, float long4, float lat5, float long5) {
        this.lat1 = lat1;
        this.long1 = long1;
        this.lat2 = lat2;
        this.long2 = long2;
        this.lat3 = lat3;
        this.long3 = long3;
        this.lat4 = lat4;
        this.long4 = long4;
        this.lat5 = lat5;
        this.long5 = long5;
    }

    public float getLat1() {
        return lat1;
    }

    public void setLat1(float lat1) {
        this.lat1 = lat1;
    }

    public float getLong1() {
        return long1;
    }

    public void setLong1(float long1) {
        this.long1 = long1;
    }

    public float getLat2() {
        return lat2;
    }

    public void setLat2(float lat2) {
        this.lat2 = lat2;
    }

    public float getLong2() {
        return long2;
    }

    public void setLong2(float long2) {
        this.long2 = long2;
    }

    public float getLat3() {
        return lat3;
    }

    public void setLat3(float la3) {
        this.lat3 = la3;
    }

    public float getLong3() {
        return long3;
    }

    public void setLong3(float long3) {
        this.long3 = long3;
    }

    public float getLat4() {
        return lat4;
    }

    public void setLat4(float lat4) {
        this.lat4 = lat4;
    }

    public float getLong4() {
        return long4;
    }

    public void setLong4(float long4) {
        this.long4 = long4;
    }

    public float getLat5() {
        return lat5;
    }

    public void setLat5(float lat5) {
        this.lat5 = lat5;
    }

    public float getLong5() {
        return long5;
    }

    public void setLong5(float long5) {
        this.long5 = long5;
    }
}
