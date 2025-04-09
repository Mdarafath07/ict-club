package com.example.ictclubcompact;


import android.content.Context;
import android.util.Log;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class SecureEmailSender {
    private static final String TAG = "SecureEmailSender";
    private final Context context;
    private static final String SHARED_PREFS_NAME = "secure_email_creds";
    private static final String EMAIL_KEY = "sender_email";
    private static final String PASSWORD_KEY = "sender_password";

    public SecureEmailSender(Context context) {
        this.context = context;
    }

    /**
     * Stores email credentials securely using Android's Encryption
     * Call this only once during app setup
     */
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

    /**
     * Sends email in background thread
     */
    public void sendEmail(String userEmail, String userMessage, EmailCallback callback) {
        new Thread(() -> {
            try {
                // 1. Get stored credentials
                String[] credentials = getStoredCredentials();
                String fromEmail = credentials[0];
                String password = credentials[1];
                String toEmail = "mdarafathuddin10@gmail.com";

                // 2. Configure email properties
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");

                // 3. Create session with authentication
                Session session = Session.getInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(fromEmail, password);
                            }
                        });

                // 4. Create and configure message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Help Request from App User");
                message.setText("User Email: " + userEmail + "\n\nMessage:\n" + userMessage);

                // 5. Send message
                Transport.send(message);
                callback.onSuccess();
            } catch (AuthenticationFailedException e) {
                Log.e(TAG, "Authentication failed", e);
                callback.onError("Authentication failed. Check email/password.");
            } catch (MessagingException e) {
                Log.e(TAG, "Email sending failed", e);
                callback.onError("Email service error: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error", e);
                callback.onError("Unexpected error: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Retrieves stored credentials securely
     */
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

        String email = sharedPreferences.getString(EMAIL_KEY, "");
        String password = sharedPreferences.getString(PASSWORD_KEY, "");

        if (email.isEmpty() || password.isEmpty()) {
            throw new IllegalStateException("Email credentials not found");
        }

        return new String[]{email, password};
    }

    public interface EmailCallback {
        void onSuccess();
        void onError(String error);
    }
}