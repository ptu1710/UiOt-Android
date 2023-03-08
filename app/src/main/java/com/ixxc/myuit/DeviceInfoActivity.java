package com.ixxc.myuit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ixxc.myuit.Adapter.AttributesAdapter;
import com.ixxc.myuit.Model.Attribute;
import com.ixxc.myuit.Model.Device;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeviceInfoActivity extends AppCompatActivity {
    private ImageView btn_actionbar_back;
    private TextView tv_actionbar_name;
    private RecyclerView rv_attributes;
    private EditText et_new_name;
    Button btn_edit;

    AttributesAdapter attributesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        InitVars();
        InitViews();
        InitEvents();

        Intent intent = getIntent();
        String device_id = intent.getStringExtra("DEVICE_ID");
        showAttributes(device_id);
    }

    private void InitVars() {

    }

    private void InitViews() {
        btn_actionbar_back = findViewById(R.id.btn_actionbar_back);
        tv_actionbar_name = findViewById(R.id.tv_actionbar_name);
        rv_attributes = findViewById(R.id.rv_attributes);
        btn_edit = findViewById(R.id.btn_edit);
        et_new_name = findViewById(R.id.et_new_name);
    }

    private void InitEvents() {
        btn_actionbar_back.setOnClickListener(view -> finish());
        btn_edit.setOnClickListener(view -> {
            boolean isEditMode = attributesAdapter.isEditMode;

            if (isEditMode) {
                et_new_name.setVisibility(View.GONE);
                btn_edit.setText("EDIT");
                attributesAdapter.isEditMode = false;
            } else {
                et_new_name.setVisibility(View.VISIBLE);
                btn_edit.setText("DONE");
                attributesAdapter.isEditMode = true;
            }

            attributesAdapter.notifyDataSetChanged();
        });
    }

    private void showAttributes(String id) {
        Device device = Device.getDeviceById(id);

        LinearLayoutManager layoutManager =  new LinearLayoutManager(getApplicationContext());
        attributesAdapter = new AttributesAdapter(device.id, device.getDeviceAttribute());

        rv_attributes.setLayoutManager(layoutManager);
        rv_attributes.setAdapter(attributesAdapter);
        rv_attributes.setHasFixedSize(true);
    }

}