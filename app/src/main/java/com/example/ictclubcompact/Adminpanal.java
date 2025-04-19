package com.example.ictclubcompact;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Adminpanal extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView scheduleap, notificationap;

    private static final String TAG = "AdminPanel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminpanal);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {

        scheduleap = findViewById(R.id.scheduleap);
        notificationap = findViewById(R.id.notificationap);

    }

    private void setupClickListeners() {
        scheduleap.setOnClickListener(v -> {
            startActivity(new Intent(Adminpanal.this, ScheduleControlActivity.class));

        });
        notificationap.setOnClickListener(v -> {
            startActivity(new Intent(Adminpanal.this, AddNotificationActivity.class));

        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            finish();
        } else {
            verifyAdminStatus(currentUser.getUid());
        }
    }

    private void verifyAdminStatus(String userId) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Boolean isAdmin = document.getBoolean("isAdmin");
                            Log.d(TAG, "isAdmin: " + isAdmin);

                            if (Boolean.TRUE.equals(isAdmin)) {
                                Log.d(TAG, "Admin access granted");
                                // Allowed
                            } else {
                                Log.d(TAG, "Access denied: not admin");
                                showAccessDenied();
                            }
                        } else {
                            Log.d(TAG, "Document not found");
                            showAccessDenied();
                        }
                    } else {
                        Log.e(TAG, "Error getting document", task.getException());
                        showAccessDenied();
                    }
                });
    }

    private void showAccessDenied() {
        Toast.makeText(this, "Admin access required", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
