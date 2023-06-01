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
        SharedPreferences userSession = context.getSharedPreferences(PreferenceType.UUID, Context.MODE_PRIVATE);
        if(!userSession.contains(PreferenceType.UUID)) {
            SharedPreferences.Editor editor = userSession.edit();
            editor.putString(PreferenceType.UUID, SecureUUIDGenerator.generateSecureUUID().toString());
            editor.apply();
        }
    }

    public void saveIdentity(KeysManagementService.SignalUser signalUser) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(signalUser);

        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceType.SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KeysManagementService.SignalUser.class.getName(), jsonString);
        editor.apply();
    }

    public KeysManagementService.SignalUser getIdentity() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceType.SESSION, Context.MODE_PRIVATE);
        // check if the user has a session
        if(!sharedPreferences.contains(KeysManagementService.SignalUser.class.getName())) {
            return null;
        }
        String jsonString = sharedPreferences.getString(KeysManagementService.SignalUser.class.getName(), null);

        Gson gson = new Gson();
        return gson.fromJson(jsonString, KeysManagementService.SignalUser.class);
    }

    public KeysManagementService.SignalUser retrieveIdentity() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceType.SESSION, Context.MODE_PRIVATE);
        String jsonString = sharedPreferences.getString(KeysManagementService.SignalUser.class.getName(), null);

        Gson gson = new Gson();
        return gson.fromJson(jsonString, KeysManagementService.SignalUser.class);
    }

    public String getUserSession() {
        SharedPreferences userSession = context.getSharedPreferences(PreferenceType.UUID, Context.MODE_PRIVATE);

        if(!userSession.contains(PreferenceType.UUID)) {
            setUserSession();
        }
        return userSession.getString(PreferenceType.UUID, null);
    }

    public void removeUserSession() {
        SharedPreferences userSession = context.getSharedPreferences(PreferenceType.UUID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSession.edit();
        editor.remove(PreferenceType.UUID);
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
