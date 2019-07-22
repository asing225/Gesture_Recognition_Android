package com.example.heartrate_android;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;

/**
 * @author amanjotsingh
 * @Date - 07/20/2019
 *
 * Description - class to start and stop the app.*/

public class MainActivity extends AppCompatActivity {

    private float[] values = new float[60];
    private String[] verticalLabels = new String[]{"500", "400", "300", "200", "100", "80", "60", "40", "20", "0",};
    private String[] horizontalLabels = new String[]{"0", "10", "20", "30", "40", "50", "60"};
    private GraphView graphView;
    private LinearLayout graph;
    private boolean runnable = false;


    // method to be called on launch of the application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RadioButton male = (RadioButton) findViewById(R.id.male);
        final RadioButton female = (RadioButton) findViewById(R.id.female);

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                female.setChecked(false);
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male.setChecked(false);
            }
        });

        graph = (LinearLayout) findViewById(R.id.graph);
        graphView = new GraphView(this, values, "Health Monitor Graph View", horizontalLabels, verticalLabels, GraphView.LINE);
        graph.addView(graphView);
        runnable = true;
        startDraw.start();

    }

    // to stop the application
    @Override
    public void onDestroy() {
        super.onDestroy();
        runnable = false;
    }

    public void setGraph(int data) {
        for (int i = 0; i < values.length - 1; i++) {
            values[i] = values[i + 1];
        }

        values[values.length - 1] = (float) data;
        graph.removeView(graphView);
        graph.addView(graphView);
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what) {

                case 0x01:
                    int axisVal = (int) (Math.random() * 500) + 1;
                    setGraph(axisVal);
                    break;
            }
        }
    };
    
    public Thread startDraw = new Thread() {
        @Override
        public void run() {
            while (runnable) {
                handler.sendEmptyMessage(0x01);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    };
}

