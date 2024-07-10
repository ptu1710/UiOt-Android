package com.ixxc.uiot;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.ixxc.uiot.Adapter.DeviceArrayAdapter;
import com.ixxc.uiot.Model.Attribute;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.DeviceModel;
import com.ixxc.uiot.Model.Rule;
import com.ixxc.uiot.Model.RuleValue;
import com.ixxc.uiot.Utils.Util;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateRuleFragment_1 extends Fragment {
    CreateRuleActivity parentActivity;
    ImageView iv_add,iv_and;
    AutoCompleteTextView act_devices, act_attribute, act_operator, act_models;

    List<String> models;
    List<Attribute> attributes;
    List<Device> devices;
    String selectedModel, selectedValueType;

    TextInputEditText tie_value,tie_rangeValue;

    Button btn_next, btn_back;

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
        tie_rangeValue = view.findViewById(R.id.tie_rangeValue);
        iv_and = view.findViewById(R.id.iv_and);

        btn_next = view.findViewById(R.id.btn_next);
        btn_back = view.findViewById(R.id.btn_back);
    }

    private void InitVars() {
        models = DeviceModel.getModelList().stream().map(model -> (model.assetDescriptor.get("name").getAsString())).collect(Collectors.toList());
        //models.add("PVSolarAsset");
        models.add("Time");
        Collections.sort(models);
        //models.sort(Comparator.comparing(o -> o.assetDescriptor.get("name").getAsString()));

        DeviceArrayAdapter adapter = new DeviceArrayAdapter(parentActivity, R.layout.dropdown_item_1, models, true);
        act_models.setHint(R.string.choose_device);
        act_models.setAdapter(adapter);
    }

    private void InitEvents() {
        act_models.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedModel = models.get(i);
            setDeviceAdapter(selectedModel);

            Device device = new Device(selectedModel);
            iv_add.setImageDrawable(device.getIconDrawable(parentActivity));

            parentActivity.rule.setRuleTypes(selectedModel);
            Log.d(Util.LOG_TAG, "setRuleTypes: " + selectedModel);
        });

        act_devices.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) {
                Log.d(Util.LOG_TAG, selectedModel);
                attributes = DeviceModel.getDeviceModel(selectedModel).attributeDescriptors;

                List<String> attributeNames = attributes.stream()
                        .map(attribute -> Util.formatString(attribute.getName()))
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
                        .map(attribute -> Util.formatString(attribute.getName()))
                        .collect(Collectors.toList());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, attributeNames);
                act_attribute.setHint(R.string.attribute);
                act_attribute.setAdapter(adapter);

                parentActivity.rule.setDeviceIds(Collections.singletonList(device.id));
                Log.d(Util.LOG_TAG, "setDeviceIds: " + device.id);
            }
        });

        act_attribute.setOnItemClickListener((adapterView, view, i, l) -> {
            act_attribute.setSelection(0);

            selectedValueType =  attributes.get(i).getType();

            List<String> operators = parentActivity.getRuleOperator(parentActivity, selectedValueType);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, operators);
            act_operator.setHint(R.string.operator);
            act_operator.setAdapter(adapter);

            parentActivity.rule.setAttributeName(attributes.get(i).getName());
            Log.d(Util.LOG_TAG, "setAttributeName: " + attributes.get(i).getName());
        });

        act_operator.setOnItemClickListener((adapterView, view, i, l) -> {

            switch (act_operator.getText().toString()){
                case "Is true":
                case "Is false":
                case "Has no value":
                case "Has a value":
                    tie_value.setVisibility(View.GONE);
                    break;
                case "Between":
                case "Is not between":
                    tie_value.setVisibility(View.VISIBLE);
                    tie_rangeValue.setVisibility(View.VISIBLE);
                    iv_and.setVisibility(View.VISIBLE);
                    break;
                default:
                    tie_value.setVisibility(View.VISIBLE);
                    break;

            }
            parentActivity.rule.setAttributeValue(0, act_operator.getText().toString(),"null");

            // TODO: get value after typing enter
            tie_value.setOnFocusChangeListener((view1, focused) -> {
                if(!focused){
                    Log.d(Util.LOG_TAG, "Value_attr " + Objects.requireNonNull(tie_value.getText()));
                    parentActivity.rule.setAttributeValue(Util.getInputType(selectedValueType), act_operator.getText().toString(), Objects.requireNonNull(tie_value.getText()).toString());
                }
            });

            tie_value.setOnEditorActionListener((textView, p, keyEvent) -> {
                if(p == EditorInfo.IME_ACTION_DONE) {
                    textView.clearFocus();
                    // TODO: Hide keyboard
                }

                return false;
            });

            tie_rangeValue.setOnFocusChangeListener((view1, focused) -> {
                if(!focused){
                    Log.d(Util.LOG_TAG, "Value_range " + Objects.requireNonNull(tie_rangeValue.getText()));
                    parentActivity.rule.setRange_value(Double.parseDouble(tie_rangeValue.getText().toString()));
                    parentActivity.rule.setAttributeValue(Util.getInputType(selectedValueType), act_operator.getText().toString(), Objects.requireNonNull(tie_value.getText()).toString());
                }
            });

            tie_rangeValue.setOnEditorActionListener((textView, p, keyEvent) -> {
                if(p == EditorInfo.IME_ACTION_DONE) {
                    textView.clearFocus();
                    // TODO: Hide keyboard
                }

                return false;
            });

            Log.d(Util.LOG_TAG, "setAttributeValue: " + selectedValueType + " - " + act_operator.getText());
        });

        setValue(parentActivity.chose);

        btn_back.setOnClickListener(view -> parentActivity.changeTab(0));

        btn_next.setOnClickListener(view -> parentActivity.changeTab(2));
    }
    private void setValue(String chose) {
        if(chose != null){
            RuleValue ruleValue = new RuleValue(Rule.rule_selected.rules);
            Log.d(Util.LOG_TAG,"Types: " + ruleValue.types);

            selectedModel = ruleValue.types;

            setDeviceAdapter(selectedModel);

            act_models.setText(Util.formatString(selectedModel));

            Device device = new Device(selectedModel);
            iv_add.setImageDrawable(device.getIconDrawable(parentActivity));

            Device device1 = devices.stream().filter(d -> d.id.equals(ruleValue.ids)).findFirst().orElse(null);
            if (device1 == null) {
                act_devices.setText(act_devices.getAdapter().getItem(0).toString());
            } else {
                act_devices.setText(device1.name);
            }
            setDeviceAdapter(selectedModel);

            attributes = DeviceModel.getDeviceModel(selectedModel).attributeDescriptors;

            List<String> attributeNames = attributes.stream()
                    .map(attribute -> Util.formatString(attribute.getName()))
                    .collect(Collectors.toList());
            int i_attribute = attributeNames.indexOf(Util.capitalizeFirst(ruleValue.attribute));
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, attributeNames);
            act_attribute.setHint(R.string.attribute);
            act_attribute.setText(attributeNames.get(i_attribute));
            act_attribute.setAdapter(adapter1);


            selectedValueType =  attributes.get(i_attribute).getType();

            List<String> operators = parentActivity.getRuleOperator(parentActivity, selectedValueType);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, operators);
            act_operator.setHint(R.string.operator);
            // TODO: Check these warning
            if(ruleValue.operator != null){
                if(ruleValue.negate == null || !ruleValue.negate){
                    if(ruleValue.operator.equals("LESS_EQUALS") || ruleValue.operator.equals("GREATER_EQUALS")){
                        act_operator.setText(Util.capitalizeFirst(ruleValue.operator).split(" ")[0] + " than or equal to");
                    }
                    else {
                        act_operator.setText(Util.capitalizeFirst(ruleValue.operator));
                    }

                }
                else if(ruleValue.negate) {
                    switch (ruleValue.operator){
                        case "BETWEEN":
                            act_operator.setText("Is not between");
                            break;
                        case "EQUALS":
                            act_operator.setText("Not equals");
                            break;
                        default:
                            act_operator.setText(Util.capitalizeFirst(ruleValue.operator));
                    }
                }
            }
            else {
                if(ruleValue.predicateType.equals("boolean")){
                    if(ruleValue.value.equals("true")){
                        act_operator.setText("Is true");
                    }
                    else {
                        act_operator.setText("Is false");
                    }
                }
                else if(ruleValue.predicateType.equals("value-empty")){
                    if(ruleValue.negate == null){
                        act_operator.setText("Has no value");
                    }
                    else {
                        act_operator.setText("Has a value");
                    }
                }
                else if (ruleValue == null || !ruleValue.negate){
                    switch (ruleValue.match){
                        case "BEGIN":
                            act_operator.setText("Starts with");
                            break;
                        case "CONTAINS":
                            act_operator.setText("Contains");
                            break;
                        case "END":
                            act_operator.setText("Ends with");
                            break;

                    }
                }
                else {
                    switch (ruleValue.match){
                        case "BEGIN":
                            act_operator.setText("Does not start with");
                            break;
                        case "CONTAINS":
                            act_operator.setText("Does not contain");
                            break;
                        case "END":
                            act_operator.setText("Does not end with");
                            break;

                    }
                }

            }
            act_operator.setAdapter(adapter);

            switch (act_operator.getText().toString()){
                case "Is true":
                case "Is false":
                case "Has no value":
                case "Has a value":
                    tie_value.setVisibility(View.GONE);
                    break;
                case "Between":
                case "Is not between":
                    tie_value.setText(ruleValue.value);
                    tie_value.setVisibility(View.VISIBLE);
                    tie_rangeValue.setText(ruleValue.rangeValue);
                    tie_rangeValue.setVisibility(View.VISIBLE);
                    iv_and.setVisibility(View.VISIBLE);
                    break;
                default:
                    tie_value.setText(ruleValue.value);
                    tie_value.setVisibility(View.VISIBLE);
                    break;

            }


        }
    }
    // TODO: remove asset
    private void setDeviceAdapter(String deviceType) {
        devices = Device.getDeviceList().stream()
                .filter(device -> device.type.equals(deviceType)).collect(Collectors.toList());

        List<String> deviceNames = devices.stream().map(device -> device.name).collect(Collectors.toList());

        deviceNames.add(0, getText(R.string.any_of_this_type).toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_dropdown_item, deviceNames);
        act_devices.setHint(R.string.select);
        act_devices.setAdapter(adapter);
    }
}