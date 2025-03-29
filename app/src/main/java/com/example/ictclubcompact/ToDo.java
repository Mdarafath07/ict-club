package com.example.ictclubcompact;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ToDo extends AppCompatActivity {

    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        db = new DatabaseHelper(this);
        taskList = new ArrayList<>();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, db);
        tasksRecyclerView.setAdapter(taskAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });

        loadAllTasks();
    }

    private void loadAllTasks() {
        taskList = db.getAllTasks();
        taskAdapter.setTasks(taskList);
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        final EditText input = new EditText(this);
        input.setHint("Enter task here");
        builder.setView(input);

        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taskText = input.getText().toString().trim();
                if (!taskText.isEmpty()) {
                    Task newTask = new Task();
                    newTask.setTask(taskText);
                    newTask.setStatus(0); // 0 for not completed, 1 for completed
                    db.insertTask(newTask);
                    loadAllTasks();
                } else {
                    Toast.makeText(ToDo.this, "Task cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}