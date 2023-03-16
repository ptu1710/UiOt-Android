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
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.Model.LinkedDevice;
import com.ixxc.myuit.Model.Role;
import com.ixxc.myuit.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserInfoActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    LinearLayout pwd_layout, pwd_layout_1, pwd_layout_2, roles_layout, roles_layout_1, roles_layout_2;
    ImageView iv_pwd_expand, iv_roles_expand;
    CheckBox cb_active;
    TextInputLayout til_email, til_firstname, til_lastname;
    EditText et_email, et_firstname, et_lastname, et_pwd, et_rePwd;
    AutoCompleteTextView act_realm_roles, act_roles;
    Button btn_custom_role_set, btn_linked_devices, btn_regenerate;
    ProgressBar pb_user_info;

    AlertDialog customRoleDialog;
    AlertDialog linkedDevicesDialog;
    ArrayAdapter realmRolesAdapter, roleSetAdapter;
    User user;
    List<Role> realmRoleList, newRealmRoleList;
    List<Role> roleList, newRoleList = new ArrayList<>();
    List<Role> roleSetList;
    List<LinkedDevice> linkedDeviceList, newLinkedDeviceList;

    boolean isGetDataDone = false;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("USER");

        if (isOK) {
            isGetDataDone = true;
            InitVars();
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
            APIManager.getRoles();
            user = APIManager.getUser(user_id);
            user.setUserRoles(APIManager.getRoles(user_id));
            user.setRealmRoles(APIManager.getRealmRoles(user_id));
            user.setLinkedDevices(APIManager.getLinkedDevices(user_id));

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("USER", user != null);
            message.setData(bundle);
            handler.sendMessage(message);
        }).start();

        InitViews();
        InitEvents();

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        btn_regenerate = findViewById(R.id.btn_regenerate);
        et_pwd = findViewById(R.id.et_pwd);
        et_rePwd = findViewById(R.id.et_repwd);
        pwd_layout = findViewById(R.id.pwd_layout);
        roles_layout = findViewById(R.id.roles_layout);
        til_email = findViewById(R.id.til_email);
        til_firstname = findViewById(R.id.til_firstname);
        til_lastname = findViewById(R.id.til_lastname);
        pb_user_info = findViewById(R.id.pb_user_info);
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

        act_roles.setOnItemClickListener((adapterView, view, pos, l) -> {
            Role selectedRole = Role.getCompositeRoleByName(roleSetAdapter.getItem(pos).toString());

            List<String> rolesIdList = roleList.stream()
                    .map(r -> r.id)
                    .collect(Collectors.toList());

            customRoleDialog = customRoleDialog();
            customRoleDialog.create();
            for (String roleId : selectedRole.compositeRoleIds) {
                int index = rolesIdList.indexOf(roleId);
                Log.d(GlobalVars.LOG_TAG, "Index: " + index);
                customRoleDialog.getListView().setItemChecked(index, true);
            }
        });

        btn_custom_role_set.setOnClickListener(view -> {
            checkRoles();
            customRoleDialog.show();
        });

        btn_linked_devices.setOnClickListener(view -> {
            if (isGetDataDone) {
                for (LinkedDevice device : linkedDeviceList) {

                    int index = IntStream.range(0, Device.getAllDevices().size())
                            .filter(i -> Device.getAllDevices().get(i).id.equals(device.id.get("assetId").getAsString()))
                            .findFirst()
                            .orElse(-1);

                    if (index != -1) {
                        linkedDevicesDialog.getListView().setItemChecked(index, true);
                    }
                }
                linkedDevicesDialog.show();
            } else {
                Toast.makeText(UserInfoActivity.this, "Data is loading, please wait!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InitVars() {
        realmRoleList = user.getRealmRoles();
        roleList = user.getRoleList();
        roleSetList = user.getCompositeRoleList();
    }

    private void checkRoles() {
        List<String> rolesIdList = roleList.stream()
                .map(r -> r.id)
                .collect(Collectors.toList());

        for (Role role : roleSetList) {
            if (role.assigned) {
                Role role1 = Role.getCompositeRoleByName(role.name);
                for (String roleId : role1.compositeRoleIds) {
                    int index = rolesIdList.indexOf(roleId);
                    Log.d(GlobalVars.LOG_TAG, "Index: " + index);
                    customRoleDialog.getListView().setItemChecked(index, true);
                }
            }
        }

        for (Role role : roleList) {
            if (role.assigned) {
                customRoleDialog.getListView().setItemChecked(roleList.indexOf(role), true);
                if (!newRoleList.contains(role)) { newRoleList.add(role); }
            }
        }
    }

    private void showUserInfo() {
        actionBar.setTitle(user.username);
        cb_active.setChecked(user.enabled);

        if (user.serviceAccount) {
            et_firstname.setText(user.username);
            et_pwd.setText(user.secret);

            btn_regenerate.setVisibility(View.VISIBLE);

            et_lastname.setVisibility(View.GONE);
            et_email.setVisibility(View.GONE);
            et_rePwd.setVisibility(View.GONE);
        } else {
            et_firstname.setText(user.firstName);
            et_lastname.setText(user.lastName);
            et_email.setText(user.email);
        }

        newLinkedDeviceList = linkedDeviceList = user.getLinkedDevices();

        List<String> hiddenRealmRole = new ArrayList<>();
        hiddenRealmRole.add("uma_authorization");
        hiddenRealmRole.add("default-roles-master");
        hiddenRealmRole.add("admin");
        hiddenRealmRole.add("offline_access");

        // Get Realm Role List (String)
        List<String> realmRoles = new ArrayList<>();
        for (Role role : realmRoleList) {
            if (!hiddenRealmRole.contains(role.name)) {
                realmRoles.add(role.name);
                if (role.assigned) {
                    String text = String.valueOf(act_realm_roles.getText());
                    if (text.equals("")) { text += role.name; }
                    else { text = String.join(", ", text, role.name); }

                    act_realm_roles.setText(text);
                }
            }
        }

        // Get Role Set List (String)
        List<String> roleSets = new ArrayList<>();
        for (Role role : roleSetList) {
            if (role.composite) {
                roleSets.add(role.name);
                if (role.assigned) {
                    String text = String.valueOf(act_roles.getText());
                    if (text.equals("")) { text += role.name; }
                    else { text = String.join(", ", text, role.name); }

                    act_roles.setText(text);
                }
            }
        }

        realmRolesAdapter = new ArrayAdapter(this, R.layout.dropdown_item, realmRoles);
        act_realm_roles.setAdapter(realmRolesAdapter);

        roleSetAdapter = new ArrayAdapter(this, R.layout.dropdown_item, roleSets);
        act_roles.setAdapter(roleSetAdapter);

        customRoleDialog = customRoleDialog();
        customRoleDialog.create();

        linkedDevicesDialog = linkedDevicesDialog();
        linkedDevicesDialog.create();

        btn_linked_devices.setText(linkedDeviceList.size() - user.getNumofConsoles() + " device(s)");

        pb_user_info.setVisibility(View.GONE);

        til_email.setVisibility(View.VISIBLE);
        til_firstname.setVisibility(View.VISIBLE);
        til_lastname.setVisibility(View.VISIBLE);
        pwd_layout.setVisibility(View.VISIBLE);
        roles_layout.setVisibility(View.VISIBLE);
    }

    private AlertDialog customRoleDialog() {
        CharSequence[] rolesName = new CharSequence[roleList.size()];

        for (Role role : roleList) {
            rolesName[roleList.indexOf(role)] = role.name;
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select role(s)")
                .setMultiChoiceItems(rolesName, null, (dialog13, indexSelected, isChecked) -> {
                    Role role = roleList.get(indexSelected);
                    if (isChecked) {
                        newRoleList.add(role);
                    } else {
                        newRoleList.remove(role);
                    }
                })
                .setPositiveButton("OK", (dialog12, id) -> {}).create();
        return dialog;
    }

    private AlertDialog linkedDevicesDialog() {
        List<Device> devices = Device.getAllDevices();
        CharSequence[] devicesName = new CharSequence[devices.size()];

        for (Device device : devices) {
            devicesName[devices.indexOf(device)] = device.name;
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select device(s) to link")
                .setMultiChoiceItems(devicesName, null, (dialog13, indexSelected, isChecked) -> {
                    Device device = devices.get(indexSelected);
                    if (isChecked) {
                        newLinkedDeviceList.add(LinkedDevice.LinkDevice(user, device));
                    } else {
                        newLinkedDeviceList.removeIf(linkedDevice -> linkedDevice.id.get("assetId").getAsString().equals(device.id));
                    }
                })
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        Menu actionbarMenu = menu;

        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.save) {
            Toast.makeText(this, "OKOK", Toast.LENGTH_SHORT).show();
            save();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void save() {
        // User info

        // Roles (JsonArray)

        // Realm Roles (JsonArray)
        for (Role role : newRoleList) {
//            Log.d(GlobalVars.LOG_TAG, "save: " + role.name);
        }

        // Previous Realm Roles (JsonArray)

        // Previous Roles (JsonArray)

        // User Asset Links (JsonArray)
        for (LinkedDevice device :
                newLinkedDeviceList) {
            Log.d(GlobalVars.LOG_TAG, "save: " + device.assetName);
        }
        // Previous Asset Links (JsonArray)

    }
}