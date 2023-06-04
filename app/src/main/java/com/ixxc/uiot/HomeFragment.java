package com.ixxc.uiot;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Model.Attribute;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.DeviceModel;
import com.ixxc.uiot.Model.User;
import com.ixxc.uiot.Model.WeatherDevice;

import java.time.DayOfWeek;
import java.util.List;

public class HomeFragment extends Fragment {
    HomeActivity parentActivity;
    ShimmerFrameLayout shimmerFrameLayout;
    ScrollView scrollView;
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
        InitWidgets();

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
        shimmerFrameLayout = view.findViewById(R.id.layout_shimmer);
        scrollView = view.findViewById(R.id.scroll_view);

        shimmerFrameLayout.startShimmer();

        new Thread(() -> {
            if (User.getMe() == null) {
                APIManager.getUserInfo();
                APIManager.getUserRoles();
            }

            APIManager.getDeviceModels();
            if (Device.getDeviceList() == null || Device.getDeviceList().size() == 0) {
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

        Device device = Device.getDeviceById("5zI6XqkQVSfdgOrZ1MyWEf");
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

    public void InitWidgets() {
        layout_main.removeAllViews();

        // Sun Widget
        View sunWid = LayoutInflater.from(parentActivity).inflate(R.layout.sun_widget, layout_main, false);
        layout_main.addView(sunWid);

        // Get saved preferences for widgets
        // One widget info is stored in one string, example: "5zI6XqkQVSfdgOrZ1MyWEf-humidity"

        String widgetString = Utils.getPreferences(parentActivity, GlobalVars.WIDGET_KEY);

        int count = 1;
        LinearLayout.LayoutParams smallWidParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        smallWidParams.weight = 1;

        LinearLayout mediumLayout = createMediumLayout();

        JsonArray widgetArray = TextUtils.isEmpty(widgetString) ? new JsonArray() : JsonParser.parseString(widgetString).getAsJsonArray();
        for (JsonElement widget : widgetArray) {
            String[] widgetInfo = widget.getAsString().split("-");
            String deviceId = widgetInfo[0];
            String attributeName = widgetInfo[1];

            Device device = Device.getDeviceById(deviceId);
            if (device == null) continue;

            List<Attribute> attributes = device.getDeviceAttribute();
            if (attributes == null) continue;

            Attribute attribute = attributes.stream().filter(attr -> attr.getName().equals(attributeName)).findFirst().orElse(null);
            if (attribute == null) continue;

            String unitString = DeviceModel.getDeviceModel(device.type).getAttributeModel(attributeName).getUnits();

            View humWid = LayoutInflater.from(parentActivity).inflate(R.layout.humidity_widget, layout_main, false);
            ImageView iv_icon = humWid.findViewById(R.id.iv_icon);
            TextView tv_name = humWid.findViewById(R.id.tv_name);
            TextView tv_value = humWid.findViewById(R.id.tv_value);
            TextView tv_unit = humWid.findViewById(R.id.tv_unit);
            iv_icon.setImageDrawable(device.getIconDrawable(parentActivity));
            tv_name.setText(attribute.getName());
            tv_value.setText(attribute.getValueString());
            tv_unit.setText(attribute.getUnit(unitString));

            humWid.setLayoutParams(smallWidParams);
            mediumLayout.addView(humWid);

            if (count % 2 == 0 || count == widgetArray.size()) {
                layout_main.addView(mediumLayout);
                mediumLayout = createMediumLayout();
            }

            count++;
        }

        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);

        scrollView.setVisibility(View.VISIBLE);
    }

    private LinearLayout createMediumLayout() {
        LinearLayout mediumLayout = new LinearLayout(parentActivity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        int sideMargin = Utils.dpToPx(parentActivity, 16);

        params.setMargins(sideMargin, 0, sideMargin, 0);

        mediumLayout.setLayoutParams(params);
        mediumLayout.setOrientation(LinearLayout.HORIZONTAL);
        mediumLayout.setWeightSum(2);

        return mediumLayout;
    }
}