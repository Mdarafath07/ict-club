package com.example.ictclubcompact;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splashscreen extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set navigation bar color
        getWindow().setNavigationBarColor(Color.parseColor("#0B2473"));

        // Handler to delay navigation
        new Handler().postDelayed(this::checkAuthAndNavigate, SPLASH_DELAY);
    }

    private void checkAuthAndNavigate() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent;

        if (currentUser != null) {
            // User is logged in, go to MainActivity
            intent = new Intent(this, MainActivity.class);
        } else {
            // No user logged in, go to LoginActivity
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}