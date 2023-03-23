package com.ixxc.myuit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.RealmItemAdapter;
import com.ixxc.myuit.Model.Realm;

import java.util.List;

public class RealmActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    RecyclerView rv_realm;
    Button btn_add_realm, btn_cancel;
    SwitchCompat sw_enable;
    EditText et_name, et_f_name;
    ProgressBar pb_loading;
    LinearLayout add_realm_layout;
    List<Realm> realmList;
    boolean isModified = false;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("REALM_OK");
        int update_code = bundle.getInt("UPDATE_REALM");
        int delete_code = bundle.getInt("DELETE_REALM");
        int create_code = bundle.getInt("CREATE_REALM");

        realmList = Realm.getRealmList();
        if (isOK) {
            setAdapter();
            rv_realm.setVisibility(View.VISIBLE);
        }
        else if (update_code == 204 || delete_code == 204 || create_code == 204) {
            cancelAdd(true);
            setAdapter();
            Toast.makeText(this, "Successfully! ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Something went wrong! " + update_code, Toast.LENGTH_SHORT).show();
        }

        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm);

        new Thread(() -> {
            APIManager.getRealm();

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
        RealmItemAdapter adapter = new RealmItemAdapter(this, realmList);
        adapter.setClickListener((view, position, realm) -> expandRoleLayout(view, realm));
        rv_realm.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void expandRoleLayout(View view, Realm realm) {
        LinearLayout layout = view.findViewById(R.id.add_realm_layout);

        ImageView iv_expand = view.findViewById(R.id.iv_expand_1);

        EditText et_name = view.findViewById(R.id.et_realm_name);
        EditText et_f_name = view.findViewById(R.id.et_realm_f_name);
        SwitchCompat sw_enabled = view.findViewById(R.id.sw_enabled);

        et_name.setText(realm.name);
        et_name.setOnFocusChangeListener((view12, b) -> Toast.makeText(RealmActivity.this, "Cannot modify realm name!", Toast.LENGTH_LONG).show());

        et_f_name.setText(realm.displayName);
        et_f_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isModified =  true;
            }
        });

        sw_enabled.setChecked(realm.enabled);

        Button btn_save = view.findViewById(R.id.btn_update_realm);
        Button btn_delete = view.findViewById(R.id.btn_delete_realm);

        if(realm.name.equals("master")) btn_delete.setVisibility(View.GONE);

        btn_save.setOnClickListener(view1 -> {
            String f_name = String.valueOf(et_f_name.getText());
            boolean isEn = sw_enabled.isChecked();
            updateRealm(f_name, isEn, realm);
        });

        btn_delete.setOnClickListener(view2 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!");
            builder.setMessage("Delete this Realm?");
            builder.setPositiveButton("Delete", (dialogInterface, i) -> { deleteRealm(realm.name); });
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> { });

            builder.show();
        });

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
        iv_expand.startAnimation(a);
    }

    private void InitView() {
        rv_realm = findViewById(R.id.rv_realm);
        toolbar = findViewById(R.id.action_bar);
        btn_add_realm = findViewById(R.id.btn_add_realm);
        btn_cancel = findViewById(R.id.btn_cancel);
        sw_enable = findViewById(R.id.sw_enabled2);
        add_realm_layout = findViewById(R.id.add_realm_layout);
        et_name = findViewById(R.id.et_realm_name);
        et_f_name = findViewById(R.id.et_realm_f_name);
        pb_loading = findViewById(R.id.pb_loading_3);
    }

    private void InitEvent() {
        btn_add_realm.setOnClickListener(v -> addRealm());
        btn_cancel.setOnClickListener(view -> cancelAdd(true));
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

    private void addRealm() {
        if (Boolean.parseBoolean(String.valueOf(btn_add_realm.getTag()))) {
            cancelAdd(false);
        } else {
            String name = String.valueOf(et_name.getText());
            String f_name = String.valueOf(et_f_name.getText());
            boolean isEn = sw_enable.isChecked();

            Realm newRealm = new Realm(name, f_name, isEn);

            new Thread(() -> {
                int code = APIManager.createRealm(newRealm.toJsonMin());
                APIManager.getRealm();

                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("CREATE_REALM", code);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }).start();
        }
    }

    private void updateRealm(String new_f_name, boolean isEn, Realm realm) {
        JsonObject body = realm.toJsonFull();

       if(new_f_name.length() >= 3 && new_f_name.length() <= 255) {
            btn_add_realm.setEnabled(false);
            pb_loading.setVisibility(View.VISIBLE);

            body.addProperty("displayName", new_f_name);
            body.addProperty("enabled", isEn);

            new Thread(() -> {
                int code = APIManager.updateRealm(realm.name, body);
                APIManager.getRealm();

                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("UPDATE_REALM", code);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }).start();
        } else {
           Toast.makeText(RealmActivity.this, "Friendly name must be at least 3 and not more than 255 characters.", Toast.LENGTH_LONG).show();
       }
    }

    private void deleteRealm(String realmName) {
        new Thread(() -> {
            int code = APIManager.deleteRealm(realmName);
            APIManager.getRealm();

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("DELETE_REALM", code);
            message.setData(bundle);
            handler.sendMessage(message);
        }).start();
    }
}