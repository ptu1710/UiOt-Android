package com.ixxc.myuit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ixxc.myuit.Interface.Test;
import com.ixxc.myuit.Model.Attribute;
import com.ixxc.myuit.GlobalVars;
import com.ixxc.myuit.Model.MetaItem;
import com.ixxc.myuit.R;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.AttrsViewHolder> {
    Context ctx;
    private final List<JsonObject> attributes;
    public static Dictionary<String, JsonObject> changedAttributes;

    public boolean isEditMode = false;

    Test test;

    public AttributesAdapter(List<JsonObject> attrsObj, Test test) {
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
    public void onBindViewHolder(@NonNull AttrsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position == attributes.size()) {
            return;
        }

        JsonObject attr = attributes.get(position);

        String name = attr.get("name").getAsString();
        String type = attr.get("type").getAsString();
        String metaItem= "";
        if(attr.get("meta")!=null){
            JsonObject meta = attr.get("meta").getAsJsonObject();
            Log.d("aaa",meta.toString());
            metaItem+=meta.toString()+"\n";
        }
        holder.tv_meta_item.setText(metaItem);


        String value = "";

        try { value = String.valueOf(attr.get("value").getAsInt()); }
        catch (UnsupportedOperationException exception) {
            if (exception.getMessage().equals("JsonObject")) {
                value = Attribute.formatJsonValue(attr.get("value").getAsJsonObject().toString());
            }
        } catch (NumberFormatException exception) {
            value = attr.get("value").getAsString();
        } catch (NullPointerException ignored) { }

        holder.tv_name.setText(name);

        if (isEditMode) {
            holder.et_value.setInputType(Attribute.GetType(type));
        }

        holder.et_value.setText(value);
        holder.et_value.setFocusable(isEditMode);
        holder.et_value.setFocusableInTouchMode(isEditMode);

        Log.d(GlobalVars.LOG_TAG, name + " - " + type);

        holder.et_value.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if (String.valueOf(holder.et_value.getText()).equals("")) {
                    attr.add("value", null);
                } else {
                    if (holder.et_value.getInputType() == InputType.TYPE_CLASS_NUMBER) {
                        attr.addProperty("value", Integer.parseInt(holder.et_value.getText().toString()));
                    } else {
                        attr.addProperty("value", holder.et_value.getText().toString());
                    }
                }

                attr.addProperty("timestamp", System.currentTimeMillis());

                changedAttributes.remove(attr);
                changedAttributes.put(name, attr);
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

    class AttrsViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_name;
        private final EditText et_value;
        private Button btn_add_config;
        private TextView tv_meta_item;


        public AttrsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            et_value = itemView.findViewById(R.id.et_value);
            btn_add_config = itemView.findViewById(R.id.btn_add_config);
            tv_meta_item = itemView.findViewById(R.id.tv_meta_item);


        }


    }



}
