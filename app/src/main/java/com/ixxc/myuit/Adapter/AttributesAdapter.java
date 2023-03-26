package com.ixxc.myuit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.ixxc.myuit.Interface.AttributeListener;
import com.google.gson.JsonPrimitive;
import com.ixxc.myuit.Model.Attribute;
import com.ixxc.myuit.Model.MetaItem;
import com.ixxc.myuit.R;
import com.ixxc.myuit.Utils;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.AttrsViewHolder> {
    Context ctx;
    private final List<Attribute> attributes;
    public static Dictionary<String, Attribute> changedAttributes;
    AttributeListener attributeListener;
    public boolean isEditMode = false;

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
    public void onBindViewHolder(@NonNull AttrsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position == attributes.size()) return;

        Attribute attr = attributes.get(position);
        String name = Utils.validateString(attr.name);
        String type = attr.type;

        // Add Meta Info
        JsonObject meta = attr.meta;
        if(meta != null) {
            // Add config view here
            for (String key : attr.meta.keySet()) {
                if (holder.layout.findViewWithTag(key) == null) {
                    holder.layout.addView(createConfigView(key, MetaItem.getMetaType(key), attr.getMetaValue(key)), 1);
                }
            }
        }

        holder.tv_name.setText(name);

        String value = attr.getValueString();
        if (value.equals("") && !isEditMode) {
            holder.tv_value.setText("null");
            holder.til_value.setVisibility(View.GONE);
        } else {
            holder.tv_value.setText(type);
            holder.et_value.setText(value);
            holder.et_value.setInputType(Attribute.GetType(type));
            holder.et_value.setFocusableInTouchMode(isEditMode);
            holder.til_value.setVisibility(View.VISIBLE);
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
        private final LinearLayout layout;

        public AttrsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_value = itemView.findViewById(R.id.tv_value);
            et_value = itemView.findViewById(R.id.et_value);
            til_value = itemView.findViewById(R.id.til_value);
            btn_add_config = itemView.findViewById(R.id.btn_add_config);
            layout = itemView.findViewById(R.id.config_layout);
        }
    }

    private View createConfigView(String name, String type, String value) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 32);

        switch (type) {
            case "boolean":
                CheckBox cb = new CheckBox(ctx);
                cb.setText(Utils.validateString(name));
                cb.setChecked(value.equals("true"));
                cb.setTag(name);
                cb.setLayoutParams(params);
                return cb;
            case "text":
            case "positiveInteger":
                TextInputLayout til = new TextInputLayout(ctx);
                til.setHint(Utils.validateString(name));
                til.setLayoutParams(params);
                til.setTag(name);

                TextInputEditText et = new TextInputEditText(til.getContext());
                et.setText(value);
                til.addView(et);

                return til;
            case "attributeLink[]":
            case "valueConstraint[]":
            case "valueFormat":
            case "text[]":
                return null;
            default: // agentLink
                return null;
        }
    }
}
