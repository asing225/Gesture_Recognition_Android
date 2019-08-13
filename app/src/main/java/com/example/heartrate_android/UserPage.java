package com.example.heartrate_android;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import service.DBConnection;
import service.DownloadFromServer;
import service.UploadToServer;

/**
 * @author amanjotsingh
 * Library Used - https://github.com/jjoe64/GraphView/wiki/Summary-and-Features
 * Reference - https://github.com/jjoe64/GraphView/wiki
 *
 * This class handles the button listeners for starting and stopping the accelerometer
 * and plotting the values in graph and also upload and download calls.
 * */

public class UserPage extends AppCompatActivity implements SensorEventListener {

    String tableName;
    String dbFile;
    private static final int PERMISSION_STORAGE_CODE = 1000;
    ProgressDialog dialog = null;
    private SensorManager sensorManager;
    private Sensor accel;
    private float x, y, z = (float) 0.0;
    List<DataPoint> xdataPoints = new ArrayList<>();
    List<DataPoint> ydataPoints = new ArrayList<>();
    List<DataPoint> zdataPoints = new ArrayList<>();
    GraphView visualiser1 = null;
    GraphView visualiser2 = null;
    GraphView visualiser3 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent = getIntent();
        tableName = intent.getStringExtra("tableName");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        dbFile = Environment.getExternalStorageDirectory().toString()
                + "/Android/Data/CSE535_ASSIGNMENT2/testHeart.db";

        Button runBtn = (Button) findViewById(R.id.run);
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRun();
            }
        });

        Button stopBtn = (Button) findViewById(R.id.stop);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStop();
            }
        });

        visualiser2 = (GraphView) findViewById(R.id.visualizer2);
        visualiser1 = (GraphView) findViewById(R.id.visualizer1);
        visualiser3 = (GraphView) findViewById(R.id.visualizer3);

        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        final UploadToServer upload = new UploadToServer();
        final String filename = "patientDB_team4.db";
        final DownloadFromServer download = new DownloadFromServer(this);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = ProgressDialog.show(UserPage.this, "", "Uploading file...", true);

                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                            }
                        });

                        File file = upload.getFileFromSDCard(filename);
                        int result = -1;
                        try {
                            result = upload.uploadFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (result == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        } else if (result == 0) {
                            dialog.dismiss();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    }
                }).start();
                dialog.dismiss();
            }
        });

        Button download_Button = (Button) findViewById((R.id.download));

        download_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_STORAGE_CODE);
                    } else {
                        download.startDownloading();

                    }
                } else {
                    download.startDownloading();

                }
            }
        });
    }

    // Registering the accelerometer
    private void onClickRun() {
        UserPage.super.onResume();
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(getApplicationContext(), "Hi there, you clicked run button", Toast.LENGTH_SHORT).show();
    }

    // stopping accelerometer
    private void onClickStop() {
        UserPage.super.onPause();
        sensorManager.unregisterListener(this);
        Toast.makeText(getApplicationContext(), "Hi there, you clicked stop button", Toast.LENGTH_SHORT).show();
    }

    // setting the accelerometer data into datapoints
    private void refreshView() {
        LineGraphSeries seriesX = new LineGraphSeries();
        for (int i = 0; i < xdataPoints.size(); i++) {
            //series.appendData(new DataPoint(dataPoints.get(i).getX(), dataPoints.get(i).getY()), true, 1000);
            seriesX.appendData(xdataPoints.get(i), true, 100);
        }
        //series.appendData(new DataPoint(Double.parseDouble("0.5"), Double.parseDouble("0.7")), true, 1000);
        seriesX.setColor(Color.RED);
        seriesX.setThickness(9);
        seriesX.setDrawDataPoints(true);
        visualiser1.addSeries(seriesX);


        LineGraphSeries seriesY = new LineGraphSeries();
        for (int i = 0; i < ydataPoints.size(); i++) {
            seriesY.appendData(ydataPoints.get(i), true, 100);
        }
        seriesY.setColor(Color.BLUE);
        seriesY.setThickness(9);
        seriesY.setDrawDataPoints(true);
        visualiser2.addSeries(seriesY);

        LineGraphSeries seriesZ = new LineGraphSeries();
        for (int i = 0; i < zdataPoints.size(); i++) {
            seriesZ.appendData(zdataPoints.get(i), true, 100);
        }
        seriesZ.setColor(Color.GREEN);
        seriesZ.setThickness(9);
        seriesZ.setDrawDataPoints(true);
        visualiser3.addSeries(seriesZ);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        UserPatient pat = new UserPatient(System.currentTimeMillis(), x, y, z);
        xdataPoints.add(new DataPoint(new Date(System.currentTimeMillis()), x));
        ydataPoints.add(new DataPoint(new Date(System.currentTimeMillis()), y));
        zdataPoints.add(new DataPoint(new Date(System.currentTimeMillis()), z));
        //dataPoints.add(new DataPoint((flag++)/10, x));
        refreshView();
        new DBConnection(this, tableName, dbFile).addHandler(tableName, pat);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

