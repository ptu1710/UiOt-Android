package com.ixxc.uiot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class HomeActivity extends AppCompatActivity {
    private FragmentManager fm;
    public AnimatedBottomBar navbar;
    public HomeFragment homeFrag;
    public DevicesFragment devicesFrag;
    public MapsFragment mapsFrag;
    public AdminFragment userFrag;
    private Fragment fragment = null;
    int selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InitVars();
        InitViews();
        InitEvents();

        fm.beginTransaction().add(R.id.main_frame, mapsFrag, "map").commit();
        fm.beginTransaction().hide(mapsFrag).commit();
        fm.beginTransaction().add(R.id.main_frame, homeFrag, "home").commit();
        fragment = homeFrag;
        navbar.selectTabAt(0, false);
    }

    private void InitVars() {
        selectedIndex = 0;

        fm = getSupportFragmentManager();

        homeFrag = new HomeFragment(this);
        devicesFrag = new DevicesFragment(this);
        mapsFrag = new MapsFragment(this);
        userFrag = new AdminFragment(this);

        Utils.delayHandler = new Handler();
    }

    private void InitViews() {
        navbar = findViewById(R.id.bottom_bar);
    }

    private void InitEvents() {
        navbar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab lastTab, int newIndex, @NonNull AnimatedBottomBar.Tab newTab) {
                if (fragment == devicesFrag) devicesFrag.changeSelectedDevice(-1, "");

                switch (newIndex) {
                    case 0:
                        if (homeFrag == null) { homeFrag = new HomeFragment(HomeActivity.this); }
                        fm.beginTransaction().hide(fragment).commit();
                        fragment = homeFrag;
                        break;
                    case 1:
                        if (fm.findFragmentByTag("devices") == null) {
                            fm.beginTransaction().add(R.id.main_frame, devicesFrag, "devices").commit();
                        }
                        fm.beginTransaction().hide(fragment).commit();
                        fragment = devicesFrag;
                        break;
                    case 2:
                        if (fm.findFragmentByTag("map") == null) {
                            fm.beginTransaction().add(R.id.main_frame, mapsFrag, "map").commit();
                        }

                        fm.beginTransaction().hide(fragment).commit();
                        fragment = mapsFrag;
                        break;
                    case 3:
                        if (fm.findFragmentByTag("user") == null) {
                            fm.beginTransaction().add(R.id.main_frame, userFrag, "user").commit();
                        }
                        fm.beginTransaction().hide(fragment).commit();
                        fragment = userFrag;
                        break;
                }

                fm.beginTransaction()
                        .show(fragment)
                        .commit();
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        switch (navbar.getSelectedIndex()) {
            case 0:
                super.onBackPressed();
            case 1:
                if(!devicesFrag.selected_device_id.equals("")) {
                    devicesFrag.changeSelectedDevice(-1, "");
                    return;
                }
                break;
            default:
                break;
        }

        navbar.selectTabAt(0, true);
    }
}