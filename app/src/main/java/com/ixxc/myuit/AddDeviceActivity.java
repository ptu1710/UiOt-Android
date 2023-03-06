package com.ixxc.myuit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputLayout;
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Model.Model;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceActivity extends AppCompatActivity {
    AutoCompleteTextView act_type;
    AutoCompleteTextView act_device;
    TextInputLayout til_type;
    TextInputLayout til_device;

    List<String> modelsType = new ArrayList<>();
    List<String> modelsName = new ArrayList<>();

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        if (bundle != null) {
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.dropdown_item, modelsType);
            act_type.setAdapter(adapter);

            adapter = new ArrayAdapter(this, R.layout.dropdown_item, modelsName);
            act_device.setAdapter(adapter);
        }

        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        act_type = findViewById(R.id.act_type);
        act_device = findViewById(R.id.act_device);

        til_type = findViewById(R.id.til_type);
        til_device = findViewById(R.id.til_device);

        modelsType.add("Agent");
        modelsType.add("Asset");

        new Thread(() -> {
            List<Model> models = APIManager.getDeviceModels();

            for (Model model : models) {
                String name = model.assetDescriptor.get("name").getAsString();
                modelsName.add(name);
            }

            Message msg = handler.obtainMessage();
            msg.setData(new Bundle());
            handler.sendMessage(msg);
        }).start();
    }
}