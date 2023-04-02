package com.example.remoteapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class vrIPSetterActivity extends AppCompatActivity {

    Button connectButton;
    EditText ipET0;
    EditText ipET1;
    EditText ipET2;
    EditText ipET3;
    EditText portET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vr_ip_setter);

        Objects.requireNonNull(getSupportActionBar()).hide();

        connectButton = findViewById(R.id.connectButton);

        ipET0 = findViewById(R.id.ipEditText0);
        ipET1 = findViewById(R.id.ipEditText1);
        ipET2 = findViewById(R.id.ipEditText2);
        ipET3 = findViewById(R.id.ipEditText3);

        portET = findViewById(R.id.portEditText);

        connectButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, vrActivity.class);
            intent.putExtra("ip",
                    ipET0.getText().toString() + "." +
                            ipET1.getText().toString() + "." +
                            ipET2.getText().toString() + "." +
                            ipET3.getText().toString());
            intent.putExtra("port", portET.getText().toString());
            startActivity(intent);
        });
    }
}