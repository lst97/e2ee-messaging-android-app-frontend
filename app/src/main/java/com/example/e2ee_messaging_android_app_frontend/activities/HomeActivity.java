package com.example.e2ee_messaging_android_app_frontend.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.example.e2ee_messaging_android_app_frontend.R;
import com.example.e2ee_messaging_android_app_frontend.handler.ServicesHandler;
import com.example.e2ee_messaging_android_app_frontend.helpers.ServiceHelper;
import com.example.e2ee_messaging_android_app_frontend.models.Room;
import com.example.e2ee_messaging_android_app_frontend.models.User;
import com.example.e2ee_messaging_android_app_frontend.networks.api.Api;
import com.example.e2ee_messaging_android_app_frontend.networks.websocket.WebSocketClient;
import com.example.e2ee_messaging_android_app_frontend.services.ServiceFactory;
import com.example.e2ee_messaging_android_app_frontend.services.authenticate.KeysManagementService;
import com.example.e2ee_messaging_android_app_frontend.services.log.LogService;
import com.example.e2ee_messaging_android_app_frontend.services.session.PreferenceType;
import com.example.e2ee_messaging_android_app_frontend.services.session.SessionService;
import com.google.gson.Gson;

import java.util.Base64;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    Button btnNewIdentity;
    Button btnMenu;
    EditText searchInput;
    RecyclerView roomRecyclerView;
    WebSocketClient webSocketClient;
    List<Room> roomList;

    private void initViews(){
        btnNewIdentity = findViewById(R.id.home_new_id_btn);
        btnMenu = findViewById(R.id.home_menu_btn);
        searchInput = findViewById(R.id.home_search_input);
        roomRecyclerView = findViewById(R.id.home_room_recycler_view);

    }

    private void initListeners(){
        btnMenu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_home, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                // Handle menu item selection
                return true;
            });
            popupMenu.show();
        });
    }

    private void initWebSocket() {
        webSocketClient = new WebSocketClient("wss://10.0.2.2:8080");
    }

    private void saveIdentity(KeysManagementService.SignalUser signalUser) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(signalUser);

        SharedPreferences sharedPreferences = this.getSharedPreferences(PreferenceType.SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SIGNAL_USER", jsonString);
        editor.apply();
    }

    private void updateDiscoveryServer(String uuid, KeysManagementService.SignalUser signalUser){
        // Convert public key to Base64 string
        String publicKeyString = Base64.getEncoder().encodeToString(signalUser.getIdentityKeyPair().getPublicKey().serialize());
        User user = new User(uuid, "", "", publicKeyString, String.valueOf(signalUser.getRegistrationId()));
        Api.registerUser("https://10.0.2.2:5000/api/v1/users", user);
    }

    private void setupKeyStore() {
        ServicesHandler servicesHandler = ServicesHandler.getInstance();
        SessionService sessionService = (SessionService) servicesHandler.getService(SessionService.class.getName());
        String uuid = sessionService.getUserSession();
        KeysManagementService keysManagementService = (KeysManagementService) servicesHandler.getService(KeysManagementService.class.getName());

        // TODO: Check if user already has a key pair
        KeysManagementService.SignalUser signalUser = sessionService.getIdentity();

        if (signalUser == null) {
            signalUser = keysManagementService.generateKeys();
            sessionService.saveIdentity(signalUser);

            updateDiscoveryServer(uuid, signalUser);
        }
    }

    private void initServices() {
        ServicesHandler servicesHandler = ServicesHandler.getInstance(new ServiceHelper(this, ServicesHandler.class.getName(), true));
        servicesHandler.addService(new SessionService(new ServiceHelper(this, SessionService.class.getName(), true)));
        servicesHandler.addService(new KeysManagementService(new ServiceHelper(this, KeysManagementService.class.getName(), true)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        initListeners();
        initServices();
        initWebSocket();
        setupKeyStore();

        // webSocketClient.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile_picture) {
            // Handle item 1 selection
            return true;
        } else if (id == R.id.action_edit_name) {
            // Handle item 2 selection
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}