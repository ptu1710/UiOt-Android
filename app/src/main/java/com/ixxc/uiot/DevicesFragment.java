package com.ixxc.uiot;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Adapter.DevicesAdapter;
import com.ixxc.uiot.Interface.DevicesListener;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.User;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DevicesFragment extends Fragment {
    HomeActivity parentActivity;
    RecyclerView rv_devices;
    ImageView iv_add, iv_delete, iv_community, iv_cancel;
    ProgressBar pb_loading_1;
    SwipeRefreshLayout srl_devices;
    View rootView;
    SearchView searchView;
    TextView tv_sort, tv_type;

    List<Device> devicesList;

    ActivityResultLauncher<Intent> mLauncher;

    DevicesAdapter devicesAdapter;

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
                Toast.makeText(parentActivity,"Device was deleted successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(parentActivity,"An error occurred while deleting the device", Toast.LENGTH_LONG).show();
            }
        }

        return false;
    });

    public DevicesFragment() { }

    public DevicesFragment(HomeActivity homeActivity) {
        this.parentActivity = homeActivity;
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
            while (Device.getDevicesList() == null) {
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
        devicesList = Device.getAssetDevices();
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
        searchView = v.findViewById(R.id.searchView);
        tv_sort = v.findViewById(R.id.tv_sort);
        tv_type = v.findViewById(R.id.tv_type);
    }

    @SuppressLint("NonConstantResourceId")
    private void InitEvents() {
        iv_add.setOnClickListener(view -> {
            Intent intent = new Intent(parentActivity, AddDeviceActivity.class);
            mLauncher.launch(intent);
        });

        iv_delete.setOnClickListener(view -> {
            if (selected_device_id.equals("")) return;

            AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
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

        iv_cancel.setOnClickListener(view -> changeSelectedDevice(-1, ""));

        srl_devices.setOnRefreshListener(this::refreshDevices);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("")) {
                    this.onQueryTextSubmit("");
                }

                filterDevices(s);
                return true;
            }
        });

        tv_sort.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(parentActivity, view);

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.itemAToZ:
                        devicesList.sort(Comparator.comparing(device -> device.name.toLowerCase()));
                        break;
                    case R.id.itemTimeCreated:
                        devicesList.sort(Comparator.comparing(device -> device.createdOn));
                        break;
                    default:
                        break;
                }

                devicesAdapter.setListDevices(devicesList);
                return true;
            });

            popupMenu.inflate(R.menu.menu_sort);
            popupMenu.show();
        });
    }

    public void refreshDevices() {
        rv_devices.smoothScrollToPosition(0);

        srl_devices.setRefreshing(true);
        searchView.clearFocus();

        final Message msg = handler.obtainMessage();
        final Bundle bundle = new Bundle();

        new Thread(() -> {
            String queryString = "{ \"realm\": { \"name\": \"master\" }}";
            JsonObject query = (JsonObject) JsonParser.parseString(queryString);
            APIManager.queryDevices(query);

            bundle.putBoolean("REFRESH", true);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }).start();
    }

    private void showDevices() {
        devicesAdapter = new DevicesAdapter(devicesList, new DevicesListener() {
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
        }, parentActivity);

        LinearLayoutManager layoutManager =  new LinearLayoutManager(rootView.getContext());
        rv_devices.setLayoutManager(layoutManager);
        rv_devices.setAdapter(devicesAdapter);
        rv_devices.setHasFixedSize(true);

        rv_devices.setVisibility(View.VISIBLE);
        pb_loading_1.setVisibility(View.GONE);
    }

    private void filterDevices(String text) {
        List<Device> filtered = devicesList.stream().filter(device -> device.name.toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
        devicesAdapter.setFilteredDevices(filtered);
    }

    private void viewDeviceInfo(String id) {
        Intent toDetails = new Intent(getContext(), DeviceInfoActivity.class);
        toDetails.putExtra("DEVICE_ID", id);
        parentActivity.startActivity(toDetails);
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