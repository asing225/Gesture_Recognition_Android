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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import service.DownloadFromServer;
import service.UploadToServer;

public class UserPage extends AppCompatActivity {

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


        Button uploadButton = (Button)findViewById(R.id.uploadButton);
        messageText = findViewById(R.id.messageText);
        final UploadToServer upload = new UploadToServer();



        Button accelerometer =(Button)findViewById(R.id.nextPagebutton);
        // SET THE FILE NAME HERE FROM NARENDRA
        final String filename = "patientDB_team4.db";

        accelerometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

        }
        });

        final String filename = "patientDB_team4.db";

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

