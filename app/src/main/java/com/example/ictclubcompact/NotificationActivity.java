package com.example.ictclubcompact;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private ArrayList<NotificationModel> notificationList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.recyclerView);
        notificationList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        firestore.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (result != null && !result.isEmpty()) {
                            notificationList.clear();  // Clear the existing notifications
                            for (DocumentSnapshot doc : result.getDocuments()) {
                                String title = doc.getString("title");
                                String message = doc.getString("message");
                                String date = doc.getString("date"); // Fetch the date
                                String link = doc.getString("link"); // Fetch the link

                                // Add the new notification to the top of the list
                                if (title != null && !title.isEmpty()) {
                                    notificationList.add(0, new NotificationModel(title, message, date, link));
                                }
                            }
                            adapter.notifyDataSetChanged();  // Update the RecyclerView with new data
                        } else {
                            Toast.makeText(this, "No notifications found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("NotificationActivity", "Firestore error: ", task.getException());
                        Toast.makeText(this, "Failed to load notifications.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
