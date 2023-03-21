package com.ixxc.myuit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.RealmItemAdapter;
import com.ixxc.myuit.Adapter.RoleItemAdapter;
import com.ixxc.myuit.Model.Realm;
import com.ixxc.myuit.Model.Role;

import java.util.List;

public class RealmActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    RecyclerView rv_realm;
    Button btn_add_realm, btn_cancel;
    EditText et_name, et_f_name;
    ProgressBar pb_loading;
    LinearLayout add_realm_layout;
    List<Realm> realmSetList;
    Realm newRealmSet;
    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("REALM_OK");
        int status_code = bundle.getInt("UPDATE_ROLE");

        if (isOK) {
            setAdapter();
            rv_realm.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_realm);

        new Thread(()->{
            APIManager.getRealm();

            realmSetList = Realm.getRealmList();
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("REALM_OK", true);
            msg.setData(bundle);
            handler.sendMessage(msg);

        }).start();
        InitView();
        InitEvent();


        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Realms");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    private void setAdapter() {
        rv_realm.setLayoutManager(new LinearLayoutManager(this));
        RealmItemAdapter adapter = new RealmItemAdapter(this, realmSetList);
        adapter.setClickListener((view, position, realm) -> expandRoleLayout(view, realm));
        rv_realm.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    private void expandRoleLayout(View view, Realm realmSet) {
        LinearLayout layout = view.findViewById(R.id.add_realm_layout);

        ImageView iv_roles_expand = view.findViewById(R.id.iv_expand_1);

        EditText et_name = view.findViewById(R.id.et_realm_name);
        EditText et_f_name = view.findViewById(R.id.et_realm_f_name);
        Switch sw_enabled = view.findViewById(R.id.sw_enabled);
        et_name.setText(realmSet.name);
        et_f_name.setText(realmSet.displayName);
        if(realmSet.enabled){
            sw_enabled.setChecked(true);
        }
        Button btn_save_role = view.findViewById(R.id.btn_update_realm);
        Button btn_delete_role = view.findViewById(R.id.btn_delete_realm);
        if(realmSet.name.equals("master")){
            btn_delete_role.setText("");
            btn_delete_role.setEnabled(false);
        }


        btn_save_role.setOnClickListener(view1 -> updateRealmSet(String.valueOf(et_name.getText()), String.valueOf(et_f_name.getText()), realmSet, false));
        btn_delete_role.setOnClickListener(view2 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("Delete this role?");
            builder.setPositiveButton("Delete", (dialogInterface, i) -> updateRealmSet(String.valueOf(et_name.getText()), String.valueOf(et_f_name.getText()), realmSet, true));
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {});

            builder.show();
        });

//        addRealmToLayout(layout, realmSet);

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


    private void InitEvent() {
        btn_add_realm.setOnClickListener(v -> {
            addRealmSet();
        });

        btn_cancel.setOnClickListener(view -> {
            cancelAdd(true);
        });

    }

    private void cancelAdd(boolean isCancel) {
        btn_add_realm.setTag(isCancel);

        if (isCancel) {
            et_name.setText("");
            et_f_name.setText("");
            btn_add_realm.setEnabled(true);
            pb_loading.setVisibility(View.GONE);
            btn_add_realm.setText("Create new");
            UserInfoActivity.collapse(add_realm_layout);
        } else {
            btn_add_realm.setText("Save");
            UserInfoActivity.expand(add_realm_layout);
        }
    }

    private void addRealmSet() {
        if (newRealmSet == null) newRealmSet = new Realm();

        if (Boolean.parseBoolean(String.valueOf(btn_add_realm.getTag()))) {
            addRealmToLayout(add_realm_layout, newRealmSet);

            cancelAdd(false);
        } else {
            String name = String.valueOf(et_name.getText());
            String f_name = String.valueOf(et_f_name.getText());

            updateRealmSet(name, f_name, newRealmSet, false);
        }

    }

    private void updateRealmSet(String name, String f_name, Realm realmSet, boolean isDelete) {
        JsonArray body = new JsonArray();
       /* if(realmSet.compositeRoleIds.size() != 0 && !TextUtils.isEmpty(name)){
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
        } else if (realmSet.compositeRoleIds.size() == 0) {
            Toast.makeText(getApplicationContext(), "No permissions have been checked!", Toast.LENGTH_LONG).show();
        }*/

        Log.d(GlobalVars.LOG_TAG, body.toString());
    }

    private void addRealmToLayout(LinearLayout layout, Realm realmSet) {
        for (Realm r : Realm.getRealmList()) {
            if (layout.findViewWithTag(r.id) == null) {
                LayoutInflater vi = getLayoutInflater();
                View v = vi.inflate(R.layout.realmset_item_layout, null);

                TextInputEditText ti_name = v.findViewById(R.id.et_realm_name);
                TextInputEditText ti_f_name = v.findViewById(R.id.et_realm_f_name);
                Switch sw_enabled = v.findViewById(R.id.sw_enabled);

                ti_name.setText(realmSet.name);
                ti_f_name.setText(realmSet.displayName);
                if(realmSet.enabled){
                    sw_enabled.setChecked(true);
                }
                else {
                    sw_enabled.setChecked(false);
                }

            }
        }
    }

    private void InitView() {
        rv_realm = findViewById(R.id.rv_realm);
        toolbar = findViewById(R.id.action_bar);
        btn_add_realm = findViewById(R.id.btn_add_realm);
        btn_cancel = findViewById(R.id.btn_cancel);
        add_realm_layout = findViewById(R.id.add_realm_layout);
        et_name = findViewById(R.id.et_realm_name);
        et_f_name = findViewById(R.id.et_realm_f_name);
        pb_loading = findViewById(R.id.pb_loading_3);
    }
}