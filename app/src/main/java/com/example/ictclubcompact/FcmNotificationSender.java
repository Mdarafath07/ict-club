package com.example.ictclubcompact;
import android.content.Context;
import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FcmNotificationSender {

    private static final String TAG = "FCMSender";
    private static final String FCM_ENDPOINT = "https://fcm.googleapis.com/v1/projects/ict-club-b7f19/messages:send";
    private static final String SCOPE = "https://www.googleapis.com/auth/firebase.messaging";

    public static void sendNotification(Context context, String title, String body) {
        new Thread(() -> {
            try {
                // Load service account key from assets
                InputStream serviceAccount = context.getAssets().open("service-account.json");

                GoogleCredentials credentials = GoogleCredentials
                        .fromStream(serviceAccount)
                        .createScoped(Collections.singletonList(SCOPE));

                credentials.refreshIfExpired();
                String accessToken = credentials.getAccessToken().getTokenValue();

                // Construct JSON payload
                JSONObject message = new JSONObject();
                JSONObject notification = new JSONObject();
                notification.put("title", title);
                notification.put("body", body);

                JSONObject messageContent = new JSONObject();
                messageContent.put("topic", "all"); // your topic
                messageContent.put("notification", notification);

                message.put("message", messageContent);

                // Send using OkHttp
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody bodyData = RequestBody.create(mediaType, message.toString());

                Request request = new Request.Builder()
                        .url(FCM_ENDPOINT.replace("ict-club-b7f19", "ict-club-b7f19")) // replace with your project ID
                        .post(bodyData)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .addHeader("Content-Type", "application/json; UTF-8")
                        .build();

                Response response = client.newCall(request).execute();
                Log.d(TAG, "Response: " + response.body().string());

            } catch (Exception e) {
                Log.e(TAG, "FCM Error: ", e);
            }
        }).start();
    }
}
