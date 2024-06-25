package com.ixxc.uiot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Utils.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SignUpFragment extends Fragment {
    TextView tv_verify;
    Button btn_sign_up, btn_back, btn_resend;
    EditText et_usr, et_email, et_pwd, et_re_pwd;
    ProgressBar pb_loading;
    ImageView iv_logo;
    LoginActivity parentActivity;

    WebView webView;

    int SELECT_PICTURE = 200;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("SIGNUP");
        if (isOK) {
            startActivity(new Intent(parentActivity, HomeActivity.class));
            parentActivity.finish();
        } else {
            boolean isOCR = bundle.getBoolean("OCR");
            if (isOCR) {
                Toast toast = Toast.makeText(parentActivity, "OCR", Toast.LENGTH_LONG);
                toast.show();

                et_usr.setText(bundle.getString("USER"));
                et_email.setText(bundle.getString("PASS"));
            }
        }

        return false;
    });

    public SignUpFragment() {
    }

    public SignUpFragment(LoginActivity activity) {
        this.parentActivity = activity;
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
        btn_sign_up = v.findViewById(R.id.btn_sign_in);
        btn_back = v.findViewById(R.id.btn_back);
        et_usr = v.findViewById(R.id.et_usr);
        et_email = v.findViewById(R.id.et_email);
        et_pwd = v.findViewById(R.id.et_pwd);
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

        btn_back.setOnClickListener(view -> {
//            parentActivity.replaceFragment(parentActivity.welcome);

//            Intent i = new Intent();
//            i.setAction(Intent.ACTION_PICK);
//            i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//
//            chooseImageLauncher.launch(i);

            startActivity(new Intent(parentActivity, FillForm.class));
            parentActivity.finish();
        });

        btn_resend.setOnClickListener(view -> {
            String script = "document.getElementsByTagName('a')[1].click();";
            webView.evaluateJavascript(script, null);
        });
    }

    ActivityResultLauncher<Intent> chooseImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    int resourceId = getResources().getIdentifier("card", "raw", this.getContext().getPackageName());


                    if (data != null) {
//                        File file = new File(data.getData().getPath());
                        InputStream in;
                        byte[] buf;
                        try {
                            in = getResources().openRawResource(resourceId);
                            buf = new byte[in.available()];
                            while (true) {
                                if (in.read(buf) == -1) break;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        RequestBody requestBody = RequestBody
                                .create(MediaType.parse("image/*"), buf);

                        new Thread(() -> {
                            JsonObject stringResult = new APIManager().uploadImage(requestBody);
                            String text = String.valueOf(stringResult.get("extract")).replace("\"", "").replace("\\n", "\n").trim();
                            List<String> lines = new ArrayList<>();
                            Arrays.asList(text.split("\\n")).forEach(line -> {
                                if (!line.equals("")) {
                                    lines.add(line);
                                    Log.d(Util.LOG_TAG, line);
                                }
                            });

                            Message message = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("OCR", true);
                            bundle.putString("USER", lines.get(3));
                            bundle.putString("PASS", lines.get(5));
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }).start();
                    }
                }
            });

    @SuppressLint("SetJavaScriptEnabled")
    private void getToken(String usr, String email, String pwd, String rePwd) {
        CookieManager.getInstance().removeAllCookies(null);

        webView = new WebView(getContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("openid-connect/registrations")) {
                    Log.d(Util.LOG_TAG, "onPageFinished: Fill form");

                    String usrScript = "document.getElementById('username').value='" + usr + "';";
                    String emailScript = "document.getElementById('email').value='" + email + "';";
                    String pwdScript = "document.getElementById('password').value='" + pwd + "';";
                    String rePwdScript = "document.getElementById('password-confirm').value='" + rePwd + "';";

                    view.evaluateJavascript(usrScript, null);
                    view.evaluateJavascript(emailScript, null);
                    view.evaluateJavascript(pwdScript, null);
                    view.evaluateJavascript(rePwdScript, null);
                    view.evaluateJavascript("document.getElementsByTagName('form')[0].submit();", null);
                } else if (url.contains("login-actions/registration")) {
                    String script = "document.getElementsByClassName('helper-text')[0].getAttribute('data-error');";
                    view.evaluateJavascript(script, s -> {
                        if (!s.equals("null")) signUpError(s);
                    });

                    String script1 = "document.getElementsByClassName('red-text')[1].innerText;";
                    view.evaluateJavascript(script1, s -> {
                        if (!s.equals("null")) signUpError(s);
                    });
                } else if (url.contains("VERIFY_EMAIL")) verifyEmail(email);
                else if (url.contains("/manager/")) {
                    getUserToken(usr, pwd);
                    webView.stopLoading();
                    webView.destroy();
                } else {
                    Log.d(Util.LOG_TAG, "onPageFinished: " + url.replace("uiot.ixxc.dev/", ""));
                }

                super.onPageFinished(view, url);
            }
        });

        String redirect_url = Util.baseUrl.replace(":", "%3A").replace("/", "%2F");
        String signUpUrl = Util.baseUrl + "auth/realms/master/protocol/openid-connect/registrations?client_id=openremote&response_type=code&redirect_uri=" + redirect_url + "manager%2F";
        webView.loadUrl(signUpUrl);
    }

    private void getUserToken(String usr, String pwd) {
        final Message msg = handler.obtainMessage();
        final Bundle bundle = new Bundle();

        new Thread(() -> {
            new APIManager().getUserToken(usr, pwd);

            bundle.putBoolean("SIGNUP", true);
            msg.setData(bundle);
            handler.sendMessage(msg);
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
        Log.d(Util.LOG_TAG, "signUpError: " + msg);

        pb_loading.setVisibility(View.GONE);
        btn_sign_up.setVisibility(View.VISIBLE);
        btn_sign_up.setEnabled(true);
        Toast.makeText(parentActivity, msg.replace("\"", ""), Toast.LENGTH_LONG).show();
    }
}