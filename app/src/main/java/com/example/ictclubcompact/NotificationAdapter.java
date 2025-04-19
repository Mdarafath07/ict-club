package com.example.ictclubcompact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ictclubcompact.model.NotificationModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final List<NotificationModel> notificationList;
    private final boolean isAdmin;

    public NotificationAdapter(Context context, List<NotificationModel> notificationList, boolean isAdmin) {
        this.context = context;
        this.notificationList = notificationList;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel model = notificationList.get(position);

        holder.textTitle.setText(model.getTitle());
        holder.textMessage.setText(model.getMessage());
        holder.textDate.setText(model.getDate());

        if (model.getLink() != null && !model.getLink().isEmpty()) {
            holder.textLink.setVisibility(View.VISIBLE);
            holder.textLink.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getLink()));
                context.startActivity(browserIntent);
            });
        } else {
            holder.textLink.setVisibility(View.GONE);
        }

        // Show delete button only if admin
        if (isAdmin) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                FirebaseFirestore.getInstance()
                        .collection("notifications")
                        .document(model.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Notification deleted", Toast.LENGTH_SHORT).show();
                            notificationList.remove(position);
                            notifyItemRemoved(position);
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show());
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textMessage, textDate, textLink;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textMessage = itemView.findViewById(R.id.textMessage);
            textDate = itemView.findViewById(R.id.textDate);
            textLink = itemView.findViewById(R.id.textLink);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
