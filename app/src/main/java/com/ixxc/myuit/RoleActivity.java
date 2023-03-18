package com.ixxc.myuit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.RoleItemAdapter;
import com.ixxc.myuit.Model.Role;

import java.util.List;

public class RoleActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    RecyclerView rv_role;
    Button btn_add_role, btn_cancel;
    EditText et_name, et_desc;
    ProgressBar pb_loading;
    LinearLayout add_role_layout;
    List<Role> roleSetList;
    Role newRoleSet;
    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("ROLE_OK");
        int status_code = bundle.getInt("UPDATE_ROLE");

        if (isOK) {
            setAdapter();
            rv_role.setVisibility(View.VISIBLE);
        }
        else if (status_code == 204) {
            cancelAdd(true);
            setAdapter();
            Toast.makeText(this, "Updated Successfully! " + status_code, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Something went wrong! " + status_code, Toast.LENGTH_SHORT).show();
        }

        return false;
    });

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
        InitVars();

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Roles");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void InitView() {
        rv_role = findViewById(R.id.rv_role);
        toolbar = findViewById(R.id.action_bar);
        btn_add_role = findViewById(R.id.btn_add_role);
        btn_cancel = findViewById(R.id.btn_cancel);
        add_role_layout = findViewById(R.id.add_role_layout);
        et_name = findViewById(R.id.et_role_name);
        et_desc = findViewById(R.id.et_role_desc);
        pb_loading = findViewById(R.id.pb_loading_2);
    }

    private void InitEvent() {
//        btn_delete_role.setOnClickListener(v -> {
////            JsonArray body = new JsonArray();
////            for (Role role: Role.getCompositeRoleList()) {
////                if(!role.id.equals(Role.getCompositeRoleList().get(pos_chosen).id)){
////                    JsonObject role_object = new JsonObject();
////                    role_object.addProperty("id",role.id);
////                    role_object.addProperty("name",role.name);
////                    role_object.addProperty("description",role.description);
////                    role_object.addProperty("composite",role.composite);
////                    Log.d("ROLE", role.compositeRoleIds.toString());
////                    JsonElement compositeRoleIds = new Gson().toJsonTree(role.compositeRoleIds);
////                    role_object.add("compositeRoleIds", compositeRoleIds);
////                    body.add(role_object);
////                }
////
////            }
////            for (Role role:Role.getRoleList()) {
////                JsonObject role_object = new JsonObject();
////                role_object.addProperty("id",role.id);
////                role_object.addProperty("name",role.name);
////                role_object.addProperty("description",role.description);
////                role_object.addProperty("composite",role.composite);
////                body.add(role_object);
////            }
////            Log.d("DEL", body.toString());
////            new Thread(()->{
////                boolean del_state =APIManager.updateRole(body);
////                if(del_state){
////                    APIManager.getRoles();
////
////                    Message msg = handler.obtainMessage();
////                    Bundle bundle = new Bundle();
////                    bundle.putBoolean("ROLE_OK", true);
////                    bundle.putBoolean("GONE", true);
////                    msg.setData(bundle);
////                    handler.sendMessage(msg);
////                }
////            }).start();
//            Log.d(GlobalVars.LOG_TAG, "DELETE");
//        });

        btn_add_role.setOnClickListener(v -> {
            addRoleSet();
        });

        btn_cancel.setOnClickListener(view -> {
            cancelAdd(true);
        });
    }

    private void InitVars() {
        roleSetList = Role.getCompositeRoleList();
    }

    private void setAdapter() {
        rv_role.setLayoutManager(new LinearLayoutManager(this));
        RoleItemAdapter adapter = new RoleItemAdapter(this, roleSetList);
        adapter.setClickListener((view, position, role) -> expandRoleLayout(view, role));
        rv_role.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void expandRoleLayout(View view, Role roleSet) {
        LinearLayout layout = view.findViewById(R.id.add_role_layout);

        ImageView iv_roles_expand = view.findViewById(R.id.iv_expand_1);

        EditText et_name = view.findViewById(R.id.et_role_name);
        EditText et_desc = view.findViewById(R.id.et_role_desc);
        et_name.setText(roleSet.name);
        et_desc.setText(roleSet.description);

        Button btn_save_role = view.findViewById(R.id.btn_update_role);
        Button btn_delete_role = view.findViewById(R.id.btn_delete_role);

        btn_save_role.setOnClickListener(view1 -> updateRoleSet(String.valueOf(et_name.getText()), String.valueOf(et_desc.getText()), roleSet, false));
        btn_delete_role.setOnClickListener(view2 -> updateRoleSet(String.valueOf(et_name.getText()), String.valueOf(et_desc.getText()), roleSet, true));

        addRoleToLayout(layout, roleSet);

        Animation a;
        if (layout.getVisibility() == View.VISIBLE) {
            a = new RotateAnimation(90.0f, 0.0f, 48, 18);
            UserInfoActivity.collapse(layout);
        }
        else {
            a = new RotateAnimation(0.0f, 90.0f, 48, 18);
            UserInfoActivity.expand(layout);
        }

        a.setDuration(100);
        a.setFillAfter(true);
        iv_roles_expand.startAnimation(a);
    }

    private void addRoleToLayout(LinearLayout layout, Role roleSet) {
        List<String> compositeRoles;
        if (roleSet == newRoleSet) {
            compositeRoles = newRoleSet.compositeRoleIds;
        } else {
            compositeRoles = roleSet.compositeRoleIds;
        }

        for (Role r : Role.getRoleList()) {
            if (layout.findViewWithTag(r.id) == null) {
                LayoutInflater vi = getLayoutInflater();
                View v = vi.inflate(R.layout.role_item_layout, null);

                CheckBox cb = v.findViewById(R.id.checkBox);
                cb.setText(r.name);
                cb.setTag(r.id);
                cb.setChecked(compositeRoles.contains(r.id));
                cb.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    String id = cb.getTag().toString();
                    if (isChecked) {
                        if (!compositeRoles.contains(id)) compositeRoles.add(id);
                    }
                    else compositeRoles.removeIf(roleId -> roleId.equals(id));
                });

                TextView tv = v.findViewById(R.id.textView10);
                tv.setText(r.description);
                tv.setOnClickListener(view1 -> cb.setChecked(!cb.isChecked()));

                layout.addView(v, 2);
            }
        }
    }

    private void addRoleSet() {
        if (newRoleSet == null) newRoleSet = new Role();

        if (Boolean.parseBoolean(String.valueOf(btn_add_role.getTag()))) {
            addRoleToLayout(add_role_layout, newRoleSet);

            cancelAdd(false);
        } else {
            String name = String.valueOf(et_name.getText());
            String desc = String.valueOf(et_desc.getText());

            updateRoleSet(name, desc, newRoleSet, false);
        }
    }

    private void updateRoleSet(String name, String desc, Role roleSet, boolean isDelete) {
        JsonArray body = new JsonArray();
        if(roleSet.compositeRoleIds.size() != 0 && !TextUtils.isEmpty(name)){
            btn_add_role.setEnabled(false);
            pb_loading.setVisibility(View.VISIBLE);

            // New role set
            if (!isDelete) {
                Role newRole = new Role(roleSet.id, name, desc, true, roleSet.compositeRoleIds);
                JsonObject o = newRole.toJSON();
                body.add(o);
            }

            for (Role role : Role.getRoleList()) { body.add(role.toJSON()); }

            for (Role role : Role.getCompositeRoleList()) {
                if (!role.id.equals(roleSet.id)) body.add(role.toJSON());
            }

            new Thread(() -> {
                int code = APIManager.updateRole(body);
                APIManager.getRoles();

                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("UPDATE_ROLE", code);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }).start();

        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Role name cannot be empty!", Toast.LENGTH_LONG).show();
        } else if (roleSet.compositeRoleIds.size() == 0) {
            Toast.makeText(getApplicationContext(), "No permissions have been checked!", Toast.LENGTH_LONG).show();
        }

        Log.d(GlobalVars.LOG_TAG, body.toString());
    }

    private void cancelAdd(boolean isCancel) {
        btn_add_role.setTag(isCancel);

        if (isCancel) {
            et_name.setText("");
            et_desc.setText("");
            btn_add_role.setEnabled(true);
            pb_loading.setVisibility(View.GONE);
            btn_add_role.setText("Create new");
            UserInfoActivity.collapse(add_role_layout);
        } else {
            btn_add_role.setText("Save");
            UserInfoActivity.expand(add_role_layout);
        }
    }

//    private ArrayList<String> isChecked(){
//        ArrayList<String> id = new ArrayList<>();
//        if(cb_r_admin.isChecked()){
//            id.add(Role.getIdByDescription("Read system settings, realms, and users"));
//        }
//        if(cb_r_assets.isChecked()){
//            id.add(Role.getIdByDescription("Read asset data"));
//        }
//        if(cb_r_logs.isChecked()){
//            id.add(Role.getIdByDescription("Read logs and log settings"));
//        }
//        if(cb_r_map.isChecked()){
//            id.add(Role.getIdByDescription("View map"));
//        }
//        if(cb_r_rules.isChecked()){
//            id.add(Role.getIdByDescription("Read rulesets"));
//        }
//        if(cb_r_users.isChecked()){
//            id.add(Role.getIdByDescription("Read limited set of user details for use in rules etc."));
//        }
//
//        if(cb_w_admin.isChecked()){
//            id.add(Role.getIdByDescription("Write system settings, realms, and users"));
//        }
//        if(cb_w_assets.isChecked()){
//            id.add(Role.getIdByDescription("Write asset data"));
//        }
//        if(cb_w_users.isChecked()){
//            id.add(Role.getIdByDescription("Write data of the authenticated user"));
//        }
//        if(cb_w_attributes.isChecked()){
//            id.add(Role.getIdByDescription("Write attribute data"));
//        }
//        if(cb_w_logs.isChecked()){
//            id.add(Role.getIdByDescription("Write log settings"));
//        }
//        if(cb_w_rules.isChecked()){
//            id.add(Role.getIdByDescription("Write rulesets (NOTE: effectively super-user access!)"));
//        }
//
//        return id;
//    }

//    private void Checked() {
//        for (String permissionID: Role.getCompositeRoleList().get(pos_chosen).compositeRoleIds) {
//            String name = Role.getNameByID(Role.getRoleList(),permissionID);
//            if(name!=null){
//                switch (name){
//                    case "read:rules":
//                        cb_r_rules.setChecked(true);
//                        break;
//                    case "read:admin":
//                        cb_r_admin.setChecked(true);
//                        break;
//                    case "read:assets":
//                        cb_r_assets.setChecked(true);
//                        break;
//                    case "read:logs":
//                        cb_r_logs.setChecked(true);
//                        break;
//                    case "read:users":
//                        cb_r_users.setChecked(true);
//                        break;
//                    case "read:map":
//                        cb_r_map.setChecked(true);
//                        break;
//                    case "write:admin":
//                        cb_w_admin.setChecked(true);
//                        break;
//                    case "write:assets":
//                        cb_w_assets.setChecked(true);
//                        break;
//                    case "write:rules":
//                        cb_w_rules.setChecked(true);
//                        break;
//                    case "write:attributes":
//                        cb_w_attributes.setChecked(true);
//                        break;
//                    case "write:logs":
//                        cb_w_logs.setChecked(true);
//                        break;
//                    case "write:user":
//                        cb_w_users.setChecked(true);
//                        break;
//                }
//            }
//        }
//    }

//    private void unChecked() {
//        cb_r_admin.setChecked(false);
//        cb_r_assets.setChecked(false);
//        cb_r_rules.setChecked(false);
//        cb_r_map.setChecked(false);
//        cb_r_logs.setChecked(false);
//        cb_r_users.setChecked(false);
//
//        cb_w_admin.setChecked(false);
//        cb_w_assets.setChecked(false);
//        cb_w_rules.setChecked(false);
//        cb_w_attributes.setChecked(false);
//        cb_w_logs.setChecked(false);
//        cb_w_users.setChecked(false);
//    }
}