package com.ixxc.myuit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ixxc.myuit.Interface.Test;
import com.google.gson.JsonPrimitive;
import com.ixxc.myuit.Model.Attribute;
import com.ixxc.myuit.GlobalVars;
import com.ixxc.myuit.R;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.AttrsViewHolder> {
    Context ctx;
    private final List<Attribute> attributes;
    public static Dictionary<String, JsonObject> changedAttributes;
    Test test;
    public boolean isEditMode = false;

    public AttributesAdapter(List<Attribute> attrsObj,Test test) {

        this.attributes = attrsObj;
        changedAttributes = new Hashtable<>();
        this.test = test;
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
        if (position == attributes.size()) return;

        Attribute attr = attributes.get(position);
        String name = attr.name;
        String type = attr.type;
        String metaItem= "";
        if(attr.meta!=null){
            JsonObject meta = attr.meta;
            for (String key:meta.keySet()) {
                metaItem+=key+"\n";
            }
        }
        holder.tv_meta_item.setText(metaItem);


        String value;
        switch (attr.getValueType()) {
            case 0: // value is NULL
                value = "";
                break;
            case 1: // value is JsonObject
                value = Attribute.formatJsonValue(String.valueOf(attr.value.getAsJsonObject()));
                break;
            case 2: // value is int
                value = String.valueOf(attr.value.getAsInt());
                break;
            default: // value is String or something else
                value = attr.value.getAsString();
        }

        holder.tv_name.setText(name);

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

        Log.d(GlobalVars.LOG_TAG, name + " - " + type);

        holder.et_value.setOnFocusChangeListener((view, focused) -> {
            EditText et = (EditText) view;
            if (!focused) {
                attr.value = new JsonPrimitive(et.getText().toString());
                attr.timestamp = System.currentTimeMillis();

                changedAttributes.remove(attr);
                changedAttributes.put(attr.name, attr);
            }
        });

        holder.btn_add_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test.onItemClicked(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attributes == null ? 0 : attributes.size() + 1;
    }

    static class AttrsViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_name, tv_value;
        private final EditText et_value;
        private final TextInputLayout til_value;
        private Button btn_add_config;
        private TextView tv_meta_item;


        public AttrsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_value = itemView.findViewById(R.id.tv_value);
            et_value = itemView.findViewById(R.id.et_value);
            til_value = itemView.findViewById(R.id.til_value);
            btn_add_config = itemView.findViewById(R.id.btn_add_config);
            tv_meta_item = itemView.findViewById(R.id.tv_meta_item);


        }
    }
}
