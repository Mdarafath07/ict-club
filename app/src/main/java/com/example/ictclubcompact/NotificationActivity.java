package com.example.ictclubcompact;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ictclubcompact.model.NotificationModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<NotificationModel> notificationList;
    private NotificationAdapter adapter;
    private FirebaseFirestore db;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // ✅ Get isAdmin status passed from intent
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        recyclerView = findViewById(R.id.notificationRecycler);
        progressBar = findViewById(R.id.notificationProgressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(this, notificationList, isAdmin);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadNotifications();
        setupSwipeToDelete();
    }

    private void loadNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("notifications").get().addOnSuccessListener(queryDocumentSnapshots -> {
            notificationList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                NotificationModel model = doc.toObject(NotificationModel.class);
                if (model != null) {
                    model.setId(doc.getId());
                    notificationList.add(model);
                }
            }
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            Log.e("NotificationActivity", "Error loading notifications", e);
        });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (!isAdmin) {
                    Toast.makeText(NotificationActivity.this, "You are not allowed to delete", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    return;
                }

                int position = viewHolder.getAdapterPosition();
                NotificationModel model = notificationList.get(position);

                db.collection("notifications").document(model.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            notificationList.remove(position);
                            adapter.notifyItemRemoved(position);
                            Toast.makeText(NotificationActivity.this, "Notification deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(NotificationActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show());
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }
}
