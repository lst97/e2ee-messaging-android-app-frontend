package com.example.e2ee_messaging_android_app_frontend.services.authenticate;

import android.content.Context;
import android.os.StrictMode;

import com.example.e2ee_messaging_android_app_frontend.handler.ServicesHandler;
import com.example.e2ee_messaging_android_app_frontend.helpers.KeyStoreHelper;
import com.example.e2ee_messaging_android_app_frontend.helpers.ServiceHelper;
import com.example.e2ee_messaging_android_app_frontend.services.ServiceFactory;
import com.example.e2ee_messaging_android_app_frontend.services.log.LogService;
import com.example.e2ee_messaging_android_app_frontend.services.session.SessionService;

import com.example.e2ee_messaging_android_app_frontend.helpers.KeyStoreHelper;
import com.google.gson.annotations.Expose;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;


public class KeysManagementService implements ServiceFactory {
    private final String serviceName;
    private final SessionService sessionService;
    private final LogService logService;
    private final Context context;

    public KeysManagementService(ServiceHelper helper) {
        this.serviceName = helper.getServiceName();
        this.sessionService = (SessionService) ServicesHandler.getInstance().getService(SessionService.class.getName());
        this.context = helper.getContext();

        SessionService sessionService = (SessionService) ServicesHandler.getInstance().getService(SessionService.class.getName());
        E2eeUser e2eeUser = sessionService.getIdentity();

        KeyStoreHelper.setKeyAlias(this.sessionService.getUserSession());


        if (helper.isLogEnabled()) {
            this.logService = (LogService) ServicesHandler.getInstance().getService(LogService.class.getName());
        } else {
            this.logService = null;
        }

    }

    public class E2eeUser {
        @Expose
        private final String uuid;
        @Expose
        private String public_key;

        public E2eeUser(String uuid) throws Exception {
            this.uuid = uuid;
            KeyStoreHelper.generateKeyPair(KeysManagementService.this.context);
            this.public_key = Base64.getEncoder().encodeToString(this.getPublicKey().getEncoded());
        }

        public String getUuid() {
            return this.uuid;
        }

        public PublicKey getPublicKey() throws Exception {
            // convert the public key to string
            PublicKey pk = KeyStoreHelper.getPublicKey();
            this.public_key = Base64.getEncoder().encodeToString(pk.getEncoded());
            return pk;
        }

        public PrivateKey getPrivateKey() throws Exception {
            return KeyStoreHelper.getPrivateKey();
        }
    }

    public E2eeUser generateKeyPair() {
        try {
            return new E2eeUser(this.sessionService.getUserSession());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }

    @Override
    public void onCreate() {

    }
}
