package com.example.ictclubcompact;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseUser currentUser;
    private DatabaseReference databaseRef;
    private ImageView profileImageView;
    private TextView nameTextView, emailTextView, phoneTextView;
    private Button changePasswordButton, backButton, logoutButton;
    private Uri imageUri;
    private ImgBBUploader imgBBUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        initializeFirebase();
        imgBBUploader = new ImgBBUploader();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        backButton = findViewById(R.id.backButton);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void initializeFirebase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            databaseRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void loadUserData() {
        if (currentUser == null || databaseRef == null) {
            return;
        }

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    return;
                }

                User user = snapshot.getValue(User.class);
                if (user != null) {
                    updateUI(user);
                    loadProfileImage(user.getProfileImageUrl());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateUI(User user) {
        runOnUiThread(() -> {
            nameTextView.setText(user.getName() != null ? user.getName() : "N/A");
            emailTextView.setText(user.getEmail() != null ? user.getEmail() : "N/A");
            phoneTextView.setText(user.getPhone() != null ? user.getPhone() : "N/A");
        });
    }

    private void loadProfileImage(String imageUrl) {
        runOnUiThread(() -> {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(ProfileActivity.this)
                        .load(imageUrl)
                        .circleCrop()
                        .placeholder(R.drawable.profile)
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.profile);
            }
        });
    }

    private void setupClickListeners() {
        profileImageView.setOnClickListener(v -> openImagePicker());
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish();
        showToast("Logged out");
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");

        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText currentPasswordEditText = view.findViewById(R.id.currentPasswordEditText);
        EditText newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        EditText confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);

        builder.setView(view);

        builder.setPositiveButton("Change", (dialog, which) -> {
            String currentPassword = currentPasswordEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (!validatePasswordInputs(currentPassword, newPassword, confirmPassword)) {
                return;
            }

            changeUserPassword(currentPassword, newPassword);
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private boolean validatePasswordInputs(String currentPassword, String newPassword, String confirmPassword) {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Fill all fields");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            showToast("Passwords don't match");
            return false;
        }

        if (newPassword.length() < 6) {
            showToast("Minimum 6 characters");
            return false;
        }

        return true;
    }

    private void changeUserPassword(String currentPassword, String newPassword) {
        if (currentUser == null || currentUser.getEmail() == null) {
            showToast("User not available");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

        currentUser.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        showToast("Password changed");
                                    } else {
                                        showToast("Failed to change");
                                    }
                                });
                    } else {
                        showToast("Authentication failed");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                Glide.with(this)
                        .load(imageUri)
                        .circleCrop()
                        .into(profileImageView);
                uploadImageToImgBB();
            }
        }
    }

    private void uploadImageToImgBB() {
        if (imageUri == null || currentUser == null) {
            showToast("Upload error");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        imgBBUploader.uploadImage(this, imageUri, "64ce96c9063d79ff088ad4b8ef183868", new ImgBBUploader.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                progressDialog.dismiss();
                saveImageUrlToDatabase(imageUrl);
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                showToast("Upload failed");
            }
        });
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        if (databaseRef == null) {
            showToast("Database error");
            return;
        }

        databaseRef.child("profileImageUrl")
                .setValue(imageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Profile updated");
                    } else {
                        showToast("Save failed");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}