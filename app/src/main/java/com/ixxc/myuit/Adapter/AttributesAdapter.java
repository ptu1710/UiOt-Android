package com.ixxc.myuit.Adapter;

import android.content.Context;
import android.util.Log;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.ixxc.myuit.Model.Attribute;
import com.ixxc.myuit.GlobalVars;
import com.ixxc.myuit.R;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.AttrsViewHolder> {
    Context ctx;
    private final List<JsonObject> attributes;
    public static Dictionary<String, JsonObject> changedAttributes;

    public boolean isEditMode = false;

    public AttributesAdapter(List<JsonObject> attrsObj) {
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

        JsonObject attr = attributes.get(position);

        String name = attr.get("name").getAsString();
        String type = attr.get("type").getAsString();

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
    }

    @Override
    public int getItemCount() {
        return attributes == null ? 0 : attributes.size() + 1;
    }

    static class AttrsViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_name;
        private final EditText et_value;

        public AttrsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            et_value = itemView.findViewById(R.id.et_value);
        }
    }


}
