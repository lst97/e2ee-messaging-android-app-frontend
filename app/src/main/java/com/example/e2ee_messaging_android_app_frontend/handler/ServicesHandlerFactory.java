package com.example.e2ee_messaging_android_app_frontend.handler;

import com.example.e2ee_messaging_android_app_frontend.services.ServiceFactory;

public interface ServicesHandlerFactory {
    void onCreate();

    void addService(ServiceFactory service);

    void removeService(ServiceFactory service);

    ServiceFactory getService(String serviceName);
}
