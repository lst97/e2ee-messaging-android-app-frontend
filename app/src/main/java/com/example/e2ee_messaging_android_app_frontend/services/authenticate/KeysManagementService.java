package com.example.e2ee_messaging_android_app_frontend.services.authenticate;

import android.os.StrictMode;

import com.example.e2ee_messaging_android_app_frontend.handler.ServicesHandler;
import com.example.e2ee_messaging_android_app_frontend.helpers.ServiceHelper;
import com.example.e2ee_messaging_android_app_frontend.services.ServiceFactory;
import com.example.e2ee_messaging_android_app_frontend.services.log.LogService;
import com.example.e2ee_messaging_android_app_frontend.services.session.SessionService;

import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;

import java.util.List;



public class KeysManagementService implements ServiceFactory {
    private final String serviceName;
    private final SessionService sessionService;
    private final LogService logService;

    public KeysManagementService(ServiceHelper helper) {
        this.serviceName = helper.getServiceName();
        this.sessionService = (SessionService) ServicesHandler.getInstance().getService(SessionService.class.getName());

        if (helper.isLogEnabled()) {
            this.logService = (LogService) ServicesHandler.getInstance().getService(LogService.class.getName());
        } else {
            this.logService = null;
        }

    }

    public static class SignalUser {
        IdentityKeyPair identityKeyPair;
        int registrationId;
        List<PreKeyRecord> preKeys;
        SignedPreKeyRecord signedPreKey;
        SignalProtocolAddress address;

        SignalUser(String uuid, int deviceId, int signedPreKeyId) {
            identityKeyPair = KeyHelper.generateIdentityKeyPair();
            registrationId = KeyHelper.generateRegistrationId(false);
            preKeys = KeyHelper.generatePreKeys(1, 100);
            try {
                signedPreKey = KeyHelper.generateSignedPreKey(identityKeyPair, signedPreKeyId);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
            address = new SignalProtocolAddress(uuid, deviceId);
        }

        public IdentityKeyPair getIdentityKeyPair() {
            return identityKeyPair;
        }

        public void setIdentityKeyPair(IdentityKeyPair identityKeyPair) {
            this.identityKeyPair = identityKeyPair;
        }

        public int getRegistrationId() {
            return registrationId;
        }

        public void setRegistrationId(int registrationId) {
            this.registrationId = registrationId;
        }

        public List<PreKeyRecord> getPreKeys() {
            return preKeys;
        }

        public void setPreKeys(List<PreKeyRecord> preKeys) {
            this.preKeys = preKeys;
        }

        public SignedPreKeyRecord getSignedPreKey() {
            return signedPreKey;
        }

        public void setSignedPreKey(SignedPreKeyRecord signedPreKey) {
            this.signedPreKey = signedPreKey;
        }

        public SignalProtocolAddress getAddress() {
            return address;
        }

        public void setAddress(SignalProtocolAddress address) {
            this.address = address;
        }
    }

    public SignalUser generateKeys() {
        return new SignalUser(this.sessionService.getUserSession(), 1, 1);

    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }

    @Override
    public void onCreate() {

    }
}
