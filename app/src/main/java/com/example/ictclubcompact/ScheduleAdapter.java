package com.example.ictclubcompact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ictclubcompact.model.ScheduleModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private Context context;
    private List<ScheduleModel> scheduleList;
    private boolean isAdmin;

    public ScheduleAdapter(Context context, List<ScheduleModel> scheduleList, boolean isAdmin) {
        this.context = context;
        this.scheduleList = scheduleList;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleModel schedule = scheduleList.get(position);

        holder.subjectText.setText(schedule.getSubject());
        holder.dateText.setText("Date: " + schedule.getDate());
        holder.timeText.setText("Time: " + schedule.getTime());
        holder.mentorText.setText("Mentor: " + schedule.getMentor());
        holder.notesText.setText("Notes: " + schedule.getNotes());

        Glide.with(context)
                .load(schedule.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.scheduleImage);

        if (isAdmin) {
            holder.btnDeleteIcon.setVisibility(View.VISIBLE);
            holder.btnDeleteIcon.setOnClickListener(v -> {
                FirebaseFirestore.getInstance()
                        .collection("schedules")
                        .document(schedule.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                            scheduleList.remove(position);
                            notifyItemRemoved(position);
                        });
            });
        } else {
            holder.btnDeleteIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        ImageView scheduleImage;
        TextView subjectText, dateText, timeText, mentorText, notesText;
        ImageButton btnDeleteIcon;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            scheduleImage = itemView.findViewById(R.id.scheduleImage);
            subjectText = itemView.findViewById(R.id.subjectText);
            dateText = itemView.findViewById(R.id.dateText);
            timeText = itemView.findViewById(R.id.timeText);
            mentorText = itemView.findViewById(R.id.mentorText);
            notesText = itemView.findViewById(R.id.notesText);
            btnDeleteIcon = itemView.findViewById(R.id.btnDeleteIcon);
        }
    }
}