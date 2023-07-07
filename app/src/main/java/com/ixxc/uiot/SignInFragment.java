package com.ixxc.uiot;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.ixxc.uiot.API.APIManager;

public class SignInFragment extends Fragment {
    Button btn_sign_in, btn_back;
    EditText et_usr, et_pwd;
    ProgressBar pb_loading;
    LoginActivity parentActivity;

    Handler loginHandler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("LOGIN");
        if (isOK) {
            startActivity(new Intent(parentActivity, HomeActivity.class));
            parentActivity.finish();
        }

        return false;
    });

    public SignInFragment() { }

    public SignInFragment(LoginActivity activity) {
        this.parentActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitViews(view);
        InitEvent();

//        btn_sign_in.performClick();
    }

    private void InitViews(View v) {
        btn_sign_in = v.findViewById(R.id.btn_sign_in);
        btn_back = v.findViewById(R.id.btn_back);
        et_usr = v.findViewById(R.id.et_pwd);
        et_pwd = v.findViewById(R.id.et_re_pwd);
        pb_loading = v.findViewById(R.id.pb_loading);
    }

    private void InitEvent() {
        btn_sign_in.setOnClickListener(view -> {
            et_usr.clearFocus();
            et_pwd.clearFocus();

            pb_loading.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            String usr = String.valueOf(et_usr.getText());
            String pwd = String.valueOf(et_pwd.getText());
            getUserToken(usr, pwd);
        });

        btn_back.setOnClickListener(view -> parentActivity.replaceFragment(parentActivity.welcome));
    }

    private void getUserToken(String usr, String pwd) {
        Message msg = loginHandler.obtainMessage();
        Bundle bundle = new Bundle();

        new Thread(() -> {
            new APIManager().getUserToken(usr, pwd);

            bundle.putBoolean("LOGIN", true);
            msg.setData(bundle);
            loginHandler.sendMessage(msg);
        }).start();
    }
}