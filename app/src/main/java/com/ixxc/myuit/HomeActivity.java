package com.ixxc.myuit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    public static BottomNavigationView navbar;
    FragmentManager fm;
    public  static HomeFragment homeFrag;
    public  static DevicesFragment devicesFrag;
    public  static MapsFragment mapsFrag;
    public  static AdminFragment userFrag;
    public static HomeActivity homeActivity;
    Fragment fragment = null;
    int selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InitVars();
        InitViews();
        InitEvents();

        fm.beginTransaction().add(R.id.main_frame, homeFrag, "home").commit();
        fragment = homeFrag;
        navbar.setSelectedItemId(R.id.nav_home);
    }

    private void InitVars() {
        selectedIndex = 0;
        homeActivity = this;

        fm = getSupportFragmentManager();

        homeFrag = new HomeFragment();
        devicesFrag = new DevicesFragment();
        mapsFrag = new MapsFragment();
        userFrag = new AdminFragment(this);
    }

    private void InitViews() {
        navbar = findViewById(R.id.navbar);
    }

    private void InitEvents() {
        navbar.setOnItemReselectedListener(item -> { });

        navbar.setOnItemSelectedListener(item -> {
            if (fragment == devicesFrag) devicesFrag.changeSelectedDevice(-1, "");

            switch (item.getItemId()) {
                case (R.id.nav_home):
                    if (homeFrag == null) { homeFrag = new HomeFragment(); }
                    fm.beginTransaction().hide(fragment).commit();
                    fragment = homeFrag;
                    break;
                case (R.id.nav_maps):
                    if (fm.findFragmentByTag("map") == null) {
                        fm.beginTransaction().add(R.id.main_frame, mapsFrag, "map").commit();
                    }
                    fm.beginTransaction().hide(fragment).commit();
                    fragment = mapsFrag;
                    break;
                case (R.id.nav_devices):
                    if (fm.findFragmentByTag("devices") == null) {
                        fm.beginTransaction().add(R.id.main_frame, devicesFrag, "devices").commit();
                    }
                    fm.beginTransaction().hide(fragment).commit();
                    fragment = devicesFrag;
                    break;
                case (R.id.nav_admin):
                    if (fm.findFragmentByTag("user") == null) {
                        fm.beginTransaction().add(R.id.main_frame, userFrag, "user").commit();
                    }
                    fm.beginTransaction().hide(fragment).commit();
                    fragment = userFrag;
                    break;
            }

            fm.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .show(fragment)
                    .commit();

            return true;
        });
    }

    @Override
    public void onBackPressed() {
        switch (navbar.getSelectedItemId()) {
            case R.id.nav_home:
                super.onBackPressed();
            case R.id.nav_devices:
                if(!devicesFrag.selected_device_id.equals("")) {
                    devicesFrag.changeSelectedDevice(-1, "");
                    return;
                }
                break;
            default:
                break;
        }

        navbar.setSelectedItemId(R.id.nav_home);
    }
}