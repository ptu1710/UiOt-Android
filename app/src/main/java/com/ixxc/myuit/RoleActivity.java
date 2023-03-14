package com.ixxc.myuit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.RoleItemAdapter;
import com.ixxc.myuit.Adapter.UserItemAdapter;
import com.ixxc.myuit.Model.Role;

import java.util.ArrayList;
import java.util.List;

public class RoleActivity extends AppCompatActivity {
    RecyclerView rv_Role;
    RelativeLayout layout_Permissions;
    CheckBox cb_r_admin, cb_r_assets, cb_r_logs,cb_r_map,cb_r_rules,cb_r_users
            ,cb_w_admin, cb_w_assets, cb_w_attributes, cb_w_logs, cb_w_rules, cb_w_users;

    List<Role> roles = new ArrayList<>();
    List<Role> roles1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);
        InitView();
        InitEvent();

    }

    private void InitEvent() {
        new Thread(()->{
            roles = Role.getCompositeRoleList();
            roles1 = Role.getRoleList();

            rv_Role.setLayoutManager(new LinearLayoutManager(this));
            RoleItemAdapter adapter = new RoleItemAdapter(getApplicationContext(), roles);
            adapter.setClickListener(new UserItemAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    unChecked();
                    layout_Permissions.setVisibility(View.VISIBLE);
                    for (String permissionID: roles.get(position).compositeRoleIds) {
                        String name = Role.getNameByID(roles1,permissionID);
                        switch (name){
                            case "read:rules":
                                cb_r_rules.setChecked(true);
                                break;
                            case "read:admin":
                                cb_r_admin.setChecked(true);
                                break;
                            case "read:assets":
                                cb_r_assets.setChecked(true);
                                break;
                            case "read:logs":
                                cb_r_logs.setChecked(true);
                                break;
                            case "read:users":
                                cb_r_users.setChecked(true);
                                break;
                            case "read:map":
                                cb_r_map.setChecked(true);
                                break;
                            case "write:admin":
                                cb_w_admin.setChecked(true);
                                break;
                            case "write:assets":
                                cb_w_assets.setChecked(true);
                                break;
                            case "write:rules":
                                cb_w_rules.setChecked(true);
                                break;
                            case "write:attributes":
                                cb_w_attributes.setChecked(true);
                                break;
                            case "write:logs":
                                cb_w_logs.setChecked(true);
                                break;
                            case "write:user":
                                cb_w_users.setChecked(true);
                                break;

                        }
                    }



                }
            });
            rv_Role.setAdapter(adapter);

        }).start();


    }

    private void unChecked() {
        cb_r_admin.setChecked(false);
        cb_r_assets.setChecked(false);
        cb_r_rules.setChecked(false);
        cb_r_map.setChecked(false);
        cb_r_logs.setChecked(false);
        cb_r_users.setChecked(false);

        cb_w_admin.setChecked(false);
        cb_w_assets.setChecked(false);
        cb_w_rules.setChecked(false);
        cb_w_attributes.setChecked(false);
        cb_w_logs.setChecked(false);
        cb_w_users.setChecked(false);
    }

    private void InitView() {
        rv_Role = (RecyclerView) findViewById(R.id.rv_role);
        layout_Permissions = (RelativeLayout) findViewById(R.id.layout_permissions);
        cb_r_admin = (CheckBox) findViewById(R.id.cb_r_admin);
        cb_r_assets = (CheckBox) findViewById(R.id.cb_r_asset);
        cb_r_logs = (CheckBox) findViewById(R.id.cb_r_logs);
        cb_r_map = (CheckBox) findViewById(R.id.cb_r_map);
        cb_r_rules= (CheckBox) findViewById(R.id.cb_r_rules);
        cb_r_users = (CheckBox) findViewById(R.id.cb_r_users);

        cb_w_admin = (CheckBox) findViewById(R.id.cb_w_admin);
        cb_w_assets = (CheckBox) findViewById(R.id.cb_w_asset);
        cb_w_logs = (CheckBox) findViewById(R.id.cb_w_logs);
        cb_w_attributes = (CheckBox) findViewById(R.id.cb_w_attributes);
        cb_w_rules= (CheckBox) findViewById(R.id.cb_w_rules);
        cb_w_users = (CheckBox) findViewById(R.id.cb_w_users);

    }
}