package com.example.ictclubcompact;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    TextView regForm;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        regForm = findViewById(R.id.regForm);


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

        adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onClick(ImageView imageView, String path) {
                Intent intent = new Intent(MainActivity.this, ImageViewActivity.class);
                intent.putExtra("image", path);
                startActivity(intent);

            }
        });



        getWindow().setNavigationBarColor(Color.parseColor("#0B2473"));


        View sheet = findViewById(R.id.sheets);
        if (sheet != null) {
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(sheet);
            bottomSheetBehavior.setPeekHeight(200);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }


        //Registration Form intent
        regForm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Registration.class);
                startActivity(intent);


            }
        });




    }

}
