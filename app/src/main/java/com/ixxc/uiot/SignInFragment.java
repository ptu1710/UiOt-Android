package com.ixxc.uiot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.ixxc.uiot.API.APIManager;

public class SignInFragment extends Fragment {
    Button btn_sign_in, btn_back;
    EditText et_usr, et_pwd;

    ProgressBar pb_loading;

    LoginActivity loginActivity;
    WebView webView;

    Handler loginHandler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("LOGIN");
        if (isOK) {
            startActivity(new Intent(loginActivity, HomeActivity.class));
            loginActivity.finish();
        }

        return false;
    });

    public SignInFragment() { }

    public SignInFragment(LoginActivity activity) {
        this.loginActivity = activity;
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
    }

    private void InitViews(View v) {
        btn_sign_in = v.findViewById(R.id.btn_sign_up_2);
        btn_back = v.findViewById(R.id.btn_back);
        et_usr = v.findViewById(R.id.et_user);
        et_pwd = v.findViewById(R.id.et_re_pwd);
        pb_loading = v.findViewById(R.id.pb_loading);
    }

    private void InitEvent() {
        btn_sign_in.setOnClickListener(view -> {
            et_usr.clearFocus();
            et_pwd.clearFocus();

            pb_loading.setVisibility(View.VISIBLE);
            btn_sign_in.setVisibility(View.GONE);
            String usr = String.valueOf(et_usr.getText());
            String pwd = String.valueOf(et_pwd.getText());
            getToken(usr, pwd);
        });

        btn_back.setOnClickListener(view -> loginActivity.replaceFragment(loginActivity.welcome));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void getToken(String usr, String pwd) {
        CookieManager cm = CookieManager.getInstance();
        cm.removeAllCookies(null);

        webView = new WebView(getContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains("&code=")) {
                    String code = url.split("&code=")[1];
                    getTokenByCode(code);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String usrScript = "document.getElementById('username').value='" + usr + "';";
                String pwdScript = "document.getElementById('password').value='" + pwd + "';";
                view.evaluateJavascript(usrScript, null);
                view.evaluateJavascript(pwdScript, null);
                view.evaluateJavascript("document.getElementsByTagName('form')[0].submit();", null);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        webView.loadUrl(GlobalVars.getCodeUrl);
    }

    private void getTokenByCode(String code) {
        final Message msg = loginHandler.obtainMessage();
        final Bundle bundle = new Bundle();

        new Thread(() -> {
            APIManager.getToken(code);

            bundle.putBoolean("LOGIN", true);
            msg.setData(bundle);
            loginHandler.sendMessage(msg);
        }).start();
    }
}