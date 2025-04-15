package com.example.ictclubcompact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private ArrayList<NotificationModel> notificationList;

    public NotificationAdapter(ArrayList<NotificationModel> notificationList) {
        this.notificationList = notificationList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, messageTextView, dateTextView, linkTextView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.notification_title);
            messageTextView = view.findViewById(R.id.notification_message);
            dateTextView = view.findViewById(R.id.notification_date);
            linkTextView = view.findViewById(R.id.notification_link);
        }
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);
        holder.titleTextView.setText(notification.getTitle());
        holder.messageTextView.setText(notification.getMessage());
        holder.dateTextView.setText(notification.getDate());  // Set the date
        holder.linkTextView.setText(notification.getLink());  // Set the link
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}
