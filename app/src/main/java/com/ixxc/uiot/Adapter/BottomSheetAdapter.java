package com.ixxc.uiot.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.ixxc.uiot.Model.Attribute;
import com.ixxc.uiot.R;
import com.ixxc.uiot.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.AttrsViewHolder> {
    private final List<Attribute> attributes;
    private final List<String> attributeNames;
    private final List<JsonElement> attributeValues;

    public BottomSheetAdapter(List<Attribute> attributes) {
        this.attributes = attributes;
        this.attributeNames = attributes.stream().map(Attribute::getName).collect(Collectors.toList());
        this.attributeValues = attributes.stream().map(Attribute::getValue).collect(Collectors.toList());
    }

    @NonNull
    @Override
    public AttrsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_attributes, parent, false);
        return new AttrsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttrsViewHolder holder, int position) {

        String label;

        if (attributes.get(position).getMeta() != null && attributes.get(position).getMeta().get("label") != null) {
            label = attributes.get(position).getMeta().get("label").getAsString();
        } else {
            label = attributeNames.get(position);
        }

        String value;

        if (attributeValues.get(position).isJsonNull()) value = "N/A";
        else value = attributeValues.get(position).getAsString();

        holder.tvAssetAttr.setText(Utils.formatString(label));

//        String unit = Device.getUnit(key);
//        if (unit.equals("")) {
//            value = Asset.NumToWindDirection(Integer.parseInt(value));
//        } else {
//            value = value + " " + unit;
//        }

        holder.tvAssetValue.setText(value);
    }

    @Override
    public int getItemCount() {
        return attributes == null ? 0 : attributes.size();
    }

    static class AttrsViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAssetAttr;
        private final TextView tvAssetValue;
        public AttrsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAssetAttr = itemView.findViewById(R.id.tv_assetAttr);
            tvAssetValue = itemView.findViewById(R.id.tv_assetValue);
        }
    }
}
