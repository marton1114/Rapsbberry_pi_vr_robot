package com.example.remoteapp;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    LinearLayout vrButton;
    LinearLayout controllerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.INTERNET}, 124);

        vrButton = findViewById(R.id.vrMenuButton);
        controllerButton = findViewById(R.id.controllerMenuButton);

        vrButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, vrIPSetterActivity.class);
            startActivity(intent);
        });

        controllerButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, controllerIPSetterActivity.class);
            startActivity(intent);
        });
    }


}