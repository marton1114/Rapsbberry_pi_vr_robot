package com.example.remoteapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Objects;


public class vrActivity extends AppCompatActivity implements SensorEventListener {

    private Socket socket;
    private OutputStream outputStream;
    int port = 5005;    // szenzorok portja


    private SensorManager sensorManager;
    private Sensor rotationVector;
    private Sensor gyroSensor;

    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];

    private float angle = 999;
    private float additionAngle = 0.0f;
    private float globalAngle = 0;

    private float angleRange = 80.0f;
    private float angleMiddle = 0;

    ByteBuffer byteBuffer;
    FloatBuffer floatBuffer;
    private float[] messageArray = {0.0f, 0.0f, 0.0f};
    WebView mywebview0;
    WebView mywebview1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // a Raspberry PI ip-címe
        String ipAddress = getIntent().getExtras().getString("ip");
        String url = "http://" + ipAddress + ":" +
                getIntent().getExtras().getString("port") + "/stream";

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

        mywebview0 = (WebView) findViewById(R.id.vrMenuButton);
        mywebview0.loadUrl(url);
        mywebview1 = (WebView) findViewById(R.id.rightEyeVideo);
        mywebview1.loadUrl(url);
        mywebview0.getSettings().setBuiltInZoomControls(true);
        mywebview1.getSettings().setBuiltInZoomControls(true);

//        WebSettings webSettings = mywebview.getSettings();
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setJavaScriptEnabled(true);


        // szenzor adatainak a küldése

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);

//        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == rotationVector) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            if (angle == 999) {
                angle = (float) Math.toDegrees(orientationAngles[0]) + 180;
            }

            additionAngle = angle - ((float) Math.toDegrees(orientationAngles[0]) + 180);

            if ((globalAngle + additionAngle) < 90 && (globalAngle + additionAngle) > -90) {
                globalAngle += additionAngle;
            }
            Log.d("ELTOL", Math.round(additionAngle) + "");
            Log.d("GLOBA", Math.round(globalAngle) + "");

            angle = (float) Math.toDegrees(orientationAngles[0]) + 180;

            messageArray[0] = globalAngle;

       }

        byteBuffer = ByteBuffer.allocate(messageArray.length * 4); // 4 bytes per float
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(messageArray);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    outputStream.write(byteBuffer.array());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}