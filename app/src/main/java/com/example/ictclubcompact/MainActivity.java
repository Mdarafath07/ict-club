package com.example.ictclubcompact;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    LinearLayout adminpanal;
    private ImageView notification, todo, news, assignment, resources, live, profile, helpem, schedule;
    private TextView regForm;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String CHANNEL_ID = "ict_club_notifications";
    private static final String PREF_NAME = "FCM_Prefs";
    private static final String KEY_TOKEN = "fcm_token";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        initializeViews();
        checkAdminStatus(currentUser.getUid());
        setupRecyclerView();
        setupClickListeners();
        setupFirebaseMessaging();
        createNotificationChannel();
        getWindow().setNavigationBarColor(Color.parseColor("#0B2473"));

        handleNotificationIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            String screen = intent.getStringExtra("screen");
            if (screen != null) {
                switch (screen) {
                    case "todo": startActivity(new Intent(this, ToDo.class)); break;
                    case "news": startActivity(new Intent(this, News.class)); break;
                    case "assignment": startActivity(new Intent(this, Assignment.class)); break;
                    case "resources": startActivity(new Intent(this, Resources.class)); break;
                    case "profile": startActivity(new Intent(this, ProfileActivity.class)); break;
                    case "live": startActivity(new Intent(this, LiveClassesActivity.class)); break;
                    case "help": startActivity(new Intent(this, HelpActivity.class)); break;
                }
            }
        }
    }

    private void setupFirebaseMessaging() {
        FirebaseMessaging.getInstance().subscribeToTopic("general");
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                if (token != null && !token.isEmpty()) {
                    saveTokenToPrefs(token);
                }
            }
        });
    }

    private void saveTokenToPrefs(String token) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        preferences.edit().putString(KEY_TOKEN, token).apply();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "ICT Club Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for ICT Club notifications");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
        schedule = findViewById(R.id.schedule);
        adminpanal = findViewById(R.id.adminpanal);

        // Default: hide admin panel
        adminpanal.setVisibility(LinearLayout.GONE);
    }

    private void checkAdminStatus(String uid) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && Boolean.TRUE.equals(documentSnapshot.getBoolean("isAdmin"))) {
                        adminpanal.setVisibility(LinearLayout.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Admin check failed", Toast.LENGTH_SHORT).show();
                });
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
        regForm.setOnClickListener(v -> startActivity(new Intent(this, Registration.class)));
        todo.setOnClickListener(v -> startActivity(new Intent(this, ToDo.class)));
        news.setOnClickListener(v -> startActivity(new Intent(this, News.class)));
        assignment.setOnClickListener(v -> startActivity(new Intent(this, Assignment.class)));
        resources.setOnClickListener(v -> startActivity(new Intent(this, Resources.class)));
        live.setOnClickListener(v -> startActivity(new Intent(this, LiveClassesActivity.class)));
        profile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        helpem.setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));
        schedule.setOnClickListener(v -> startActivity(new Intent(this, ScheduleActivity.class)));
        notification.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        adminpanal.setOnClickListener(v -> startActivity(new Intent(this, Adminpanal.class)));
    }
}
