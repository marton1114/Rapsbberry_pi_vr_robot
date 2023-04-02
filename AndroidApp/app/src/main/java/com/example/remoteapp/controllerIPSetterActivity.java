package com.example.remoteapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class controllerIPSetterActivity extends AppCompatActivity {
    Button connectButton;
    EditText ipET0;
    EditText ipET1;
    EditText ipET2;
    EditText ipET3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_ip_setter);

        Objects.requireNonNull(getSupportActionBar()).hide();

        connectButton = findViewById(R.id.connectButton0);

        ipET0 = findViewById(R.id.ipEditText00);
        ipET1 = findViewById(R.id.ipEditText10);
        ipET2 = findViewById(R.id.ipEditText20);
        ipET3 = findViewById(R.id.ipEditText30);

        connectButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, controllerActivity.class);
            intent.putExtra("ip",
                    ipET0.getText().toString() + "." +
                            ipET1.getText().toString() + "." +
                            ipET2.getText().toString() + "." +
                            ipET3.getText().toString());
            startActivity(intent);
        });
    }
}