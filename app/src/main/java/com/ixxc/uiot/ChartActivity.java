package com.ixxc.uiot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Utils.Logger;
import com.ixxc.uiot.Utils.Util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ChartActivity extends AppCompatActivity {
    Toolbar toolbar;
    LineChart lineChart;
    AutoCompleteTextView act_attributeName, act_ending, act_timeframe;
    TextInputLayout til_attributeName, til_timeFrame, til_ending;
    Button btn_show_chart;
    LinearLayout layout_timeframe;
    Device current_device;
    String selectedAttribute =  "", interval;
    Long timestampMillis, dis;
    List<String> attributes;
    Calendar calendar;
    SimpleDateFormat sdf;
    JsonArray dataPoints;
    int current_color;

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

        toolbar.setBackgroundColor(current_color);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Chart");

        btn_show_chart.setBackgroundColor(current_color);
        til_attributeName.setHintTextColor(ColorStateList.valueOf(current_color));
        til_attributeName.setBoxStrokeColor(current_color);
        til_timeFrame.setHintTextColor(ColorStateList.valueOf(current_color));
        til_timeFrame.setBoxStrokeColor(current_color);
        til_ending.setHintTextColor(ColorStateList.valueOf(current_color));
        til_ending.setBoxStrokeColor(current_color);
    }

    private void initView() {
        lineChart = findViewById(R.id.lineChart);
        btn_show_chart = findViewById(R.id.btn_showChart);
        til_attributeName = findViewById(R.id.til_attributeName);
        til_timeFrame = findViewById(R.id.til_timeFrame);
        til_ending = findViewById(R.id.til_ending);
        act_attributeName = findViewById(R.id.act_attributeName);
        act_timeframe = findViewById(R.id.act_timeFrame);
        act_ending = findViewById(R.id.act_ending);
        layout_timeframe = findViewById(R.id.layout_timeframe);
        toolbar = findViewById(R.id.actionbar);
    }

    private void initVars() {
        String device_id = getIntent().getStringExtra("DEVICE_ID");
        current_device = Device.getDeviceById(device_id);
        assert current_device != null;
        attributes = current_device.getStoredAttributes().stream().map(Util::formatString).collect(Collectors.toList());

        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault(Locale.Category.FORMAT));

        act_ending.setText(sdf.format(calendar.getTime()));

        timestampMillis = calendar.getTimeInMillis();
        dis = 24 * 3600 * 1000L;
        interval = "HOUR";

        assert current_device != null;
        current_color = current_device.getColorId(this);
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

        btn_show_chart.setOnClickListener(view -> {
            if (selectedAttribute.isEmpty()) {
                til_attributeName.setError("Please select an attribute");
                return;
            } else {
                til_attributeName.setError(null);
            }

            new Thread(() -> {
                JsonObject body = new JsonObject();
                body.addProperty("type", "lttb");
                body.addProperty("amountOfPoints", 100);
                body.addProperty("fromTimestamp", timestampMillis - dis);
                body.addProperty("toTimestamp", timestampMillis);

                dataPoints = new APIManager().getDatapoint(current_device.id, selectedAttribute, body);
                if (dataPoints == null) dataPoints = new JsonArray();

                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putBoolean("CALL_OK", true);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }).start();
        });

        act_ending.setOnClickListener(view -> dateTimeDialog());

        act_ending.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                dateTimeDialog();
            }
            else {
                timestampMillis = calendar.getTimeInMillis();
            }
        });
    }

    private void dateTimeDialog() {
        // Date Select Listener
        DatePickerDialog.OnDateSetListener datePickerListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            act_ending.clearFocus();
        };

        // Time Select Listener
        TimePickerDialog.OnTimeSetListener timePickerListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

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

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                Logger.logMsg("Date Picker Dialog Cancelled");
                timestampMillis = calendar.getTimeInMillis();
                act_ending.setText(sdf.format(calendar.getTime()));
            }
        });

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                Logger.logMsg("Date Picker Dialog OK");
                timePickerDialog.show();
            }
        });

        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                Logger.logMsg("Time Picker Dialog Cancelled");
                datePickerDialog.show();
            }
        });

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                Logger.logMsg("Time Picker Dialog OK");
                timestampMillis = calendar.getTimeInMillis();
                act_ending.setText(sdf.format(calendar.getTime()));
            }
        });

        datePickerDialog.show();
    }

    private String xValueConvert(long x, String type) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(x), ZoneId.systemDefault());

        switch (type) {
            case "MINUTE":
                return dateTime.getHour() + ":" + (dateTime.getMinute() < 10 ? "0" + dateTime.getMinute() : dateTime.getMinute());
            case "HOUR":
                return String.valueOf(dateTime.getHour());
            case "DAY":
                return String.valueOf(dateTime.getDayOfMonth());
            case "MONTH":
                return String.valueOf(dateTime.getMonthValue());
        }

        return "";
    }

    private void showChart() {
        if (lineChart.getVisibility() == View.VISIBLE) lineChart.setVisibility(View.GONE);

        float maxYValue = 0f;

        List<Entry> lineValues = new ArrayList<>();
        List<String> xAxisValues = new ArrayList<>();

        for (JsonElement dataPoint : dataPoints) {
            JsonObject data = dataPoint.getAsJsonObject();
            if (data.get("y") != null) {
                float y = data.get("y").getAsFloat();
                lineValues.add(new Entry(lineValues.size(), y));
                if (y > maxYValue) maxYValue = y;
                xAxisValues.add(xValueConvert(data.get("x").getAsLong(), interval));
            }
        }

        LineDataSet linedataset = new LineDataSet(lineValues, Util.formatString(selectedAttribute));
        linedataset.setDrawValues(false);
        linedataset.setLineWidth(3f);
        linedataset.setDrawFilled(true);
        linedataset.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        linedataset.setValueTextColor(getApplicationContext().getColor(R.color.lightBlue_10));
        linedataset.setColor(getApplicationContext().getColor(R.color.lightBlue_10));
        linedataset.setCircleColor(getApplicationContext().getColor(R.color.lightBlue_10));
        linedataset.setFillColor(getApplicationContext().getColor(R.color.lightBlue_20));

        LineData data = new LineData(linedataset);
        lineChart.setData(data);
        lineChart.setNoDataText("NO DATA");
        lineChart.getDescription().setText(interval);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisLeft().setAxisMaximum(maxYValue + 5f);
        lineChart.getAxisLeft().setGranularity(maxYValue / 10f);
        lineChart.getAxisLeft().setLabelCount(10, true);

        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));

        lineChart.zoom(0, 0, 0, 0);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.animateXY(800, 1000);
        lineChart.invalidate();
        lineChart.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}