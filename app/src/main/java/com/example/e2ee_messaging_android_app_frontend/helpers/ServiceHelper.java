package com.example.e2ee_messaging_android_app_frontend.helpers;

import android.content.Context;

// information about the service
public class ServiceHelper {
    private final Context context;
    private final String serviceName;

    private boolean isLogEnabled = false;

    public ServiceHelper(Context context, String name) {
        this.serviceName = name;
        this.context = context;
    }

    public ServiceHelper(Context context, String name, boolean isLogEnabled) {
        this.serviceName = name;
        this.context = context;
        this.isLogEnabled = isLogEnabled;
    }

    public boolean isLogEnabled() {
        return isLogEnabled;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Context getContext() {
        return context;
    }
}
