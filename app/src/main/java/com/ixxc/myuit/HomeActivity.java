package com.ixxc.myuit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    LinearLayout homeLayout, devicesLayout, mapsLayout, userLayout;
    ImageView iv_home, iv_devices, iv_maps, iv_user;
    TextView tv_home, tv_devices, tv_maps, tv_user;

    Fragment homeFrag, devicesFrag, mapsFrag, userFrag;

    public static HomeActivity homeActivity;

    int selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InitVars();
        InitViews();
        InitEvents();

        replaceFragment(homeFrag);
    }

    private void InitVars() {
        selectedIndex = 0;
        homeActivity = this;

        homeFrag = new HomeFragment();
        devicesFrag = new DevicesFragment();
        mapsFrag = new MapsFragment();
        userFrag = new UserFragment();
    }

    private void InitViews() {
        homeLayout = findViewById(R.id.homeLayout);
        devicesLayout = findViewById(R.id.devicesLayout);
        mapsLayout = findViewById(R.id.mapsLayout);
        userLayout = findViewById(R.id.userLayout);

        iv_home = findViewById(R.id.iv_home);
        iv_devices = findViewById(R.id.iv_devices);
        iv_maps = findViewById(R.id.iv_maps);
        iv_user = findViewById(R.id.iv_user);

        tv_home = findViewById(R.id.tv_home);
        tv_devices = findViewById(R.id.tv_devices);
        tv_maps = findViewById(R.id.tv_maps);
        tv_user = findViewById(R.id.tv_user);
    }

    private void InitEvents() {
        homeLayout.setOnClickListener(view -> {
            if (selectedIndex != 0) {
                replaceFragment(homeFrag);
                changeIndex(0);
            }
        });

        devicesLayout.setOnClickListener(view -> {
            if (selectedIndex != 1) {
                replaceFragment(devicesFrag);
                changeIndex(1);
            }
        });

        mapsLayout.setOnClickListener(view -> {
            if (selectedIndex != 2) {
                replaceFragment(mapsFrag);
                changeIndex(2);
            }
        });

        userLayout.setOnClickListener(view -> {
            if (selectedIndex != 3) {
                replaceFragment(userFrag);
                changeIndex(3);
            }
        });
    }

    private void changeIndex(int i) {
        tv_home.setVisibility(View.GONE);
        tv_devices.setVisibility(View.GONE);
        tv_maps.setVisibility(View.GONE);
        tv_user.setVisibility(View.GONE);

        iv_home.setImageResource(R.drawable.ic_home);
        iv_devices.setImageResource(R.drawable.ic_devices);
        iv_maps.setImageResource(R.drawable.ic_maps);
        iv_user.setImageResource(R.drawable.ic_user);

        homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        devicesLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mapsLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        userLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        selectedIndex = i;
        ScaleAnimation sa = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.4f, Animation.RELATIVE_TO_SELF, 0f);
        sa.setDuration(200);
        sa.setFillAfter(true);

        switch (i) {
            case 0:
                tv_home.setVisibility(View.VISIBLE);
                iv_home.setImageResource(R.drawable.ic_home_selected);
                homeLayout.setBackgroundResource(R.drawable.home_round_100);
                homeLayout.startAnimation(sa);
                break;
            case 1:
                tv_devices.setVisibility(View.VISIBLE);
                iv_devices.setImageResource(R.drawable.ic_devices_selected);
                devicesLayout.setBackgroundResource(R.drawable.home_round_100);
                devicesLayout.startAnimation(sa);
                break;
            case 2:
                tv_maps.setVisibility(View.VISIBLE);
                iv_maps.setImageResource(R.drawable.ic_maps_selected);
                mapsLayout.setBackgroundResource(R.drawable.home_round_100);
                mapsLayout.startAnimation(sa);
                break;
            default:
                tv_user.setVisibility(View.VISIBLE);
                iv_user.setImageResource(R.drawable.ic_user);
                userLayout.setBackgroundResource(R.drawable.home_round_100);
                userLayout.startAnimation(sa);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.mainFragment, fragment);
        ft.commit();
    }
}