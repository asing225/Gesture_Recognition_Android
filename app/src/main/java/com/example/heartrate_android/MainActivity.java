package com.example.heartrate_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.util.Random;


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


    SQLiteDatabase db;
    Context context;
    UserDBHelper userDBHelper;
    String Name;
    String ID;
    String Age;
    String Gender;
    String Table_Name;



    //This part handles the button backend (Event handlers) and Data Initialization for Graph
    //@author Narendra Mohan Murali Mohan and Amanjot Singh
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.runBtn).setOnClickListener(this);
        findViewById(R.id.stopBtn).setOnClickListener(this);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        EditText ID_text = (EditText) findViewById(R.id.et_patientid);
        ID = ID_text.toString();
        EditText Age_Text = (EditText) findViewById((R.id.et_age));
        Age = Age_Text.toString();
        EditText Name_Text = (EditText) findViewById(R.id.et_patientName);
        Name = Name_Text.toString();
        RadioButton male = (RadioButton) findViewById(R.id.btn_male);
        RadioButton female = (RadioButton) findViewById((R.id.btn_female));

        if(male.isChecked()){
            Gender = male.toString();
        }

        else{
            Gender = female.toString();
        }

        Table_Name = Name + ID + Age + Gender;

        Button SecondPageButton = (Button)findViewById(R.id.nextPagebutton);
        SecondPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserPage.class);
                startActivity(intent);

                NewUserContact nc = new NewUserContact();
                nc.createDB();

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int in) {
                View radioBtn = radioGroup.findViewById(in);
                int flag = radioGroup.indexOfChild(radioBtn);
                switch (flag) {
                    case 0:
                        break;
                    default:
                        break;
                }
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
    private class MyRunnable implements Runnable{
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
}