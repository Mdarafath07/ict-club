package com.example.ictclubcompact;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageView notification, todo, news, assignment, resources, live, profile, helpem;
    private TextView regForm;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        initializeViews();
        setupRecyclerView();
        setupClickListeners();

        // Set navigation bar color
        getWindow().setNavigationBarColor(Color.parseColor("#0B2473"));
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void initializeViews() {
        regForm = findViewById(R.id.regForm);
        notification = findViewById(R.id.notification);
        todo = findViewById(R.id.todo);
        news = findViewById(R.id.news);
        assignment = findViewById(R.id.assignment);
        resources = findViewById(R.id.resources);
        live = findViewById(R.id.live);
        profile = findViewById(R.id.profile);
        helpem = findViewById(R.id.helpem);


    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("https://i.pinimg.com/736x/19/9f/f3/199ff3ef358f2b5cbcf4f06213a7a16f.jpg");
        arrayList.add("https://i.pinimg.com/736x/b4/f5/52/b4f552deedaae2f1a4689dc048c56f77.jpg");
        arrayList.add("https://i.pinimg.com/736x/30/71/d0/3071d0dbceaaa54e3a2e6aefd93fad92.jpg");
        arrayList.add("https://i.pinimg.com/736x/4b/e4/75/4be4754a3f2c1e3f07c4bf4e66881895.jpg");
        arrayList.add("https://i.pinimg.com/736x/5a/41/3e/5a413e21dda6862a9279261bc70fb2f0.jpg");
        arrayList.add("https://i.pinimg.com/736x/8b/b8/43/8bb8434bea64536cb0f21b8f44c7dfc5.jpg");

        ImageAdapter adapter = new ImageAdapter(MainActivity.this, arrayList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((imageView, path) -> {
            Intent intent = new Intent(MainActivity.this, ImageViewActivity.class);
            intent.putExtra("image", path);
            startActivity(intent);
        });
    }

    private void setupClickListeners() {

        // Registration Form
        regForm.setOnClickListener(v -> startActivity(new Intent(this, Registration.class)));

        // Notification
        notification.setOnClickListener(v -> startActivity(new Intent(this, Notification.class)));

        // ToDo
        todo.setOnClickListener(v -> startActivity(new Intent(this, ToDo.class)));

        // News
        news.setOnClickListener(v -> startActivity(new Intent(this, News.class)));

        // Assignment
        assignment.setOnClickListener(v -> startActivity(new Intent(this, Assignment.class)));

        // Resources
        resources.setOnClickListener(v -> startActivity(new Intent(this, Resources.class)));

        // Live Classes
        live.setOnClickListener(v -> startActivity(new Intent(this, LiveClassesActivity.class)));

        // Profile
        profile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        //help email
        helpem.setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
        }
    }
}