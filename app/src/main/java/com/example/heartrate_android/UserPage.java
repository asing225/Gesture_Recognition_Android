package com.example.heartrate_android;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import service.DBConnection;
import service.DownloadFromServer;
import service.UploadToServer;



public class UserPage extends AppCompatActivity {
    private GraphView graphDisplay;
    private GraphView graphDisplay1;
    private GraphView graphDisplay2;
    private float[] graphPlotValues;
    private static boolean graphMove = true;
    private static Handler graphControlHandler = new Handler();
    private int plotRefresh = 0;
    public MyRunnable runnableGraph;
    private final int interval = 8;
    //MainActivity tname;
    String tableName;
    private static final int PERMISSION_STORAGE_CODE = 1000 ;
    ProgressDialog dialog = null;
    TextView messageText = null;
    List<UserPatient> patientValues;

    private float x,y,z = (float) 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent = getIntent();
        tableName = intent.getStringExtra("tableName");
        DBConnection dbc= new DBConnection();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                UserPatient pat = new UserPatient(System.currentTimeMillis(), x, y, z);
              /*  try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                dbc.addHandler(tableName, pat);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        Button runBtn =(Button)findViewById(R.id.run);
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                onClickRun();
            }

            private void onClickRun() {
                //dataGeneration();
                //tname = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
                patientValues= dbc.getValues(tableName);
                Toast.makeText(getApplicationContext(), "Hi there, you clicked run button", Toast.LENGTH_SHORT).show();
            }
        });

        Button stopBtn =(Button)findViewById(R.id.stop);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onClickStop();
            }

            private void onClickStop() {
                clearView();
                Toast.makeText(getApplicationContext(), "Hi there, you clicked stop button", Toast.LENGTH_SHORT).show();
                graphMove = false;
            }


        });


        FrameLayout graphVisualizer = (FrameLayout)findViewById(R.id.visualizer1);
        graphPlotValues = new float[50];
        String[] labelHorizontal = new String[]{"100", "200", "300", "400", "500"};
        String[] labelVertical = new String[]{"100", "200", "300", "400", "500"};
        graphDisplay = new GraphView(this, graphPlotValues, "GraphicView of the Team4", labelHorizontal, labelVertical, true);
        graphVisualizer.addView(graphDisplay);
        Button uploadButton = (Button)findViewById(R.id.uploadButton);

        final UploadToServer upload = new UploadToServer();

        FrameLayout graphVisualizer1 = (FrameLayout)findViewById(R.id.visualizer2);
        graphPlotValues = new float[50];
        String[] labelHorizontal1 = new String[]{"100", "200", "300", "400", "500"};
        String[] labelVertical1 = new String[]{"100", "200", "300", "400", "500"};
        graphDisplay1 = new GraphView(this, graphPlotValues, "GraphicView of the Team4", labelHorizontal, labelVertical, true);
        graphVisualizer1.addView(graphDisplay1);


        FrameLayout graphVisualizer2 = (FrameLayout)findViewById(R.id.visualizer3);
        graphPlotValues = new float[50];

        String[] labelHorizontal2 = new String[]{"100", "200", "300", "400", "500"};
        String[] labelVertical2 = new String[]{"100", "200", "300", "400", "500"};
        graphDisplay2 = new GraphView(this, graphPlotValues, "GraphicView of the Team4", labelHorizontal, labelVertical, true);
        graphVisualizer2.addView(graphDisplay2);


        // SET THE FILE NAME HERE FROM NARENDRA
        final String filename = "patientDB_team4.db";

       /* Button accelerometer =(Button)findViewById(R.id.nextPagebutton);


        accelerometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

        }
        });*/

        //final String filename = "patientDB_team4.db";

        final DownloadFromServer download= new DownloadFromServer(this);


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = ProgressDialog.show(UserPage.this, "", "Uploading file...", true);

                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                messageText.setText("uploading started.....");
                            }
                        });

                        File file = upload.getFileFromSDCard(filename);
                        int result = -1;
                        try {
                            result = upload.uploadFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(result == 1){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageText.setText("Upload Complete.");
                                }
                            });
                        }
                        else if(result == 0){
                            dialog.dismiss();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageText.setText("There was some error uploading file. Please try again.");
                                }
                            });
                        }
                    }
                }).start();
                dialog.dismiss();
            }
        });

        Button download_Button = (Button)findViewById((R.id.download));


        download_Button.setOnClickListener(new View.OnClickListener() {

            String nameOfTable = tableName;
           // patientValues = dbc.getxyz(tableName);


            @Override
            public void onClick(View view) {


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permissions, PERMISSION_STORAGE_CODE);
                    }

                    else{
                        download.startDownloading();

                    }
                }

                else{
                    download.startDownloading();

                }
            }
        });
    }

    //This method clears the graph using the stop functionality
    //@author Sakshi Gautam
    private void clearView(){
        graphDisplay.setValues(new float[0]);
        graphDisplay.invalidate();
        graphDisplay1.setValues(new float[0]);
        graphDisplay1.invalidate();
        graphDisplay2.setValues(new float[0]);
        graphDisplay2.invalidate();
    }
    //This method generates the initial data for the graph
    //@author Amanjot
    private void dataGeneration(){
        if (graphMove) {
            graphControlHandler.removeCallbacks(runnableGraph);
            graphPlotValues = new float[0];
            plotRefresh = 50;
        }
        runnableGraph = new MyRunnable();
        graphMove = true;
        graphControlHandler.post(runnableGraph);

    }
    //This method ensures the graph is moving using runnable
    //@author Amanjot
    public class MyRunnable implements Runnable{
        @Override
        public void run() {
            if(graphMove){
                graphRefresh();
                graphControlHandler.postDelayed(this, 150);
            }
        }
    }
    //This method refreshes the data when the run button is called again
    //@author Narendra Mohan Murali Mohan
    public void refreshData(){
        Random randomData = new Random();
        final int N = 50;
        float[] val = new float[N];
        final int minimum_Step_Value = 2;
        final int maximum_start_value = 10;
        if(graphPlotValues == null || graphPlotValues.length == 0){
            // Initializing
            for(int i = 0; i < N - 1; i++){
                val[i] = randomData.nextInt(minimum_Step_Value);
            }
            val[N - 1] = randomData.nextInt(minimum_Step_Value) + maximum_start_value;
        }else{

            final int increment_value = 5;
            for(int i = 0; i < N - increment_value; i++){
                val[i] = graphPlotValues[i + increment_value];
            }
            for(int i = N - increment_value; i< N - 1; i++){
                val[i] = randomData.nextInt(minimum_Step_Value);
            }
            if(plotRefresh % interval == 0){
                val[N - 1] = randomData.nextInt(minimum_Step_Value) + maximum_start_value;
                plotRefresh = 0;
            }else{
                val[N - 1] = randomData.nextInt(1);
            }
        }
        graphPlotValues = val;
        plotRefresh++;
    }

    //This method refreshes the graph after if run is pressed multiple times
    //@author Narendra Mohan Murali Mohan
    public void graphRefresh(){
        refreshData();
        graphDisplay.setValues(graphPlotValues);
        graphDisplay.invalidate();
        graphDisplay1.setValues(graphPlotValues);
        graphDisplay1.invalidate();
        graphDisplay2.setValues(graphPlotValues);
        graphDisplay2.invalidate();
    }


}

