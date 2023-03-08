package com.ixxc.myuit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.DevicesAdapter;
import com.ixxc.myuit.Interface.DevicesListener;
import com.ixxc.myuit.Model.Device;

import java.util.List;

public class DevicesFragment extends Fragment {
    RecyclerView rv_devices;
    ImageView iv_add, iv_delete, iv_community, iv_cancel;
    ProgressBar pb_loading_1;
    SwipeRefreshLayout srl_devices;
    View rootView;

    public static String selected_device_id = "";

    Handler handler;

    DevicesAdapter devicesAdapter;

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

        // Wait to show all devices
        new Thread(() -> {
            while (Device.getAllDevices() == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();

            bundle.putBoolean("SHOW_DEVICES", true);
            msg.setData(bundle);

            handler.sendMessage(msg);
        }).start();

        super.onViewCreated(view, savedInstanceState);
    }

    private void InitVars() {
        handler = new Handler(message -> {
            Bundle bundle = message.getData();
            boolean delete_device = bundle.getBoolean("DELETE_DEVICE");
            boolean showDevices = bundle.getBoolean("SHOW_DEVICES");
            boolean refresh = bundle.getBoolean("REFRESH");

            if (showDevices || refresh) {
                showDevices();
                srl_devices.setRefreshing(false);
            } else {
                if (delete_device) {
                    refreshDevices();
                    Toast.makeText(HomeActivity.homeActivity,"Device was deleted successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomeActivity.homeActivity,"An error occurred while deleting the device", Toast.LENGTH_LONG).show();
                }
            }

            return false;
        });
    }

    private void InitViews(View v) {
        rootView = v;
        rv_devices = v.findViewById(R.id.rv_devices);
        iv_add = v.findViewById(R.id.iv_add);
        iv_community = v.findViewById(R.id.iv_community);
        iv_delete = v.findViewById(R.id.iv_delete);
        iv_cancel = v.findViewById(R.id.iv_cancel);
        pb_loading_1 = v.findViewById(R.id.pb_loading_1);
        srl_devices = v.findViewById(R.id.srl_devices);
    }

    private void InitEvents() {
        iv_add.setOnClickListener(view -> startActivity(new Intent(HomeActivity.homeActivity, AddDeviceActivity.class)));

        iv_delete.setOnClickListener(view -> {
            if (selected_device_id.equals("")) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.homeActivity);
            builder.setTitle("Warning!");
            builder.setMessage("Delete this device?");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new Thread(() -> {
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();

                        bundle.putBoolean("DELETE_DEVICE", APIManager.delDevice(selected_device_id));
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }).start();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            builder.show();
        });

        iv_cancel.setOnClickListener(view -> {
            iv_add.setVisibility(View.VISIBLE);
            iv_community.setVisibility(View.VISIBLE);
            iv_delete.setVisibility(View.GONE);
            iv_cancel.setVisibility(View.GONE);

            devicesAdapter.notifyItemChanged(DevicesAdapter.checkedPos);
            DevicesAdapter.checkedPos = -1;

            selected_device_id = "";
        });

        srl_devices.setOnRefreshListener(() -> {
            srl_devices.setRefreshing(false);
            refreshDevices();
        });
    }

    public void refreshDevices() {
        rv_devices.smoothScrollToPosition(0);
        if (srl_devices.isRefreshing()) return;

        srl_devices.setRefreshing(true);
//        sv.setQuery("", true);
//        sv.clearFocus();

        final Message msg = handler.obtainMessage();
        final Bundle bundle = new Bundle();

        new Thread(() -> {
            APIManager.getDevices();

            bundle.putBoolean("REFRESH", true);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }).start();
    }

    private void showDevices() {
        List<Device> deviceList = Device.getAllDevices();

        devicesAdapter = new DevicesAdapter(deviceList, new DevicesListener() {
            @Override
            public void onItemClicked(View v, Device device) {

            }

            @Override
            public void onItemLongClicked(View v, Device device) {
                iv_add.setVisibility(View.GONE);
                iv_community.setVisibility(View.GONE);
                iv_delete.setVisibility(View.VISIBLE);
                iv_cancel.setVisibility(View.VISIBLE);

                selected_device_id = device.id;
            }
        });

        LinearLayoutManager layoutManager =  new LinearLayoutManager(rootView.getContext());
        rv_devices.setLayoutManager(layoutManager);
        rv_devices.setAdapter(devicesAdapter);
        rv_devices.setHasFixedSize(true);

        rv_devices.setVisibility(View.VISIBLE);
        pb_loading_1.setVisibility(View.GONE);
    }
}