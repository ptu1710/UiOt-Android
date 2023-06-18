package com.ixxc.uiot;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.ixxc.uiot.Model.Rule;
import com.ixxc.uiot.Model.RuleValue;
import com.ixxc.uiot.Model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateRuleFragment_2 extends Fragment {
    CreateRuleActivity parentActivity;
    AutoCompleteTextView act_actions, act_attribute, act_devices, act_unlock,act_recipients, act_assets;
    List<String> models;
    String selectedModel, selectedValueType;
    ImageView iv_add;

    String value;
    String message;

    List<Attribute> attributes;
    List<Device> devices;

    LinearLayout layout_4,layout_4_1;
    RelativeLayout layout_value;

    Button btn_message,btn_back,btn_save;

    List<String> recipientsList = Arrays.asList("Users","Assets");
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

        btn_save = view.findViewById(R.id.btn_save);
        btn_back = view.findViewById(R.id.btn_back);
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
        Collections.sort(models);

        DeviceArrayAdapter adapter = new DeviceArrayAdapter(parentActivity, R.layout.dropdown_item_1, models, true);
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

            parentActivity.rule.setTargetIds(User.getMe().id);
            //parentActivity.rule.setTargetIds("USER_ID");

            selectedModel = models.get(i);
            switch (selectedModel) {
                case "Email":
                case "Push Notification":
                    parentActivity.rule.setRuleAction("notification");
                    break;
                default:
                    parentActivity.rule.setRuleAsset(selectedModel.replaceAll(" ",""));
                    parentActivity.rule.setRuleAction("write-attribute");
            }

            Device device = new Device(selectedModel);
            iv_add.setImageDrawable(device.getIconDrawable(parentActivity));

            setLayout();



        });

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

            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(cb.isChecked()){
                        parentActivity.rule.setValue_then("true");
                    }
                    else{
                        parentActivity.rule.setValue_then("false");
                    }
                }
            });

            // TODO: get value after typing enter
            tie_value.setOnFocusChangeListener((view1, focused) -> {
                if(!focused){
                    Log.d("AAA", "Value_then " + tie_value.getText().toString());
                    parentActivity.rule.setValue_then(Objects.requireNonNull(tie_value.getText()).toString());
                }


            });

            tie_value.setOnEditorActionListener((textView, p, keyEvent) -> {
                if(p == EditorInfo.IME_ACTION_DONE) {
                    textView.clearFocus();
                    // TODO: Hide keyboard
                }
                return false;
            });


            act_unlock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    parentActivity.rule.setValue_then(list_unlock.get(i).replaceAll(" ","_"));
                }
            });

            //parentActivity.rule.setAttributeName(attributes.get(i).getName());
            parentActivity.rule.setAttributeName_then(attributes.get(i).getName());
            Log.d(GlobalVars.LOG_TAG, "setAttributeName: " + attributes.get(i).getName());
        });

        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMessageDialog();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentActivity.changeTab(1);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentActivity.createRule(parentActivity.rule);
            }
        });

        setValue(parentActivity.chose);

    }

    private void setLayout() {
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
    }

    private void setValue(String chose) {
        if(chose != null){
            RuleValue ruleValue = new RuleValue(Rule.rule_selected.rules);
            Log.d(GlobalVars.LOG_TAG,"Types then: " + ruleValue.types_then);

            selectedModel = ruleValue.types_then;
            if(ruleValue.action_then.equals("notification")){
                message = ruleValue.message_body;
                if(ruleValue.notification_type.equals("push")){
                    selectedModel = "Push Notification";
                }
                else {
                    selectedModel = "Email";
                }
            }

            DeviceArrayAdapter adapter = new DeviceArrayAdapter(parentActivity, R.layout.dropdown_item_1, models, true);
            act_actions.setText(Utils.formatString(selectedModel));
            act_actions.setAdapter(adapter);


            Device device = new Device(selectedModel);
            iv_add.setImageDrawable(device.getIconDrawable(parentActivity));

            setDeviceAdapter(selectedModel);

            Device device1 = devices.stream().filter(d -> d.id.equals(ruleValue.ids)).findFirst().orElse(null);
            if (device1 == null) {
                act_devices.setText(act_devices.getAdapter().getItem(0).toString());
            } else {
                act_devices.setText(device1.name);
            }
            setDeviceAdapter(selectedModel);

            setLayout();

            switch (selectedModel){
                case "Email":
                case "Push Notification":
                    ArrayAdapter<String> adapter_recipients = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, recipientsList);
                    act_recipients.setText(recipientsList.get(0));
                    act_recipients.setAdapter(adapter_recipients);
                    break;
                default:
                    attributes = DeviceModel.getDeviceModel(selectedModel).attributeDescriptors;

                    List<String> attributeNames = attributes.stream()
                            .map(attribute -> Utils.formatString(attribute.getName()))
                            .collect(Collectors.toList());
                    int i_attribute = attributeNames.indexOf(Utils.capitalizeFirst(ruleValue.attribute_then));
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, attributeNames);
                    act_attribute.setHint(R.string.attribute);
                    act_attribute.setText(attributeNames.get(i_attribute));
                    act_attribute.setAdapter(adapter1);

                    value = ruleValue.value_then;
                    setValueLayout(act_attribute.getText().toString());
            }



        }


    }

    private void OpenMessageDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_dialog_layout);

        Window window = dialog.getWindow();
        if(window == null) return;

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true);

        EditText edt_mess = dialog.findViewById(R.id.edt_message);
        Button btn_OK = dialog.findViewById(R.id.btn_OK);
        Button btn_Cancel = dialog.findViewById(R.id.btn_Cancel);

        if(message != null) edt_mess.setText(message);

        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("AAA", edt_mess.getText().toString());
                parentActivity.rule.setMessageObj(selectedModel,edt_mess.getText().toString());
                dialog.dismiss();

            }
        });

        dialog.show();
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
                try{
                    if(value.equals("true")){
                        cb.setChecked(true);
                    }
                }catch (Exception e){}

                cb.setVisibility(View.VISIBLE);
                tie_value.setVisibility(View.GONE);
                til_unlock.setVisibility(View.GONE);
                break;
            case "Unlock":
            case "Force Charge":
                til_unlock.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, list_unlock);
                try {
                    act_unlock.setText(value);
                }catch (Exception e){}
                act_unlock.setAdapter(adapter2);
                cb.setVisibility(View.GONE);
                tie_value.setVisibility(View.GONE);
                break;
            case "Connector Type":
                til_unlock.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter3 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, list_connector_type);
                try {
                    act_unlock.setText(value);
                }catch (Exception e){}
                act_unlock.setAdapter(adapter3);
                cb.setVisibility(View.GONE);
                tie_value.setVisibility(View.GONE);
                break;
            case "Panel Orientation":
                til_unlock.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter4 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, list_orientation);
                try {
                    act_unlock.setText(value);
                }catch (Exception e){}
                act_unlock.setAdapter(adapter4);
                cb.setVisibility(View.GONE);
                tie_value.setVisibility(View.GONE);
                break;
            case "Child Asset Type":
                til_unlock.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter5 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, list_child_asset_type);
                try {
                    act_unlock.setText(value);
                }catch (Exception e){}
                act_unlock.setAdapter(adapter5);
                cb.setVisibility(View.GONE);
                tie_value.setVisibility(View.GONE);
                break;
            default:
                try {
                    tie_value.setText(value);
                }catch (Exception e){}

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