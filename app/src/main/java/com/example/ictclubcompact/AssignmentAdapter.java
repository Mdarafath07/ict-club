package com.example.ictclubcompact;
import com.example.ictclubcompact.model.Assignment;
import com.example.ictclubcompact.AssignmentAdapter;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ictclubcompact.model.Assignment;
import com.example.ictclubcompact.SubmitAssignmentActivity;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    private List<Assignment> assignmentList;
    private Context context;

    public AssignmentAdapter(List<Assignment> assignmentList, Context context) {
        this.assignmentList = assignmentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);
        holder.title.setText(assignment.getTitle());
        holder.dueDate.setText(assignment.getDueDate());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SubmitAssignmentActivity.class);
            intent.putExtra("assignment_id", assignment.getId());
            intent.putExtra("assignment_title", assignment.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, dueDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.assignmentTitle);
            dueDate = itemView.findViewById(R.id.assignmentDueDate);
        }
    }
}