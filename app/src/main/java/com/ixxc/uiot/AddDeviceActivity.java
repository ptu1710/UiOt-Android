package com.ixxc.uiot;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Adapter.DeviceArrayAdapter;
import com.ixxc.uiot.Model.Attribute;
import com.ixxc.uiot.Model.CreateDeviceReq;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.DeviceModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddDeviceActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    AutoCompleteTextView act_device, act_parent;
    TextInputLayout til_type;
    TextInputEditText ti_name;
    Button btn_add;
    RadioGroup rg_type;
    ChipGroup cg_optional;
    List<String> modelsType, modelsName, parentNames;
    List<DeviceModel> models;
    List<Device> parentDevices;
    List<Attribute> selectedOptional;
    ArrayAdapter<String> typeAdapter;
    DeviceArrayAdapter devicesAdapter;
    ArrayAdapter<String> parentAdapter;

    String parentId = "None";
    String selectedDevice = "";

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();

        boolean createDevice = bundle.getBoolean("CREATE_DEV");

        if (createDevice) {
            setResult(Utils.UPDATE_DEVICE);
            finish();
            Toast.makeText(this, "Device created!", Toast.LENGTH_LONG).show();
        }

        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        InitVars();
        InitViews();
        InitEvents();

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("New device");

        act_device.setAdapter(devicesAdapter);
        act_parent.setAdapter(parentAdapter);
    }

    private void InitVars() {
        selectedOptional = new ArrayList<>();
        modelsType = new ArrayList<>();
        modelsName = new ArrayList<>();

        parentDevices = Device.getDeviceList();

        parentNames = parentDevices.stream().map(device -> device.name).collect(Collectors.toList());
        parentNames.add(0, "None");

        modelsType.add("Agent");
        modelsType.add("Asset");

        models = DeviceModel.getModelList();
        modelsName = models.stream().map(model -> model.assetDescriptor.get("name").getAsString()).collect(Collectors.toList());
        Collections.sort(modelsName);

        typeAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, modelsType);
        devicesAdapter = new DeviceArrayAdapter(this, R.layout.dropdown_item_1, modelsName, true);
        parentAdapter = new DeviceArrayAdapter(this, R.layout.dropdown_item_1, parentNames, false);
    }

    private void InitViews() {
        act_device = findViewById(R.id.act_device);
        act_parent = findViewById(R.id.act_parent);
        ti_name = findViewById(R.id.et_device_name);
        til_type = findViewById(R.id.til_type);
        btn_add = findViewById(R.id.btn_add);
        toolbar = findViewById(R.id.action_bar);
        rg_type = findViewById(R.id.rg_type);
        cg_optional = findViewById(R.id.cg_optional);
    }

    private void InitEvents() {
        rg_type.setOnCheckedChangeListener((radioGroup, id) -> {
            clearFocus();
            act_device.setText("");
            cg_optional.removeAllViews();

            View checked = radioGroup.findViewById(id);
            String tag = checked.getTag().toString();
            List<String> newList;
            if (tag.equals("all")) {
                newList = new ArrayList<>(modelsName);
            } else {
                newList = modelsName.stream().filter(name -> name.toLowerCase().contains(tag)).sorted().collect(Collectors.toList());
            }

            devicesAdapter = new DeviceArrayAdapter(AddDeviceActivity.this, R.layout.dropdown_item_1, newList, true);
            act_device.setAdapter(devicesAdapter);
        });

        act_device.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedOptional.clear();
            selectedDevice = adapterView.getAdapter().getItem(i).toString();
            act_device.setText(Utils.formatString(selectedDevice));
            createOptionalViews();
        });

        act_parent.setOnItemClickListener((adapterView, view, i, l) -> {
            // Different between 2 parentNames and parentDevices (parentNames has 1 more item is "None" at index 0);
            if (i == 0) return;
            parentId = parentDevices.get(i - 1).id;
        });

        btn_add.setOnClickListener(view -> {
            List<DeviceModel> result = models.stream()
                    .filter(item -> item.assetDescriptor.get("name").getAsString().equals(selectedDevice))
                    .collect(Collectors.toList());

            if(result.size() == 0 || String.valueOf(ti_name.getText()).equals("")) {
                Toast.makeText(AddDeviceActivity.this, "Device type and device name fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Attribute> requireAttributes = result.get(0).attributeDescriptors.stream()
                    .filter(item -> !item.isOptional())
                    .collect(Collectors.toList());

            List<Attribute> finalAttributes = Stream.concat(requireAttributes.stream(), selectedOptional.stream())
                    .collect(Collectors.toList());

            JsonObject attributes = new JsonObject();

            for (Attribute a : finalAttributes) {
                String name = a.getName();
                String type = a.getType();
                JsonObject meta = a.getMeta();

                JsonObject attribute = new JsonObject();
                attribute.addProperty("name", name);
                attribute.addProperty("type", type);
                if (meta != null) attribute.add("meta", meta);
                attributes.add(name, attribute);
            }

            new Thread(() -> {
                CreateDeviceReq req = new CreateDeviceReq();
                req.setName(Objects.requireNonNull(ti_name.getText()).toString());
                req.setType(selectedDevice);
                req.setParentId(parentId);
                req.setAttributes(attributes);

                APIManager.createDevice(req.getJsonObj());

                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putBoolean("CREATE_DEV", true);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }).start();
        });
    }

    private void createOptionalViews() {
        DeviceModel model = models.stream()
                .filter(item -> item.assetDescriptor.get("name").getAsString().equals(selectedDevice))
                .findFirst().orElse(null);

        if (model == null) {
            Toast.makeText(AddDeviceActivity.this, "Please select a device first!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Attribute> optionalAttributes = model.attributeDescriptors.stream()
                .filter(Attribute::isOptional)
                .sorted(Comparator.comparing(Attribute::getName))
                .collect(Collectors.toList());

        cg_optional.removeAllViews();
        for (Attribute a: optionalAttributes) {
            Chip chip = new Chip(this);

            chip.setText(Utils.formatString(a.getName()));
            chip.setCheckable(true);

            chip.setTextStartPadding(Utils.dpToPx(this, 12));
            chip.setTextEndPadding(Utils.dpToPx(this, 12));
            chip.setIconStartPadding(Utils.dpToPx(this, 6));

            chip.setCheckedIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check, null));
            chip.setChipStrokeWidth(2);
            chip.setChipStrokeColor(getColorStateList(R.color.bg3));
            chip.setTextColor(getColorStateList(R.color.chip_text));
            chip.setCheckedIconTint(getColorStateList(R.color.chip_text));
            chip.setChipBackgroundColor(getColorStateList(R.color.chip_bg));

            chip.setOnCheckedChangeListener((compoundButton, checked) -> {
                if (chip.isChecked()) {
                    selectedOptional.add(a);
                    chip.setTextStartPadding(Utils.dpToPx(this, 6));
                } else {
                    selectedOptional.remove(a);
                    chip.setTextStartPadding(Utils.dpToPx(this, 12));
                }
            });

            cg_optional.addView(chip);
        }
    }

    private void clearFocus() {
        act_device.clearFocus();
        act_parent.clearFocus();
        ti_name.clearFocus();
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