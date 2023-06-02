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
import com.example.e2ee_messaging_android_app_frontend.helpers.KeyStoreHelper;
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
    public interface UserMappingCallback {
        void onUserMappingComplete(List<User> users);
    }

    Button btnNewIdentity;
    Button btnMenu;
    EditText searchInput;
    RecyclerView roomRecyclerView;
    WebSocketClient webSocketClient;
    List<Room> roomList;
    List<User> userList;

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

    private void updateDiscoveryServer(String uuid, KeysManagementService.E2eeUser e2eeUser){
        // Convert public key to Base64 string
        String publicKeyString = null;
        try {
            publicKeyString = Base64.getEncoder().encodeToString(e2eeUser.getPublicKey().getEncoded());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        User user = new User(uuid, "", "", publicKeyString);
        Api.registerUser("https://10.0.2.2:5000/api/v1/users", user);
    }

    private void setupKeyStore() {
        ServicesHandler servicesHandler = ServicesHandler.getInstance();
        SessionService sessionService = (SessionService) servicesHandler.getService(SessionService.class.getName());
        KeysManagementService keysManagementService = (KeysManagementService) servicesHandler.getService(KeysManagementService.class.getName());

        // TODO: Check if user already has a key pair
        KeysManagementService.E2eeUser e2eeUser = sessionService.getIdentity();

        if (e2eeUser == null) {
            e2eeUser = keysManagementService.generateKeyPair();
            sessionService.saveIdentity(e2eeUser);
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
//        SessionService sessionService = (SessionService) ServicesHandler.getInstance().getService(SessionService.class.getName());
//        sessionService.removeUserSession();
        setupKeyStore();

        SessionService sessionService = (SessionService) ServicesHandler.getInstance().getService(SessionService.class.getName());

        KeysManagementService.E2eeUser e2eeUser = sessionService.getIdentity();
        String uuid = sessionService.getUserSession();
        updateDiscoveryServer(uuid, e2eeUser);
        // webSocketClient.start();

        // Get all users
        Api.getAllUsers("https://10.0.2.2:5000/api/v1/users", new UserMappingCallback() {
            @Override
            public void onUserMappingComplete(List<User> users) {
                userList = users;
                // update UI
            }
        });


        // Select a user to chat with

        // Key exchange with the selected user

        // Create a room with the selected user

        // Send a message to the room



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