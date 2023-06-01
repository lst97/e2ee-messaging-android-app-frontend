package com.example.e2ee_messaging_android_app_frontend.networks.api;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;

import com.example.e2ee_messaging_android_app_frontend.handler.HttpRequestHandler;
import com.example.e2ee_messaging_android_app_frontend.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class Api {
    public static int getDeviceId(String url) {
        HttpRequestHandler httpRequestHandler = new HttpRequestHandler(url);

        try {
            JSONObject jsonResponse = httpRequestHandler.GET();
            return jsonResponse.getInt("device_id");
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static void registerUser(String url, User user) {

        HttpRequestHandler httpRequestHandler = new HttpRequestHandler(url);

        new Thread(() -> new Handler(Looper.getMainLooper()).post(() -> {
            try {
                httpRequestHandler.POST(user);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })).start();
    }
}
