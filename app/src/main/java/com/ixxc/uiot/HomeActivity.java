package com.ixxc.uiot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

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
import androidx.fragment.app.FragmentTransaction;

import com.ixxc.uiot.Utils.Util;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class HomeActivity extends AppCompatActivity {
    private FragmentManager fm;
    public AnimatedBottomBar navbar;
    public HomeFragment homeFrag;
    public DevicesFragment devicesFrag;
    public MapsFragment mapsFrag;
    public AdminFragment userFrag;
    private Fragment fragment = null;

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
                } else {
                    navbar.selectTabAt(0, true);
                }
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InitVars();
        InitViews();
        InitEvents();

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Log.w("API LOG", "Fetching FCM registration token failed", task.getException());
//                        return;
//                    }
//
//                    // Get new FCM registration token
//                    String token = task.getResult();
//
//                    new Thread(() -> new APIManager().registerDevice(token)).start();
//
//                    Log.d(Utils.LOG_TAG, "FCM token: " + token);
//                });

        askNotificationPermission();

        fm.beginTransaction()
//                .add(R.id.main_frame, mapsFrag, "2")
                .add(R.id.main_frame, fragment = homeFrag, "0")
//                .hide(mapsFrag)
                .commit();

        navbar.selectTabAt(0, false);

//        startActivity(new Intent(this, AccountActivity.class));
    }

    private void InitVars() {
        fm = getSupportFragmentManager();

        homeFrag = new HomeFragment(this);
        devicesFrag = new DevicesFragment(this);
        mapsFrag = new MapsFragment(this);
        userFrag = new AdminFragment(this);

        Util.delayHandler = new Handler();
    }

    private void InitViews() {
        navbar = findViewById(R.id.bottom_bar);
    }

    private void InitEvents() {
        navbar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab lastTab, int newIndex, @NonNull AnimatedBottomBar.Tab newTab) {
                fm.beginTransaction().hide(fragment).commit();

                String tag = String.valueOf(newIndex);

                switch (newIndex) {
                    case 0:
                        fragment = homeFrag;
                        break;
                    case 1:
                        fragment = devicesFrag;
                        break;
                    case 2:
                        fragment = mapsFrag;
                        break;
                    case 3:
                        fragment = userFrag;
                        break;
                }

                if (fm.findFragmentByTag(tag) == null) {
                    fm.beginTransaction().add(R.id.main_frame, fragment, tag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                } else {
                    fm.beginTransaction().show(fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                }
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