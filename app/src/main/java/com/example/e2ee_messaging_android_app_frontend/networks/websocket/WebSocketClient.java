package com.example.e2ee_messaging_android_app_frontend.networks.websocket;

import androidx.annotation.NonNull;

import com.example.e2ee_messaging_android_app_frontend.handler.KeyExchangeHandler;
import com.example.e2ee_messaging_android_app_frontend.handler.ServicesHandler;
import com.example.e2ee_messaging_android_app_frontend.helpers.KeyStoreHelper;
import com.example.e2ee_messaging_android_app_frontend.helpers.RequestHelper;
import com.example.e2ee_messaging_android_app_frontend.networks.api.Api;
import com.example.e2ee_messaging_android_app_frontend.networks.request.Agreement;
import com.example.e2ee_messaging_android_app_frontend.networks.request.MetadataType;
import com.example.e2ee_messaging_android_app_frontend.services.session.SessionService;
import com.example.e2ee_messaging_android_app_frontend.utils.Cryptographic;
import com.example.e2ee_messaging_android_app_frontend.utils.HashUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;


import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient extends WebSocketListener {
    private final String WS_URL;
    private WebSocket webSocket;

    public WebSocketClient(String webSocketUrl) {
        WS_URL = webSocketUrl;

    }

    private OkHttpClient createSslClient(){
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }

        return new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    public void start() {
        OkHttpClient client = createSslClient();

        Request request = new Request.Builder()
                .url(this.WS_URL)
                .build();

        webSocket = client.newWebSocket(request, this);
    }

    public void stop() {
        webSocket.close(1000, "Closing WebSocket connection");
    }

    @Override
    public void onOpen(WebSocket webSocket, @NonNull Response response) {
        // WebSocket connection is established
        // Request to register the user

    }

    private void handleKeyExchange(JSONObject data, JSONObject metadata){
        String publicKeyBase64 = null;
        String uuidHash = null;
        try {
            publicKeyBase64 = data.getString("public_key");
            uuidHash = data.getString("hash");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] publicKeyBytes = decoder.decode(publicKeyBase64);

        PublicKey publicKey = null;
        try {
            publicKey = KeyExchangeHandler.reconstructPublicKey(publicKeyBytes);
            KeyExchangeHandler.receive(publicKey, webSocket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String decodeMessage(JSONObject data, JSONObject metadata){
        String messageBase64 = null;
        PrivateKey privateKey = null;
        try {
            messageBase64 = data.getString("message");
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] messageBytes = decoder.decode(messageBase64);

            privateKey = KeyStoreHelper.getPrivateKey();
            messageBytes = Cryptographic.decrypt(messageBytes, privateKey);

            return new String(messageBytes);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        // Received a text message from the server
        // Handle the message as needed

        // decode the message
        JSONObject jsonMessage = null;
        try {
            jsonMessage = new JSONObject(text);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONObject data = jsonMessage.getJSONObject("data");

            JSONObject metadata = jsonMessage.getJSONObject("metadata");
            String type = metadata.getString("type");

            switch (type){
                case MetadataType.KEY_EXCHANGE:
                    handleKeyExchange(data, metadata);
                    break;
                case MetadataType.MESSAGE:
                    // encrypted message
                    String message = decodeMessage(data, metadata);

                    //TODO: Display to UI

                    break;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        // WebSocket connection is closed
        // Clean up resources or perform any necessary actions here
        System.out.println("Closed: " + reason);
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
        // WebSocket connection failure
        // Handle the failure or error here
        System.out.println("Error: " + t.getMessage());
    }
}