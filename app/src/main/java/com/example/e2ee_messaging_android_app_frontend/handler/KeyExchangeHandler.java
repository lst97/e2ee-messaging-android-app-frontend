package com.example.e2ee_messaging_android_app_frontend.handler;

import com.example.e2ee_messaging_android_app_frontend.services.authenticate.KeysManagementService;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import okhttp3.WebSocket;


public class KeyExchangeHandler{
    public static class PendingExchange{
        private WebSocket webSocket;
        private KeysManagementService.E2eeUser sender;
        private PublicKey recipientPublicKey;

        public PendingExchange(WebSocket webSocket, KeysManagementService.E2eeUser sender){
            this.webSocket = webSocket;
            this.sender = sender;
            this.recipientPublicKey = null;
        }

        public WebSocket getWebSocket() {
            return webSocket;
        }

        public void setWebSocket(WebSocket webSocket) {
            this.webSocket = webSocket;
        }

        public KeysManagementService.E2eeUser getSender() {
            return sender;
        }

        public void setSender(KeysManagementService.E2eeUser sender) {
            this.sender = sender;
        }

        public PublicKey getRecipientPublicKey() {
            return recipientPublicKey;
        }

        public void setRecipientPublicKey(PublicKey recipientPublicKey) {
            this.recipientPublicKey = recipientPublicKey;
        }

    }
    private static final List<PendingExchange> pendingExchanges = new ArrayList<>();

    public static PendingExchange getPendingExchange(WebSocket webSocket){
        for(PendingExchange pendingExchange : pendingExchanges){
            if(pendingExchange.getWebSocket().equals(webSocket)){
                return pendingExchange;
            }
        }
        return null;
    }

    public static void receive(PublicKey recipientPublicKey, WebSocket webSocket) throws Exception {
        PendingExchange pe = getPendingExchange(webSocket);
        if (pe == null) {
            return;
        }
        pe.setRecipientPublicKey(recipientPublicKey);

        // no need to do the three-way handshake because the wss already does it
    }

    public static PublicKey reconstructPublicKey(byte[] publicKeyBytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
    }
}