package com.ixxc.uiot.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ixxc.uiot.Interface.MetaItemListener;
import com.ixxc.uiot.Model.MetaItem;
import com.ixxc.uiot.R;
import com.ixxc.uiot.Utils;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationAdapter extends RecyclerView.Adapter<ConfigurationAdapter.ViewHolder>{
    Context ctx;
    List<MetaItem> metaItems;
    List<MetaItem> selected_items = new ArrayList<>();
    MetaItemListener listener;

    public ConfigurationAdapter(Context ctx, List<MetaItem> metaItems, MetaItemListener listener) {
        this.ctx = ctx;
        this.metaItems = metaItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConfigurationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.checkbox_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ConfigurationAdapter.ViewHolder holder, int position) {
        String name = Utils.formatString(metaItems.get(position).getName());
        holder.cb_item.setText(name);
        holder.cb_item.setButtonTintList(ColorStateList.valueOf(ResourcesCompat.getColor(ctx.getResources(), R.color.bg, null)));
        holder.cb_item.setOnClickListener(v -> listener.metaItemListener(selected_items));
    }

    @Override
    public int getItemCount() {
        return metaItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_item = itemView.findViewById(R.id.cb_item);
        }
    }
}
