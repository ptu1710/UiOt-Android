package com.ixxc.myuit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.ixxc.myuit.R;

import java.util.List;

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.AttrsViewHolder> {
    private final List<JsonObject> attributes;
    Context ctx;
    String id;

    public boolean isEditMode = false;

    public AttributesAdapter(String id, List<JsonObject> attrsObj) {
        this.attributes = attrsObj;
        this.id = id;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.attribute_layout;
    }

    @NonNull
    @Override
    public AttrsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        ctx = parent.getContext();
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attribute_layout, parent, false);

        return new AttrsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttrsViewHolder holder, int position) {
        if (position == attributes.size()) {
            return;
        }

        JsonObject attr = attributes.get(position);

        if (attr == null) {
            return;
        }

        String name = attr.get("name").getAsString();
        String value;

        try {
            value = attr.get("value").getAsString();
        } catch (UnsupportedOperationException exception) {
            value = "null";
        }

        holder.tv_attribute_name.setText(name);
        holder.et_attribute_value.setText(value);
        if (!value.equals("null") && isEditMode) {
            holder.et_attribute_value.setEnabled(true);
        }
        holder.cv_attribute.setOnClickListener(view -> {
            String toast = holder.tv_attribute_name.getText() + " is " + holder.et_attribute_value.getText();
            Toast.makeText(ctx, "toast", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return attributes == null ? 0 : attributes.size() + 1;
    }

    static class AttrsViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_attribute_name;
        private final EditText et_attribute_value;
        private final CardView cv_attribute;

        public AttrsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_attribute_name = itemView.findViewById(R.id.tv_attribute_name);
            et_attribute_value = itemView.findViewById(R.id.et_attribute_value);
            cv_attribute = itemView.findViewById(R.id.cv_attribute);
        }
    }
}
