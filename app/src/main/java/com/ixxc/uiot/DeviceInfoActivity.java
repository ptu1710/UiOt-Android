package com.ixxc.uiot;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Adapter.AttributesAdapter;
import com.ixxc.uiot.Adapter.ConfigurationAdapter;
import com.ixxc.uiot.Interface.MetaItemListener;
import com.ixxc.uiot.Model.Attribute;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.MetaItem;
import com.ixxc.uiot.Model.Model;
import com.ixxc.uiot.Model.User;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceInfoActivity extends AppCompatActivity implements MetaItemListener {
    Toolbar toolbar;
    ActionBar actionBar;
    Menu actionbarMenu;
    RecyclerView rv_attribute;
    EditText et_name;
    TextInputLayout til_parent, til_name;
    ImageView iv_clear_parent;
    CheckBox cb_public;
    AutoCompleteTextView act_parent;
    Button btn_add_attribute;

    String device_id, selected_id;
    Device current_device;
    Model current_model;
    List<Attribute> attributeList;
    AttributesAdapter attributesAdapter;
    List<String> parentNames;
    List<MetaItem> metaItems;
    ArrayList<MetaItem> selectedMetaItems;
    User me;

    boolean isEditMode = false;

    @SuppressLint("NotifyDataSetChanged")
    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("DEVICE_OK");
        boolean isUpdated = bundle.getBoolean("UPDATE_DEVICE");

        if (isUpdated) {
            boolean isUpdateOK = bundle.getBoolean("UPDATE_OK");
            if (isUpdateOK) {
                attributesAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
            }

            isOK = isUpdateOK;
        }

        if (isOK) {
            metaItems = MetaItem.getMetaItemList();

            actionBar.setTitle(current_device.name);
            et_name.setText(current_device.name);
            cb_public.setChecked(current_device.accessPublicRead);

            selected_id = current_device.getParentId();
            act_parent.setText(selected_id);

            act_parent.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item, parentNames));

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

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void InitVars() {
        me = User.getMe();
        parentNames = Device.getDeviceNames();

        new Thread(() -> {
            APIManager.getDeviceModels();
            current_device = APIManager.getDevice(device_id);
            current_model = Model.getDeviceModel(current_device.type);
            APIManager.getMetaItem(null);

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("DEVICE_OK", true);
            message.setData(bundle);
            handler.sendMessage(message);
        }).start();
    }

    private void InitViews() {
        rv_attribute = findViewById(R.id.rv_attribute);
        et_name = findViewById(R.id.til_device_name);
        act_parent = findViewById(R.id.act_parent);
        til_name = findViewById(R.id.til_username);
        til_parent = findViewById(R.id.til_parent);
        iv_clear_parent = findViewById(R.id.iv_clear_parent);
        toolbar = findViewById(R.id.action_bar);
        cb_public = findViewById(R.id.cb_public);
        btn_add_attribute = findViewById(R.id.btn_add_attribute);
    }

    private void InitEvents() {
        btn_add_attribute.setOnClickListener(view -> {
            Dialog dialog = new Dialog(DeviceInfoActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.add_device_attribute);
            Window window = dialog.getWindow();

            if (window == null) return;
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            AutoCompleteTextView act_type = dialog.findViewById(R.id.act_type);
            AutoCompleteTextView act_value = dialog.findViewById(R.id.act_value);
            TextInputLayout til_value = dialog.findViewById(R.id.til_value);
            EditText et_name_1 = dialog.findViewById(R.id.et_name);
            Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
            Button btn_add = dialog.findViewById(R.id.btn_add);

            List<String> types = current_model.getOptional().stream()
                    .filter(item -> attributeList.stream().noneMatch(a -> a.name.equals(item.name)))
                    .map(item -> item.name)
                    .collect(Collectors.toList());

            act_type.setText(R.string.custom);
            types.add(0, "Custom");
            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(DeviceInfoActivity.this, R.layout.dropdown_item, types);
            act_type.setAdapter(typeAdapter);
            act_type.setOnItemClickListener((adapterView, view1, pos, l) -> {
                if (pos == 0) {
                    et_name_1.setVisibility(View.VISIBLE);
                    et_name_1.setVisibility(View.VISIBLE);
                } else {
                    et_name_1.setVisibility(View.GONE);
                    til_value.setVisibility(View.GONE);
                }
            });

            List<String> valueTypes = current_model.valueDescriptors;
            ArrayAdapter<String> valueTypeAdapter = new ArrayAdapter<>(DeviceInfoActivity.this, R.layout.dropdown_item, valueTypes);
            act_value.setAdapter(valueTypeAdapter);

            btn_add.setOnClickListener(view1 -> {
                String type = act_type.getText().toString();
                String name = et_name_1.getText().toString();
                String valueType = act_value.getText().toString();
                if (addAttribute(type, name, valueType)) dialog.dismiss();
                else Toast.makeText(this, "Some fields are empty!", Toast.LENGTH_SHORT).show();
            });

            btn_cancel.setOnClickListener(view2 -> dialog.dismiss());

            dialog.show();
        });

        act_parent.setOnItemClickListener((adapterView, view, i, l) -> {
            selected_id = getSelectedId(parentNames.get(i));
            act_parent.setSelection(0);
        });

        iv_clear_parent.setOnClickListener(view -> {
            act_parent.setText("");
            selected_id = "";
            act_parent.clearFocus();
        });
    }

    private boolean addAttribute(String type, String name, String valueType) {
        if (type.equals("Custom") && (name.equals("") || valueType.equals(""))) return false;

        Attribute attribute;
        if (type.equals("Custom")) attribute = new Attribute(name, valueType);
        else {
            attribute = current_model.getOptional(type);
            attribute.value = JsonParser.parseString("");
        }

        attributeList.add(attribute);
        attributesAdapter.notifyItemInserted(attributeList.size() - 1);

        return true;
    }

    private String getSelectedId(String s) {
        return s.substring(s.indexOf("(") + 1, s.indexOf(")"));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAttributes() {
        attributeList = current_device.getDeviceAttribute();

        attributesAdapter = new AttributesAdapter(attributeList, (v, position) -> {

                Dialog dlg = new Dialog(DeviceInfoActivity.this);
                dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dlg.setContentView(R.layout.add_configuration_items);
                Window window = dlg.getWindow();
                if (window == null) return;
                Button btn_cancel = dlg.findViewById(R.id.btn_cancel_configuration);
                btn_cancel.setOnClickListener(v12 -> dlg.dismiss());

                Button btn_add = dlg.findViewById(R.id.btn_add_configuration);
                btn_add.setOnClickListener(v1 -> {
                    for (MetaItem item : selectedMetaItems) {
                        if (MetaItem.getMetaType(item.name).equals("boolean")) {
                            attributeList.get(position).meta.addProperty(item.name, false);
                        } else {
                            attributeList.get(position).meta.addProperty(item.name, "");
                        }
                    }

                    attributesAdapter.notifyDataSetChanged();
                    dlg.dismiss();
                });

                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                RecyclerView rv_config_item= dlg.findViewById(R.id.rv_config_item);
                rv_config_item.setLayoutManager(new LinearLayoutManager(DeviceInfoActivity.this));

                JsonObject meta = attributeList.get(position).meta == null ? new JsonObject() : attributeList.get(position).meta;
                List<MetaItem> ms = metaItems.stream().filter(metaItem -> !meta.keySet().contains(metaItem.name)).collect(Collectors.toList());

                ConfigurationAdapter configurationAdapter = new ConfigurationAdapter(this, ms, DeviceInfoActivity.this);
                rv_config_item.setAdapter(configurationAdapter);
                dlg.show();
            });

        rv_attribute.setLayoutManager(new LinearLayoutManager(this));
        rv_attribute.setAdapter(attributesAdapter);
        rv_attribute.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (me.canWriteDevices()) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
            actionbarMenu = menu;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.edit || id == R.id.save) {
            editModeEnable();
            actionbarMenu.findItem(R.id.edit).setVisible(!isEditMode);
            actionbarMenu.findItem(R.id.save).setVisible(isEditMode);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void editModeEnable() {
        isEditMode = attributesAdapter.isEditMode = !isEditMode;

        if (isEditMode) {
            til_name.setVisibility(View.VISIBLE);
            til_parent.setVisibility(View.VISIBLE);
            iv_clear_parent.setVisibility(View.VISIBLE);
            cb_public.setVisibility(View.VISIBLE);
            btn_add_attribute.setVisibility(View.VISIBLE);

            et_name.setText(current_device.name);
            attributesAdapter.notifyDataSetChanged();
        } else {
            rv_attribute.clearFocus();

            JsonObject body = new JsonObject();

            current_device.path.clear();
            current_device.path.add(current_device.id);

            if (!selected_id.equals("")) {
                current_device.path.add(selected_id);
                body.addProperty("parentId", selected_id);
            }

            JsonElement path = new Gson().toJsonTree(current_device.path);
            body.add("path", path);

            Enumeration<String> keys = AttributesAdapter.changedAttributes.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                attributeList.add(AttributesAdapter.changedAttributes.get(key));
            }

            body.addProperty("id", current_device.id);
            body.addProperty("version",  current_device.version);
            body.addProperty("createdOn", current_device.createdOn);
            // Change name
            body.addProperty("name", String.valueOf(et_name.getText()));

            body.addProperty("accessPublicRead", cb_public.isChecked());
            body.addProperty("realm", current_device.realm);
            body.addProperty("type", current_device.type);

            // Change attributes
            for (Attribute a : attributeList) {
                current_device.attributes.add(a.name, a.toJson());
            }

            body.add("attributes", current_device.attributes);

            Log.d(GlobalVars.LOG_TAG, body.toString());

            // Commit device changes here
            new Thread(() -> {
                boolean updated = APIManager.updateDeviceInfo(device_id, body);
                current_device = APIManager.getDevice(device_id);

                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putBoolean("UPDATE_DEVICE", true);
                bundle.putBoolean("UPDATE_OK", updated);
                message.setData(bundle);
                handler.sendMessage(message);
            }).start();

            til_name.setVisibility(View.GONE);
            til_parent.setVisibility(View.GONE);
            iv_clear_parent.setVisibility(View.GONE);
            cb_public.setVisibility(View.GONE);
            btn_add_attribute.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMetaItemListener(ArrayList<MetaItem> metaItems) {
        selectedMetaItems = metaItems;
    }
}