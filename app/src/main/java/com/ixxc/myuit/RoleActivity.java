package com.ixxc.myuit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.RoleItemAdapter;
import com.ixxc.myuit.Adapter.UserItemAdapter;
import com.ixxc.myuit.Model.Role;

import java.util.ArrayList;
import java.util.List;

public class RoleActivity extends AppCompatActivity {
    RecyclerView rv_Role;
    RelativeLayout layout_Permissions,layout_del_save,layout_cancel_create;
    CheckBox cb_r_admin, cb_r_assets, cb_r_logs,cb_r_map,cb_r_rules,cb_r_users
            ,cb_w_admin, cb_w_assets, cb_w_attributes, cb_w_logs, cb_w_rules, cb_w_users;

    Integer pos_chosen = null;
    TextView tv_save,tv_delete,tv_add,tv_cancel,tv_create;
    TextInputEditText ti_role,ti_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);
        new Thread(()->{
            APIManager.getRoles();

            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("ROLE_OK", true);
            msg.setData(bundle);
            handler.sendMessage(msg);

        }).start();
        InitView();
        InitEvent();

    }

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isRole = bundle.getBoolean("ROLE_OK");
        boolean isUpdated = bundle.getBoolean("UPDATE_OK");
        boolean isGone = bundle.getBoolean("GONE");

        if (isRole) {
            setAdapter();
        }
        else if (isUpdated) {
            unChecked();
            Checked();
        }
        if (isGone) {
            layout_Permissions.setVisibility(View.GONE);
            layout_del_save.setVisibility(View.GONE);
            layout_cancel_create.setVisibility(View.GONE);

        }
        return false;
    });

    private void InitEvent() {
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonArray body = new JsonArray();
                for (Role role: Role.getCompositeRoleList()) {
                    JsonObject role_object = new JsonObject();
                    role_object.addProperty("id",role.id);
                    role_object.addProperty("name", String.valueOf(ti_role.getText()));
                    role_object.addProperty("description", String.valueOf(ti_description.getText()));
                    role_object.addProperty("composite",role.composite);
                    JsonElement compositeRoleIds;
                    if(role.id.equals(Role.getCompositeRoleList().get(pos_chosen).id)){
                        compositeRoleIds = new Gson().toJsonTree(isChecked());
                    }
                    else {
                        compositeRoleIds = new Gson().toJsonTree(role.compositeRoleIds);
                    }
                    role_object.add("compositeRoleIds", compositeRoleIds);
                    body.add(role_object);
                }
                for (Role role:Role.getRoleList()) {
                    JsonObject role_object = new JsonObject();
                    role_object.addProperty("id",role.id);
                    role_object.addProperty("name",role.name);
                    role_object.addProperty("description",role.description);
                    role_object.addProperty("composite",role.composite);
                    body.add(role_object);
                }
                Log.d("SAVE", body.toString());
                new Thread(()->{
                    Boolean save_state =APIManager.updateRole(body);
                    if(save_state){
                        APIManager.getRoles();

                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("ROLE_OK", true);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }).start();
            }

        });

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonArray body = new JsonArray();
                for (Role role: Role.getCompositeRoleList()) {
                    if(!role.id.equals(Role.getCompositeRoleList().get(pos_chosen).id)){
                        JsonObject role_object = new JsonObject();
                        role_object.addProperty("id",role.id);
                        role_object.addProperty("name",role.name);
                        role_object.addProperty("description",role.description);
                        role_object.addProperty("composite",role.composite);
                        Log.d("ROLE", role.compositeRoleIds.toString());
                        JsonElement compositeRoleIds = new Gson().toJsonTree(role.compositeRoleIds);
                        role_object.add("compositeRoleIds", compositeRoleIds);
                        body.add(role_object);
                    }

                }
                for (Role role:Role.getRoleList()) {
                    JsonObject role_object = new JsonObject();
                    role_object.addProperty("id",role.id);
                    role_object.addProperty("name",role.name);
                    role_object.addProperty("description",role.description);
                    role_object.addProperty("composite",role.composite);
                    body.add(role_object);
                }
                Log.d("DEL", body.toString());
                new Thread(()->{
                    Boolean del_state =APIManager.updateRole(body);
                    if(del_state){
                        APIManager.getRoles();

                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("ROLE_OK", true);
                        bundle.putBoolean("GONE", true);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }).start();

            }
        });

        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_Permissions.setVisibility(View.VISIBLE);
                layout_del_save.setVisibility(View.GONE);
                layout_cancel_create.setVisibility(View.VISIBLE);
                ti_role.setText("");
                ti_description.setText("");
                unChecked();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_Permissions.setVisibility(View.GONE);
                layout_del_save.setVisibility(View.GONE);
                layout_cancel_create.setVisibility(View.GONE);
            }
        });

        tv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonArray body = new JsonArray();

                if(isChecked()!=null || ti_role.getText() != null){

                    for (Role role: Role.getCompositeRoleList()) {
                        JsonObject role_object = new JsonObject();
                        role_object.addProperty("id",role.id);
                        role_object.addProperty("name",role.name);
                        role_object.addProperty("description",role.description);
                        role_object.addProperty("composite",role.composite);
                        Log.d("ROLE", role.compositeRoleIds.toString());
                        JsonElement compositeRoleIds = new Gson().toJsonTree(role.compositeRoleIds);
                        role_object.add("compositeRoleIds", compositeRoleIds);
                        body.add(role_object);

                    }

                    JsonObject new_object = new JsonObject();
                    new_object.addProperty("composite",true);
                    new_object.addProperty("name", String.valueOf(ti_role.getText()));
                    JsonElement new_compositeRoleIds = new Gson().toJsonTree(isChecked());
                    new_object.add("compositeRoleIds",new_compositeRoleIds);
                    new_object.addProperty("description", String.valueOf(ti_description.getText()));
                    body.add(new_object);


                    for (Role role:Role.getRoleList()) {
                        JsonObject role_object = new JsonObject();
                        role_object.addProperty("id",role.id);
                        role_object.addProperty("name",role.name);
                        role_object.addProperty("description",role.description);
                        role_object.addProperty("composite",role.composite);
                        body.add(role_object);
                    }
                    Log.d("CREATE", body.toString());
                    new Thread(()->{
                        Boolean create_state =APIManager.updateRole(body);
                        if(create_state){
                            APIManager.getRoles();

                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("ROLE_OK", true);
                            bundle.putBoolean("GONE", true);
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        }
                    }).start();
                }


            }
        });



    }


    private void setAdapter() {
        rv_Role.setLayoutManager(new LinearLayoutManager(this));
        RoleItemAdapter adapter = new RoleItemAdapter(getApplicationContext(), Role.getCompositeRoleList());
        adapter.setClickListener(new UserItemAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                pos_chosen = position;
                layout_Permissions.setVisibility(View.VISIBLE);
                layout_del_save.setVisibility(View.VISIBLE);
                layout_cancel_create.setVisibility(View.GONE);
                ti_role.setText(Role.getCompositeRoleList().get(position).name);
                ti_description.setText(Role.getCompositeRoleList().get(position).description);

                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putBoolean("UPDATE_OK", true);
                msg.setData(bundle);
                handler.sendMessage(msg);

            }
        });
        rv_Role.setAdapter(adapter);
    }

    private ArrayList<String> isChecked(){
        ArrayList<String> id = new ArrayList<>();
        if(cb_r_admin.isChecked()){
            id.add(Role.getIdByDescription("Read system settings, realms, and users"));
        }
        if(cb_r_assets.isChecked()){
            id.add(Role.getIdByDescription("Read asset data"));
        }
        if(cb_r_logs.isChecked()){
            id.add(Role.getIdByDescription("Read logs and log settings"));
        }
        if(cb_r_map.isChecked()){
            id.add(Role.getIdByDescription("View map"));
        }
        if(cb_r_rules.isChecked()){
            id.add(Role.getIdByDescription("Read rulesets"));
        }
        if(cb_r_users.isChecked()){
            id.add(Role.getIdByDescription("Read limited set of user details for use in rules etc."));
        }

        if(cb_w_admin.isChecked()){
            id.add(Role.getIdByDescription("Write system settings, realms, and users"));
        }
        if(cb_w_assets.isChecked()){
            id.add(Role.getIdByDescription("Write asset data"));
        }
        if(cb_w_users.isChecked()){
            id.add(Role.getIdByDescription("Write data of the authenticated user"));
        }
        if(cb_w_attributes.isChecked()){
            id.add(Role.getIdByDescription("Write attribute data"));
        }
        if(cb_w_logs.isChecked()){
            id.add(Role.getIdByDescription("Write log settings"));
        }
        if(cb_w_rules.isChecked()){
            id.add(Role.getIdByDescription("Write rulesets (NOTE: effectively super-user access!)"));
        }

        return id;
    }
    private void Checked() {
        for (String permissionID: Role.getCompositeRoleList().get(pos_chosen).compositeRoleIds) {
            String name = Role.getNameByID(Role.getRoleList(),permissionID);
            if(name!=null){
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
        layout_del_save = (RelativeLayout) findViewById(R.id.layout_save);
        layout_cancel_create = (RelativeLayout) findViewById(R.id.layout_create);

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

        tv_add = (TextView) findViewById(R.id.tv_add_role);
        tv_save = (TextView) findViewById(R.id.tv_save_role);
        tv_delete = (TextView) findViewById(R.id.tv_del_role);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel_role);
        tv_create = (TextView) findViewById(R.id.tv_create_role);

        ti_role = (TextInputEditText) findViewById(R.id.ti_role);
        ti_description = (TextInputEditText) findViewById(R.id.ti_description);

    }
}