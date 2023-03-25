package com.ixxc.myuit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ixxc.myuit.Model.Attribute;
import com.ixxc.myuit.Model.MetaItem;
import com.ixxc.myuit.Model.Role;
import com.ixxc.myuit.R;

import java.util.List;

public class ConfigurationAdapter extends RecyclerView.Adapter<ConfigurationAdapter.ViewHolder>{

    Context context;
    List<MetaItem> metaItems;
    ConfigurationAdapter.ViewHolder viewHolder;
    private ConfigurationAdapter.ItemClickListener mClickListener;

    public ConfigurationAdapter(Context context, List<MetaItem> metaItems) {
        this.context = context;
        this.metaItems = metaItems;
    }


    @NonNull
    @Override
    public ConfigurationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.configuration_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigurationAdapter.ViewHolder holder, int position) {
        viewHolder = holder;

        holder.cb_config_item.setText(metaItems.get(position).name);
        holder.cb_config_item.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return metaItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox cb_config_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            cb_config_item = itemView.findViewById(R.id.cb_config_item);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition(), metaItems.get(getAdapterPosition()));
            }
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, MetaItem metaItem);
    }
}
