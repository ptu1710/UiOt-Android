package com.ixxc.myuit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WelcomeFragment extends Fragment {
    Button btn_sign_in, btn_sign_up, btn_with_google;

    LoginActivity loginActivity;

    public WelcomeFragment() { }

    public WelcomeFragment(LoginActivity activity) {
        this.loginActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitViews(view);
        InitEvent();

        btn_sign_in.performClick();
    }

    private void InitViews(View v) {
        btn_sign_in = v.findViewById(R.id.btn_sign_in_1);
        btn_sign_up = v.findViewById(R.id.btn_sign_up);
        btn_with_google = v.findViewById(R.id.btn_with_google);
    }

    private void InitEvent() {
        btn_sign_in.setOnClickListener(view -> loginActivity.replaceFragment(loginActivity.sign_in));
        btn_sign_up.setOnClickListener(view -> loginActivity.replaceFragment(loginActivity.sign_up));
    }
}