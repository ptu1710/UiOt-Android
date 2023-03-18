package com.ixxc.myuit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.UserRoleAdapter;
import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.Model.LinkedDevice;
import com.ixxc.myuit.Model.Role;
import com.ixxc.myuit.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserInfoActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    LinearLayout pwd_layout, pwd_layout_1, pwd_layout_2, roles_layout, roles_layout_1, roles_layout_2;
    ImageView iv_pwd_expand, iv_roles_expand;
    CheckBox cb_active;
    TextInputLayout til_email, til_firstname, til_lastname, til_rePwd;
    EditText et_email, et_firstname, et_lastname, et_pwd, et_rePwd;
    AutoCompleteTextView act_realm_roles, act_roles;
    Button btn_custom_role_set, btn_linked_devices, btn_regenerate;
    ProgressBar pb_user_info;

    AlertDialog customRoleDialog;
    AlertDialog linkedDevicesDialog;
    UserRoleAdapter realmRolesAdapter, roleSetAdapter;
    User user;
    List<Role> realmRoleList;
    List<Role> roleList;
    List<Role> roleSetList;
    List<LinkedDevice> linkedDeviceList, newLinkedDeviceList = new ArrayList<>();

    boolean isGetDataDone = false;
    boolean isRealmRoleModified = false;
    boolean isPwdModified = false;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("USER");
        int updateCode = bundle.getInt("UPDATE_USER");

        if (isOK) {
            isGetDataDone = true;
            InitVars();
            showUserInfo();
        } else Toast.makeText(this, "Update with status: " + updateCode, Toast.LENGTH_SHORT).show();

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
        til_rePwd = findViewById(R.id.til_repwd);
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

        btn_custom_role_set.setOnClickListener(view -> {
            customRoleDialog.show();
        });

        btn_linked_devices.setOnClickListener(view -> {
            if (isGetDataDone) {
                for (LinkedDevice device : linkedDeviceList) {
                    int index = IntStream.range(0, Device.getAllDevices().size())
                            .filter(i -> Device.getAllDevices().get(i).id.equals(device.id.get("assetId").getAsString()))
                            .findFirst()
                            .orElse(-1);

                    if (index != -1) linkedDevicesDialog.getListView().setItemChecked(index, true);
                }
                linkedDevicesDialog.show();
            } else {
                Toast.makeText(UserInfoActivity.this, "Data is loading, please wait!", Toast.LENGTH_SHORT).show();
            }
        });

        et_rePwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals(et_pwd.getText().toString())) {
                    til_rePwd.setError("Your password does not match!");
                    isPwdModified = false;
                } else {
                    til_rePwd.setError(null);
                    isPwdModified = true;
                }
            }
        });
    }

    private void InitVars() {
        realmRoleList = user.getRealmRoles();

        linkedDeviceList = user.getLinkedDevices();
        newLinkedDeviceList.addAll(linkedDeviceList);

        roleList = user.getRoleList();
        roleSetList = user.getCompositeRoleList();
    }

    private void checkRoles() {
        List<String> compositeRoleIds = roleList.stream().map(r -> r.id).collect(Collectors.toList());

        for (Role role : roleSetList) {
            if (role.assigned) {
                for (String id : role.compositeRoleIds) {
                    int index = compositeRoleIds.indexOf(id);
                    customRoleDialog.getListView().setItemChecked(index, true);
                }
            }
        }

        for (Role role : roleList) {
            if (role.assigned) customRoleDialog.getListView().setItemChecked(roleList.indexOf(role), true);
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

        List<String> hiddenRealmRole = new ArrayList<>();
        hiddenRealmRole.add("uma_authorization");
        hiddenRealmRole.add("default-roles-master");
        hiddenRealmRole.add("admin");
        hiddenRealmRole.add("offline_access");

        // Get Realm Role List (String)
        ArrayList<Role> realmRoles = new ArrayList<>();
        for (Role role : realmRoleList) { if (!hiddenRealmRole.contains(role.name)) realmRoles.add(role); }

        // Set text for Realm Roles AutoCompleteView
        List<String> realmRolesName = new ArrayList<>();
        for (Role role : realmRoles) { if (role.assigned) realmRolesName.add(role.name); }
        act_realm_roles.setText(String.join(", ", realmRolesName));

        realmRolesAdapter = new UserRoleAdapter(this, R.layout.spinner_item, realmRoles, (v, role, isChecked) -> {
            isRealmRoleModified = true;
            role.assigned = isChecked;
        });

        act_realm_roles.setAdapter(realmRolesAdapter);

        List<String> compositeRoleIds = roleList.stream().map(r -> r.id).collect(Collectors.toList());
        roleSetAdapter = new UserRoleAdapter(this, R.layout.spinner_item, roleSetList, (v, role, isChecked) -> {
            role.assigned = isChecked;

            for (String id : role.compositeRoleIds) {
                int index = compositeRoleIds.indexOf(id);
                customRoleDialog.getListView().setItemChecked(index, isChecked);
                roleList.get(index).assigned = isChecked;
            }
        });
        act_roles.setAdapter(roleSetAdapter);

        customRoleDialog = customRoleDialog();
        customRoleDialog.create();
        checkRoles();

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

        for (Role role : roleList) rolesName[roleList.indexOf(role)] = role.name;

        return new AlertDialog.Builder(this)
                .setTitle("Select role(s)")
                .setPositiveButton("OK", (dialog12, id) -> { })
                .setMultiChoiceItems(rolesName, null, (dialog13, indexSelected, isChecked) -> {
                    Role selected = roleList.get(indexSelected);
                    selected.assigned = isChecked;
                    if (!isChecked) {
                        for (Role role : roleSetList) {
                            if (role.compositeRoleIds.contains(selected.id)) role.assigned = false;
                        }
                    }
                }).create();
    }

    private AlertDialog linkedDevicesDialog() {
        List<Device> devices = Device.getAllDevices();
        CharSequence[] devicesName = new CharSequence[devices.size()];

        for (Device device : devices) devicesName[devices.indexOf(device)] = device.name;

        return new AlertDialog.Builder(this)
                .setTitle("Select device(s) to link")
                .setPositiveButton("OK", (dialog12, id) -> { })
                .setMultiChoiceItems(devicesName, null, (dialog13, indexSelected, isChecked) -> {
                    Device device = devices.get(indexSelected);
                    if (isChecked) newLinkedDeviceList.add(LinkedDevice.LinkDevice(user, device));
                    else newLinkedDeviceList.removeIf(linkedDevice -> linkedDevice.id.get("assetId").getAsString().equals(device.id));
                }).create();
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
                if(interpolatedTime == 1) v.setVisibility(View.GONE);
                else {
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
        JsonObject body = user.toJson();
        body.addProperty("loading", false);
        body.addProperty("loaded", true);

        // User info
        boolean enabled = cb_active.isChecked();
        String email = String.valueOf(et_email.getText());
        String firstName = String.valueOf(et_firstname.getText());
        String lastName = String.valueOf(et_lastname.getText());

        String pwd = String.valueOf(et_pwd.getText());
        String rePwd = String.valueOf(et_rePwd.getText());

        body.addProperty("enabled", enabled);
        body.addProperty("email", email);
        body.addProperty("firstName", firstName);
        body.addProperty("lastName", lastName);

        // Modify linked devices
        JsonArray linkToUser = new JsonArray();
        for (LinkedDevice device : newLinkedDeviceList) {
            if (!linkedDeviceList.contains(device)) linkToUser.add(device.toJson());
        }

        JsonArray unlinkToUser = new JsonArray();
        for (LinkedDevice device : linkedDeviceList) {
            if (!newLinkedDeviceList.contains(device)) unlinkToUser.add(device.toJson());
        }

        // Modify Realm Roles
        JsonArray newRealmRoles = new JsonArray();
        for (Role role : realmRoleList) {
            if (role.assigned) {
                JsonObject o = role.toJSON();
                o.addProperty("assigned", true);
                newRealmRoles.add(o);
            }
        }

        // Modify Roles
        JsonArray newRoles = new JsonArray();
        for (Role roleSet : roleSetList) {
            if (roleSet.assigned) {
                JsonObject o = roleSet.toJSON();
                o.addProperty("assigned", true);
                newRoles.add(o);
            }

        }

        for (Role role : roleList) {
            if (role.assigned) {
                JsonObject o = role.toJSON();
                o.addProperty("assigned", true);
                newRoles.add(o);
            }
        }

        new Thread(() -> {
            int updateCode = APIManager.updateUserInfo(body);

            // Update password
            if (isPwdModified && !et_pwd.getText().toString().equals("")) {
                JsonObject o = new JsonObject();
                o.addProperty("value", et_pwd.getText().toString());
                APIManager.updatePassword(user.id, o);
            }

            // Modify linked devices
            if (linkToUser.size() > 0) { APIManager.setLinkedDevices(linkToUser); }
            if (unlinkToUser.size() > 0) { APIManager.setUnlinkedDevices(unlinkToUser); }

            // Modify Realm Roles
            if (isRealmRoleModified) APIManager.setRealmRoles(user.id, newRealmRoles);

            APIManager.setRoles(user.id, newRoles);

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("UPDATE_USER", updateCode);
            message.setData(bundle);
            handler.sendMessage(message);
        }).start();
    }
}