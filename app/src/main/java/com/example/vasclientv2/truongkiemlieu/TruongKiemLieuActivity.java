package com.example.vasclientv2.truongkiemlieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.vasclientv2.R;
import com.example.vasclientv2.setting.SettingsActivity;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.ui.WareHouse;
import com.example.vasclientv2.ui.login.LoggedInUserView;
import com.example.vasclientv2.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;

public class TruongKiemLieuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    LoggedInUserView loggedInUserView = new LoggedInUserView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truongkiemlieu_main);
        Toolbar toolbar = findViewById(R.id.tkl_toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.tkl_drawer_layout);
        NavigationView navigationView = findViewById(R.id.tkl_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.tkl_nav_gallery, R.id.tkl_nav_home, R.id.tkl_nav_kl_da_duyet)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.tkl_nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        //Get data Intent -> loggedInUser model
        try {
            loggedInUserView = new LoggedInUserView(getIntent().getStringExtra("FULL_NAME"), getIntent().getStringExtra("ROLE_CODE"), getIntent().getStringExtra("USER_ID"));
        } catch (Exception e) {
            startActivity(new Intent(TruongKiemLieuActivity.this, LoginActivity.class));
            finish();
        }

        // Set Text Full name
        View headerView = navigationView.getHeaderView(0);
        TextView txtFullName = headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(loggedInUserView.getDisplayName());

        Button btnLogOut = drawer.findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(v -> {
            logout();
            finish();
        });
        // button Setting-> hiện tại dùng để đổi mật khẩu
        ImageButton btnSetting = headerView.findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(v -> startActivity(new Intent(TruongKiemLieuActivity.this, SettingsActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }
    protected void logout() {
        // Move to Login Activity
        startActivity(new Intent(TruongKiemLieuActivity.this, LoginActivity.class));
        // clear shared reference
        removeUserReference();
    }
    protected void removeUserReference(){
        WareHouse.UserId = null;
        
        TransferData transferData = TransferData.getInstance(this);
        transferData.removeData("IS_LOGGED");
        transferData.removeData("USER_ID");
        transferData.removeData("FULL_NAME");
        transferData.removeData("ROLE_CODE");
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.tkl_nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}