package com.ixxc.uiot.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceRecyclerAdapter extends RecyclerView.Adapter<DeviceRecyclerAdapter.DeviceViewHolder> implements Filterable {
    private final List<Device> devices;
    private final List<Device> filteredDevices;
    private final Context ctx;
    private final int normalColor;
    public int checkedPos = -1;

    public DeviceRecyclerAdapter(Context context, List<Device> devices) {
        this.ctx = context;
        this.devices = new ArrayList<>(devices);
        this.filteredDevices = new ArrayList<>();

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
        holder.bind(filteredDevices.get(holder.getAbsoluteAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return filteredDevices.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Device> filteredList;
                if (charSequence == null || charSequence.length() == 0) {
                    filteredList = new ArrayList<>();
                } else {
                    filteredList = devices.stream()
                            .filter(device -> device.name.toLowerCase().contains(charSequence.toString().toLowerCase().trim()))
                            .collect(Collectors.toList());
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredDevices.clear();
                filteredDevices.addAll((Collection<? extends Device>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_name;
        private final ImageView iv_icon, iv_expand;
        private final CardView cv_device;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_icon =  itemView.findViewById(R.id.iv_icon);
            iv_expand =  itemView.findViewById(R.id.iv_expand_3);
            cv_device = itemView.findViewById(R.id.cv_device);
        }

        void bind(Device device) {
            if (device == null) return;

            if (checkedPos == -1) cv_device.setCardBackgroundColor(normalColor);
            else {
                if (checkedPos == getAbsoluteAdapterPosition()) {
                    cv_device.setCardBackgroundColor(normalColor);
                } else {
                    cv_device.setCardBackgroundColor(normalColor);
                }
            }

            tv_name.setText(device.name);
            iv_icon.setImageDrawable(device.getIconDrawable(ctx));
            iv_expand.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull DeviceViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
}
