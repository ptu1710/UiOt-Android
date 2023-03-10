package com.ixxc.myuit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.AttributesAdapter;
import com.ixxc.myuit.Model.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceInfoActivity extends AppCompatActivity {
    private ImageView btn_actionbar_back;
    private TextView tv_actionbar_name;
    private RecyclerView rv_attribute;

    EditText et_name;

    TextInputLayout til_parent;

    AutoCompleteTextView act_parent;

    Button btn_edit;

    String device_id, selected_id = "";
    Device current_device;

    List<JsonObject> current_attributes;

    AttributesAdapter attributesAdapter;

    ArrayAdapter parentAdapter;
    List<Device> parentDevices;
    List<String> parentNames;

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
            et_name.setText(current_device.name);

            for (String path : current_device.path) {
                if (!path.equals(current_device.id)) {
                    selected_id = path;
                    act_parent.setText(selected_id);
                    break;
                }
            }
            act_parent.setAdapter(parentAdapter);

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
        parentNames = new ArrayList<>();
        parentDevices = Device.getAllDevices();
        for (Device d : parentDevices) {
            parentNames.add(d.name + "(" + d.id + ")");
        }

        parentAdapter = new ArrayAdapter(this, R.layout.dropdown_item, parentNames);

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
        rv_attribute = findViewById(R.id.rv_attribute);
        et_name = findViewById(R.id.et_name);
        act_parent = findViewById(R.id.act_parent);
        til_parent = findViewById(R.id.til_parent);
    }

    private void InitEvents() {
        act_parent.setOnItemClickListener((adapterView, view, i, l) -> selected_id = parentDevices.get(i).id);

        btn_actionbar_back.setOnClickListener(view -> finish());
//        btn_edit.setOnClickListener(view -> {
//            boolean isEditMode = attributesAdapter.isEditMode;
//
//            if (isEditMode) {
//                et_new_name.setVisibility(View.GONE);
//                btn_edit.setText("EDIT");
//                attributesAdapter.isEditMode = false;
//
//                JsonObject body = new JsonObject();
//                JsonElement path = new Gson().toJsonTree(current_device.path);
//
//                if (attributesAdapter.selectedIndex != -1) {
//                    JsonObject attribute = current_attributes.get(attributesAdapter.selectedIndex);
//                    attribute.remove("value");
//
//                    EditText et_attribute_value = rv_attribute.findViewHolderForAdapterPosition(attributesAdapter.selectedIndex)
//                            .itemView.findViewById(R.id.et_attribute_value);
//                    attribute.addProperty("value", Integer.parseInt(String.valueOf(et_attribute_value.getText())));
//                }
//
//                body.addProperty("id", current_device.id);
//                body.addProperty("version",  current_device.version);
//                body.addProperty("createdOn", current_device.createdOn);
//                // Change name
//                body.addProperty("name", String.valueOf(et_new_name.getText()));
//
//                body.addProperty("accessPublicRead", current_device.accessPublicRead);
//                body.addProperty("realm", current_device.realm);
//                body.addProperty("type", current_device.type);
//                body.add("path", path);
//
//                // Change attributes
//                body.add("attributes", current_device.attributes);
//
//                // Commit device changes here
//                new Thread(() ->{
//                    Log.d("API LOG", body.toString());
//                    boolean updated = APIManager.updateDeviceInfo(device_id, body);
//                    current_device = APIManager.getDevice(device_id);
//
//                    Message message = handler.obtainMessage();
//                    Bundle bundle = new Bundle();
//                    bundle.putBoolean("UPDATE_DEVICE", true);
//                    bundle.putBoolean("UPDATE_OK", updated);
//                    message.setData(bundle);
//                    handler.sendMessage(message);
//                }).start();
//
//            } else {
//                et_new_name.setVisibility(View.VISIBLE);
//                et_new_name.setText(current_device.name);
//                btn_edit.setText("DONE");
//                attributesAdapter.isEditMode = true;
//            }
//
//            attributesAdapter.notifyDataSetChanged();
//        });
    }

    private void showAttributes() {
        current_attributes = current_device.getDeviceAttribute();

        LinearLayoutManager layoutManager =  new LinearLayoutManager(getApplicationContext());
        attributesAdapter = new AttributesAdapter(getSupportFragmentManager(), device_id, current_attributes);

        rv_attribute.setLayoutManager(layoutManager);
        rv_attribute.setAdapter(attributesAdapter);
        rv_attribute.setHasFixedSize(true);
    }

}