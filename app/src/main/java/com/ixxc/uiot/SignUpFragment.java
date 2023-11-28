package com.ixxc.uiot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ixxc.uiot.API.APIManager;

public class SignUpFragment extends Fragment {
    TextView tv_verify;
    Button btn_sign_up, btn_back, btn_resend;
    EditText et_usr, et_email, et_pwd, et_re_pwd;
    ProgressBar pb_loading;
    ImageView iv_logo;
    LoginActivity loginActivity;

    WebView webView;

    Handler loginHandler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("SIGNUP");
        if (isOK) {
            startActivity(new Intent(loginActivity, HomeActivity.class));
            loginActivity.finish();
        }

        return false;
    });

    public SignUpFragment() { }

    public SignUpFragment(LoginActivity activity) {
        this.loginActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitViews(view);
        InitEvent();
    }

    private void InitViews(View v) {
        btn_sign_up = v.findViewById(R.id.btn_sign_up_2);
        btn_back = v.findViewById(R.id.btn_back);
        et_usr = v.findViewById(R.id.et_usr);
        et_email = v.findViewById(R.id.et_email);
        et_pwd = v.findViewById(R.id.et_user);
        et_re_pwd = v.findViewById(R.id.et_re_pwd);
        pb_loading = v.findViewById(R.id.pb_loading);
        tv_verify = v.findViewById(R.id.tv_verify);
        btn_resend = v.findViewById(R.id.btn_resend);
        iv_logo = v.findViewById(R.id.iv_logo_1);
    }

    private void InitEvent() {
        btn_sign_up.setOnClickListener(view -> {
            String tag = String.valueOf(view.getTag());
            if (tag.equals("refresh")) {
                webView.reload();
            } else {
                pb_loading.setVisibility(View.VISIBLE);
                btn_sign_up.setVisibility(View.INVISIBLE);
                btn_sign_up.setEnabled(false);

                String usr = String.valueOf(et_usr.getText());
                String email = String.valueOf(et_email.getText());
                String pwd = String.valueOf(et_pwd.getText());
                String rePwd = String.valueOf(et_re_pwd.getText());
                getToken(usr, email, pwd, rePwd);
            }
        });

        btn_back.setOnClickListener(view -> loginActivity.replaceFragment(loginActivity.welcome));

        btn_resend.setOnClickListener(view -> {
            String script = "document.getElementsByTagName('a')[1].click();";
            webView.evaluateJavascript(script, null);
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void getToken(String usr, String email, String pwd, String rePwd) {
        CookieManager cm = CookieManager.getInstance();
        cm.removeAllCookie();

        webView = new WebView(getContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                if (url.contains("openid-connect/auth") && url.contains("state=AAAA")) {
                    String redirect = "document.getElementsByTagName('a')[1].click();";
                    view.evaluateJavascript(redirect, null);
                } else if (url.contains("login-actions/registration")) {
                    String script = "document.getElementsByClassName('helper-text')[0].getAttribute('data-error');";
                    String script1 = "document.getElementsByClassName('red-text')[1].innerText;";
                    view.evaluateJavascript(script, s -> {
                        if (s.equals("null")) {
                            view.evaluateJavascript(script1, s1 -> {
                                if (s1.equals("null")) {
                                    Log.d(GlobalVars.LOG_TAG, "onPageFinished: KO");
                                    String usrScript = "document.getElementById('username').value='" + usr + "';";
                                    String emailScript = "document.getElementById('email').value='" + email + "';";
                                    String pwdScript = "document.getElementById('password').value='" + pwd + "';";
                                    String rePwdScript = "document.getElementById('password-confirm').value='" + rePwd + "';";

                                    view.evaluateJavascript(usrScript, null);
                                    view.evaluateJavascript(emailScript, null);
                                    view.evaluateJavascript(pwdScript, null);
                                    view.evaluateJavascript(rePwdScript, null);
                                    view.evaluateJavascript("document.getElementsByTagName('form')[0].submit();", null);
                                } else {
                                    signUpError(s1);
                                }
                            });
                        } else {
                            signUpError(s);
                        }
                    });
                } else if (url.contains("VERIFY_EMAIL")) {
                    Log.d(GlobalVars.LOG_TAG, "onPageFinished: VERIFY_EMAIL");
                    verifyEmail(email);
                } else {
                    if (url.contains("&code=")) {
                        String code = url.split("&code=")[1];
                        Log.d(GlobalVars.LOG_TAG, "onPageStarted: " + code);
                        getTokenByCode(code);
                    }
                }

                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                handler.proceed();
            }
        });

        webView.loadUrl(GlobalVars.signUpUrl);
    }

    private void getTokenByCode(String code) {
        final Message msg = loginHandler.obtainMessage();
        final Bundle bundle = new Bundle();

        new Thread(() -> {
            APIManager.getToken(code);

            bundle.putBoolean("SIGNUP", true);
            msg.setData(bundle);
            loginHandler.sendMessage(msg);
        }).start();
    }

    private void verifyEmail(String email) {
        pb_loading.setVisibility(View.GONE);
        iv_logo.setVisibility(View.GONE);
        btn_sign_up.setVisibility(View.VISIBLE);
        tv_verify.setVisibility(View.VISIBLE);
        btn_resend.setVisibility(View.VISIBLE);

        btn_sign_up.setEnabled(true);

        String msg = "An email with instructions to verify your email address has been sent to " + email;
        tv_verify.setText(msg);
        et_usr.setEnabled(false);
        et_email.setEnabled(false);
        et_pwd.setEnabled(false);
        et_re_pwd.setEnabled(false);
        btn_sign_up.setText(R.string.refresh);
        btn_sign_up.setTag("refresh");
}

    private void signUpError(String msg) {
        pb_loading.setVisibility(View.GONE);
        btn_sign_up.setVisibility(View.VISIBLE);
        btn_sign_up.setEnabled(true);
        Toast.makeText(loginActivity, msg.replace("\"", ""), Toast.LENGTH_LONG).show();
    }
}