package com.ixxc.uiot.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ixxc.uiot.Interface.AttributeListener;
import com.ixxc.uiot.Model.Attribute;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.Map;
import com.ixxc.uiot.Model.MetaItem;
import com.ixxc.uiot.R;
import com.ixxc.uiot.Utils;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.AnnotationType;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.GesturesUtils;
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.AttrsViewHolder> {
    Context ctx;
    private final List<Attribute> attributes;
    private final String deviceId;
    public static Dictionary<String, Attribute> changedAttributes;
    AttributeListener attributeListener;
    public boolean isEditMode = false;
    private boolean hasMap = false;

    public AttributesAdapter(String deviceId, List<Attribute> attrsObj, AttributeListener attributeListener) {
        this.attributes = attrsObj;
        changedAttributes = new Hashtable<>();
        this.attributeListener = attributeListener;
        this.deviceId = deviceId;
    }

    @Override
    public int getItemViewType(int position) {
        return position == attributes.size() ? R.layout.end_device_details : R.layout.attribute_layout;
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
            if (!hasMap) {
                setMapView(holder);
            }
            return;
        }

        Attribute attr = attributes.get(position);
        String name = Utils.formatString(attr.name);
        String value = attr.getValueString();
        String type = attr.type;
        String validatedType = Utils.formatString(attr.type);

        holder.tv_name.setText(name);
        holder.et_value.setInputType(Attribute.GetInputType(type));

        if(isEditMode) {
            holder.btn_add_config.setVisibility(View.VISIBLE);
            holder.til_value.setVisibility(View.VISIBLE);
            holder.et_value.setFocusableInTouchMode(true);

            // Add Meta Info
            JsonObject meta = attr.meta;
            int viewCount = 0;
            // Clear old views first
            holder.meta_layout.removeAllViews();
            if(meta != null) {
                // Add config view here
                for (String key : attr.meta.keySet()) {
                    if (holder.meta_layout.findViewWithTag(key) == null) {
                        View view = createConfigView(position, key, MetaItem.getMetaType(key), attr.getMetaValue(key));
                        if (view instanceof CheckBox) holder.meta_layout.addView(view, viewCount++);
                        else holder.meta_layout.addView(view, 0);
                    }
                }
            }
        }
        else {
            holder.btn_add_config.setVisibility(View.GONE);
            setExpandedView(holder, validatedType, value, true);
        }

        if (value.equals("")) {
            holder.tv_value.setText(R.string.no_value);
            holder.et_value.setText("");
        } else {

            if (isEditMode) {
                holder.et_value.setText(value);
                holder.tv_value.setText(validatedType);
            } else {

                if (Attribute.canShowValue) holder.tv_value.setText(value);
                else holder.tv_value.setText(validatedType);
            }
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

        holder.btn_add_config.setOnClickListener(v -> attributeListener.addConfigClicked(position));

        holder.linear_label.setOnClickListener(view -> {
            if (isEditMode) return;

            boolean isExpanded = view.getTag() != null && (boolean) view.getTag();

            Attribute.GetInputType(type);
            setExpandedView(holder, validatedType, value, isExpanded);
            view.setTag(!isExpanded);
        });
    }

    private void setExpandedView(AttrsViewHolder holder, String type, String value, boolean isExpanded) {

        if (isExpanded) {
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
        } else {
            holder.iv_expand.setRotation(90);

            if (value.equals("")) {
                holder.tv_value.setText(R.string.no_value);
                holder.til_value.setVisibility(View.VISIBLE);
            } else {
                holder.tv_value.setText(type);
                holder.et_value.setText(value);
                holder.til_value.setVisibility(View.VISIBLE);
            }

            attributeListener.onAttributeClicked(holder.getBindingAdapterPosition());
        }
    }

    private void setMapView(AttrsViewHolder holder) {

        Device device = Device.getDeviceById(deviceId);

        if (device != null && device.getPoint() != null) {
            Map mapData = Map.getMapObj();

            MapboxMap mapboxMap = holder.mapView.getMapboxMap();

            // Load style and map data
            mapboxMap.loadStyleJson(Objects.requireNonNull(new Gson().toJson(mapData)), style -> {

                // Get the scale bar plugin instance and disable it
                ScaleBarPlugin scaleBarPlugin = holder.mapView.getPlugin(Plugin.MAPBOX_SCALEBAR_PLUGIN_ID);
                assert scaleBarPlugin != null;
                scaleBarPlugin.setEnabled(true);

                // Disable map scroll gestures
                GesturesPlugin gesturesPlugin = GesturesUtils.getGestures((holder.mapView));
                gesturesPlugin.setScrollEnabled(false);

                // Get the annotation plugin instance
                AnnotationPlugin annoPlugin = AnnotationPluginImplKt.getAnnotations(holder.mapView);
                AnnotationConfig annoConfig = new AnnotationConfig("map_annotation");
                PointAnnotationManager pointAnnoManager = (PointAnnotationManager) annoPlugin.createAnnotationManager(AnnotationType.PointAnnotation, annoConfig);

                // Add device marker to the map
                Bitmap bitmap = device.getIconPinBitmap(ctx);

                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                        .withPoint(device.getPoint())
                        .withIconImage(bitmap);

                pointAnnoManager.create(pointAnnotationOptions);
            });

            // Set camera position
            mapboxMap.setCamera(
                    new CameraOptions.Builder()
                            .center(device.getPoint())
                            .zoom(mapData.getZoom())
                            .build()
            );
        } else {
            holder.mapView.setVisibility(View.GONE);
        }

        hasMap = true;
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
        private final LinearLayout meta_layout;
        private final ImageView iv_expand;
        private final LinearLayout linear_label;
        private final MapView mapView;

        public AttrsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_value = itemView.findViewById(R.id.tv_value);
            et_value = itemView.findViewById(R.id.et_value);
            til_value = itemView.findViewById(R.id.til_value);
            btn_add_config = itemView.findViewById(R.id.btn_add_config);
            meta_layout = itemView.findViewById(R.id.meta_layout);
            iv_expand = itemView.findViewById(R.id.iv_expand);
            linear_label = itemView.findViewById(R.id.linear_label);
            mapView = itemView.findViewById(R.id.mapView);
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
                et.setInputType(Attribute.GetInputType(type));
                et.setText(value);
                et.setOnFocusChangeListener((view, focused) -> attributes.get(pos).meta.addProperty(name, String.valueOf(et.getText())));
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
