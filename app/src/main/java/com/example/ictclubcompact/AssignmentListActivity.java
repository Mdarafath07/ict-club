package com.example.ictclubcompact;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;



import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ictclubcompact.model.Assignment;
import com.example.ictclubcompact.AssignmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class AssignmentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        assignmentList = new ArrayList<>();
        assignmentList.add(new Assignment("1", "make menu ber", "Due: 15th June"));
        assignmentList.add(new Assignment("2", "home page", "Due: 20th June"));
        assignmentList.add(new Assignment("3", "loging page", "Due: 25th June"));

        adapter = new AssignmentAdapter(assignmentList, this);
        recyclerView.setAdapter(adapter);
    }
}