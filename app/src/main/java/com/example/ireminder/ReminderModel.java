package com.example.ireminder;

public class ReminderModel {
    private int ID;
    private String remName;
    private Double latitude, longitude;
    private String address = "", details, userID;

    public ReminderModel(int ID, String remName, Double latitude, Double longitude, String address, String details, String userID) {
        this.ID = ID;
        this.remName = remName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.details = details;
        this.userID = userID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getRemName() {
        return remName;
    }

    public void setRemName(String remName) {
        this.remName = remName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return  remName +
                "\n\n" + address +
                "\n\n" + details;
    }
}
