package com.example.heartrate_android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SensorHandler extends Service implements SensorEventListener{

    private DataBaseAcc manageDB= new DataBaseAcc(this);
    static String titleDB="ID_AGE_NAME_SEX";
    private SensorManager handleSensor;
    private Sensor acc;
    private int sample_rate = 1000000;  // 1 sec
    private String user = "";
    public static String LOG_TAG = "SensorListenerService";
    private long timeStamp;

    @Override
    public void onCreate() {
        //get sensor status and register for updates
        handleSensor = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc = handleSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d(LOG_TAG,"CreateService()");
        if(acc==null)
        {

            Toast.makeText(this,"Accelerometer is not connected!",Toast.LENGTH_LONG);

        }
        handleSensor.registerListener(this,acc,sample_rate);
        Toast.makeText(this,"Service Started",Toast.LENGTH_LONG);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG,"onStartCommand()");
        user = intent.getStringExtra("table_name");
        Toast.makeText(this,"Service Started"+ user,Toast.LENGTH_LONG);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

   
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float xVal = sensorEvent.values[0];
            float yVal = sensorEvent.values[1];
            float zVal = sensorEvent.values[2];
            long timeStamp = System.currentTimeMillis()/1000;
            addToDB(timeStamp,xVal,yVal,zVal);
            }
        }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Most probably not needed
    }


    @Override
    public void onDestroy() {
        handleSensor.unregisterListener(this);
        Log.d(LOG_TAG,"Service Stopped!");
        super.onDestroy();
    }
    //This function is called in the main activity after the user information has been entered in order
    //to set up the database with the correct name
    public static void setDbName(String name)
    {
        titleDB = name;
    }
    //This should be called only after the run button is pressed in the GUI so the correct table
    //name is passed.
    public void addToDB(float timestamp,float x, float y, float z)
    {
        // x, y, and z are values retrieved from the sensor in this class

        //Initialize table using table name
        manageDB.createPatientTable(titleDB);
        //create a timestamp that the x, y, and z values are generated in
        //timestamp=System.currentTimeMillis()/1000;
        //Create an object using the current timestamp, x, y, and z
        UserPatient user = new UserPatient(timeStamp, x, y, z);
        //Send the patient to the database
        manageDB.addHandler(user);
    }
}