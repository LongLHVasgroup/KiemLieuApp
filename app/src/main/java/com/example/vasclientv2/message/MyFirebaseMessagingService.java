package com.example.vasclientv2.message;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.vasclientv2.R;
import com.example.vasclientv2.kiemlieu.KiemLieuActivity;
import com.example.vasclientv2.splash.SplashScreenActivity;
import com.example.vasclientv2.truongkiemlieu.TruongKiemLieuActivity;
import com.example.vasclientv2.ui.TransferData;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "CHANNEL_ID_VAS_CLIENT";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a notification payload.
        // On foreground

        if (remoteMessage.getData().size() > 0) {
            // Show custom notify
            // buider
            DataMessage dataNoty = new DataMessage();
            dataNoty.setTitle(remoteMessage.getData().get("title"));
            dataNoty.setMessage(remoteMessage.getData().get("message"));
            dataNoty.setRfid(remoteMessage.getData().get("rfid"));
            dataNoty.setSender_id(remoteMessage.getData().get("sender_id"));
            dataNoty.setReciver_token(remoteMessage.getData().get("reciver_token"));
            dataNoty.setVerhicle_number(remoteMessage.getData().get("verhicle_number"));


            //Set Intent
            // Create an explicit intent for an Activity in your app
            // Check role
            TransferData transferData = TransferData.getInstance(this);
            Intent intent = new Intent();
            if (transferData.getData("ROLE_CODE", "").equals("TRUONGKIEMLIEU")) {
                intent = new Intent(this, TruongKiemLieuActivity.class);
            } else if (transferData.getData("ROLE_CODE", "").equals("KIEMLIEU")) {
                intent = new Intent(this, KiemLieuActivity.class);
            } else {
                intent = new Intent(this, SplashScreenActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setContentTitle(dataNoty.getTitle())
                    .setContentText(dataNoty.getMessage())
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            int notifyId = (int) System.currentTimeMillis();

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(notifyId, builder.build());
            Log.d(TAG, dataNoty.toString());

        }

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "Refreshed token: " + s);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
    }




}
