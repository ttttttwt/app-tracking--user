package com.example.myapplication.network.model;

public class LocationNetwork {
    String active_id;
    float latitude;
    float longitude;
    String time;

    public LocationNetwork(String active_id, float latitude, float longitude, String time) {
        this.active_id = active_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public LocationNetwork() {
    }

    public String getActive_id() {
        return active_id;
    }

    public void setActive_id(String active_id) {
        this.active_id = active_id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
