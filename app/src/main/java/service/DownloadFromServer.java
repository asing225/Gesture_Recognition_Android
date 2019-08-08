package service;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.heartrate_android.UserPage;

import java.io.File;

public class DownloadFromServer extends UserPage {
    private static final int PERMISSION_STORAGE_CODE = 1000 ;

    private Context context;

    public DownloadFromServer(Context context){
        this.context = context;
    }


    public void startDownloading() {

        String url = "http://impact.asu.edu/CSE535Spring19Folder/UploadToServer.php";
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
        request.setDestinationInExternalPublicDir("Android/data/CSE535_ASSIGNMENT2_DOWN","patientDB_team4.db");
        DownloadManager manager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
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






