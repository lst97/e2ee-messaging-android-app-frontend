package com.example.e2ee_messaging_android_app_frontend.handler;

import com.example.e2ee_messaging_android_app_frontend.helpers.ServiceHelper;
import com.example.e2ee_messaging_android_app_frontend.services.ServiceFactory;
import com.example.e2ee_messaging_android_app_frontend.services.log.LogService;
import com.example.e2ee_messaging_android_app_frontend.services.log.LogTypes;

import java.util.ArrayList;
import java.util.List;

public class ServicesHandler implements ServicesHandlerFactory {
    private static ServicesHandler instance = null;
    List<ServiceFactory> services;
    private LogService logService;

    private ServicesHandler() {
        services = new ArrayList<>();
        this.logService = (LogService) this.getService(LogService.class.getName());

        // add log service automatically
        if (this.logService == null) {
            this.addService(new LogService(new ServiceHelper(null, LogService.class.getName(), false)));
        }
        onCreate();
    }

    // Used to create a singleton instance of the ServicesHandler
    public static ServicesHandler getInstance(ServiceHelper helper) {
        if (instance == null) {
            instance = new ServicesHandler();
        }
        return instance;
    }

    // Used to get the singleton instance of the ServicesHandler
    public static ServicesHandler getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        this.addService(new LogService(new ServiceHelper(null, LogService.class.getName(), true)));
        if (logService != null) {
            logService.log("ServicesHandler Created", LogTypes.INFO);
        }
    }

    public void addService(ServiceFactory service) {
        // check if service already exists
        for (ServiceFactory s : services) {
            if (s.getServiceName().equals(service.getServiceName())) {
                if (logService != null)
                    logService.log("Service " + service.getServiceName() + " already exist.", LogTypes.INFO);
                return;
            }
        }

        services.add(service);
        logService = (LogService) this.getService(LogService.class.getName());
        if (logService != null)
            logService.log("Service " + service.getServiceName() + " added", LogTypes.INFO);
    }

    public void removeService(ServiceFactory service) {
        services.remove(service);
        if (logService != null)
            logService.log("Service " + service.getServiceName() + " removed", LogTypes.INFO);
    }

    public ServiceFactory getService(String serviceName) {
        for (ServiceFactory service : services) {
            if (service.getServiceName().equals(serviceName)) {
                return service;
            }
        }
        return null;
    }
}
