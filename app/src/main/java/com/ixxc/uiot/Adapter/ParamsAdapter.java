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

import com.google.gson.JsonObject;
import com.ixxc.uiot.Interface.ParamItemListener;
import com.ixxc.uiot.R;
import com.ixxc.uiot.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParamsAdapter extends RecyclerView.Adapter<ParamsAdapter.ViewHolder> implements ParamItemListener {
    Context ctx;
    List<String> paramsKey;
    JsonObject agentLinkObject;
    ParamItemListener listener;

    public ParamsAdapter(Context ctx, JsonObject agentLinkObject) {
        this.ctx = ctx;
        this.agentLinkObject = agentLinkObject;

        if (agentLinkObject.has("type") && agentLinkObject.get("type").getAsString().equals("HTTPAgentLink")) {
            this.paramsKey = new ArrayList<>(Arrays.asList(ctx.getResources().getStringArray(R.array.http_agent_params)));
        } else {
            this.paramsKey = new ArrayList<>(Arrays.asList(ctx.getResources().getStringArray(R.array.mqtt_agent_params)));
        }
    }

    @NonNull
    @Override
    public ParamsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.checkbox_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ParamsAdapter.ViewHolder holder, int position) {
        String name = paramsKey.get(position);
        holder.cb_item.setText(Utils.formatString(name));
        holder.cb_item.setChecked(agentLinkObject.has(name));
        holder.cb_item.setButtonTintList(ColorStateList.valueOf(ResourcesCompat.getColor(ctx.getResources(), R.color.bg, null)));
        holder.cb_item.setOnClickListener(view -> onParamItemClick(holder.cb_item.isChecked(), name));
    }

    @Override
    public int getItemCount() {
        return paramsKey.size();
    }

    public void setListener(ParamItemListener listener) {
        this.listener = listener;
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
