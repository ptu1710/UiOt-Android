package com.ixxc.myuit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Model.Attribute;
import com.ixxc.myuit.Model.CreateAssetReq;
import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.Model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddDeviceActivity extends AppCompatActivity {
    AutoCompleteTextView act_type, act_device, act_parent;

    TextInputLayout til_type;

    TextInputEditText ti_name;

    Button btn_add;

    ImageView iv_back_1;

    LinearLayout add_optional_layout;

    List<String> modelsType, modelsName, parentList;

    List<Model> models;

    List<Device> deviceList;

    ArrayAdapter typeAdapter, devicesAdapter, parentAdapter;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        InitVars();
        InitViews();
        InitEvents();

        new Thread(() -> {
            models = APIManager.getDeviceModels();
            deviceList = Device.getAllDevices();

            for (Model model : models) {
                String name = model.assetDescriptor.get("name").getAsString();
                modelsName.add(name);
            }

            parentList.add("None");
            for (Device d : deviceList) {
                parentList.add(d.name);
            }

            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("GET_DEV", true);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }).start();
    }

    private void InitVars() {
        modelsType = new ArrayList<>();
        modelsName = new ArrayList<>();
        parentList = new ArrayList<>();

        modelsType.add("Agent");
        modelsType.add("Asset");

        handler = new Handler(message -> {
            Bundle bundle = message.getData();

            boolean getDevice = bundle.getBoolean("GET_DEV");
            boolean createDevice = bundle.getBoolean("CREATE_DEV");

            if (getDevice) {
                typeAdapter = new ArrayAdapter(this, R.layout.dropdown_item, modelsType);
                act_type.setAdapter(typeAdapter);

                devicesAdapter = new ArrayAdapter(this, R.layout.dropdown_item, modelsName);
                act_device.setAdapter(devicesAdapter);

                parentAdapter = new ArrayAdapter(this, R.layout.dropdown_item, parentList);
                act_parent.setAdapter(parentAdapter);
            } else if (createDevice) {
                HomeActivity.devicesFrag.refreshDevices();
                finish();
                Toast.makeText(this, "CREATED!", Toast.LENGTH_SHORT).show();
            }

            return false;
        });
    }

    private void InitViews() {
        act_type = findViewById(R.id.act_type);
        act_device = findViewById(R.id.act_device);
        act_parent = findViewById(R.id.act_parent);

        ti_name = findViewById(R.id.ti_name);

        til_type = findViewById(R.id.til_type);

        add_optional_layout = findViewById(R.id.add_optional_layout);

        btn_add = findViewById(R.id.btn_add);
        iv_back_1 = findViewById(R.id.btn_actionbar_back);
    }

    private void InitEvents() {
        act_type.setOnItemClickListener((adapterView, view, i, l) -> {
            String type = modelsType.get(i);
            List<String> newList =  modelsName.stream().filter(name -> name.contains(type)).collect(Collectors.toList());
            devicesAdapter = new ArrayAdapter(AddDeviceActivity.this, R.layout.dropdown_item, newList);
            act_device.setAdapter(devicesAdapter);
        });

        add_optional_layout.setOnClickListener(view -> {
            List<Model> result = models.stream()
                    .filter(item -> item.assetDescriptor.get("name").getAsString().equals(act_device.getText().toString()))
                    .collect(Collectors.toList());

            if (result.size() == 0) {
                Toast.makeText(AddDeviceActivity.this, "Please select a device first!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Attribute> optional = result.get(0).attributeDescriptors.stream()
                    .filter(item -> item.optional)
                    .collect(Collectors.toList());

            CharSequence[] optionalName = new CharSequence[optional.size()];

            for (Attribute a :
                    optional) {
                optionalName[optional.indexOf(a)] = a.name;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(AddDeviceActivity.this);
            builder.setTitle("Select optional attribute");
            builder.setMultiChoiceItems(optionalName, null, (dialog, i, isChecked) -> {
                if (isChecked) {
                    Toast.makeText(AddDeviceActivity.this, optional.get(i).name, Toast.LENGTH_SHORT).show();
                }
            });

            builder.setPositiveButton("Add", (dialogInterface, i) -> Toast.makeText(AddDeviceActivity.this, "OK", Toast.LENGTH_SHORT).show());

            builder.create();
            builder.show();
        });

        btn_add.setOnClickListener(view -> {
            Model result = models.stream()
                    .filter(item -> item.assetDescriptor.get("name").getAsString().equals(act_device.getText().toString()))
                    .collect(Collectors.toList()).get(0);

            List<Attribute> require = result.attributeDescriptors.stream()
                    .filter(item -> !item.optional)
                    .collect(Collectors.toList());

            JsonObject attributes = new JsonObject();

            for (Attribute a : require) {
                String name = a.name;
                String type = a.type;
                JsonObject meta = a.meta;

                JsonObject attribute = new JsonObject();
                attribute.addProperty("name", name);
                attribute.addProperty("type", type);
                if (meta != null) {
                    attribute.add("meta", meta);
                }

                attributes.add(name, attribute);

            }

            new Thread(() -> {
                CreateAssetReq req = new CreateAssetReq(ti_name.getText().toString(), act_device.getText().toString(), "master", attributes);
                APIManager.createDevice(req.getJsonObj());

                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putBoolean("CREATE_DEV", true);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }).start();
        });

        iv_back_1.setOnClickListener(view -> finish());
    }
}