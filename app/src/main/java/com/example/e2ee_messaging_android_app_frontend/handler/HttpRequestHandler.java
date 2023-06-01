package com.example.e2ee_messaging_android_app_frontend.handler;

import android.os.StrictMode;

import com.example.e2ee_messaging_android_app_frontend.helpers.RequestHelper;
import com.example.e2ee_messaging_android_app_frontend.models.User;
import com.example.e2ee_messaging_android_app_frontend.networks.request.MetadataType;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpRequestHandler {
    private final String url;
    public HttpRequestHandler(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    private JSONObject handleResponse(Response response) throws JSONException, IOException {
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            if (body != null) {
                String responseBody = body.string();
                return new JSONObject(responseBody);
            }
        } else {
            System.out.println("Request failed: " + response.code());
        }
        return null;
    }

    public JSONObject GET() throws IOException {
        String url = this.url;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            return this.handleResponse(client.newCall(request).execute());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String standardizing(String data){
        return new Gson().toJson(new RequestHelper(MetadataType.API, data));
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
    public JSONObject POST(User user) throws IOException {

        String url = this.url;
        OkHttpClient client = createSslClient();

        Gson gson = new Gson();
        String data = standardizing( gson.toJson(user));

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(data, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            return this.handleResponse(client.newCall(request).execute());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
