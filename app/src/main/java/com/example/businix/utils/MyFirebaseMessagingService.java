package com.example.businix.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.businix.R;
import com.example.businix.models.UserRole;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String DEFAULT_CHANNEL_ID = "default_channel";
    private static final String IMPORTANT_CHANNEL_ID = "important_channel";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // In ra FCM registration token của thiết bị
        Log.d("FCM Token", token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {LoginManager loginManager = new LoginManager(this);
        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            String channelId = remoteMessage.getData().get("channelId");
            String type = remoteMessage.getData().get("type");

            if (loginManager.getLoggedInRole() != null) {
                String role = loginManager.getLoggedInRole();
                if (type.equals(role))
                    showNotification(title, message, channelId);
            }
        } else if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            showNotification(title, message, DEFAULT_CHANNEL_ID);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            NotificationChannel defaultChannel = new NotificationChannel(
                    DEFAULT_CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            defaultChannel.setDescription("Kênh mặc định");
            notificationManager.createNotificationChannel(defaultChannel);

            NotificationChannel importantChannel = new NotificationChannel(
                    IMPORTANT_CHANNEL_ID,
                    "Important Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            importantChannel.setDescription("Kênh quan trọng");
            notificationManager.createNotificationChannel(importantChannel);
        }
    }

    private void showNotification(String title, String message, String chanelId) {
        // Tạo một đối tượng NotificationCompat.Builder
        @SuppressLint("ResourceAsColor") NotificationCompat.Builder builder = new NotificationCompat.Builder(this, chanelId)
                .setSmallIcon(R.drawable.ic_businix)
                .setContentTitle(title)
                .setColorized(true)
                .setColor(R.color.light_purple)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Tạo một đối tượng Notification từ đối tượng Builder
        Notification notification = builder.build();

        // Lấy đối tượng NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Hiển thị thông báo trên thiết bị
        notificationManager.notify((int) System.currentTimeMillis(), notification);

    }

}
