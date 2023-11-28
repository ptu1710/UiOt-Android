package com.ixxc.uiot;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import android.widget.TextView;

import org.w3c.dom.Text;

public class ResetPwdFragment extends Fragment {
    LoginActivity loginActivity;
    WebView webView;
    Button btn_submit;
    TextView tv_back, tv_desc;
    EditText et_usr;
    ProgressBar pb_loading;

    public ResetPwdFragment() { }

    public ResetPwdFragment(LoginActivity activity) {
        this.loginActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_pwd, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitViews(view);
        InitEvent();
    }

    private void InitViews(View v) {
        et_usr = v.findViewById(R.id.et_user);
        btn_submit = v.findViewById(R.id.btn_submit);
        tv_back = v.findViewById(R.id.tv_back);
        tv_desc = v.findViewById(R.id.tv_desc);
        pb_loading = v.findViewById(R.id.pb_loading);
    }

    private void InitEvent() {
        btn_submit.setOnClickListener(view -> {
            String username = et_usr.getText().toString();
            if (username.length() > 0) {
                doResetPwd(username, view);
                pb_loading.setVisibility(View.VISIBLE);
                btn_submit.setVisibility(View.INVISIBLE);
            } else {
                et_usr.setError("Please enter your username");
            }
        });
        tv_back.setOnClickListener(view -> loginActivity.replaceFragment(loginActivity.welcome));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void doResetPwd(String usr, View v) {
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();

        cookieManager.setAcceptCookie(true);
        cookieManager.acceptCookie();
        CookieManager.setAcceptFileSchemeCookies(true);
        CookieManager.getInstance().setAcceptCookie(true);
        cookieManager.getCookie(GlobalVars.baseUrl);

        webView = new WebView(loginActivity);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("reset-credentials"))  {
                    String usrScript = "document.getElementById('username').value='" + usr + "';";
                    view.evaluateJavascript(usrScript, null);
                    view.evaluateJavascript("document.getElementsByTagName('form')[0].submit();", null);
                    super.onPageFinished(view, url);
                }
                if (url.contains("login-actions/authenticate")) {
                    System.out.println("onPageFinished: " + url);
                    doResetSuccess();
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        webView.loadUrl(GlobalVars.resetPwdUrl);
    }

    private void doResetSuccess() {
        tv_desc.setText("You should receive an email shortly with further instructions.");
        tv_desc.setTextColor(loginActivity.getColor(R.color.bg));
        pb_loading.setVisibility(View.GONE);
        btn_submit.setVisibility(View.GONE);
    }
}