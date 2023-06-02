package com.example.e2ee_messaging_android_app_frontend.networks.api;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;

import com.example.e2ee_messaging_android_app_frontend.activities.HomeActivity;
import com.example.e2ee_messaging_android_app_frontend.handler.HttpRequestHandler;
import com.example.e2ee_messaging_android_app_frontend.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Api {
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

    public static void getAllUsers(String url, HomeActivity.UserMappingCallback callback) {

        HttpRequestHandler httpRequestHandler = new HttpRequestHandler(url);

        new Thread(() -> new Handler(Looper.getMainLooper()).post(() -> {
            try {
                List<User> users = new ArrayList<>();

                JSONObject jsonResponse = httpRequestHandler.GET();

                JSONObject data = jsonResponse.getJSONObject("data");
                JSONObject metadata = jsonResponse.getJSONObject("metadata");

                JSONArray usersJsonArray = data.getJSONArray("users");
                for (int i = 0; i < usersJsonArray.length(); i++) {
                    JSONObject user = usersJsonArray.getJSONObject(i);
                    String hash = user.getString("hash");
                    String name = user.getString("name");
                    String picture = user.getString("picture");
                    String public_key = user.getString("public_key");
                    User newUser = new User(hash, name, picture, public_key);
                }

                callback.onUserMappingComplete(users);

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        })).start();
    }
}
