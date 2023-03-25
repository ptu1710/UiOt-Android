package com.ixxc.myuit.Adapter;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    public boolean isEditMode = false;

    public AttributesAdapter(List<Attribute> attrsObj) {
        this.attributes = attrsObj;
        changedAttributes = new Hashtable<>();
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
            return;
        }

        Attribute attr = attributes.get(position);

        String name = attr.name;
        String type = attr.type;

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
                attr.value = new JsonParser().parse(et.getText().toString());
                attr.timestamp = System.currentTimeMillis();

                changedAttributes.remove(attr);
                changedAttributes.put(attr.name, attr.toJson());
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

        public AttrsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_value = itemView.findViewById(R.id.tv_value);
            et_value = itemView.findViewById(R.id.et_value);
            til_value = itemView.findViewById(R.id.til_value);
        }
    }
}
