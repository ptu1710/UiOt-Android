package com.ixxc.myuit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.DevicesAdapter;
import com.ixxc.myuit.Interface.DevicesListener;
import com.ixxc.myuit.Model.Device;

import java.util.List;

public class DevicesFragment extends Fragment {

    RecyclerView rv_devices;
    ImageView iv_add;

    View rootView;

    private List<Device> deviceList;
    private DevicesAdapter adapter;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("DEVICES");
        if (isOK) {
            showDevices();
        }

        return false;
    });

    public DevicesFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        InitVars();
        InitViews(view);
        InitEvents();

        GetDevices();

        super.onViewCreated(view, savedInstanceState);
    }

    private void GetDevices() {
        //Get devices
        new Thread(() -> {
            APIManager.getDevices();

            Message m = handler.obtainMessage();
            Bundle bundle = new Bundle();

            bundle.putBoolean("DEVICES", true);
            m.setData(bundle);
            handler.sendMessage(m);
        }).start();
    }

    private void showDevices() {
        deviceList = Device.getAllDevices();

        adapter = new DevicesAdapter(deviceList, new DevicesListener() {
            @Override
            public void onItemClicked(Device device) {
//                gotoDetail(device.id);
            }

            @Override
            public void onItemLongClicked(View v, Device device) {
//                PopupMenu popupMenu =  new PopupMenu(v.getContext(), v);
//                popupMenu.inflate(R.menu.context_menu);
//                popupMenu.setOnMenuItemClickListener(item -> {
//                    MainActivity.home.InitWeatherData(device.id);
//                    Utilities.showToast(getActivity(), getContext(), "HERE", Toast.LENGTH_LONG, -1);
//                    return false;
//                });
//                popupMenu.show();
            }
        });

        LinearLayoutManager layoutManager =  new LinearLayoutManager(rootView.getContext());
        rv_devices.setLayoutManager(layoutManager);
        rv_devices.setAdapter(adapter);
        rv_devices.setHasFixedSize(true);
    }

    private void InitVars() {

    }

    private void InitViews(View v) {
        rootView = v;
        rv_devices = v.findViewById(R.id.rv_devices);
        iv_add = v.findViewById(R.id.iv_add);
    }


    private void InitEvents() {
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.homeActivity, AddDeviceActivity.class));
            }
        });
    }
}