package com.ixxc.uiot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ixxc.uiot.API.APIManager;

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

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // New back button handler system
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                int selectedIndex = navbar.getSelectedIndex();
                if (selectedIndex == 0) {
                    finish();
                } else if (selectedIndex == 1) {
                    if (!devicesFrag.selected_device_id.isEmpty()) {
                        devicesFrag.changeSelectedDevice(-1, "");
                    }
                }
                navbar.selectTabAt(0, true);
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InitVars();
        InitViews();
        InitEvents();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("API LOG", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    new Thread(() -> APIManager.registerDevice(token)).start();

                    Log.d(GlobalVars.LOG_TAG, token);
                });

        askNotificationPermission();

        fm.beginTransaction().add(R.id.main_frame, mapsFrag, "map").commit();
        fm.beginTransaction().hide(mapsFrag).commit();

        fm.beginTransaction().add(R.id.main_frame, homeFrag, "home").commit();

        fragment = homeFrag;
        navbar.selectTabAt(0, false);

//        Intent toDetails = new Intent(this, DeviceInfoActivity.class);
//        toDetails.putExtra("DEVICE_ID", "4EqQeQ0L4YNWNNTzvTOqjy");
//        startActivity(toDetails);
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

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // TODO: Notification permission granted or denied
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    // Directly ask for the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
            }
        }
    }
}