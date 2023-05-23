package com.ixxc.uiot.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.R;

import java.util.ArrayList;
import java.util.List;

public class DeviceRecyclerAdapter extends RecyclerView.Adapter<DeviceRecyclerAdapter.DeviceViewHolder> implements Filterable {
    private final List<Device> devices;
    private final Context ctx;
    private final int normalColor;
    public int checkedPos = -1;

    private final List<Device> itemsAll;
    private final List<Device> suggestions;

    public DeviceRecyclerAdapter(Context context, List<Device> devices) {
        this.devices = devices;
        this.ctx = context;

        this.itemsAll = new ArrayList<>(devices);
        this.suggestions = new ArrayList<>();

        this.normalColor = ResourcesCompat.getColor(ctx.getResources(), R.color.white, null);
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_layout, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        holder.bind(devices.get(holder.getAbsoluteAdapterPosition()));
        Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.devices_rv_anim);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return devices == null ? 0 : devices.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Device> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(devices);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Device item : devices) {
                        if (item.name.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    devices.clear();
                    devices.addAll((List<Device>) results.values);
                    notifyDataSetChanged();
                }
            }
        };
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

            if (checkedPos == -1) cv_device.setCardBackgroundColor(normalColor);
            else {
                if (checkedPos == getAbsoluteAdapterPosition()) {
                    cv_device.setCardBackgroundColor(normalColor);
                } else {
                    cv_device.setCardBackgroundColor(normalColor);
                }
            }

            tv_name.setText(device.name);
            iv_icon.setImageResource(device.getIconRes());
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull DeviceViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
}
