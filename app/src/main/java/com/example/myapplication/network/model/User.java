package com.example.myapplication.network.model;

import com.google.gson.annotations.SerializedName;

public final class User {
    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String username, String password, String email) {
        this.password = password;
        this.username = username;
        this.email = email;
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {};


    public User(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
