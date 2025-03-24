package com.example.ictclubcompact;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ictclubcompact.R;



public class ImageViewActivity extends AppCompatActivity {
    ImageView fulligarrow;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_view);
        ImageView imageView = findViewById(R.id.imageView);

        Glide.with(ImageViewActivity.this).load(getIntent().getStringExtra("image")).into(imageView);


        fulligarrow = findViewById(R.id.fulligarrow);    //back button
        fulligarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }
}

