package com.example.ictclubcompact;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM_Service";
    private static final String CHANNEL_ID = "ict_club_notifications";
    private static final String CHANNEL_NAME = "ICT Club Notifications";
    private static final String PREF_NAME = "FCM_Prefs";
    private static final String KEY_TOKEN = "fcm_token";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New Token: " + token);
        saveTokenToPrefs(token);
        sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = getString(R.string.app_name); // Default title
        String body = "You have a new message";      // Default body

        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null) {
                title = remoteMessage.getNotification().getTitle();
            }
            if (remoteMessage.getNotification().getBody() != null) {
                body = remoteMessage.getNotification().getBody();
            }
        }

        // Handle additional data from the notification payload
        Map<String, String> data = remoteMessage.getData();
        Log.d(TAG, "Message received - Title: " + title + ", Body: " + body);

        sendNotification(title, body, data);
    }

    private void sendNotification(String title, String messageBody, Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Attach extra data from payload (if any)
        if (data != null && !data.isEmpty()) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Small Icon Fix: fallback icon if yours is missing
        int iconRes = R.drawable.notifications1;
        try {
            getResources().getDrawable(iconRes); // test if icon exists
        } catch (Exception e) {
            iconRes = android.R.drawable.ic_dialog_info;
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody)) // Big text support
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.parseColor("#0B2473"));

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            createNotificationChannel(manager);
            int notificationId = (int) System.currentTimeMillis(); // Unique ID for notifications
            manager.notify(notificationId, builder.build());
        } else {
            Log.e(TAG, "NotificationManager is null");
        }
    }

    private void createNotificationChannel(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for ICT Club Notifications");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }
    }

    private void saveTokenToPrefs(String token) {
        try {
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            prefs.edit().putString(KEY_TOKEN, token).apply();
            Log.d(TAG, "Token saved to SharedPreferences");
        } catch (Exception e) {
            Log.e(TAG, "Error saving token to SharedPreferences", e);
        }
    }

    private void sendTokenToServer(String token) {
        Log.d(TAG, "Send this token to your server: " + token);
        // Add code to send the token to your server if needed
    }
}
