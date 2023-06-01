package com.example.e2ee_messaging_android_app_frontend.networks.request;

import com.google.gson.annotations.Expose;

public class Agreement {
    @Expose
    private final String deviceId;
    @Expose
    private final String uuid;
    @Expose
    private final String signedPreKeyId;

    public Agreement(String deviceId, String uuid, String signedPreKeyId) {
        this.deviceId = deviceId;
        this.uuid = uuid;
        this.signedPreKeyId = signedPreKeyId;
    }

    public Agreement(int deviceId, String uuid, int signedPreKeyId) {
        this.deviceId = String.valueOf(deviceId);
        this.uuid = uuid;
        this.signedPreKeyId = String.valueOf(signedPreKeyId);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getSignedPreKeyId() {
        return signedPreKeyId;
    }
}
