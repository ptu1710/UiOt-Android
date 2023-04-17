package com.ixxc.uiot;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewHolderFactory;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ixxc.uiot.API.APIManager;
import com.ixxc.uiot.Adapter.DevicesAdapter;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.User;

import java.util.List;

public class DevicesFragment extends Fragment {
    HomeActivity parentActivity;
    ShimmerFrameLayout layout_shimmer;
    RecyclerView rv_devices;
    ImageView iv_add, iv_delete, iv_community, iv_cancel;
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
            Utils.delayHandler.postDelayed(this::showDevices, 640);

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

        Utils.delayHandler.postDelayed(() -> layout_shimmer.setVisibility(View.VISIBLE), 320);

        // Wait to show all devices
        new Thread(() -> {

            while (!Device.devicesLoaded) {
                try {
                    Thread.sleep(240);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            setDevicesAdapter();

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
        devicesList = Device.getDevicesList();
    }

    private void InitViews(View v) {
        rootView = v;
        layout_shimmer = v.findViewById(R.id.layout_shimmer);
        rv_devices = v.findViewById(R.id.rv_devices);
        iv_add = v.findViewById(R.id.iv_add);
        iv_community = v.findViewById(R.id.iv_community);
        iv_delete = v.findViewById(R.id.iv_delete);
        iv_cancel = v.findViewById(R.id.iv_cancel);
        srl_devices = v.findViewById(R.id.srl_devices);
        searchView = v.findViewById(R.id.searchView);
        tv_sort = v.findViewById(R.id.tv_sort);
        tv_type = v.findViewById(R.id.tv_type);
    }

    private void InitEvents() {
        iv_add.setOnClickListener(view -> {
//            Intent intent = new Intent(parentActivity, AddDeviceActivity.class);
//            mLauncher.launch(intent);

            // select node
            TreeNode node = devicesAdapter.getTreeNodes().get(2);

//            devicesAdapter.expandNode(node);
            devicesAdapter.expandNodeBranch(node);
//            devicesAdapter.expandNodeToLevel(node, 1);

            // Child node
//            TreeNode child = node.getChildren().get(0);
//            Log.d(GlobalVars.LOG_TAG, "InitEvents: " + ((Device) child.getValue()).name);

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

//                filterDevices(s);
                return true;
            }
        });

        tv_sort.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(parentActivity, view);

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.itemAToZ) {
                    Log.d(GlobalVars.LOG_TAG, "InitEvents: A to Z");
                    // devicesList.stream().filter(device -> device.getChildLevel() == 1).collect(Collectors.toList()).sort(Comparator.comparing(device -> device.name.toLowerCase()));
                } else if (id ==  R.id.itemTimeCreated) {
                    Log.d(GlobalVars.LOG_TAG, "InitEvents: Item Time Created");
                    // devicesList.stream().filter(device -> device.getChildLevel() == 1).collect(Collectors.toList()).sort(Comparator.comparing(device -> device.createdOn));
                } else {
                    return false;
                }

                // devicesAdapter.setListDevices(devicesList);

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
            APIManager.queryDevices(query);

            setDevicesAdapter();

            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("REFRESH", true);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }).start();
    }

    private void setDevicesAdapter() {

        devicesList = Device.getDevicesList();

        TreeViewHolderFactory factory = (v, layout) -> new DevicesAdapter.MyViewHolder(v, parentActivity);
        devicesAdapter = new DevicesAdapter(factory, devicesList);

        devicesAdapter.setTreeNodeClickListener((treeNode, view) -> {
            if (treeNode.getChildren().size() > 0 && treeNode.isExpanded()) {
                DevicesAdapter.selectedPosition = devicesAdapter.getTreeNodes().indexOf(treeNode);
            }
        });

        devicesAdapter.setTreeNodeLongClickListener((treeNode, view) -> {
            if (!me.canWriteDevices()) return false;
            changeSelectedDevice(0, ((Device) treeNode.getValue()).id);
            return true;
        });
    }

    private void showDevices() {

        rv_devices.setLayoutManager(new LinearLayoutManager(parentActivity));
        rv_devices.setHasFixedSize(true);

        rv_devices.setAdapter(devicesAdapter);

        rv_devices.setVisibility(View.VISIBLE);
        layout_shimmer.setVisibility(View.GONE);
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

//            devicesAdapter.notifyItemChanged(devicesAdapter.checkedPos);
//            devicesAdapter.checkedPos = index;
        }

        selected_device_id = id;
    }
}