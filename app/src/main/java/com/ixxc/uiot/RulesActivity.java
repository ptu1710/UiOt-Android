package com.ixxc.uiot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Adapter.RuleAdapter;
import com.ixxc.uiot.Interface.RecyclerViewItemListener;
import com.ixxc.uiot.Model.Rule;

import java.util.List;

public class RulesActivity extends AppCompatActivity {
    RecyclerView rv_rules;
    ImageView iv_add;
    SwipeRefreshLayout srl_rules;
    List<Rule> ruleList;
    RuleAdapter adapter;
    ActivityResultLauncher<Intent> mGetContent;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("GET_RULES");
        boolean isUpdated = bundle.getBoolean("UPDATE_RULES");

        if (isOK) {
            adapter = new RuleAdapter(this, ruleList, new RecyclerViewItemListener() {
                @Override
                public void onItemClicked(View v, int pos) {
                    Log.d(GlobalVars.LOG_TAG, "onItemClicked: " + ruleList.get(pos).getName());
                }

                @Override
                public void onItemLongClicked(View v, int pos) { }
            });

            rv_rules.setLayoutManager(new LinearLayoutManager(this));
            rv_rules.setHasFixedSize(true);
            rv_rules.setAdapter(adapter);
        } else if (isUpdated) {
            adapter.setRuleList(ruleList);
            srl_rules.setRefreshing(false);
        }

        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null && result.getResultCode() == RESULT_OK) {
                Log.d(GlobalVars.LOG_TAG, "onCreate: HERE");
                srl_rules.setRefreshing(true);
                new Thread(() -> {
                    ruleList = APIManager.queryRules();

                    Message message = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("UPDATE_RULES", true);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }).start();
            }
        });

        new Thread(() -> {
            ruleList = APIManager.queryRules();

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("GET_RULES", true);
            message.setData(bundle);
            handler.sendMessage(message);
        }).start();

        rv_rules = findViewById(R.id.rv_rules);
        iv_add = findViewById(R.id.iv_add);
        srl_rules = findViewById(R.id.srl_rules);

        iv_add.setOnClickListener(v -> {
            Intent intent = new Intent(RulesActivity.this, CreateRuleActivity.class);
            mGetContent.launch(intent);
        });

        srl_rules.setOnRefreshListener(() -> srl_rules.setRefreshing(false));
    }
}