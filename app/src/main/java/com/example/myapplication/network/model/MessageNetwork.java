package com.example.myapplication.network.model;

public class MessageNetwork {

    String message;

    String id;

    public MessageNetwork() {
    }

    public MessageNetwork(String msg) {
        this.message = msg;
    }

    public String getMsg() {
        return message;
    }

    public MessageNetwork(String message, String id) {
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
