package com.example.remoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Objects;

public class controllerActivity extends AppCompatActivity {

    private Socket socket;
    private OutputStream outputStream;
    int port = 5005;    // szenzorok portja

    float[] directionVector = { 0, 0, 0 };

    Button upButton, downButton, leftButton, rightButton;
    WebView mywebview;
    SeekBar seekBar;

    void sendData() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(directionVector.length * 4); // 4 bytes per float
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(directionVector);
//        String finalMessage = directionVector[0] + ";" + directionVector[1] + ";" + directionVector[2];
        //                    ^servo ebben az activity-ben mindig 0
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    outputStream.write(byteBuffer.array());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String ipAddress = getIntent().getExtras().getString("ip");

        // kamera képe
        mywebview = (WebView) findViewById(R.id.cameraWebView);
        String url = "http://" + ipAddress + ":" + "8080" + "/stream";
        mywebview.loadUrl(url);
        mywebview.getSettings().setBuiltInZoomControls(true);

        // socket
        new Thread(new Runnable() {
            @Override
            public void run() {
                // socket megnyitása
                try {
                    socket = new Socket(ipAddress, port);
                    outputStream = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        seekBar = findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                directionVector[0] = -1 * (i - 80.0f);
                sendData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        upButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    directionVector[2] = 1.0f;
                    sendData();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    directionVector[2] = 0.0f;
                    sendData();
                }

                return true;
            }
        });
        downButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    directionVector[2] = -1.0f;
                    sendData();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    directionVector[2] = 0.0f;
                    sendData();
                }

                return true;
            }
        });
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    directionVector[1] = -1.0f;
                    sendData();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    directionVector[1] = 0.0f;
                    sendData();
                }

                return true;
            }
        });
        rightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    directionVector[1] = 1.0f;
                    sendData();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    directionVector[1] = 0.0f;
                    sendData();
                }

                return true;
            }
        });


    }
}