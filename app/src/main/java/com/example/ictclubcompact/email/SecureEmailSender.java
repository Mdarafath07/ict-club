package com.example.ictclubcompact.email;

import android.content.Context;
import android.util.Log;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class SecureEmailSender {
    // Configuration Constants
    private static final String TAG = "SecureEmailSender";
    private static final String SHARED_PREFS_NAME = "secure_email_creds";
    private static final String EMAIL_KEY = "sender_email";
    private static final String PASSWORD_KEY = "sender_password";
    private static final String RECEIVER_EMAIL = "mdarafathuddin10@gmail.com"; // Fixed receiver

    private final Context context;

    public SecureEmailSender(Context context) {
        this.context = context;
    }


    public void storeCredentials(String email, String password) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences)
                    EncryptedSharedPreferences.create(
                            SHARED_PREFS_NAME,
                            masterKeyAlias,
                            context,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );

            sharedPreferences.edit()
                    .putString(EMAIL_KEY, email)
                    .putString(PASSWORD_KEY, password)
                    .apply();

            Log.i(TAG, "Credentials stored securely");
        } catch (Exception e) {
            Log.e(TAG, "Failed to store credentials", e);
        }
    }


    public void sendEmail(String userEmail, String userMessage, EmailCallback callback) {
        new Thread(() -> {
            try {
                // 1. Get stored credentials
                String[] credentials = getStoredCredentials();
                String fromEmail = credentials[0];
                String password = credentials[1];

                // 2. Configure email properties
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");

                // 3. Create authenticated session
                Session session = Session.getInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(fromEmail, password);
                            }
                        });

                // 4. Compose message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(RECEIVER_EMAIL));
                message.setSubject("Help Request from App User");
                message.setText("User Email: " + userEmail + "\n\nMessage:\n" + userMessage);

                // 5. Send email
                Transport.send(message);
                callback.onSuccess();
            } catch (Exception e) {
                Log.e(TAG, "Email sending failed", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private String[] getStoredCredentials() throws Exception {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences)
                EncryptedSharedPreferences.create(
                        SHARED_PREFS_NAME,
                        masterKeyAlias,
                        context,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );

        return new String[]{
                sharedPreferences.getString(EMAIL_KEY, "mdarafathuddin7@gmail.com"),
                sharedPreferences.getString(PASSWORD_KEY, "vuuq kpcf anlf bohl")
        };
    }

    public interface EmailCallback {
        void onSuccess();
        void onError(String error);
    }
}