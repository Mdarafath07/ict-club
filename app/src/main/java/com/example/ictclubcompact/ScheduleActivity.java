package com.example.ictclubcompact;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ictclubcompact.model.ScheduleModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<ScheduleModel> scheduleList;
    private ScheduleAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerView = findViewById(R.id.scheduleRecycler);
        progressBar = findViewById(R.id.scheduleProgressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleList = new ArrayList<>();
        adapter = new ScheduleAdapter(this, scheduleList, false); // false = user mode
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        createNotificationChannel();
        subscribeToFCMTopic();

        fetchSchedules();
    }

    private void fetchSchedules() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("schedules").get().addOnSuccessListener(queryDocumentSnapshots -> {
            scheduleList.clear();

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                ScheduleModel schedule = doc.toObject(ScheduleModel.class);
                if (schedule != null) {
                    schedule.setId(doc.getId());
                    scheduleList.add(schedule);
                }
            }

            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Failed to load schedules", Toast.LENGTH_SHORT).show();
            Log.e("ScheduleActivity", "Error loading: ", e);
        });
    }

    private void subscribeToFCMTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", "Subscribed to topic: all");
                    } else {
                        Log.e("FCM", "Subscription failed", task.getException());
                    }
                });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "schedule_channel",
                    "Class Schedule Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifies when new class schedules are posted");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
