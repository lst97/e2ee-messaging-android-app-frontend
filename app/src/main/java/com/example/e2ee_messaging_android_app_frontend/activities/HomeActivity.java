package com.example.e2ee_messaging_android_app_frontend.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.example.e2ee_messaging_android_app_frontend.R;
import com.example.e2ee_messaging_android_app_frontend.models.Room;
import com.example.e2ee_messaging_android_app_frontend.networks.websocket.WebSocketClient;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    Button btnNewIdentity;
    Button btnMenu;
    EditText searchInput;
    RecyclerView roomRecyclerView;
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
         WebSocketClient webSocketClient = new WebSocketClient("wss://10.0.2.2:8080");
         webSocketClient.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        initListeners();
        initWebSocket();
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