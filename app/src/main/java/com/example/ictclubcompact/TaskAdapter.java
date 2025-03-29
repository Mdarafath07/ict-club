package com.example.ictclubcompact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;
    private DatabaseHelper db;

    public TaskAdapter(Context context, DatabaseHelper db) {
        this.context = context;
        this.db = db;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskText.setText(task.getTask());
        holder.taskCheckBox.setChecked(task.getStatus() == 1);

        // Remove previous listeners to avoid duplicate calls
        holder.taskCheckBox.setOnCheckedChangeListener(null);
        holder.taskCheckBox.setChecked(task.getStatus() == 1);

        holder.taskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Task currentTask = taskList.get(currentPosition);
                    currentTask.setStatus(isChecked ? 1 : 0);
                    db.updateTask(currentTask);
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Task currentTask = taskList.get(currentPosition);
                    db.deleteTask(currentTask.getId());
                    taskList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, taskList.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setTasks(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskCheckBox;
        TextView taskText;
        ImageButton deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            taskText = itemView.findViewById(R.id.taskText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}