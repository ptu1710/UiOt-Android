package com.ixxc.uiot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Model.DataPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    String device_id;
    LineChart lineChart;
    AutoCompleteTextView act_attributeName, act_ending, act_timeframe;
    Button btn_show_chart;
    RelativeLayout layout_below;
    String attrName, timeFrame;
    ImageButton btn_date;
    String date, time_12,time_24;
    Integer year,month,day,hour,minute;
    Long to, dis;
    DataPoint dataPoint= new DataPoint();

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
        SetTimeDefault();

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
                String timeFra = adapterView.getItemAtPosition(i).toString();
                switch (timeFra){
                    case "Hour":
                        timeFrame = "MINUTE";
                        dis = 3600000L;
                        break;
                    case "Day":
                        dis = 86400000L;
                        timeFrame = "HOUR";
                        break;
                    case "Week":
                        dis = 604800000L;
                        timeFrame = "HOUR";
                        break;
                    case "Month":
                        dis = 2678400000L;
                        timeFrame = "DAY";
                        break;
                    case "Year":
                        dis = 31536000000L;
                        timeFrame = "MONTH";
                        break;
                }

            }
        });

        btn_show_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(()->{
                    APIManager.getDatapoint(device_id,attrName,timeFrame,to-dis, to);

                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("CALL_OK", true);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }).start();


            }
        });

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        act_ending.setText(new SimpleDateFormat("dd/MM/yyyy' 'KK:mm' 'a").format(new Date()));
    }

    private void SetTimeDefault() {
        year = dataPoint.getDateNow("year");
        month = dataPoint.getDateNow("month");
        day = dataPoint.getDateNow("day");
        hour = dataPoint.getDateNow("hour");
        minute = dataPoint.getDateNow("minute");
        String fm = day +"/"+ month +"/"+ year +" "+ hour +":"+ minute;
        to = dataPoint.getTimestamp(fm);
        dis = 86400000L;
        timeFrame = "HOUR";
    }

    private void openDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                date = day +"/"+ ++month +"/"+ year;
            }
        }, year, month-1, day);


        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                time_24= hour +":"+ minute;
                String am_pm = (hour < 12) ? "AM" : "PM";
                if (hour > 12) {hour -= 12;}
                if(minute<10){
                    time_12 = hour +":"+"0"+ minute +" "+ am_pm;
                }
                else {
                    time_12 = hour +":"+ minute +" "+ am_pm;
                }

            }
        }, hour, minute, true);

        datePickerDialog.show();
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                timePickerDialog.show();

            }
        });


        timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                act_ending.setText(date+ " "+ time_12);
                to = dataPoint.getTimestamp(date +" "+time_24);
                Log.d("AAA", to.toString());

            }
        });

    }

    private void showChart() {
        List<Entry> lineValues =new ArrayList<Entry>();
        for (DataPoint data:DataPoint.getDataPointList()) {
            if(data.y != null && data.x != null){
                Float temp;
                switch (timeFrame){
                    case "MINUTE":
                    case "HOUR":
                        temp = 0F;

                }

                lineValues.add(new Entry(data.x,data.y));
            }


        }

        LineDataSet linedataset = new LineDataSet(lineValues, attrName);
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
        lineChart.getDescription().setText(timeFrame);
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
        act_ending = (AutoCompleteTextView) findViewById(R.id.act_ending);
        layout_below= (RelativeLayout) findViewById(R.id.layout_chart);

        btn_date= (ImageButton) findViewById(R.id.btn_date);
    }
}