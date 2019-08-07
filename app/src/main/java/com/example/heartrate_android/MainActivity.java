
package com.example.heartrate_android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

import java.io.File;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.Random;

import service.DBConnection;


/**
 * HeartRate_Android code was written by the Team4
 * Mainly aimed to take inputs from patient- id, name, age and Sex
 * and graph is plotted displaying their heart rate
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private GraphView graphDisplay;
    private float[] graphPlotValues;
    private boolean graphMove = true;
    private Handler graphControlHandler = new Handler();
    private int plotRefresh = 0;
    private MyRunnable runnableGraph;
    private final int interval = 8;


    private EditText patientID;
    private EditText age;
    private EditText name;
    private RadioGroup radioGroup;

    private String idText;
    private String ageText;
    private String nameText;
    private String sex;
    private String tableName;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    //This part handles the button backend (Event handlers) and Data Initialization for Graph
    //@author Narendra Mohan Murali Mohan and Amanjot Singh
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.runBtn).setOnClickListener(this);
        findViewById(R.id.stopBtn).setOnClickListener(this);

        patientID = (EditText) findViewById(R.id.et_patientId);
        age = (EditText) findViewById(R.id.et_age);
        name = (EditText) findViewById(R.id.patientName);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int in) {
                View radioBtn = radioGroup.findViewById(in);
                int flag = radioGroup.indexOfChild(radioBtn);
                switch (flag) {
                    case 0:
                        sex = "male";
                        break;
                    case 1:
                        sex = "female";
                        break;
                }
            }
        });

        final DBConnection conn = new DBConnection();
        Button createDBButton = (Button) findViewById(R.id.uploadToDB);
        Context appContext = this.getApplicationContext();
        verifyStoragePermissions(this);
        createDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idText = patientID.getText().toString();
                ageText = age.getText().toString();
                nameText = name.getText().toString();
                tableName = nameText + "_" + idText + "_" + ageText + "_" + sex;
                if(!idText.equals("")){
                    if(!ageText.equals("")){
                        if(!nameText.equals("")){
                            if(sex != null){
                                try{
                                    conn.createDB(tableName, new File(Environment.getExternalStorageDirectory().toString() + "/Android/data" ));//appContext.getExternalFilesDir(""));
                                }
                                catch(Exception e){
                                    Toast.makeText(MainActivity.this, "Error occurred while creating DB"
                                            , Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Please choose a Sex", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please enter a Name", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Please enter a Age", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Please enter an ID", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button secondPageButton = (Button)findViewById(R.id.nextPagebutton);
        secondPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserPage.class);
                startActivity(intent);
            }
        });

        FrameLayout graphVisualizer = (FrameLayout)findViewById(R.id.visualizer);
        graphPlotValues = new float[50];
        String[] labelHorizontal = new String[]{"100", "200", "300", "400", "500"};
        String[] labelVertical = new String[]{"100", "200", "300", "400", "500"};
        graphDisplay = new GraphView(this, graphPlotValues, "GraphicView of the Team4", labelHorizontal, labelVertical, true);
        graphVisualizer.addView(graphDisplay);
    }

    //This method has a switch between the Run and Stop buttons
    //@autgor Manisha Miriyala
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.runBtn:
                onClickRun();
                break;
            case R.id.stopBtn:
                onClickStop();
                break;
            default:
        }
    }

    //This method takes care of the logic of the Run button
    //@ Manisha Miriyala
    private void onClickRun(){
        dataGeneration();
        Toast.makeText(this, "Graph Started", Toast.LENGTH_SHORT).show();
    }
    //This method takes care of the logic of the Stop button
    //@author Sakshi Gautam
    private void onClickStop(){
        clearView();
        Toast.makeText(this, "Graph Stopped", Toast.LENGTH_SHORT).show();
        graphMove = false;

    }

    //This method clears the graph using the stop functionality
    //@author Sakshi Gautam
    private void clearView(){
        graphDisplay.setValues(new float[0]);
        graphDisplay.invalidate();
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
    private void refreshData(){
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
    private void graphRefresh(){
        refreshData();
        graphDisplay.setValues(graphPlotValues);
        graphDisplay.invalidate();
    }

    //this method will ask for user permissions for read/write
    //@author Amanjot Singh
    public static void verifyStoragePermissions(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}