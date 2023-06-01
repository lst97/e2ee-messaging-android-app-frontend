package com.example.e2ee_messaging_android_app_frontend.models;


import com.google.gson.annotations.Expose;

// need to match the naming convention of the backend
public class User {
    @Expose
    private String uuid;
    @Expose
    private String name;
    @Expose
    private String picture;
    @Expose
    private String public_key;
    @Expose
    private String registered_id;

    public User(String uuid, String name, String picture, String key_pair, String registered_id) {
        this.uuid = uuid;
        this.name = name;
        this.picture = picture;
        this.public_key = key_pair;
        this.registered_id = registered_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getKey_pair() {
        return public_key;
    }

    public void setKey_pair(String public_key) {
        this.public_key = public_key;
    }

    public String getRegistered_id() {
        return registered_id;
    }

    public void setRegistered_id(String registered_id) {
        this.registered_id = registered_id;
    }
}
