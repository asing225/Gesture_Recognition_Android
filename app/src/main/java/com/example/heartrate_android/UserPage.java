package com.example.heartrate_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import service.UploadToServer;

public class UserPage extends AppCompatActivity {

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
    }
}

