package com.example.ictclubcompact;
import com.example.ictclubcompact.SubmitAssignmentActivity;

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
    private Button submitButton;
    private ProgressBar progressBar;
    private String assignmentId, assignmentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_assignment);

        // Assignment ডিটেইলস পাওয়া
        assignmentId = getIntent().getStringExtra("assignment_id");
        assignmentTitle = getIntent().getStringExtra("assignment_title");

        TextView assignmentTitleView = findViewById(R.id.assignmentTitle);
        assignmentTitleView.setText("Submit: " + assignmentTitle);

        imagePreview = findViewById(R.id.imagePreview);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);

        Button selectImageButton = findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(v -> openFileChooser());

        submitButton.setOnClickListener(v -> submitAssignment());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
            imagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void submitAssignment() {
        String description = descriptionEditText.getText().toString().trim();

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            descriptionEditText.setError("Please enter a description");
            descriptionEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);

        // ডেমো হিসেবে 2 সেকেন্ড ডিলে দেওয়া হলো (আসল অ্যাপে এখানে API কল করবেন)
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