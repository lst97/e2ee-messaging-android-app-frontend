package com.example.e2ee_messaging_android_app_frontend.services.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.e2ee_messaging_android_app_frontend.handler.ServicesHandler;
import com.example.e2ee_messaging_android_app_frontend.helpers.ServiceHelper;
import com.example.e2ee_messaging_android_app_frontend.services.ServiceFactory;
import com.example.e2ee_messaging_android_app_frontend.services.authenticate.KeysManagementService;
import com.example.e2ee_messaging_android_app_frontend.services.log.LogService;
import com.example.e2ee_messaging_android_app_frontend.services.log.LogTypes;
import com.example.e2ee_messaging_android_app_frontend.utils.SecureUUIDGenerator;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

public class SessionService implements ServiceFactory {
    private final String serviceName;
    private final LogService logService;
    private final Context context;

    public SessionService(ServiceHelper helper) {
        this.serviceName = helper.getServiceName();
        this.context = helper.getContext();

        if (helper.isLogEnabled()) {
            this.logService = (LogService) ServicesHandler.getInstance().getService(LogService.class.getName());
        } else {
            this.logService = null;
        }

    }

    public void setUserSession() {
        SharedPreferences userSession = context.getSharedPreferences(PreferenceType.SESSION, Context.MODE_PRIVATE);
        if(!userSession.contains(PreferenceType.UUID)) {
            SharedPreferences.Editor editor = userSession.edit();
            editor.putString(PreferenceType.UUID, SecureUUIDGenerator.generateSecureUUID().toString());
            editor.apply();
        }
    }

    public void saveIdentity(KeysManagementService.E2eeUser e2eeUser) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceType.SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString("PUBLIC_KEY", Base64.getEncoder().encodeToString(e2eeUser.getPublicKey().getEncoded()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        editor.apply();
    }

    public KeysManagementService.E2eeUser getIdentity() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceType.SESSION, Context.MODE_PRIVATE);
        // check if the user has a session
        if(!sharedPreferences.contains(KeysManagementService.E2eeUser.class.getName())) {
            return null;
        }
        String pk = sharedPreferences.getString("PUBLIC_KEY", "");
        String uuid = sharedPreferences.getString("UUID", "");
        if (pk.equals("") || uuid.equals("")) {
            return null;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("public_key", pk);
            jsonObject.put("uuid", uuid);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new Gson();
        return gson.fromJson(String.valueOf(jsonObject), KeysManagementService.E2eeUser.class);
    }

    public KeysManagementService.E2eeUser retrieveIdentity() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceType.SESSION, Context.MODE_PRIVATE);
        String jsonString = sharedPreferences.getString(KeysManagementService.E2eeUser.class.getName(), null);

        Gson gson = new Gson();
        return gson.fromJson(jsonString, KeysManagementService.E2eeUser.class);
    }

    public String getUserSession() {
        SharedPreferences userSession = context.getSharedPreferences(PreferenceType.SESSION, Context.MODE_PRIVATE);

        if(!userSession.contains(PreferenceType.UUID)) {
            setUserSession();
        }
        return userSession.getString(PreferenceType.UUID, null);
    }

    public void removeUserSession() {
        SharedPreferences userSession = context.getSharedPreferences(PreferenceType.SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSession.edit();

        editor.remove("PUBLIC_KEY");
        editor.remove("UUID");
        editor.apply();
    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }

    @Override
    public void onCreate() {
        logService.log("SessionService created", LogTypes.INFO);
    }
}
