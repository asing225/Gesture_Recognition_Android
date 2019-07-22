package com.example.heartrate_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {

    private float[] values = new float[60];

    private String[] verlabels = new String[]{"600", "500", "400", "300", "200", "100", "80", "60", "40", "20", "0",};
    private String[] horlabels = new String[]{"0", "10", "20", "30", "40", "50", "60"};
    private GraphView graphView;
    private LinearLayout graph;
    private boolean runnable = false;


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

//        float[] values = new float[] { 2.0f,1.5f, 2.5f, 1.0f , 3.0f };
//        String[] verlabels = new String[]{ "great", "ok", "bad" };
//        String[] horlabels = new String[]{"today", "tomorrow", "next week", "next month" };
//        GraphView graphView = new GraphView(this, values, "GraphViewDemo",horlabels, verlabels, GraphView.LINE);
//        setContentView(graphView);

        graph = (LinearLayout) findViewById(R.id.graph);
        graphView = new GraphView(MainActivity.this, values, "TEST GRAPH", horlabels, verlabels, GraphView.LINE);
        graph.addView(graphView);
        runnable = true;
        startDraw.start();

    }

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
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case 0x01:
                    int testValue = (int) (Math.random() * 60) + 1;
                    setGraph(testValue);
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
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    };
}

