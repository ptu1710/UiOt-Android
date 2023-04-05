package com.ixxc.myuit;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.DevicesAdapter;
import com.ixxc.myuit.Interface.DevicesListener;
import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.Model.User;

import java.util.List;

public class DevicesFragment extends Fragment {
    HomeActivity homeActivity;
    RecyclerView rv_devices;
    ImageView iv_add, iv_delete, iv_community, iv_cancel;
    ProgressBar pb_loading_1;
    SwipeRefreshLayout srl_devices;
    View rootView;

    ActivityResultLauncher<Intent> mLauncher;

    User me;

    public String selected_device_id = "";

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean delete_device = bundle.getBoolean("DELETE_DEVICE");
        boolean show_devices = bundle.getBoolean("SHOW_DEVICES");
        boolean refresh = bundle.getBoolean("REFRESH");

        if (show_devices || refresh) {
            showDevices();
            iv_cancel.performClick();
            srl_devices.setRefreshing(false);
        } else {
            if (delete_device) {
                refreshDevices();
                Toast.makeText(homeActivity,"Device was deleted successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(homeActivity,"An error occurred while deleting the device", Toast.LENGTH_LONG).show();
            }
        }

        return false;
    });;

    DevicesAdapter devicesAdapter;

    public DevicesFragment() { }

    public DevicesFragment(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) refreshDevices();
        });
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
        me = User.getMe();
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
        iv_add.setOnClickListener(view -> {
            Intent intent = new Intent(homeActivity, AddDeviceActivity.class);
            mLauncher.launch(intent);
        });

        iv_delete.setOnClickListener(view -> {
            if (selected_device_id.equals("")) return;

            AlertDialog.Builder builder = new AlertDialog.Builder(homeActivity);
            builder.setTitle("Warning!");
            builder.setMessage("Delete this device?");
            builder.setPositiveButton("Delete", (dialogInterface, i) -> new Thread(() -> {
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putBoolean("DELETE_DEVICE", APIManager.delDevice(selected_device_id));
                msg.setData(bundle);
                handler.sendMessage(msg);
            }).start());

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> { });
            builder.show();
        });

        iv_cancel.setOnClickListener(view -> {
            changeSelectedDevice(-1, "");
        });

        srl_devices.setOnRefreshListener(this::refreshDevices);
    }

    public void refreshDevices() {
        rv_devices.smoothScrollToPosition(0);

        srl_devices.setRefreshing(true);

        final Message msg = handler.obtainMessage();
        final Bundle bundle = new Bundle();

        new Thread(() -> {
            String queryString = "{ \"realm\": { \"name\": \"master\" }}";
            JsonParser jsonParser = new JsonParser();
            JsonObject query = (JsonObject)jsonParser.parse(queryString);
            APIManager.queryDevices(query);

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
                viewDeviceInfo(device.id);
                changeSelectedDevice(-1, "");
            }

            @Override
            public void onItemLongClicked(View v, Device device) {
                if (!me.canWriteDevices()) return;
                changeSelectedDevice(0, device.id);
            }
        }, homeActivity);

        LinearLayoutManager layoutManager =  new LinearLayoutManager(rootView.getContext());
        rv_devices.setLayoutManager(layoutManager);
        rv_devices.setAdapter(devicesAdapter);
        rv_devices.setHasFixedSize(true);

        rv_devices.setVisibility(View.VISIBLE);
        pb_loading_1.setVisibility(View.GONE);
    }

    private void viewDeviceInfo(String id) {
        Intent toDetails = new Intent(getContext(), DeviceInfoActivity.class);
        toDetails.putExtra("DEVICE_ID", id);
        homeActivity.startActivity(toDetails);
    }

    public void changeSelectedDevice(int index, String id) {
        if (index != -1) {
            iv_add.setVisibility(View.GONE);
            iv_delete.setVisibility(View.VISIBLE);
            iv_cancel.setVisibility(View.VISIBLE);
        } else {
            if (!me.canWriteDevices()) iv_add.setVisibility(View.GONE);
            else iv_add.setVisibility(View.VISIBLE);

            iv_delete.setVisibility(View.GONE);
            iv_cancel.setVisibility(View.GONE);

            devicesAdapter.notifyItemChanged(devicesAdapter.checkedPos);
            devicesAdapter.checkedPos = index;
        }

        selected_device_id = id;
    }
}