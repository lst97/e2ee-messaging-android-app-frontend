package com.example.e2ee_messaging_android_app_frontend.models;


import com.example.e2ee_messaging_android_app_frontend.utils.HashUtil;
import com.google.gson.annotations.Expose;

import java.security.NoSuchAlgorithmException;

// need to match the naming convention of the backend
public class User {

    private transient String uuid;
    @Expose
    private String hash;
    @Expose
    private String name;
    @Expose
    private String picture;
    @Expose
    private String public_key;

    public User(String uuid, String name, String picture, String key_pair) {
        this.uuid = uuid;
        try {
            this.hash = HashUtil.sha256(uuid);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.name = name;
        this.picture = picture;
        this.public_key = key_pair;
    }

    public User(String name, String picture, String key_pair) {
        this.uuid = "";
        try {
            this.hash = HashUtil.sha256(uuid);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.name = name;
        this.picture = picture;
        this.public_key = key_pair;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }
}
