package com.ixxc.uiot;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ixxc.uiot.Adapter.DeviceArrayAdapter;
import com.ixxc.uiot.Model.Attribute;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.DeviceModel;
import com.ixxc.uiot.Model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreateRuleFragment_2 extends Fragment {
    CreateRuleActivity parentActivity;
    AutoCompleteTextView act_actions, act_attribute, act_devices, act_unlock,act_recipients, act_assets;
    List<String> models;
    String selectedModel, selectedValueType;
    ImageView iv_add;

    List<Attribute> attributes;
    List<Device> devices;

    LinearLayout layout_4,layout_4_1;
    RelativeLayout layout_value;

    Button btn_message;

    List<String> list_unlock = Arrays.asList("REQUEST START","REQUEST REPEATING","REQUEST CANCEL","READY","COMPLETED","RUNNING","CANCELLED");
    List<String> list_connector_type = Arrays.asList("YAZAKI","MENNEKES","LE GRAND","CHADEMO","COMBO","SCHUKO","ENERGYLOCK");
    List<String> list_orientation = Arrays.asList("SOUTH","EAST WEST");
    List<String> list_child_asset_type = Arrays.asList("Plug asset","Tradfri light asset","People counter asset","Bluetooth mesh agent","Velbus serial agent"
            ,"Electric vehicle fleet group asset","Velbus TCP agent","Gateway asset","Group asset", "Microphone asset","Presence sensor asset","Serial agent"
            ,"Electricity producer wind asset","Tradfri plug asset","Electricity producer asset","UDP agent","Electricity battery asset","Websocket agent"
            ,"Electric","vehicle asset","Electricity charger asset","Room asset","Storage simulator agent","Simulator agent","Console asset"
            ,"Thermostat asset","Light asset","HTTP agent","TCP agent","Parking asset","Artnet light asset","Ventilation asset","Weather asset","Building asset"
            ,"Z wave agent","Energy optimisation asset","SNMP agent","Door asset","Ship asset","MQTT agent","Environment sensor asset","KNX agent"
            ,"Electricity consumer asset","Electricity supplier asset","Groundwater sensor asset","City asset","Electricity producer solar asset","Thing asset" );

    TextInputLayout til_unlock;
    TextInputEditText tie_value;
    CheckBox cb;

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
        til_unlock = view.findViewById(R.id.til_unlock);
        act_unlock = view.findViewById(R.id.act_unlock);
        cb = view.findViewById(R.id.cb_locked);

        act_recipients = view.findViewById(R.id.act_recipients);
        act_assets = view.findViewById(R.id.act_assets);
        btn_message = view.findViewById(R.id.btn_message);

        layout_4_1 = view.findViewById(R.id.layout_4_1);
        layout_4 = view.findViewById(R.id.layout_4);
        layout_value =view.findViewById(R.id.layout_value);
    }

    private void InitVars() {
        //List<String> actions = Arrays.asList("Push notification", "Email");
        //models = Model.getModelList().stream().map(model -> Utils.formatString((model.assetDescriptor.get("name").getAsString()))).collect(Collectors.toList());

        //models = Model.getModelList().stream().map(model -> (model.assetDescriptor.get("name").getAsString())).collect(Collectors.toList());
        models = DeviceModel.getModelList().stream().filter(model -> !model.assetDescriptor.get("name").getAsString().contains("Agent"))
                .map(model -> (model.assetDescriptor.get("name").getAsString()))
                .collect(Collectors.toList());
        //models.add("PVSolarAsset");
        models.add("Email");
        models.add("Push Notification");
        models.add("Wait");
        models.add("Webhook");

        DeviceArrayAdapter adapter = new DeviceArrayAdapter(parentActivity, R.layout.dropdown_item_1, models);
        act_actions.setAdapter(adapter);

        act_actions.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) {

                attributes = DeviceModel.getDeviceModel(selectedModel).attributeDescriptors;

                List<String> attributeNames = attributes.stream()
                        .map(attribute -> Utils.formatString(attribute.getName()))
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
                        .map(attribute -> Utils.formatString(attribute.getName()))
                        .collect(Collectors.toList());

                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, attributeNames);
                act_attribute.setHint(R.string.attribute);
                act_attribute.setAdapter(adapter2);
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
            iv_add.setImageDrawable(device.getIconDrawable(parentActivity));

            switch (selectedModel){
                case "Webhook":
                    btn_message.setVisibility(View.VISIBLE);
                    layout_4.setVisibility(View.GONE);
                    layout_value.setVisibility(View.GONE);
                    tie_value.setVisibility(View.GONE);
                    cb.setVisibility(View.GONE);
                    layout_4_1.setVisibility(View.GONE);
                    break;
                case "Wait":
                    btn_message.setVisibility(View.GONE);
                    layout_4.setVisibility(View.GONE);
                    layout_value.setVisibility(View.GONE);
                    tie_value.setVisibility(View.GONE);
                    cb.setVisibility(View.GONE);
                    layout_4_1.setVisibility(View.GONE);
                    break;
                case "Email":
                case "Push Notification":
                    layout_4_1.setVisibility(View.VISIBLE);
                    btn_message.setVisibility(View.VISIBLE);
                    layout_4.setVisibility(View.GONE);
                    layout_value.setVisibility(View.GONE);
                    break;
                default:
                    layout_4.setVisibility(View.VISIBLE);
                    layout_value.setVisibility(View.VISIBLE);
                    tie_value.setVisibility(View.GONE);
                    btn_message.setVisibility(View.GONE);
                    cb.setVisibility(View.GONE);
                    layout_4_1.setVisibility(View.GONE);


            }

        });

        List<String> recipientsList = Arrays.asList("Users","Assets");
        ArrayAdapter<String> adapter_recipients = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, recipientsList);
        act_recipients.setAdapter(adapter_recipients);

        List<String> assetsList = new ArrayList<>();
        assetsList.add("Matched");
        ArrayAdapter<String> adapter_assets = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, assetsList);
        act_assets.setAdapter(adapter_assets);


        setDeviceAdapter(selectedModel);
        act_devices.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) {
                Log.d("AAA", selectedModel);
                attributes = DeviceModel.getDeviceModel(selectedModel).attributeDescriptors;

                List<String> attributeNames = attributes.stream()
                        .map(attribute -> Utils.formatString(attribute.getName()))
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
                        .map(attribute -> Utils.formatString(attribute.getName()))
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

            selectedValueType = attributes.get(i).getType();
            setValueLayout(act_attribute.getText().toString());

            parentActivity.rule.setAttributeName(attributes.get(i).getName());
            Log.d(GlobalVars.LOG_TAG, "setAttributeName: " + attributes.get(i).getName());
        });
    }

    private void setValueLayout(String s) {
        switch (s){
            case "Locked":
            case "Position":
            case "Supports Export":
            case "Supports Import":
            case "Vehicle Connected":
            case "Include Forecast Solar Service":
            case "Include Forecast Wind Service":
            case "Set Actual Solar Value With Forecast":
            case "Set Wind Actual Value With Forecast":
            case "Optimisation Disabled":
            case "Disabled":
            case "On Off":
            case "Presence":
            case "Cooling":
                cb.setVisibility(View.VISIBLE);
                tie_value.setVisibility(View.GONE);
                til_unlock.setVisibility(View.GONE);
                break;
            case "Unlock":
            case "Force Charge":
                til_unlock.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, list_unlock);
                act_unlock.setAdapter(adapter2);
                cb.setVisibility(View.GONE);
                tie_value.setVisibility(View.GONE);
                break;
            case "Connector Type":
                til_unlock.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter3 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, list_connector_type);
                act_unlock.setAdapter(adapter3);
                cb.setVisibility(View.GONE);
                tie_value.setVisibility(View.GONE);
                break;
            case "Panel Orientation":
                til_unlock.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter4 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, list_orientation);
                act_unlock.setAdapter(adapter4);
                cb.setVisibility(View.GONE);
                tie_value.setVisibility(View.GONE);
                break;
            case "Child Asset Type":
                til_unlock.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter5 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, list_child_asset_type);
                act_unlock.setAdapter(adapter5);
                cb.setVisibility(View.GONE);
                tie_value.setVisibility(View.GONE);
                break;
            default:
                tie_value.setVisibility(View.VISIBLE);
                til_unlock.setVisibility(View.GONE);
                cb.setVisibility(View.GONE);

                break;

        }
    }

    private void setDeviceAdapter(String deviceType) {
        devices = Device.getDeviceList().stream()
                .filter(device -> device.type.equals(deviceType)).collect(Collectors.toList());

        List<String> deviceNames = devices.stream().map(device -> device.name).collect(Collectors.toList());

        deviceNames.add(0,"Matched");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, deviceNames);
        act_devices.setHint(R.string.select);
        act_devices.setAdapter(adapter);
    }
}