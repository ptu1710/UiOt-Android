package com.ixxc.uiot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.textfield.TextInputLayout;
import com.ixxc.uiot.API.APIManager;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    String device_id;
    LineChart lineChart;
    AutoCompleteTextView act_attributeName, act_timeframe;
    Button btn_show_chart;
    RelativeLayout layout_below;
    String attrName ="";
    String timeFrame ="";
    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("CALL_OK");

        if (isOK) {
            showChart();
        }
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        device_id = getIntent().getStringExtra("DEVICE_ID");

        initView();
        initEvent();
    }

    private void initEvent() {
        String[] attr = getResources().getStringArray(R.array.attribute_name);
        ArrayAdapter<String> attr_adapter = new ArrayAdapter<String>(this,R.layout.dropdown_item,attr);

        String[] time = getResources().getStringArray(R.array.timeframe);
        ArrayAdapter<String> time_adapter = new ArrayAdapter<String>(this,R.layout.dropdown_item,time);

        act_attributeName.setAdapter(attr_adapter);
        act_attributeName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                layout_below.setVisibility(View.VISIBLE);
                btn_show_chart.performClick();
                attrName= adapterView.getItemAtPosition(i).toString().split(" ")[0].toLowerCase();

            }
        });

        act_timeframe.setAdapter(time_adapter);
        act_timeframe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                timeFrame = adapterView.getItemAtPosition(i).toString().toUpperCase();

            }
        });

        btn_show_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(()->{
                    APIManager.getDatapoint(device_id,"humidity");

                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("CALL_OK", true);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }).start();


            }
        });
    }

    private void showChart() {
        List<Entry> lineValues =new ArrayList<Entry>();
        int i = 1;
        while (i<=10){
            lineValues.add(new Entry(i,i*5));
            i++;
        }

        LineDataSet linedataset = new LineDataSet(lineValues, "LABLE");
        linedataset.setDrawValues(false);
        linedataset.setLineWidth(3f);
        linedataset.setDrawFilled(true);
            linedataset.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        linedataset.setValueTextColor(getApplicationContext().getColor(R.color.lightBlue_10));
        linedataset.setColor(getApplicationContext().getColor(R.color.lightBlue_10));
        linedataset.setCircleColor(getApplicationContext().getColor(R.color.lightBlue_10));
        linedataset.setFillColor(getApplicationContext().getColor(R.color.lightBlue_20));

        LineData data = new LineData(linedataset);
        lineChart.zoom(0, 0, 0, 0);
        lineChart.setData(data);
        lineChart.setNoDataText("No data");
        lineChart.getDescription().setText("description");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setGranularity(1f);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.animateXY(800, 1000);
        lineChart.setVisibility(View.VISIBLE);
        lineChart.invalidate();

    }

    private void initView() {
        lineChart = (LineChart) findViewById(R.id.lineChart);
        btn_show_chart = (Button) findViewById(R.id.btn_showChart);
        act_attributeName = (AutoCompleteTextView) findViewById(R.id.act_attributeName);
        act_timeframe = (AutoCompleteTextView) findViewById(R.id.act_timeFrame);
        layout_below= (RelativeLayout) findViewById(R.id.layout_chart);
    }
}