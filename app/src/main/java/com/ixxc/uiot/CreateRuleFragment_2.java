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
import com.ixxc.uiot.Model.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreateRuleFragment_2 extends Fragment {
    CreateRuleActivity parentActivity;
    AutoCompleteTextView act_actions, act_attribute, act_devices;
    List<String> models;
    String selectedModel, selectedValueType;
    ImageView iv_add;

    List<Attribute> attributes;
    List<Device> devices;

    TextInputEditText tie_value;

    public CreateRuleFragment_2() { }

    public CreateRuleFragment_2(CreateRuleActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_rule_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        InitViews(view);
        InitVars();
        InitEvents();

        super.onViewCreated(view, savedInstanceState);
    }

    private void InitViews(View view) {
        act_actions = view.findViewById(R.id.act_actions);
        iv_add = view.findViewById(R.id.iv_add);
        act_attribute = view.findViewById(R.id.act_attribute);
        act_devices = view.findViewById(R.id.act_devices);
        tie_value = view.findViewById(R.id.tie_value);
    }

    private void InitVars() {
        //List<String> actions = Arrays.asList("Push notification", "Email");
        //models = Model.getModelList().stream().map(model -> Utils.formatString((model.assetDescriptor.get("name").getAsString()))).collect(Collectors.toList());

        //models = Model.getModelList().stream().map(model -> (model.assetDescriptor.get("name").getAsString())).collect(Collectors.toList());
        models = Model.getModelList().stream().filter(model -> !model.assetDescriptor.get("name").getAsString().contains("Agent"))
                .map(model -> (model.assetDescriptor.get("name").getAsString()))
                .collect(Collectors.toList());
        models.add("Email");
        models.add("Push Notification");
        models.add("Wait");
        models.add("Webhook");

        DeviceArrayAdapter adapter = new DeviceArrayAdapter(parentActivity, R.layout.dropdown_item_1, models);
        act_actions.setAdapter(adapter);

        act_actions.setOnItemClickListener((adapterView, view, i, l) -> {
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

                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, attributeNames);
                act_attribute.setHint(R.string.attribute);
                act_attribute.setAdapter(adapter2);

                parentActivity.rule.setDeviceIds(Collections.singletonList(device.id));
                Log.d(GlobalVars.LOG_TAG, "setDeviceIds: " + device.id);
            }
        });
    }

    // TODO: remove asset
    private void InitEvents() {
        act_actions.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) parentActivity.rule.setRuleAction("notification");

            parentActivity.rule.setTargetIds(User.getMe().id);

            selectedModel = models.get(i);

            Device device = new Device(selectedModel.replaceAll(" ",""));
            iv_add.setImageResource(device.getIconRes());

        });
        setDeviceAdapter(selectedModel);
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

            parentActivity.rule.setAttributeName(attributes.get(i).name);
            Log.d(GlobalVars.LOG_TAG, "setAttributeName: " + attributes.get(i).name);
        });
    }

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