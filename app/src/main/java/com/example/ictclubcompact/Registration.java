package com.example.ictclubcompact;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.saadahmedev.popupdialog.PopupDialog;

public class Registration extends AppCompatActivity {

    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnSubmit = findViewById(R.id.btnSubmit);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Registration.this, "Clicked", Toast.LENGTH_SHORT).show();

                PopupDialog.getInstance(Registration.this)
                        .statusDialogBuilder()
                        .createSuccessDialog()
                        .setHeading("Well Done")
                        .setDescription("You have successfully Registared")
                        .build(Dialog::dismiss)
                        .show();


            }
        });




    }
}