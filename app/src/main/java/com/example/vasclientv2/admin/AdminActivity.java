package com.example.vasclientv2.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.vasclientv2.R;
import com.example.vasclientv2.admin.adminsetting.AdminSettingActivity;
import com.example.vasclientv2.admin.createuser.CreateUserActivity;
import com.example.vasclientv2.admin.resetpass.ResetPassUserActivity;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.ui.WareHouse;
import com.example.vasclientv2.ui.login.LoginActivity;

public class AdminActivity extends AppCompatActivity {
    private CardView cvResetPass,cvCreateNewUser, cvLogout, cvConfig;
    private TextView txtCopyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setupControlls();
        setupEvents();
    }

    private void setupEvents() {
        cvResetPass.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ResetPassUserActivity.class));
        });

        cvCreateNewUser.setOnClickListener(v->{
            startActivity(new Intent(AdminActivity.this, CreateUserActivity.class));
        });
        cvLogout.setOnClickListener(v -> {
            logout();
            finish();
        });

        cvConfig.setOnClickListener(v->{
            startActivity(new Intent(AdminActivity.this, AdminSettingActivity.class));
        });
    }

    private void setupControlls() {
        cvResetPass = findViewById(R.id.cv_resetPass);
        cvCreateNewUser = findViewById(R.id.cv_create_user);
        cvLogout = findViewById(R.id.cv_logout);
        cvConfig = findViewById(R.id.cv_config);
        txtCopyright = findViewById(R.id.copyright);
        // Set the copyright
        try {
            txtCopyright.setText(getResources().getString(R.string.copyright,getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
        }catch (Exception e){
            txtCopyright.setText(getResources().getString(R.string.copyright,"1.0.0"));
        }

    }

    protected void logout() {
        // Move to Login Activity
        startActivity(new Intent(AdminActivity.this, LoginActivity.class));
        // clear shared reference
        removeUserReference();
    }

    protected void removeUserReference() {

        WareHouse.UserId = null;
        TransferData transferData = TransferData.getInstance(this);
        transferData.removeData("IS_LOGGED");
        transferData.removeData("USER_ID");
        transferData.removeData("FULL_NAME");
        transferData.removeData("ROLE_CODE");
    }
}