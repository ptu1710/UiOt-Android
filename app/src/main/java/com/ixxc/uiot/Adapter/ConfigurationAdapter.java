package com.ixxc.uiot.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.ixxc.uiot.Interface.ParamItemListener;
import com.ixxc.uiot.Model.MetaItem;
import com.ixxc.uiot.R;
import com.ixxc.uiot.Utils;

import java.util.List;

public class ConfigurationAdapter extends RecyclerView.Adapter<ConfigurationAdapter.ViewHolder> implements ParamItemListener {
    Context ctx;
    List<MetaItem> metaItems;
    JsonObject meta;
    ParamItemListener listener;
    int color;

    public ConfigurationAdapter(Context ctx, JsonObject meta, int color) {
        this.ctx = ctx;
        this.meta = meta;
        this.metaItems = MetaItem.getMetaItemList();
        this.color = color;
    }

    public void setListener(ParamItemListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public ConfigurationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.checkbox_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ConfigurationAdapter.ViewHolder holder, int position) {
        String name = metaItems.get(position).getName();
        String displayName = Utils.formatString(name);
        boolean isCheck = meta.has(name);

        holder.cb_item.setButtonTintList(ColorStateList.valueOf(color));
        holder.cb_item.setText(displayName);
        holder.cb_item.setOnCheckedChangeListener((compoundButton, checked) -> onParamItemClick(checked, name));
        holder.cb_item.setChecked(isCheck);
    }

    @Override
    public int getItemCount() {
        return metaItems.size();
    }

    @Override
    public void onParamItemClick(boolean checked, String name) {
        listener.onParamItemClick(checked, name);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_item = itemView.findViewById(R.id.cb_item);
        }
    }
}
