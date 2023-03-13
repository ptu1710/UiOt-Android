package com.ixxc.myuit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Model.User;

public class UserInfoActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    LinearLayout pwd_layout_1, pwd_layout_2, roles_layout_1, roles_layout_2;
    ImageView iv_pwd_expand, iv_roles_expand;
    CheckBox cb_active;
    EditText et_email, et_firstname, et_lastname;
    AutoCompleteTextView act_realm_roles;
    User current_user;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("USER");

        if (isOK) {
            showUserInfo();
        }

        return false;
    });

    private void showUserInfo() {
        actionBar.setTitle(current_user.username);
        cb_active.setChecked(current_user.enabled);
        et_firstname.setText(current_user.firstName);
        et_lastname.setText(current_user.lastName);
        et_email.setText(current_user.email);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        String user_id = getIntent().getStringExtra("USER_ID");
        new Thread(() -> {
            current_user = APIManager.getUser(user_id);


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