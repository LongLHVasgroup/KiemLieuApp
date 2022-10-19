package com.example.vasclientv2.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.vasclientv2.R;
import com.example.vasclientv2.baove.BaoVeActivity;
import com.example.vasclientv2.kiemlieu.KiemLieuActivity;
import com.example.vasclientv2.model.entities.UserModel;
import com.example.vasclientv2.truongkiemlieu.TruongKiemLieuActivity;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.ui.WareHouse;
import com.example.vasclientv2.ui.login.LoginActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        createNotificationChannel();

        AppCenter.start(getApplication(), "b20b0e78-b512-4323-ab6f-2502d347b3a7",
                Analytics.class, Crashes.class);

        // Tự động login dựa trên shared refernce
//        autoLogin();


        // close splash activity và chuyển sang màn hình đăng nhập
        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        finish();
    }

    private void autoLogin() {
        // shared reference
        TransferData transferData = TransferData.getInstance(this);
        try {
            if (transferData.getBoolean("IS_LOGGED", false)) {
                UserModel userModel = new UserModel();
                userModel.setFullName(transferData.getData("FULL_NAME", null));
                userModel.setUserId(transferData.getData("USER_ID", null));
                userModel.setRoldCode(transferData.getData("ROLE_CODE", null));

                updateUiWithUser(userModel);

            } else {
                // Start home activity
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            }
        } catch (Exception e) {
            Log.e("ERROR_LOGIN", e.toString());
            // Start home activity
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        }

    }

    private void updateUiWithUser(UserModel model) {
        // Save WareHouse
        WareHouse.UserId = model.getUserId();
        // Change Activity
        switch (model.getRoldCode()) {
            case "BAOVE":
                Intent baoveIntent = new Intent(SplashScreenActivity.this, BaoVeActivity.class);
                baoveIntent.putExtra("USER_ID", model.getUserId());
                baoveIntent.putExtra("FULL_NAME", model.getFullName());
                baoveIntent.putExtra("ROLE_CODE", model.getRoldCode());
                startActivity(baoveIntent);
                break;
            case "KIEMLIEU":
                Intent kiemlieuIntent = new Intent(SplashScreenActivity.this, KiemLieuActivity.class);
                kiemlieuIntent.putExtra("USER_ID", model.getUserId());
                kiemlieuIntent.putExtra("FULL_NAME", model.getFullName());
                kiemlieuIntent.putExtra("ROLE_CODE", model.getRoldCode());
                startActivity(kiemlieuIntent);
                break;
            case "TRUONGKIEMLIEU":
                Intent truongkiemlieuIntent = new Intent(SplashScreenActivity.this, TruongKiemLieuActivity.class);
                truongkiemlieuIntent.putExtra("USER_ID", model.getUserId());
                truongkiemlieuIntent.putExtra("FULL_NAME", model.getFullName());
                truongkiemlieuIntent.putExtra("ROLE_CODE", model.getRoldCode());
                startActivity(truongkiemlieuIntent);
                break;
        }


    }
    // Tạo channel nhận thông báo cho ứng dụng
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_notification_channel_id);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID_VAS_CLIENT", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d("Token", "createNotificationChannel");
        }
    }

}