package com.example.ictclubcompact;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageView notification;
    ImageView todo;
    ImageView news;
    ImageView assignment;
    ImageView resources;
    ImageView live;

    TextView regForm;








    @SuppressLint({"NonConstantResourceId", "WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        regForm = findViewById(R.id.regForm);
        notification = findViewById(R.id.notification);
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

        getWindow().setNavigationBarColor(Color.parseColor("#0B2473"));

        // Registration Form intent
        regForm.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Registration.class)));

        // Notification Intent
        notification.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Notification.class)));
        //TODO
        todo = findViewById(R.id.todo);
        todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ToDo.class);
                startActivity(intent);
            }
        });
        //news
        news =findViewById(R.id.news);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), News.class);
                startActivity(intent);
            }
        });
        //assignment
        assignment =findViewById(R.id.assignment);
        assignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Assignment.class);
                startActivity(intent);
            }
        });
        // Resources
        resources =findViewById(R.id.resources);
        resources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Resources.class);
                startActivity(intent);
            }
        });
        //live
        live =findViewById(R.id.live);
        live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LiveClassesActivity.class);
                startActivity(intent);
            }
        });







    }
}