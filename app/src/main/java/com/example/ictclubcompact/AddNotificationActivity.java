package com.example.ictclubcompact;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ictclubcompact.model.NotificationModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AddNotificationActivity extends AppCompatActivity {

    EditText titleInput, messageInput, linkInput, dateInput;
    Button postBtn;
    FirebaseFirestore db;

    RecyclerView recyclerView;
    List<NotificationModel> notificationList;
    NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notification);

        titleInput = findViewById(R.id.notificationTitle);
        messageInput = findViewById(R.id.notificationMessage);
        linkInput = findViewById(R.id.notificationLink);
        dateInput = findViewById(R.id.notificationDate);
        postBtn = findViewById(R.id.postNotificationBtn);

        db = FirebaseFirestore.getInstance();

        postBtn.setOnClickListener(v -> postNotification());

        recyclerView = findViewById(R.id.notificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(this, notificationList, true); // true = admin
        recyclerView.setAdapter(adapter);

        loadNotifications();
    }

    private void postNotification() {
        String title = titleInput.getText().toString();
        String message = messageInput.getText().toString();
        String link = linkInput.getText().toString();
        String date = dateInput.getText().toString();

        if (title.isEmpty() || message.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = UUID.randomUUID().toString();

        HashMap<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("message", message);
        notification.put("link", link);
        notification.put("date", date);
        notification.put("id", id);

        db.collection("notifications").document(id)
                .set(notification)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Notification posted", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    loadNotifications();

                    // 🔔 Send push notification to /topics/all
                    FcmNotificationSender.sendNotification(this, title, message);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to post notification", Toast.LENGTH_SHORT).show());
    }

    private void clearInputs() {
        titleInput.setText("");
        messageInput.setText("");
        linkInput.setText("");
        dateInput.setText("");
    }

    private void loadNotifications() {
        db.collection("notifications").get().addOnSuccessListener(query -> {
            notificationList.clear();
            for (DocumentSnapshot doc : query) {
                NotificationModel model = doc.toObject(NotificationModel.class);
                if (model != null) {
                    model.setId(doc.getId());
                    notificationList.add(model);
                }
            }
            adapter.notifyDataSetChanged();
        });
    }
}
