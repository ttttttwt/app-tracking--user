package com.example.myapplication.network.model;

import com.google.gson.annotations.SerializedName;

public class StatisticalNetwork {
    float time;
    @SerializedName("average_speed")
    float speed;
    float distance;

    public StatisticalNetwork(float time, float speed, float distance) {
        this.time = time;
        this.speed = speed;
        this.distance = distance;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
