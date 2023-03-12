package com.ixxc.myuit.Adapter;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.ixxc.myuit.Model.Attribute;
import com.ixxc.myuit.R;

import java.util.List;

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.AttrsViewHolder> {
    private final List<JsonObject> attributes;
    Context ctx;
    FragmentManager fm;
    String id;
    Attribute attribute;

    public int selectedIndex = -1;

    public boolean isEditMode = false;

    public AttributesAdapter(FragmentManager fm, String id, List<JsonObject> attrsObj) {



        this.attributes = attrsObj;
        this.id = id;
        this.fm = fm;
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

        try {
            value = attr.get("value").getAsString();
        } catch (UnsupportedOperationException | NullPointerException ignored) { }

        holder.til_attribute_name.setHint(name);
        holder.et_attribute_value.setText(value);
        holder.et_attribute_value.setInputType(attribute.GetType(type));
        holder.et_attribute_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    selectedIndex = holder.getAdapterPosition();
                }
            }
        });

        holder.cv_attribute.setOnClickListener(view -> {
            String toast = holder.til_attribute_name.getHint() + " is " + holder.et_attribute_value.getText();
            Toast.makeText(ctx, toast, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return attributes == null ? 0 : attributes.size() + 1;
    }

    static class AttrsViewHolder extends RecyclerView.ViewHolder {
        private final TextInputLayout til_attribute_name;
        private final EditText et_attribute_value;
        private final CardView cv_attribute;

        public AttrsViewHolder(@NonNull View itemView) {
            super(itemView);
            til_attribute_name = itemView.findViewById(R.id.til_name);
            et_attribute_value = itemView.findViewById(R.id.et_name);
            cv_attribute = itemView.findViewById(R.id.cv_attribute);
        }
    }


}
