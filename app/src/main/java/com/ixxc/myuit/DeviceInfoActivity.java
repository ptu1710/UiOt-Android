package com.ixxc.myuit;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.AttributesAdapter;
import com.ixxc.myuit.Model.Attribute;
import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.Model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceInfoActivity extends AppCompatActivity {
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

    AttributesAdapter attributesAdapter;
    List<String> parentNames;

    boolean isEditMode = false;

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
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void InitVars() {
        parentNames = Device.getDeviceNames();

        new Thread(() -> {
            current_device = APIManager.getDevice(device_id);
            APIManager.getDeviceModels();

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
            Dialog d = new Dialog(DeviceInfoActivity.this);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.add_device_attribute);
            Window window = d.getWindow();

            if (window == null) return;
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            AutoCompleteTextView act_type = d.findViewById(R.id.act_type);
            AutoCompleteTextView act_value = d.findViewById(R.id.act_value);

            List<Model> result = Model.getModelList().stream()
                    .filter(item -> item.assetDescriptor.get("name").getAsString().equals(current_device.type))
                    .collect(Collectors.toList());

            List<String> types = result.get(0).attributeDescriptors.stream()
                    .filter(item -> item.optional)
                    .map(item -> item.name)
                    .collect(Collectors.toList());

            types.add(0, "Custom");

            ArrayAdapter typeAdapter = new ArrayAdapter(DeviceInfoActivity.this, R.layout.dropdown_item, types);
            act_type.setAdapter(typeAdapter);

            List<String> valueType = result.get(0).valueDescriptors;
            ArrayAdapter valueTypeAdapter = new ArrayAdapter(DeviceInfoActivity.this, R.layout.dropdown_item, valueType);
            act_value.setAdapter(valueTypeAdapter);

            d.show();
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

    private String getSelectedId(String s) {
        return s.substring(s.indexOf("(") + 1, s.indexOf(")"));
    }

    private void showAttributes() {
        attributesAdapter = new AttributesAdapter(current_device.getDeviceAttribute());

        rv_attribute.setLayoutManager(new LinearLayoutManager(this));
        rv_attribute.setAdapter(attributesAdapter);

        rv_attribute.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        actionbarMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.edit:
            case R.id.save:
                editModeEnable();
                actionbarMenu.findItem(R.id.edit).setVisible(!isEditMode);
                actionbarMenu.findItem(R.id.save).setVisible(isEditMode);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

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
                current_device.attributes.add(key, AttributesAdapter.changedAttributes.get(key));
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
            body.add("attributes", current_device.attributes);

            // Commit device changes here
            new Thread(() ->{
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
}