package com.example.ictclubcompact;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ictclubcompact.model.ScheduleModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScheduleControlActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String uploadedImageUrl;

    private ImageView imageView;
    private Button chooseImageBtn, postBtn;
    private EditText subjectInput, dateInput, timeInput, mentorInput, notesInput;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    private RecyclerView scheduleRecyclerView;
    private List<ScheduleModel> scheduleList;
    private ScheduleAdapter adminAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_control);

        db = FirebaseFirestore.getInstance();

        imageView = findViewById(R.id.imagePreview);
        chooseImageBtn = findViewById(R.id.chooseImageBtn);
        postBtn = findViewById(R.id.postScheduleBtn);

        subjectInput = findViewById(R.id.subjectInput);
        dateInput = findViewById(R.id.dateInput);
        timeInput = findViewById(R.id.timeInput);
        mentorInput = findViewById(R.id.mentorInput);
        notesInput = findViewById(R.id.notesInput);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        chooseImageBtn.setOnClickListener(v -> openImagePicker());

        postBtn.setOnClickListener(v -> {
            if (uploadedImageUrl != null) {
                uploadSchedule();
            } else {
                Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
            }
        });

        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleList = new ArrayList<>();
        adminAdapter = new ScheduleAdapter(this, scheduleList, true);
        scheduleRecyclerView.setAdapter(adminAdapter);

        loadSchedules();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            uploadImageToImgbb(imageUri);
        }
    }

    private void uploadImageToImgbb(Uri imageUri) {
        try {
            progressDialog.show();
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            String apiKey = "64ce96c9063d79ff088ad4b8ef183868";
            String url = "https://api.imgbb.com/1/upload?key=" + apiKey;

            RequestBody requestBody = new FormBody.Builder()
                    .add("image", encodedImage)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {
                            String json = response.body().string();
                            JSONObject jsonObject = new JSONObject(json);
                            uploadedImageUrl = jsonObject.getJSONObject("data").getString("url");
                            runOnUiThread(() ->
                                    Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadSchedule() {
        String subject = subjectInput.getText().toString();
        String date = dateInput.getText().toString();
        String time = timeInput.getText().toString();
        String mentor = mentorInput.getText().toString();
        String notes = notesInput.getText().toString();

        if (subject.isEmpty() || date.isEmpty() || time.isEmpty() || mentor.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = UUID.randomUUID().toString();

        HashMap<String, Object> schedule = new HashMap<>();
        schedule.put("imageUrl", uploadedImageUrl);
        schedule.put("subject", subject);
        schedule.put("date", date);
        schedule.put("time", time);
        schedule.put("mentor", mentor);
        schedule.put("notes", notes);
        schedule.put("id", id);

        db.collection("schedules").document(id)
                .set(schedule)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Schedule posted", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    loadSchedules();
                    FcmNotificationSender.sendNotification(ScheduleControlActivity.this, "New Class Schedule", subject + " on " + date);
                });
    }

    private void loadSchedules() {
        db.collection("schedules").get().addOnSuccessListener(queryDocumentSnapshots -> {
            scheduleList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                ScheduleModel schedule = doc.toObject(ScheduleModel.class);
                if (schedule != null) {
                    schedule.setId(doc.getId());
                    scheduleList.add(schedule);
                }
            }
            adminAdapter.notifyDataSetChanged();
        });
    }

    private void clearInputs() {
        subjectInput.setText("");
        dateInput.setText("");
        timeInput.setText("");
        mentorInput.setText("");
        notesInput.setText("");
        imageView.setImageResource(0);
        uploadedImageUrl = null;
    }
}
