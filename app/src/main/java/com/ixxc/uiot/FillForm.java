package com.ixxc.uiot;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.ixxc.uiot.API.APIManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FillForm extends AppCompatActivity {

    Button btn_choose;
    ImageView iv_image;
    ProgressBar pb_progress;
    TextView tv_info_1, tv_info_2;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("OK");
        if (isOK) {
            tv_info_1.setText(bundle.getString("INFO1"));
            tv_info_2.setText(bundle.getString("INFO2"));

            pb_progress.setVisibility(ProgressBar.INVISIBLE);
            btn_choose.setEnabled(true);
            btn_choose.setVisibility(Button.VISIBLE);
        }

        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_form);
        InitViews();
        InitEvents();
    }

    private void InitEvents() {
        btn_choose.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_PICK);
            i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

            chooseImageLauncher.launch(i);
        });
    }

    private void InitViews() {
        btn_choose = findViewById(R.id.btn_choose);
        iv_image = findViewById(R.id.iv_image);
        pb_progress = findViewById(R.id.pb_progress);
        tv_info_1 = findViewById(R.id.tv_info_1);
        tv_info_2 = findViewById(R.id.tv_info_2);
    }

    ActivityResultLauncher<Intent> chooseImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null) {
                        iv_image.setImageURI(data.getData());
                        pb_progress.setVisibility(ProgressBar.VISIBLE);
                        btn_choose.setEnabled(false);
                        btn_choose.setVisibility(Button.INVISIBLE);

                        try {
                            InputStream inputStream = getContentResolver().openInputStream(data.getData());
                            byte[] bytes = new byte[inputStream.available()];
                            while (true) {
                                if (inputStream.read(bytes) == -1) break;
                            }
                            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), bytes);

                            new Thread(() -> {
                                JsonObject stringResult = new APIManager().uploadImage(requestBody);
                                Message message = handler.obtainMessage();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("OK", true);

                                if (stringResult == null) {
                                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                                    bundle.putString("INFO1", "ERROR");
                                    bundle.putString("INFO2", "ERROR");
                                } else {
                                    String text = String.valueOf(stringResult.get("extract")).replace("\"", "").replace("\\n", "\n").trim();
                                    List<String> lines = new ArrayList<>();
                                    Arrays.asList(text.split("\\n")).forEach(line -> {
                                        if (!line.trim().equals("")) {
                                            lines.add(line);
                                            Log.d(Utils.LOG_TAG, line);
                                        }
                                    });
                                    bundle.putString("INFO1", lines.get(3));
                                    bundle.putString("INFO2", lines.get(5));
                                }

                                message.setData(bundle);
                                handler.sendMessage(message);
                            }).start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
}