package com.ixxc.uiot.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.ixxc.uiot.GlobalVars;
import com.ixxc.uiot.Interface.AttributeListener;
import com.google.gson.JsonPrimitive;
import com.ixxc.uiot.Model.Attribute;
import com.ixxc.uiot.Model.MetaItem;
import com.ixxc.uiot.R;
import com.ixxc.uiot.Utils;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.AttrsViewHolder> {
    Context ctx;
    private final List<Attribute> attributes;
    public static Dictionary<String, Attribute> changedAttributes;
    AttributeListener attributeListener;
    public boolean isEditMode = false;

    private boolean isExpanded = false;

    public AttributesAdapter(List<Attribute> attrsObj, AttributeListener attributeListener) {
        this.attributes = attrsObj;
        changedAttributes = new Hashtable<>();
        this.attributeListener = attributeListener;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == attributes.size()) ? R.layout.end_device_details : R.layout.attribute_layout;
    }

    @NonNull
    @Override
    public AttrsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        ctx = parent.getContext();

        if(viewType == R.layout.attribute_layout){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attribute_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.end_device_details, parent, false);
        }

        return new AttrsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttrsViewHolder holder, int position) {
        if (position == attributes.size()) {
            holder.btn_show_chart.setOnClickListener(v -> attributeListener.onItemClicked2(v,position));
            return;
        }

        Attribute attr = attributes.get(position);
        String name = Utils.formatString(attr.name);
        String type = attr.type;
        String validatedType = Utils.formatString(attr.type);

        if(isEditMode) {
            holder.btn_add_config.setVisibility(View.VISIBLE);
            holder.til_value.setVisibility(View.VISIBLE);
            holder.et_value.setFocusableInTouchMode(true);
        }
        else holder.btn_add_config.setVisibility(View.GONE);

        // Add Meta Info
        JsonObject meta = attr.meta;
        if(meta != null && isEditMode) {
            // Add config view here
            for (String key : attr.meta.keySet()) {
                if (holder.layout_config.findViewWithTag(key) == null) {
                    holder.layout_config.addView(createConfigView(position, key, MetaItem.getMetaType(key), attr.getMetaValue(key)), 1);
                }
            }
        }

        holder.tv_name.setText(name);

        String value = attr.getValueString();
        if (value.equals("")) {
            holder.tv_value.setText(R.string.no_value);
        } else {
            int inputType = Attribute.GetInputType(type);
            holder.et_value.setInputType(inputType);

            if (Attribute.canShowValue) holder.tv_value.setText(value);
            else holder.tv_value.setText(validatedType);
        }

        holder.et_value.setOnFocusChangeListener((view, focused) -> {
            EditText et = (EditText) view;
            if (!focused) {
                attr.value = new JsonPrimitive(et.getText().toString());
                attr.timestamp = System.currentTimeMillis();

                changedAttributes.remove(attr);
                changedAttributes.put(attr.name, attr);
            }
        });

        holder.btn_add_config.setOnClickListener(v -> attributeListener.onItemClicked(v,position));

        holder.linear_label.setOnClickListener(view -> {
            if (isEditMode) return;

            isExpanded = !isExpanded;

            Attribute.GetInputType(type);
            setExpandedView(holder, validatedType, value, isExpanded);
        });
    }

    private void setExpandedView(AttrsViewHolder holder, String type, String value, boolean isExpanded) {

        if (isExpanded) {
            holder.iv_expand.setRotation(90);

            if (value.equals("")) {
                holder.tv_value.setText(R.string.no_value);
                holder.til_value.setVisibility(View.VISIBLE);
            } else {
                holder.tv_value.setText(type);
                holder.et_value.setText(value);
                holder.til_value.setVisibility(View.VISIBLE);
            }

        } else {
            holder.iv_expand.setRotation(0);

            if (value.equals("")) {
                holder.tv_value.setText(R.string.no_value);
                holder.til_value.setVisibility(View.GONE);
            } else {

                if (Attribute.canShowValue) {
                    holder.tv_value.setText(value);
                    holder.til_value.setVisibility(View.GONE);
                } else {
                    holder.tv_value.setText(type);
                    holder.til_value.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return attributes == null ? 0 : attributes.size() + 1;
    }

    static class AttrsViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_name, tv_value;
        private final EditText et_value;
        private final TextInputLayout til_value;
        private final Button btn_add_config;
        private final LinearLayout layout_config;
        private final Button btn_show_chart;
        private final ImageView iv_expand;
        private final LinearLayout linear_label;

        public AttrsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_value = itemView.findViewById(R.id.tv_value);
            et_value = itemView.findViewById(R.id.et_value);
            til_value = itemView.findViewById(R.id.til_value);
            btn_add_config = itemView.findViewById(R.id.btn_add_config);
            layout_config = itemView.findViewById(R.id.config_layout);
            btn_show_chart = itemView.findViewById(R.id.btn_showChart);
            iv_expand = itemView.findViewById(R.id.iv_expand);
            linear_label = itemView.findViewById(R.id.linear_label);
        }
    }

    private View createConfigView(int pos, String name, String type, String value) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 32);

        switch (type) {
            case "boolean":
                CheckBox cb = new CheckBox(ctx);
                cb.setText(Utils.formatString(name));
                cb.setChecked(value.equals("true"));
                cb.setTag(name);
                cb.setLayoutParams(params);
                cb.setOnCheckedChangeListener((compoundButton, checked) -> attributes.get(pos).meta.addProperty(name, checked));
                cb.setClickable(isEditMode);
                return cb;
            case "text":
            case "positiveInteger":
            case "agentLink":
            case "attributeLink[]":
                TextInputLayout til = new TextInputLayout(ctx);
                til.setHint(Utils.formatString(name));
                til.setLayoutParams(params);
                til.setTag(name);

                TextInputEditText et = new TextInputEditText(til.getContext());
                et.setText(value);
                et.setInputType(Attribute.GetInputType(type));
                et.setOnFocusChangeListener((view, focused) -> attributes.get(pos).meta.addProperty(name, String.valueOf(et.getText())));
                et.setFocusableInTouchMode(isEditMode);
                til.addView(et);

                return til;
            case "valueConstraint[]":
            case "valueFormat":
            case "text[]":
                return null;
            default: // agentLink
                return null;
        }
    }
}
