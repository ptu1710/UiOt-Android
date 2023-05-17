package com.ixxc.uiot;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.ixxc.uiot.Adapter.DeviceArrayAdapter;
import com.ixxc.uiot.Model.Attribute;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.Model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CreateRuleFragment_1 extends Fragment {
    CreateRuleActivity parentActivity;
    ImageView iv_add;
    AutoCompleteTextView act_devices, act_attribute, act_operator, act_models;

    List<String> models;
    List<Attribute> attributes;
    List<Device> devices;
    String selectedModel, selectedValueType;

    TextInputEditText tie_value;

    public CreateRuleFragment_1() { }

    public CreateRuleFragment_1(CreateRuleActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_rule_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        InitViews(view);
        InitVars();
        InitEvents();

        super.onViewCreated(view, savedInstanceState);
    }

    private void InitViews(View view) {
        iv_add = view.findViewById(R.id.iv_add);
        act_devices = view.findViewById(R.id.act_devices);
        act_attribute = view.findViewById(R.id.act_attribute);
        act_operator = view.findViewById(R.id.act_operator);
        act_models = view.findViewById(R.id.act_models);
        tie_value = view.findViewById(R.id.tie_value);
    }

    private void InitVars() {
        models = Model.getModelList().stream().map(model -> (model.assetDescriptor.get("name").getAsString())).collect(Collectors.toList());
        models.add("Time");
        //models.sort(Comparator.comparing(o -> o.assetDescriptor.get("name").getAsString()));

        DeviceArrayAdapter adapter = new DeviceArrayAdapter(parentActivity, R.layout.dropdown_item_1, models);
        act_models.setHint(R.string.choose_device);
        act_models.setAdapter(adapter);
    }

    private void InitEvents() {
        act_models.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedModel = models.get(i);
            setDeviceAdapter(selectedModel);

            Device device = new Device(selectedModel);
            iv_add.setImageResource(device.getIconRes());

            parentActivity.rule.setRuleTypes(selectedModel);
            Log.d(GlobalVars.LOG_TAG, "setRuleTypes: " + selectedModel);
        });

        act_devices.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) {
                Log.d("AAA", selectedModel);
                attributes = Model.getDeviceModel(selectedModel).attributeDescriptors;

                List<String> attributeNames = attributes.stream()
                        .map(attribute -> Utils.formatString(attribute.name))
                        .collect(Collectors.toList());

                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, attributeNames);
                act_attribute.setHint(R.string.attribute);
                act_attribute.setAdapter(adapter1);
                return;
            }

            Device device = Device.getDeviceById(devices.get(i - 1).id);
            if (device !=  null) {
                attributes = device.getDeviceAttribute().stream()
                        .filter(attribute -> attribute.getMetaValue("ruleState").equals("true"))
                        .collect(Collectors.toList());

                List<String> attributeNames = attributes.stream()
                        .map(attribute -> Utils.formatString(attribute.name))
                        .collect(Collectors.toList());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, attributeNames);
                act_attribute.setHint(R.string.attribute);
                act_attribute.setAdapter(adapter);

                parentActivity.rule.setDeviceIds(Collections.singletonList(device.id));
                Log.d(GlobalVars.LOG_TAG, "setDeviceIds: " + device.id);
            }
        });

        act_attribute.setOnItemClickListener((adapterView, view, i, l) -> {
            act_attribute.setSelection(0);

            selectedValueType = attributes.get(i).type;

            List<String> operators = parentActivity.getRuleOperator(parentActivity, selectedValueType);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, operators);
            act_operator.setHint(R.string.operator);
            act_operator.setAdapter(adapter);

            parentActivity.rule.setAttributeName(attributes.get(i).name);
            Log.d(GlobalVars.LOG_TAG, "setAttributeName: " + attributes.get(i).name);
        });

        act_operator.setOnItemClickListener((adapterView, view, i, l) -> {
            tie_value.setVisibility(View.VISIBLE);
            parentActivity.rule.setAttributeValue(selectedValueType, act_operator.getText().toString());
            Log.d(GlobalVars.LOG_TAG, "setAttributeValue: " + selectedValueType + " - " + act_operator.getText());
        });

        Log.d("AAA", tie_value.getText().toString());
    }
    // TODO: remove asset
    private void setDeviceAdapter(String deviceType) {
        devices = Device.getDevicesList().stream()
                .filter(device -> device.type.equals(deviceType)).collect(Collectors.toList());

        List<String> deviceNames = devices.stream().map(device -> device.name).collect(Collectors.toList());

        deviceNames.add(0, getText(R.string.any_of_this_type).toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, deviceNames);
        act_devices.setHint(R.string.select);
        act_devices.setAdapter(adapter);
    }
}