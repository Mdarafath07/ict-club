package com.example.ictclubcompact;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ictclubcompact.email.SecureEmailSender;
import com.saadahmedev.popupdialog.PopupDialog;

public class HelpActivity extends AppCompatActivity {
    private EditText emailEditText, messageEditText;
    private ProgressDialog progressDialog;
    private SecureEmailSender emailSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Initialize the email sender
        emailSender = new SecureEmailSender(this);

        // Initialize UI components
        initializeViews();
        setupSubmitButton();


    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        messageEditText = findViewById(R.id.messageEditText);
    }

    private void setupSubmitButton() {
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String message = messageEditText.getText().toString().trim();

            if (email.isEmpty()) {
                emailEditText.setError("Please enter your email");
                return;
            }

            if (message.isEmpty()) {
                messageEditText.setError("Please enter your message");
                return;
            }

            showProgressDialog("Sending your message...");
            sendHelpEmail(email, message);
        });
    }

    private void sendHelpEmail(String email, String message) {
        emailSender.sendEmail(email, message, new SecureEmailSender.EmailCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    dismissProgressDialog();
                    Toast.makeText(HelpActivity.this,
                            "Message sent successfully!", Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    dismissProgressDialog();
                    Toast.makeText(HelpActivity.this,
                            "Failed to send message: " + error, Toast.LENGTH_LONG).show();

                });
            }
        });
    }

    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }
}