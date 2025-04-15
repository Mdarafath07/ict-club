package com.example.ictclubcompact;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseUser currentUser;
    private DatabaseReference databaseRef;
    private ImageView profileImageView, backButton, editSaveButton;
    private EditText nameEditText, emailEditText, phoneEditText, dobEditText, sessionEditText;
    private Spinner bloodGroupSpinner, semesterSpinner, departmentSpinner;
    private Button changePasswordButton, logoutButton, saveButton;
    private Uri imageUri;
    private ImgBBUploader imgBBUploader;
    private ValueEventListener userDataListener;
    private ProgressDialog progressDialog;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        initializeFirebase();
        setupSpinners();
        imgBBUploader = new ImgBBUploader();
        loadUserData();
        setupClickListeners();
        disableAllFields();
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> bloodGroupAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_groups,
                R.layout.spinner_item
        );
        bloodGroupAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(bloodGroupAdapter);

        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.semesters,
                R.layout.spinner_item
        );
        semesterAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        semesterSpinner.setAdapter(semesterAdapter);

        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.departments,
                R.layout.spinner_item
        );
        departmentAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        departmentSpinner.setAdapter(departmentAdapter);

        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    ((TextView) view).setTextColor(Color.BLUE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void disableAllFields() {
        nameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        phoneEditText.setEnabled(false);
        dobEditText.setEnabled(false);
        sessionEditText.setEnabled(false);

        bloodGroupSpinner.setEnabled(false);
        semesterSpinner.setEnabled(false);
        departmentSpinner.setEnabled(false);

        nameEditText.setFocusable(false);
        emailEditText.setFocusable(false);
        phoneEditText.setFocusable(false);
        sessionEditText.setFocusable(false);
        dobEditText.setFocusable(false);

        nameEditText.setClickable(false);
        emailEditText.setClickable(false);
        phoneEditText.setClickable(false);
        sessionEditText.setClickable(false);

        nameEditText.setBackgroundResource(android.R.color.transparent);
        emailEditText.setBackgroundResource(android.R.color.transparent);
        phoneEditText.setBackgroundResource(android.R.color.transparent);
        dobEditText.setBackgroundResource(android.R.color.transparent);
        sessionEditText.setBackgroundResource(android.R.color.transparent);

        bloodGroupSpinner.setBackgroundResource(android.R.color.transparent);
        semesterSpinner.setBackgroundResource(android.R.color.transparent);
        departmentSpinner.setBackgroundResource(android.R.color.transparent);
    }

    private void enableAllFields() {
        nameEditText.setEnabled(true);
        emailEditText.setEnabled(true);
        phoneEditText.setEnabled(true);
        dobEditText.setEnabled(true);
        sessionEditText.setEnabled(true);

        bloodGroupSpinner.setEnabled(true);
        semesterSpinner.setEnabled(true);
        departmentSpinner.setEnabled(true);

        nameEditText.setFocusableInTouchMode(true);
        emailEditText.setFocusableInTouchMode(true);
        phoneEditText.setFocusableInTouchMode(true);
        sessionEditText.setFocusableInTouchMode(true);
        dobEditText.setFocusableInTouchMode(true);

        nameEditText.setClickable(true);
        emailEditText.setClickable(true);
        phoneEditText.setClickable(true);
        sessionEditText.setClickable(true);

        nameEditText.setBackgroundResource(R.drawable.edit_text_bg);
        emailEditText.setBackgroundResource(R.drawable.edit_text_bg);
        phoneEditText.setBackgroundResource(R.drawable.edit_text_bg);
        dobEditText.setBackgroundResource(R.drawable.edit_text_bg);
        sessionEditText.setBackgroundResource(R.drawable.edit_text_bg);

        bloodGroupSpinner.setBackgroundResource(R.drawable.spinner_background);
        semesterSpinner.setBackgroundResource(R.drawable.spinner_background);
        departmentSpinner.setBackgroundResource(R.drawable.spinner_background);
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.profileImageView);
        backButton = findViewById(R.id.backButton);
        editSaveButton = findViewById(R.id.editSaveButton);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        dobEditText = findViewById(R.id.dobEditText);
        sessionEditText = findViewById(R.id.sessionEditText);

        bloodGroupSpinner = findViewById(R.id.bloodGroupSpinner);
        semesterSpinner = findViewById(R.id.semesterSpinner);
        departmentSpinner = findViewById(R.id.departmentSpinner);

        changePasswordButton = findViewById(R.id.changePasswordButton);
        logoutButton = findViewById(R.id.logoutButton);
        saveButton = findViewById(R.id.saveButton);
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

        if (userDataListener != null) {
            databaseRef.removeEventListener(userDataListener);
        }

        userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isDestroyed() && snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        updateUI(user);
                        loadProfileImage(user.getProfileImageUrl());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isDestroyed()) {
                    Log.e(TAG, "Database error: " + error.getMessage());
                }
            }
        };

        databaseRef.addValueEventListener(userDataListener);
    }

    private void updateUI(User user) {
        if (isDestroyed()) return;

        runOnUiThread(() -> {
            if (isDestroyed()) return;

            nameEditText.setText(user.getName() != null ? user.getName() : "N/A");
            emailEditText.setText(user.getEmail() != null ? user.getEmail() : "N/A");
            phoneEditText.setText(user.getPhone() != null ? user.getPhone() : "N/A");

            if (user.getDob() != null) dobEditText.setText(user.getDob());
            if (user.getBloodGroup() != null) setSpinnerSelection(bloodGroupSpinner, user.getBloodGroup());
            if (user.getSemester() != null) setSpinnerSelection(semesterSpinner, user.getSemester());
            if (user.getDepartment() != null) setSpinnerSelection(departmentSpinner, user.getDepartment());
            if (user.getSession() != null) sessionEditText.setText(user.getSession());
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void loadProfileImage(String imageUrl) {
        if (isDestroyed()) return;

        runOnUiThread(() -> {
            if (isDestroyed()) return;

            try {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(ProfileActivity.this)
                            .load(imageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.profile)
                            .into(profileImageView);
                } else {
                    profileImageView.setImageResource(R.drawable.profile);
                }
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Glide load error: " + e.getMessage());
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

        editSaveButton.setOnClickListener(v -> toggleEditMode());
        saveButton.setOnClickListener(v -> saveChanges());

        dobEditText.setOnClickListener(v -> {
            if (isEditMode) {
                showDatePickerDialog();
            }
        });
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;

        if (isEditMode) {
            editSaveButton.setImageResource(R.drawable.ic_close);
            saveButton.setVisibility(View.VISIBLE);
            enableAllFields();
        } else {
            editSaveButton.setImageResource(R.drawable.edit);
            saveButton.setVisibility(View.GONE);
            disableAllFields();
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    dobEditText.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveChanges() {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String dob = dobEditText.getText().toString();
        String bloodGroup = bloodGroupSpinner.getSelectedItem().toString();
        String semester = semesterSpinner.getSelectedItem().toString();
        String department = departmentSpinner.getSelectedItem().toString();
        String session = sessionEditText.getText().toString();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        showProgressDialog("Saving changes...");

        databaseRef.child("name").setValue(name);
        databaseRef.child("email").setValue(email);
        databaseRef.child("phone").setValue(phone);
        databaseRef.child("dob").setValue(dob);
        databaseRef.child("bloodGroup").setValue(bloodGroup);
        databaseRef.child("semester").setValue(semester);
        databaseRef.child("department").setValue(department);
        databaseRef.child("session").setValue(session)
                .addOnCompleteListener(task -> {
                    dismissProgressDialog();

                    if (task.isSuccessful()) {
                        showToast("Profile updated successfully");
                        toggleEditMode();
                    } else {
                        showToast("Failed to update profile");
                    }
                });
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

        showProgressDialog("Changing password...");

        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

        currentUser.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (isDestroyed()) {
                        dismissProgressDialog();
                        return;
                    }

                    if (task.isSuccessful()) {
                        currentUser.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    dismissProgressDialog();

                                    if (isDestroyed()) return;

                                    if (updateTask.isSuccessful()) {
                                        showToast("Password changed");
                                    } else {
                                        showToast("Failed to change");
                                    }
                                });
                    } else {
                        dismissProgressDialog();
                        showToast("Authentication failed");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null && !isDestroyed()) {
                try {
                    Glide.with(this)
                            .load(imageUri)
                            .circleCrop()
                            .placeholder(R.drawable.profile)
                            .into(profileImageView);
                    uploadImageToImgBB();
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Glide load error: " + e.getMessage());
                }
            }
        }
    }

    private void uploadImageToImgBB() {
        if (imageUri == null || currentUser == null || isDestroyed()) {
            showToast("Upload error");
            return;
        }

        showProgressDialog("Uploading...");

        imgBBUploader.uploadImage(this, imageUri, "64ce96c9063d79ff088ad4b8ef183868", new ImgBBUploader.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                if (!isDestroyed()) {
                    dismissProgressDialog();
                    saveImageUrlToDatabase(imageUrl);
                }
            }

            @Override
            public void onError(String error) {
                if (!isDestroyed()) {
                    dismissProgressDialog();
                    showToast("Upload failed");
                }
            }
        });
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        if (databaseRef == null || isDestroyed()) {
            showToast("Database error");
            return;
        }

        showProgressDialog("Saving...");

        databaseRef.child("profileImageUrl")
                .setValue(imageUrl)
                .addOnCompleteListener(task -> {
                    dismissProgressDialog();

                    if (isDestroyed()) return;

                    if (task.isSuccessful()) {
                        showToast("Profile updated");
                    } else {
                        showToast("Save failed");
                    }
                });
    }

    private void showProgressDialog(String message) {
        if (isDestroyed()) return;

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                Log.e(TAG, "Error dismissing progress dialog: " + e.getMessage());
            }
        }
    }

    private void showToast(String message) {
        if (!isDestroyed()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseRef != null && userDataListener != null) {
            databaseRef.removeEventListener(userDataListener);
        }
        dismissProgressDialog();
    }
}