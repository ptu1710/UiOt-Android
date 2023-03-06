package com.ixxc.myuit.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ixxc.myuit.Interface.DevicesListener;
import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.R;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {
    private List<Device> devices;
    private final DevicesListener devicesListener;

    public DevicesAdapter(List<Device> devices, DevicesListener listener) {
        this.devices = devices;
        this.devicesListener = listener;
    }

    public void setFilteredDevices(List<Device> filteredDevices) {
        this.devices = filteredDevices;
        notifyDataSetChanged();
    }

    public void setListDevices(List<Device> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_layout, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = devices.get(position);
        if (device == null) {
            return;
        }

        holder.tv_Name.setText(device.name);
        holder.tv_Id.setText("ID: " + device.id);
//        holder.iv_Icon.setImageResource(com.mapbox.mapboxsdk.R.drawable.mapbox_compass_icon);
//        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.cardView.getContext(), R.anim.devices_rv_anim));
//        holder.cardView.setOnClickListener(view -> devicesListener.onItemClicked(device));
//        holder.cardView.setOnLongClickListener(v -> {
//            devicesListener.onItemLongClicked(v, device);
//            return false;
//        });
    }

    @Override
    public int getItemCount() {
        return devices == null ? 0 : devices.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_Name, tv_Id;
        private ImageView iv_Icon;
        private CardView cardView;
        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_Name = itemView.findViewById(R.id.tv_Name);
            tv_Id = itemView.findViewById(R.id.tv_ID);
            iv_Icon =  itemView.findViewById(R.id.iv_Icon);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
