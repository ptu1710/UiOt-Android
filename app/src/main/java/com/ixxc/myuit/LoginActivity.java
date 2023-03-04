package com.ixxc.myuit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ixxc.myuit.API.APIManager;

public class LoginActivity extends AppCompatActivity {
    public static boolean isDefaultPage = true;
    public Fragment welcome, signin, signup;
    boolean isSignIn = false;

    Handler loginHandler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isPublic = bundle.getBoolean("LOGIN");
        if (isPublic) {
//            String usr = String.valueOf(et_usr.getText());
//            String pwd = String.valueOf(et_pwd.getText());
//            getToken(false, usr, pwd);
        } else {
            Toast.makeText(this, "NEXT ACTIVITY", Toast.LENGTH_SHORT).show();
        }

        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitVars();
        InitViews();
        InitEvent();

        replaceFragment(welcome);
    }

    private void InitVars() {
        welcome = new WelcomeFragment(this);
        signin = new SignInFragment(this);
    }

    private void InitViews() {

    }

    private void InitEvent() {
    }

    private void SignInBtnClick() {
        if (isSignIn) {
            getToken(true, "public", "public");
        } else {
            isSignIn = true;
            replaceFragment(signin);
        }
    }

    private void getToken(boolean isPublic, String usr, String pwd) {
        CookieManager cm = CookieManager.getInstance();
        cm.removeAllCookie();

        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains("&code=")) {
                    String code = url.split("&code=")[1];
                    getTokenByCode(isPublic, code);
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

    private void getTokenByCode(boolean isPublic, String code) {
        final Message msg = loginHandler.obtainMessage();
        final Bundle bundle = new Bundle();

        new Thread(() -> {
            APIManager.getToken(isPublic, code);

            bundle.putBoolean("LOGIN", isPublic);
            msg.setData(bundle);
            loginHandler.sendMessage(msg);
        }).start();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (fragment == welcome) {
            isDefaultPage = true;
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        } else {
            isDefaultPage = false;
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }

        ft.replace(R.id.loginFrame, fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (isDefaultPage) {
            super.onBackPressed();
            finish();
        }
        else {
            replaceFragment(welcome);
        }
    }
}