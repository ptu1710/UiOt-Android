package com.ixxc.uiot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Model.DataPoint;
import com.ixxc.uiot.Model.Device;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity {
    Toolbar toolbar;
    LineChart lineChart;
    AutoCompleteTextView act_attributeName, act_ending, act_timeframe;
    Button btn_show_chart;
    LinearLayout layout_timeframe;
    String device_id;
    String selectedAttribute, interval;
    String date, dateTime;
    Long timestampMillis, dis;
    List<String> attributes;
    Calendar calendar;
    SimpleDateFormat sdf;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("CALL_OK");

        if (isOK) showChart();

        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        initView();
        initVars();
        initEvent();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Chart");
    }

    private void initView() {
        lineChart = findViewById(R.id.lineChart);
        btn_show_chart = findViewById(R.id.btn_showChart);
        act_attributeName = findViewById(R.id.act_attributeName);
        act_timeframe = findViewById(R.id.act_timeFrame);
        act_ending = findViewById(R.id.act_ending);
        layout_timeframe = findViewById(R.id.layout_timeframe);
        toolbar = findViewById(R.id.actionbar);
    }

    private void initVars() {
        device_id = getIntent().getStringExtra("DEVICE_ID");
        Device device = Device.getDeviceById(device_id);
        assert device != null;
        attributes = device.getStoredAttributes();

        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault(Locale.Category.FORMAT));

        act_ending.setText(dateTime = sdf.format(calendar.getTime()));

        timestampMillis = dateToMillisTimestamp(dateTime);
        dis = 24 * 3600 * 1000L;
        interval = "HOUR";
    }

    private void initEvent() {

        ArrayAdapter<String> attr_adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, attributes);
        act_attributeName.setAdapter(attr_adapter);

        List<String> timeframes = Arrays.asList(getResources().getStringArray(R.array.timeframe));
        ArrayAdapter<String> time_adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, timeframes);
        act_timeframe.setAdapter(time_adapter);

        act_attributeName.setOnItemClickListener((adapterView, view, i, l) -> {
            layout_timeframe.setVisibility(View.VISIBLE);
            selectedAttribute = adapterView.getItemAtPosition(i).toString().toLowerCase();
        });

        act_timeframe.setOnItemClickListener((adapterView, view, i, l) -> {
            String timeFrame = adapterView.getItemAtPosition(i).toString();

            switch (timeFrame){
                case "Hour":
                    interval = "MINUTE";
                    dis = 3600 * 1000L;
                    break;
                case "Day":
                    dis = 24 * 3600 * 1000L;
                    interval = "HOUR";
                    break;
                case "Week":
                    dis = 7 * 24 * 3600 * 1000L;
                    interval = "HOUR";
                    break;
                case "Month":
                    dis = 31 * 24 * 3600 * 1000L;
                    interval = "DAY";
                    break;
                case "Year":
                    dis = 365 * 24 * 3600 * 1000L;
                    interval = "MONTH";
                    break;
            }
        });

        btn_show_chart.setOnClickListener(view -> new Thread(()->{
            Log.d(GlobalVars.LOG_TAG, "initEvent: " + selectedAttribute + " " + interval + " " + timestampMillis + " " + (timestampMillis - dis));
            APIManager.getDatapoint(device_id, selectedAttribute, interval, timestampMillis - dis, timestampMillis);

            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("CALL_OK", true);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }).start());

        act_ending.setOnClickListener(view -> dateTimeDialog());

        act_ending.setOnFocusChangeListener((view, focused) -> {
            if (focused) dateTimeDialog();
            else timestampMillis = dateToMillisTimestamp(dateTime);
        });
    }

    private void dateTimeDialog() {
        // Date Select Listener
        DatePickerDialog.OnDateSetListener datePickerListener = (view, year, monthOfYear, dayOfMonth) -> {
            int month = (monthOfYear + 1);
            date = (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "/" + (month < 10 ? "0" + month : month) + "/" + year;
        };

        // Time Select Listener
        TimePickerDialog.OnTimeSetListener timePickerListener = (view, hourOfDay, minute) -> {
            dateTime = date + " " + (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + minute;
            act_ending.setText(dateTime);
            act_ending.clearFocus();
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                datePickerListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                timePickerListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);

        datePickerDialog.setOnDismissListener(dialogInterface -> timePickerDialog.show());
        timePickerDialog.setOnDismissListener(dialogInterface -> act_ending.setText(dateTime));

        datePickerDialog.show();
    }

    // date format: dd/MM/yyyy HH:mm to timestamp (millis): 1625097600000
    public static long dateToMillisTimestamp(String dateString) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        Date date = Date.from(localDateTime.atZone(zoneOffset).toInstant());

        Log.d(GlobalVars.LOG_TAG, "dateTime: " + dateString);
        Log.d(GlobalVars.LOG_TAG, "TimestampMillis: " + date.getTime());

        return date.getTime();
    }

    private void showChart() {
        List<Entry> lineValues = new ArrayList<>();

        for (DataPoint data : DataPoint.getDataPointList()) {
            if(data.x != null && data.y != null){
                lineValues.add(new Entry(data.x,data.y));
            }
        }

        LineDataSet linedataset = new LineDataSet(lineValues, selectedAttribute);
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
        lineChart.setNoDataText("NO DATA");
        lineChart.getDescription().setText(interval);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setGranularity(1f);
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.animateXY(800, 1000);
        lineChart.setVisibility(View.VISIBLE);
        lineChart.invalidate();
    }
}