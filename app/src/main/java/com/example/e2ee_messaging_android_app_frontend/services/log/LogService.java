package com.example.e2ee_messaging_android_app_frontend.services.log;

import com.example.e2ee_messaging_android_app_frontend.helpers.ServiceHelper;
import com.example.e2ee_messaging_android_app_frontend.services.ServiceFactory;

public class LogService implements ServiceFactory {
    private static LogService instance = null;
    private final String serviceName;

    public LogService(ServiceHelper helper) {
        this.serviceName = helper.getServiceName();
    }

    public static LogService getInstance(ServiceHelper helper) {
        if (instance == null) {
            instance = new LogService(helper);
        }
        return instance;
    }

    public static LogService getInstance() {
        return instance;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public void onCreate() {
        log("LogService Created", LogTypes.INFO);
    }

    public void log(String message, int type) {
        String TAG = "Log Service";
        switch (type) {
            case 0:
                android.util.Log.i(TAG, serviceName + ": " + message);
                break;
            case 1:
                android.util.Log.w(TAG, serviceName + ": " + message);
                break;
            case 2:
                android.util.Log.d(TAG, serviceName + ": " + message);
                break;
            default:
                android.util.Log.e(TAG, serviceName + ": " + message);
                break;
        }
    }

}