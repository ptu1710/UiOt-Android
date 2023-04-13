package com.ixxc.uiot.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ixxc.uiot.GlobalVars;
import com.ixxc.uiot.Interface.DevicesListener;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.R;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {
    private List<Device> devices;
    private final DevicesListener devicesListener;
    private final Context ctx;

    private final int normalColor;
    private final int selectedColor;

     public int checkedPos = -1;

    public DevicesAdapter(List<Device> devices, DevicesListener listener, Context context) {
        this.devices = devices;
        this.devicesListener = listener;
        this.ctx = context;

        this.normalColor = ResourcesCompat.getColor(ctx.getResources(), R.color.white, null);
        this.selectedColor = ResourcesCompat.getColor(ctx.getResources(), R.color.red, null);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredDevices(List<Device> filteredDevices) {
        this.devices = filteredDevices;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
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
        holder.bind(devices.get(holder.getAdapterPosition()));
        Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.devices_rv_anim);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return devices == null ? 0 : devices.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_name;
        private final ImageView iv_icon;
        private final CardView cv_device;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_icon =  itemView.findViewById(R.id.iv_icon);
            cv_device = itemView.findViewById(R.id.cv_device);
        }

        @SuppressLint("SetTextI18n")
        void bind(Device device) {
            if (device == null) {
                return;
            }

            // log here
            Log.d(GlobalVars.LOG_TAG, "bind: " + device.name + " " + device.getChildLevel());

            if (checkedPos == -1) cv_device.setCardBackgroundColor(normalColor);
            else {
                if (checkedPos == getAbsoluteAdapterPosition()) {
                    cv_device.setCardBackgroundColor(normalColor);
                } else {
                    cv_device.setCardBackgroundColor(normalColor);
                }
            }

            tv_name.setText(device.name);
            iv_icon.setImageResource(device.getIconRes(device.type));

            cv_device.setOnClickListener(view -> devicesListener.onItemClicked(view, device));
            cv_device.setOnLongClickListener(view -> {
                devicesListener.onItemLongClicked(view, device);
                cv_device.setCardBackgroundColor(normalColor);
                if (checkedPos != getAbsoluteAdapterPosition()) {
                    notifyItemChanged(checkedPos);
                    checkedPos = getAbsoluteAdapterPosition();
                }

                return true;
            });
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull DeviceViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
}
