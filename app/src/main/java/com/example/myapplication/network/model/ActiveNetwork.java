package com.example.myapplication.network.model;

public class ActiveNetwork {
    private String id;
    private String user_id;
    private String date;
    private float distance;
    private float time;
    private float speed;

    public ActiveNetwork(String id, String user_id, String date, float distance, float time, float speed) {
        this.id = id;
        this.user_id = user_id;
        this.date = date;
        this.distance = distance;
        this.time = time;
        this.speed = speed;
    }

    public ActiveNetwork() {
    }

    public ActiveNetwork(String date, float distance, float time, float speed) {
        this.date = date;
        this.distance = distance;
        this.time = time;
        this.speed = speed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getDistance() {
        return distance;
    }

    public float getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Active{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", date='" + date + '\'' +
                ", distance='" + distance + '\'' +
                ", time=" + time +
                ", speed=" + speed +
                '}';
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

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
