package com.ixxc.uiot;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Model.CreateRuleReq;

import java.util.Arrays;
import java.util.List;

public class CreateRuleActivity extends AppCompatActivity {
    Button btn_next, btn_back;
    CreateRuleFragment_0 create_0 = new CreateRuleFragment_0(this);
    CreateRuleFragment_1 create_1 = new CreateRuleFragment_1(this);
    CreateRuleFragment_2 create_2 = new CreateRuleFragment_2(this);
    FragmentManager fm;
    int currentTabIndex = 0;

    public CreateRuleReq rule = new CreateRuleReq();

    Handler handler = new Handler(msg -> {
        if (msg.getData().getInt("CREATED") > -1) {
            Toast.makeText(this, "Rule created successfully", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        }

        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_rule);

        new Thread(APIManager::getDeviceModels).start();

        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragment_wizard, create_0).commit();

        btn_next = findViewById(R.id.btn_next);
        btn_back = findViewById(R.id.btn_back);

        btn_next.setOnClickListener(v -> {
            if (currentTabIndex < 2) {
                btn_next.setText(R.string.next);
                currentTabIndex++;
                changeTab(currentTabIndex);

                if (currentTabIndex >= 2) btn_next.setText(R.string.save);
            } else {
                createRule(rule);
            }
        });

        btn_back.setOnClickListener(view -> {
            if (currentTabIndex > 0) {
                currentTabIndex--;
                changeTab(currentTabIndex);
            } else {
                finish();
            }
        });
    }

    private void changeTab(int newTabIndex) {

        switch (newTabIndex) {
            case 0:
                fm.beginTransaction().replace(R.id.fragment_wizard, create_0).commit();
                break;
            case 1:
                fm.beginTransaction().replace(R.id.fragment_wizard, create_1).commit();
                break;
            case 2:
                fm.beginTransaction().replace(R.id.fragment_wizard, create_2).commit();
                break;
        }
    }

    private void createRule(CreateRuleReq rule) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", 0);
        jsonObject.addProperty("type", "realm");
        jsonObject.addProperty("name", rule.getRuleName());
        jsonObject.addProperty("lang", "JSON");
        jsonObject.addProperty("realm", "master");
        jsonObject.addProperty("rules", rule.toJson().toString());

        new Thread(() -> {
            int ruleId = APIManager.createRule(jsonObject);

            Log.d(GlobalVars.LOG_TAG, "Rule ID: " + ruleId);

            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("CREATED", ruleId);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }).start();
    }

    public List<String> getRuleOperator(Context ctx, String valueType) {
        switch (valueType) {
            case "positiveInteger":
            case "positiveNumber":
            case "number":
            case "TCP_IPPortNumber":
            case "direction":
            case "positiveInteger[][]":
                return Arrays.asList(ctx.getResources().getStringArray(R.array.number));
            case "consoleProviders":
            case "colourRGB":
            case "usernameAndPassword":
            case "oAuthGrant":
            case "multivaluedTextMap":
            case "websocketSubscription":
                return Arrays.asList(ctx.getResources().getStringArray(R.array.others));
            case "GEO_JSONPoint":
                return Arrays.asList(ctx.getResources().getStringArray(R.array.geo_point));
            case "boolean":
                return Arrays.asList(ctx.getResources().getStringArray(R.array.Boolean));
        }

        return Arrays.asList(ctx.getResources().getStringArray(R.array.text));
    }
}