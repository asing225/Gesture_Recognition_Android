package service;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFromServer  {

    private String upLoadServerUri = "http://tamilfreemp3s.com/down.php?file=Tamil%202019%20Songs/Maari%202/128/Rowdy%20Baby.mp3";
    private String sdCardFilePath = "/Android/Data/CSE535_ASSIGNMENT2_DOWN";

    public File getFileFromSDCard(String filename){
        File sdCard = Environment.getExternalStorageDirectory();
        File uploadFile = new File(sdCard, sdCardFilePath+filename);
        return uploadFile;
    }

    public int uploadFile(File sourceFile) throws IOException {

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        int serverResponseCode = 0;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        if (!sourceFile.isFile()) {
            return 0;
        }
        else {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            dos = new DataOutputStream(conn.getOutputStream());
            try {
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", sourceFile.getPath());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + sourceFile.getPath() + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necessary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    return 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
            finally {
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            }
        }
        return 0;
    }
    }






