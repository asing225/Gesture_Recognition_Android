package com.example.heartrate_android;


import android.app.Activity;
import android.app.ProgressDialog;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import service.DownloadFromServer;
import service.UploadToServer;

public class UserPage extends AppCompatActivity {
    private GraphView graphDisplay;
    private float[] graphPlotValues;
    private boolean graphMove = true;
    private Handler graphControlHandler = new Handler();
    private int plotRefresh = 0;
    private MainActivity.MyRunnable runnableGraph;
    private final int interval = 8;
    private static final int PERMISSION_STORAGE_CODE = 1000 ;
    ProgressDialog dialog = null;
    TextView messageText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Button btn2 = (Button) findViewById(R.id.button);
        this.getApplicationContext();

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previousPage = new Intent(UserPage.this, MainActivity.class);
                startActivity(previousPage);
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
        graphDisplay = new GraphView(this, graphPlotValues, "GraphicView of the Team4", labelHorizontal, labelVertical, true);
        graphVisualizer1.addView(graphDisplay);


        FrameLayout graphVisualizer2 = (FrameLayout)findViewById(R.id.visualizer3);
        graphPlotValues = new float[50];
        String[] labelHorizontal2 = new String[]{"100", "200", "300", "400", "500"};
        String[] labelVertical2 = new String[]{"100", "200", "300", "400", "500"};
        graphDisplay = new GraphView(this, graphPlotValues, "GraphicView of the Team4", labelHorizontal, labelVertical, true);
        graphVisualizer2.addView(graphDisplay);


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

}

