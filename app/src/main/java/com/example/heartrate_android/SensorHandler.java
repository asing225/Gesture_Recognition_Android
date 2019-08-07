package com.example.heartrate_android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import service.DBConnection;
/*
The following code performs all accelerometer operations
@author Sakshi Gautam
*/
public class SensorHandler extends Service implements SensorEventListener{

    private DBConnection manageDB= new DBConnection();
    static String titleDB="ID_AGE_NAME_SEX";
    private SensorManager handleSensor;
    private Sensor acc;
    private int sample_rate = 1000000;
    private String user = "";
    public static String LOG_TAG = "SensorListenerService";
    private long timeStamp;

    //This function creates a service for accelerometer
    @Override
    public void onCreate() {
        //start sensor
        handleSensor = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc = handleSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d(LOG_TAG,"CreateService()");
        //if sensor is not accelerometer
        if(acc==null)
        {

            Toast.makeText(this,"Accelerometer is not connected..Try again!",Toast.LENGTH_LONG);

        }
        handleSensor.registerListener(this,acc,sample_rate);
        Toast.makeText(this,"Service Started",Toast.LENGTH_LONG);
        super.onCreate();
    }

   //This function starts the service once it senses motion and  starts logging data
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG,"onStartCommand()");
        user = intent.getStringExtra("table_name");
        Toast.makeText(this,"Service started for "+ user,Toast.LENGTH_LONG);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

   //This function adds values to the database only if the accelerometer is the sensor
    @Override
    public void onSensorChanged(SensorEvent eventSense) {

        if (eventSense.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float xVal = eventSense.values[0];
            float yVal = eventSense.values[1];
            float zVal = eventSense.values[2];
            long timeStamp = System.currentTimeMillis()/1000;
            addToDB(timeStamp,xVal,yVal,zVal);
            }
        }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    //this function stops the function for the sensor once the motion has been stopped
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

    //This function is called after the run button is pressed in the GUI so the correct table is called
    public void addToDB(float timestamp,float x, float y, float z)
    {
        UserPatient user = new UserPatient(timeStamp, x, y, z);
        manageDB.findHandler(timeStamp);
        //Send the patient to the database
        manageDB.addHandler(titleDB, user);
    }
}