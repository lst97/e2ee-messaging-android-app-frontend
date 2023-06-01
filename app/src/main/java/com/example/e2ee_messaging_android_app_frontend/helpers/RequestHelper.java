package com.example.e2ee_messaging_android_app_frontend.helpers;

import com.google.gson.annotations.Expose;

import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.Date;

class Metadata {
    private String time;
    private String type;
    private String version = "0.1";

    public Metadata(String type) {

        long timestamp = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", java.util.Locale.getDefault());
        Date date = new Date(timestamp);

        this.time = dateFormat.format(date);
        this.type = type;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}


public class RequestHelper {
    @Expose
    private String metadata;
    @Expose
    private String data;


    public RequestHelper(String type, String data) {
        this.metadata = new Metadata(type).toJson();
        this.data = data;
    }

}
