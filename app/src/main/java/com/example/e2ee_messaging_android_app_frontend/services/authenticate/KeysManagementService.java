package com.example.e2ee_messaging_android_app_frontend.services.authenticate;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.e2ee_messaging_android_app_frontend.handler.ServicesHandler;
import com.example.e2ee_messaging_android_app_frontend.helpers.ServiceHelper;
import com.example.e2ee_messaging_android_app_frontend.services.ServiceFactory;
import com.example.e2ee_messaging_android_app_frontend.services.log.LogService;

public class KeysManagementService implements ServiceFactory {
    private final String serviceName;
    private final LogService logService;
//    private final SharedPreferences sharedPreferences;
//    private final UserRepository userRepository;

    public KeysManagementService(ServiceHelper helper) {
        this.serviceName = helper.getServiceName();
        // this.userRepository = (UserRepository) RepositoriesHandler.getInstance().getRepository(UserRepository.class.getName());

        if (helper.isLogEnabled()) {
            this.logService = (LogService) ServicesHandler.getInstance().getService(LogService.class.getName());
        } else {
            this.logService = null;
        }

        // this.sharedPreferences = helper.getContext().getSharedPreferences(PreferenceType.REMEMBER_ME, Context.MODE_PRIVATE);

    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public void onCreate() {

    }
}
