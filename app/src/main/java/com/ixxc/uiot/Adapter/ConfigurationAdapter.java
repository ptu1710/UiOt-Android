package com.ixxc.uiot.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ixxc.uiot.Interface.MetaItemListener;
import com.ixxc.uiot.Model.MetaItem;
import com.ixxc.uiot.R;
import com.ixxc.uiot.Utils;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationAdapter extends RecyclerView.Adapter<ConfigurationAdapter.ViewHolder>{

    Context context;
    List<MetaItem> metaItems;
    ArrayList<MetaItem> items_chosen= new ArrayList<>();
    MetaItemListener metaItemListener;

    public ConfigurationAdapter(Context context, List<MetaItem> metaItems,MetaItemListener metaItemListener) {
        this.context = context;
        this.metaItems = metaItems;
        this.metaItemListener = metaItemListener;
    }

    @NonNull
    @Override
    public ConfigurationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.configuration_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigurationAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String name = Utils.formatString(metaItems.get(position).name);
        holder.cb_config_item.setText(name);
        holder.cb_config_item.setOnClickListener(v -> {
            if(holder.cb_config_item.isChecked()) items_chosen.add(metaItems.get(position));
            else items_chosen.remove(metaItems.get(position));

            metaItemListener.metaItemListener(items_chosen);
        });
    }

    @Override
    public int getItemCount() {
        return metaItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_config_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cb_config_item = itemView.findViewById(R.id.cb_config_item);
        }
    }
}
