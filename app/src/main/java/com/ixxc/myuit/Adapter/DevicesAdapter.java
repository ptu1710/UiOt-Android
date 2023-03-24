package com.ixxc.myuit.Adapter;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.ixxc.myuit.DevicesFragment;
import com.ixxc.myuit.Interface.DevicesListener;
import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.R;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {
    private List<Device> devices;
    private final DevicesListener devicesListener;
    private Context ctx;

     public int checkedPos = -1;
     public int lastPosition = -1;

    public DevicesAdapter(List<Device> devices, DevicesListener listener, Context context) {
        this.devices = devices;
        this.devicesListener = listener;
        this.ctx = context;
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
        holder.bind(devices.get(holder.getAdapterPosition()));
        Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.devices_rv_anim);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return devices == null ? 0 : devices.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_id;
        private ImageView iv_icon;
        private CardView cv_device;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_id = itemView.findViewById(R.id.tv_id);
            iv_icon =  itemView.findViewById(R.id.iv_icon);
            cv_device = itemView.findViewById(R.id.cv_device);
        }

        void bind(Device device) {
            if (device == null) {
                return;
            }

            if (checkedPos == -1) {
                cv_device.setCardBackgroundColor(cv_device.getResources().getColor(R.color.bg));
            } else {
                if (checkedPos == getAdapterPosition()) {
                    cv_device.setCardBackgroundColor(cv_device.getResources().getColor(R.color.red));
                } else {
                    cv_device.setCardBackgroundColor(cv_device.getResources().getColor(R.color.bg));
                }
            }

            tv_name.setText(device.name);
            tv_id.setText("ID: " + device.id);
            iv_icon.setImageResource(R.drawable.ic_vn);

            cv_device.setOnClickListener(view -> devicesListener.onItemClicked(view, device));
            cv_device.setOnLongClickListener(view -> {
                devicesListener.onItemLongClicked(view, device);
                cv_device.setCardBackgroundColor(cv_device.getResources().getColor(R.color.red));
                if (checkedPos != getAdapterPosition()) {
                    notifyItemChanged(checkedPos);
                    checkedPos = getAdapterPosition();
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
