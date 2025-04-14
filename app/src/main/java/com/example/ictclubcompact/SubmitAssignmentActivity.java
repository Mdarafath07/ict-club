package com.example.ictclubcompact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SubmitAssignmentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imagePreview;
    private Uri imageUri;
    private EditText descriptionEditText;
    private Button submitButton, removeImageButton;
    private ProgressBar progressBar;
    private String assignmentId, assignmentTitle;
    private Button selectImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_assignment);

        // Assignment
        assignmentId = getIntent().getStringExtra("assignment_id");
        assignmentTitle = getIntent().getStringExtra("assignment_title");

        TextView assignmentTitleView = findViewById(R.id.assignmentTitle);
        assignmentTitleView.setText("Submit: " + assignmentTitle);

        imagePreview = findViewById(R.id.imagePreview);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);
        selectImageButton = findViewById(R.id.selectImageButton);

        selectImageButton.setOnClickListener(v -> openFileChooser());

        // Add remove image button functionality
        Button removeImageButton = findViewById(R.id.removeImageButton);
        removeImageButton.setOnClickListener(v -> {
            imageUri = null;
            imagePreview.setImageURI(null);
            imagePreview.setVisibility(View.GONE);
            Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show();
        });

        submitButton.setOnClickListener(v -> submitAssignment());
    }

    private void openFileChooser() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening file chooser", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            try {
                imageUri = data.getData();
                imagePreview.setImageURI(imageUri);
                imagePreview.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void submitAssignment() {
        String description = descriptionEditText.getText().toString().trim();

        if (description.isEmpty()) {
            descriptionEditText.setError("Please enter a description");
            descriptionEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            showSuccessDialog();
        }, 2000);
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Assignment Submitted Successfully!")
                .setMessage("Your assignment has been submitted successfully.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}