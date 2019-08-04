package com.example.heartrate_android;

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
   // private String sdCardFilePath = "/Android/Data/CSE535_ASSIGNMENT2_DOWN";
    ProgressDialog dialog = null;
    TextView messageText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Button btn2 = (Button) findViewById(R.id.button);
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

        // SET THE FILE NAME HERE FROM NARENDRA
        final String filename = "";
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

        Button download_Button = (Button)findViewById((R.id.download));

        download_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //DownloadFromServer dfs = new DownloadFromServer();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permissions, PERMISSION_STORAGE_CODE);
                    }

                    else{
                        startDownloading();
                        //dfs.doInBackground();
                    }
                }

                else{
                    startDownloading();
                    //dfs.doInBackground();
                }
            }
        });
    }

    private void startDownloading() {

        //String url = "http://impact.asu.edu/CSE535Spring19Folder/UploadToServer.php";
        String url = "http://tamilfreemp3s.com/down.php?file=Tamil%202019%20Songs/Maari%202/128/Rowdy%20Baby.mp3";
        String path = "sdcard/Android/data/CSE535_ASSIGNMENT2_DOWN";

        File direct = new File(Environment.getExternalStorageDirectory() +
                "Android/data/CSE535_ASSIGNMENT2_DOWN");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        else
            Log.d("error","dir exists");

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download");
        request.setDescription("Downloading File.....");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir("Android/data/CSE535_ASSIGNMENT2_DOWN","test.mp3");
       // request.setDestinationUri(Uri.parse("file://" + path + "/rowdy_baby.mp3"));
//        request.setDestinationUri(Uri.parse(Uri.fromFile(
//                Environment.getExternalStorageDirectory()).toString()
//                + File.separator +"Android/data/CSE535_ASSIGNMENT2_DOWN"));
        DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startDownloading();
                }
                else{
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

