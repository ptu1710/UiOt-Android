package com.ixxc.uiot;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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

import com.amrdeveloper.treeview.TreeViewHolderFactory;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Adapter.DeviceRecyclerAdapter;
import com.ixxc.uiot.Adapter.DeviceTreeViewAdapter;
import com.ixxc.uiot.Interface.RecyclerViewItemListener;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.User;
import com.ixxc.uiot.Utils.Util;

import java.util.List;

public class DevicesFragment extends Fragment {
    HomeActivity parentActivity;
    ShimmerFrameLayout layout_shimmer;
    RecyclerView rv_devices;
    ImageView iv_add;
    SwipeRefreshLayout srl_devices;
    View rootView;
    SearchView searchView;
    TextView tv_sort, tv_type;
    List<Device> devicesList;
    ActivityResultLauncher<Intent> launcher;
    DeviceTreeViewAdapter deviceTreeViewAdapter;
    DeviceRecyclerAdapter deviceRecyclerAdapter;
    User me;
    APIManager api = new APIManager();

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean delete_device = bundle.getBoolean("DELETE_DEVICE");
        boolean show_devices = bundle.getBoolean("SHOW_DEVICES");
        boolean refresh = bundle.getBoolean("REFRESH");

        if (show_devices || refresh) {
            InitDevicesAdapter();

            Util.delayHandler.postDelayed(this::showDevices, 320);

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
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Util.UPDATE_DEVICE) refreshDevices();

            parentActivity.homeFrag.InitWidgets();
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

        if (me.canWriteDevices()) {
            iv_add.setVisibility(View.VISIBLE);
        }

        Util.delayHandler.postDelayed(() -> layout_shimmer.setVisibility(View.VISIBLE), 320);

        // Wait to show all devices
        new Thread(() -> {

            while (!Device.devicesLoaded) {
                try {
                    Thread.sleep(500);
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
        devicesList = Device.getDeviceList();
    }

    private void InitViews(View v) {
        rootView = v;
        layout_shimmer = v.findViewById(R.id.layout_shimmer);
        rv_devices = v.findViewById(R.id.rv_devices);
        iv_add = v.findViewById(R.id.iv_add);
        srl_devices = v.findViewById(R.id.srl_devices);
        searchView = v.findViewById(R.id.searchView);
        tv_sort = v.findViewById(R.id.tv_sort);
        tv_type = v.findViewById(R.id.tv_type);
    }

    private void InitEvents() {
        iv_add.setOnClickListener(view -> {
            Intent intent = new Intent(parentActivity, AddDeviceActivity.class);
            launcher.launch(intent);
        });

        srl_devices.setOnRefreshListener(this::refreshDevices);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                deviceRecyclerAdapter.getFilter().filter(s);

                if (s.equals("")) {
                    if (rv_devices.getAdapter() != deviceTreeViewAdapter) {
                        rv_devices.swapAdapter(deviceTreeViewAdapter, true);
                    }
                } else {
                    if (rv_devices.getAdapter() != deviceRecyclerAdapter) {
                        rv_devices.swapAdapter(deviceRecyclerAdapter, true);
                    }
                }

                return false;
            }
        });

        tv_sort.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(parentActivity, view);

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.itemAToZ) {
                    Log.d(Util.LOG_TAG, "InitEvents: A to Z");
//                    devicesList.stream().filter(device -> device.getChildLevel() == 1).collect(Collectors.toList()).sort(Comparator.comparing(device -> device.name.toLowerCase()));
                } else if (id ==  R.id.itemTimeCreated) {
                    Log.d(Util.LOG_TAG, "InitEvents: Item Time Created");
//                     devicesList.stream().filter(device -> device.getChildLevel() == 1).collect(Collectors.toList()).sort(Comparator.comparing(device -> device.createdOn));
                } else {
                    return false;
                }

//                 devicesAdapter.setListDevices(devicesList);

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

        new Thread(() -> {
            String queryString = "{ \"realm\": { \"name\": \"master\" }}";
            JsonObject query = (JsonObject) JsonParser.parseString(queryString);
            api.queryDevices(query);

            InitDevicesAdapter();

            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("REFRESH", true);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }).start();
    }

    private void InitDevicesAdapter() {

        devicesList = Device.getDeviceList();

        deviceRecyclerAdapter = new DeviceRecyclerAdapter(parentActivity, devicesList, new RecyclerViewItemListener() {
            @Override
            public void onItemClicked(View v, Object item) {
                searchView.clearFocus();
                Intent intent = new Intent(parentActivity, DeviceInfoActivity.class);
                intent.putExtra("DEVICE_ID", ((Device) item).id);
                launcher.launch(intent);
            }

            @Override
            public void onItemLongClicked(View v, Object item) {
                searchView.clearFocus();
                performLongClick(((Device) item));
            }
        });

        TreeViewHolderFactory factory = (v, layout) -> new DeviceTreeViewAdapter.MyViewHolder(v, parentActivity);
        deviceTreeViewAdapter = new DeviceTreeViewAdapter(factory, devicesList);

        deviceTreeViewAdapter.setTreeNodeClickListener((treeNode, view) -> {
            searchView.clearFocus();
            if (treeNode.getChildren().size() == 0) {
                Intent intent = new Intent(parentActivity, DeviceInfoActivity.class);
                intent.putExtra("DEVICE_ID", ((Device) treeNode.getValue()).id);
                launcher.launch(intent);
            }
        });

        deviceTreeViewAdapter.setTreeNodeLongClickListener((treeNode, view) -> {
            searchView.clearFocus();
            performLongClick(((Device) treeNode.getValue()));
            return true;
        });
    }

    private void showDevices() {
        rv_devices.setLayoutManager(new LinearLayoutManager(parentActivity));
        rv_devices.setAdapter(deviceTreeViewAdapter);
        rv_devices.setVisibility(View.VISIBLE);
        layout_shimmer.setVisibility(View.GONE);
    }

    public void performLongClick(Device device) {
        // TODO: if (!me.canWriteDevices()) return false;

        Dialog dialog = new Dialog(parentActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.devices_bottom_dialog);

        ImageView iv_icon = dialog.findViewById(R.id.iv_icon);
        TextView tv_name = dialog.findViewById(R.id.tv_name);
        TextView tv_id = dialog.findViewById(R.id.tv_id);
        LinearLayout layout_delete = dialog.findViewById(R.id.layout_delete);
        LinearLayout layout_info = dialog.findViewById(R.id.layout_info);
        LinearLayout layout_on_maps = dialog.findViewById(R.id.layout_on_maps);

        iv_icon.setImageDrawable(device.getIconDrawable(parentActivity));
        tv_name.setText(device.name);
        tv_id.setText(device.id);

        layout_info.setOnClickListener(view -> {
            Intent intent = new Intent(parentActivity, DeviceInfoActivity.class);
            intent.putExtra("DEVICE_ID", device.id);
            launcher.launch(intent);
            dialog.dismiss();
        });

        layout_on_maps.setOnClickListener(view -> {
            if (device.getPoint() == null) {
                Toast.makeText(parentActivity, "This device has no location on maps!", Toast.LENGTH_SHORT).show();
                return;
            }

            parentActivity.navbar.selectTabAt(2, true);
            Util.delayHandler.postDelayed(() -> parentActivity.mapsFrag.setBottomSheet(device.id), 320);

            dialog.dismiss();
            Toast.makeText(parentActivity, "layout_on_maps", Toast.LENGTH_SHORT).show();
        });

        layout_delete.setOnClickListener(view -> {
            String alertMsg = "Delete \"" + device.name + "\" ? This action cannot be undone!";

            AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
            builder.setTitle("Warning!");
            builder.setMessage(alertMsg);
            builder.setPositiveButton("Delete", (dialogInterface, i) -> {
                boolean hasChild = devicesList.stream().anyMatch(d -> d.getParentId().equals(device.id));
                if (hasChild) {
                    Toast.makeText(parentActivity, "Cannot be delete this device because it contains child device(s)!", Toast.LENGTH_LONG).show();
                    refreshDevices();
                    return;
                }

                new Thread(() -> {
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("DELETE_DEVICE", api.delDevice(device.id));
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }).start();
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> { });
            builder.show();

            dialog.dismiss();
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }
}