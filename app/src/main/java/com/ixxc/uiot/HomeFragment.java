package com.ixxc.uiot;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.User;
import com.ixxc.uiot.Model.WeatherDevice;

import java.time.DayOfWeek;

public class HomeFragment extends Fragment {
    HomeActivity parentActivity;
    LinearLayout layout_main;
    ConstraintLayout layout_top;
    TextView tv_username;
    ProgressBar pb_username;

    User me;
    WeatherDevice defaultDevice;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("IS_OK");
        if (!isOK) return false;

        showBasicInfo();
        InitWidgetViews();

        return false;
    });

    public HomeFragment() { }

    public HomeFragment(HomeActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        layout_top = view.findViewById(R.id.layout_top);
        layout_main = view.findViewById(R.id.layout_main);
        tv_username = view.findViewById(R.id.tv_username);
        pb_username = view.findViewById(R.id.pb_username);

        new Thread(() -> {
            if (User.getMe() == null) {
                APIManager.getUserInfo();
                APIManager.getUserRoles();
            }

            if (Device.getDevicesList() == null || Device.getDevicesList().size() == 0) {
                String queryString = "{ \"realm\": { \"name\": \"master\" }}";
                JsonObject query = JsonParser.parseString(queryString).getAsJsonObject();
                APIManager.queryDevices(query);
            }

            APIManager.getMap();

            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("IS_OK", true);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }).start();

        super.onViewCreated(view, savedInstanceState);
    }

    private void showBasicInfo() {
        me = User.getMe();

        Device device = Device.getDeviceById("73IzmDYku2bX2mGuaiFHbx");
        if (device != null) defaultDevice = new WeatherDevice(device);
        else defaultDevice = new WeatherDevice();

        tv_username.setText(me.username);
        tv_username.setVisibility(View.VISIBLE);
        pb_username.setVisibility(View.GONE);

        TextView tv_dow = layout_top.findViewById(R.id.tv_dow);
        TextView tv_temper = layout_top.findViewById(R.id.tv_temper);

        String dow = Utils.formatString(DayOfWeek.from(java.time.LocalDate.now()).toString().toLowerCase());
        String temper = String.join("", defaultDevice.temperature.getValueString(), getResources().getString(R.string.celsius));

        tv_dow.setText(dow);
        tv_temper.setText(temper);
    }

    private void InitWidgetViews() {

        // loop 3 times
        for (int i = 0; i < 2; i++) {
            View sunWid = LayoutInflater.from(parentActivity).inflate(R.layout.sun_widget, layout_main, false);
            layout_main.addView(sunWid);
        }

        LinearLayout mediumLayout = new LinearLayout(parentActivity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        int sideMargin = Utils.dpToPx(parentActivity, 16);
        int otherMargin = Utils.dpToPx(parentActivity, 8);

        params.setMargins(sideMargin, otherMargin, sideMargin, otherMargin);

        mediumLayout.setLayoutParams(params);
        mediumLayout.setOrientation(LinearLayout.HORIZONTAL);
        mediumLayout.setWeightSum(2);

        LinearLayout.LayoutParams smallWidParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        smallWidParams.weight = 1;

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        ResourcesCompat.getColor(parentActivity.getResources(), R.color.lime, null),
                        ResourcesCompat.getColor(parentActivity.getResources(), R.color.yellow, null),
                        ResourcesCompat.getColor(parentActivity.getResources(), R.color.red, null),
                        ResourcesCompat.getColor(parentActivity.getResources(), R.color.purple, null)});

        gradientDrawable.setCornerRadius(Utils.dpToPx(parentActivity, 8));

        View uvWid = LayoutInflater.from(parentActivity).inflate(R.layout.uv_widget, layout_main, false);
        ImageView iv_uv = uvWid.findViewById(R.id.iv_uv);
        iv_uv.setImageDrawable(gradientDrawable);

        View humWid = LayoutInflater.from(parentActivity).inflate(R.layout.humidity_widget, layout_main, false);
        TextView tv_hum = humWid.findViewById(R.id.tv_humidity);
        tv_hum.setText(defaultDevice.humidity.getValueString());

        uvWid.setLayoutParams(smallWidParams);
        humWid.setLayoutParams(smallWidParams);

        mediumLayout.addView(uvWid);
        mediumLayout.addView(humWid);

        layout_main.addView(mediumLayout);
    }
}