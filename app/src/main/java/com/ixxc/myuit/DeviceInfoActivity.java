package com.ixxc.myuit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ixxc.myuit.API.APIManager;
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

    String device_id;
    Device current_device;

    List<JsonObject> current_attributes;

    AttributesAdapter attributesAdapter;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("DEVICE_OK");
        boolean isUpdated = bundle.getBoolean("UPDATE_DEVICE");

        if (isUpdated) {
            boolean isUpdateOK = bundle.getBoolean("UPDATE_OK");
            if (isUpdateOK) {
                attributesAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
                tv_actionbar_name.setText(current_device.name);
            }
        }

        if (isOK) {
            tv_actionbar_name.setText(current_device.name);
            showAttributes();
        }
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        Intent intent = getIntent();
        device_id = intent.getStringExtra("DEVICE_ID");

        InitVars();
        InitViews();
        InitEvents();
    }

    private void InitVars() {
        new Thread(() -> {
            current_device = APIManager.getDevice(device_id);

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("DEVICE_OK", true);
            message.setData(bundle);
            handler.sendMessage(message);
        }).start();
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

                JsonObject body = new JsonObject();
                JsonElement path = new Gson().toJsonTree(current_device.path);

                if (attributesAdapter.selectedIndex != -1) {
                    JsonObject attribute = current_attributes.get(attributesAdapter.selectedIndex);
                    attribute.remove("value");

                    EditText et_attribute_value = rv_attributes.findViewHolderForAdapterPosition(attributesAdapter.selectedIndex)
                            .itemView.findViewById(R.id.et_attribute_value);
                    attribute.addProperty("value", Integer.parseInt(String.valueOf(et_attribute_value.getText())));
                }

                body.addProperty("id", current_device.id);
                body.addProperty("version",  current_device.version);
                body.addProperty("createdOn", current_device.createdOn);
                // Change name
                body.addProperty("name", String.valueOf(et_new_name.getText()));

                body.addProperty("accessPublicRead", current_device.accessPublicRead);
                body.addProperty("realm", current_device.realm);
                body.addProperty("type", current_device.type);
                body.add("path", path);

                // Change attributes
                body.add("attributes", current_device.attributes);

                // Commit device changes here
                new Thread(() ->{
                    Log.d("API LOG", body.toString());
                    boolean updated = APIManager.updateDeviceInfo(device_id, body);
                    current_device = APIManager.getDevice(device_id);

                    Message message = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("UPDATE_DEVICE", true);
                    bundle.putBoolean("UPDATE_OK", updated);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }).start();

            } else {
                et_new_name.setVisibility(View.VISIBLE);
                et_new_name.setText(current_device.name);
                btn_edit.setText("DONE");
                attributesAdapter.isEditMode = true;
            }

            attributesAdapter.notifyDataSetChanged();
        });
    }

    private void showAttributes() {
        current_attributes = current_device.getDeviceAttribute();

        LinearLayoutManager layoutManager =  new LinearLayoutManager(getApplicationContext());
        attributesAdapter = new AttributesAdapter(device_id, current_attributes);

        rv_attributes.setLayoutManager(layoutManager);
        rv_attributes.setAdapter(attributesAdapter);
        rv_attributes.setHasFixedSize(true);
    }

}