package com.ixxc.myuit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Model.LinkedDevice;
import com.ixxc.myuit.Model.Role;
import com.ixxc.myuit.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfoActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    LinearLayout pwd_layout_1, pwd_layout_2, roles_layout_1, roles_layout_2;
    ImageView iv_pwd_expand, iv_roles_expand;
    CheckBox cb_active;
    EditText et_email, et_firstname, et_lastname;
    AutoCompleteTextView act_realm_roles, act_roles;
    Button btn_custom_role_set, btn_linked_devices;

    User current_user;
    AlertDialog customRoleDialog;
    AlertDialog linkedDevicesDialog;
    ArrayAdapter realmRolesAdapter, roleSetAdapter;

    List<Role> roleModels;
    List<LinkedDevice> linkedDeviceList;

    boolean isGetDataDone = false;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("USER");

        if (isOK) {
            isGetDataDone = true;
            showUserInfo();
        }

        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        String user_id = getIntent().getStringExtra("USER_ID");
        new Thread(() -> {
            current_user = APIManager.getUser(user_id);
            APIManager.getRoles();
            APIManager.getRealmRoles();
            APIManager.getLinkedDevices(user_id);

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("USER", current_user != null);
            message.setData(bundle);
            handler.sendMessage(message);
        }).start();

        InitViews();
        InitEvents();

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void InitEvents() {
        pwd_layout_1.setOnClickListener(view -> {
            boolean isExpanded = pwd_layout_2.getVisibility() == View.VISIBLE;

            Animation a;
            if (isExpanded) {
                a = new RotateAnimation(90.0f, 0.0f, 20, 18);
                collapse(pwd_layout_2);
            } else {
                a = new RotateAnimation(0.0f, 90.0f, 20, 18);
                expand(pwd_layout_2);
            }

            a.setDuration(100);
            a.setFillAfter(true);
            iv_pwd_expand.startAnimation(a);
        });

        roles_layout_1.setOnClickListener(view -> {
            boolean isExpanded = roles_layout_2.getVisibility() == View.VISIBLE;

            Animation a;
            if (isExpanded) {
                a = new RotateAnimation(90.0f, 0.0f, 20, 18);
                collapse(roles_layout_2);
            } else {
                a = new RotateAnimation(0.0f, 90.0f, 20, 18);
                expand(roles_layout_2);
            }

            a.setDuration(100);
            a.setFillAfter(true);
            iv_roles_expand.startAnimation(a);
        });

        act_roles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Role selectedRole = Role.getCompositeRoleByName(roleSetAdapter.getItem(pos).toString());

                List<String> rolesIdList = roleModels.stream()
                        .map(r -> r.id)
                        .collect(Collectors.toList());

                customRoleDialog = customRoleDialog();
                customRoleDialog.create();
                for (String roleId : selectedRole.compositeRoleIds) {
                    int index = rolesIdList.indexOf(roleId);
                    Log.d(GlobalVars.LOG_TAG, "Index: " + index);
                    customRoleDialog.getListView().setItemChecked(index, true);
                }
            }
        });

        btn_custom_role_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customRoleDialog.show();
            }
        });

        btn_linked_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGetDataDone) {
                    linkedDevicesDialog.show();
                } else {
                    Toast.makeText(UserInfoActivity.this, "Data is loading, please wait!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void InitViews() {
        toolbar = findViewById(R.id.action_bar);
        pwd_layout_1 = findViewById(R.id.pwd_layout_1);
        pwd_layout_2 = findViewById(R.id.pwd_layout_2);
        iv_pwd_expand = findViewById(R.id.iv_expand_1);
        iv_roles_expand = findViewById(R.id.iv_expand_2);
        roles_layout_1 = findViewById(R.id.roles_layout_1);
        roles_layout_2 = findViewById(R.id.roles_layout_2);
        cb_active = findViewById(R.id.cb_active);
        et_email = findViewById(R.id.et_email);
        et_firstname = findViewById(R.id.et_firstname);
        et_lastname = findViewById(R.id.et_lastname);
        act_realm_roles = findViewById(R.id.act_realm_roles);
        act_roles = findViewById(R.id.act_roles);
        btn_custom_role_set = findViewById(R.id.btn_custom_role_set);
        btn_linked_devices = findViewById(R.id.btn_linked_devices);
    }

    private void showUserInfo() {
        actionBar.setTitle(current_user.username);
        cb_active.setChecked(current_user.enabled);
        et_firstname.setText(current_user.firstName);
        et_lastname.setText(current_user.lastName);
        et_email.setText(current_user.email);

        linkedDeviceList = LinkedDevice.getLinkedDeviceList();

        List<String> hiddenRealmRole = new ArrayList<>();
        hiddenRealmRole.add("uma_authorization");
        hiddenRealmRole.add("default-roles-master");
        hiddenRealmRole.add("admin");
        hiddenRealmRole.add("offline_access");

        // Get Realm Role List (String)
        List<String> realmRoleList = new ArrayList<>();
        for (Role role : Role.getRealmRoleList()) {
            if (!hiddenRealmRole.contains(role.name)) {
                realmRoleList.add(role.name);
            }
        }

        // Get Role Set List (String)
        List<String> roleSetList = new ArrayList<>();
        for (Role role : Role.getCompositeRoleList()) {
            roleSetList.add(role.name);
        }

        realmRolesAdapter = new ArrayAdapter(this, R.layout.dropdown_item, realmRoleList);
        act_realm_roles.setAdapter(realmRolesAdapter);

        roleSetAdapter = new ArrayAdapter(this, R.layout.dropdown_item, roleSetList);
        act_roles.setAdapter(roleSetAdapter);

        customRoleDialog = customRoleDialog();
        customRoleDialog.create();

        linkedDevicesDialog = linkedDevicesDialog();
        linkedDevicesDialog.create();

        btn_linked_devices.setText(linkedDeviceList.size() + " device(s)");
    }

    private AlertDialog customRoleDialog() {
        roleModels = Role.getRoleList();
        CharSequence[] rolesName = new CharSequence[roleModels.size()];

        for (Role role : roleModels) {
            rolesName[roleModels.indexOf(role)] = role.name;
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select role(s)")
                .setMultiChoiceItems(rolesName, null, (dialog13, indexSelected, isChecked) -> { })
                .setPositiveButton("OK", (dialog12, id) -> Toast.makeText(UserInfoActivity.this, "OK", Toast.LENGTH_SHORT).show())
                .setNegativeButton("Cancel", (dialog1, id) -> Toast.makeText(UserInfoActivity.this, "Cancel", Toast.LENGTH_SHORT).show()).create();
        return dialog;
    }

    private AlertDialog linkedDevicesDialog() {
        CharSequence[] linkedDevicesName = new CharSequence[linkedDeviceList.size()];

        for (LinkedDevice device : linkedDeviceList) {
            linkedDevicesName[linkedDeviceList.indexOf(device)] = device.assetName;
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select device(s) to link")
                .setMultiChoiceItems(linkedDevicesName, null, (dialog13, indexSelected, isChecked) -> { })
                .setPositiveButton("OK", (dialog12, id) -> Toast.makeText(UserInfoActivity.this, "OK", Toast.LENGTH_SHORT).show())
                .setNegativeButton("Cancel", (dialog1, id) -> Toast.makeText(UserInfoActivity.this, "Cancel", Toast.LENGTH_SHORT).show()).create();
        return dialog;
    }

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(100);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(100);
        v.startAnimation(a);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}