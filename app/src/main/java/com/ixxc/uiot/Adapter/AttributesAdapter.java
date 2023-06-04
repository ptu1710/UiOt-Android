package com.ixxc.uiot.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ixxc.uiot.GlobalVars;
import com.ixxc.uiot.Interface.AttributeListener;
import com.ixxc.uiot.Model.Attribute;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.Map;
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

import java.util.List;
import java.util.Objects;

public class AttributesAdapter extends RecyclerView.Adapter<AttributesAdapter.AttrsViewHolder> {
    private final Context ctx;
    private final List<Attribute> attributes;
    private final String deviceId;
    private final AttributeListener attributeListener;
    private boolean hasMap = false;

    public AttributesAdapter(Context ctx, String deviceId, List<Attribute> attrsObj, AttributeListener attributeListener) {
        this.ctx = ctx;
        this.attributes = attrsObj;
        this.attributeListener = attributeListener;
        this.deviceId = deviceId;
    }

    @Override
    public int getItemViewType(int position) {
        return position == attributes.size() ? 1 : 2;
    }

    @NonNull
    @Override
    public AttrsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType != 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attribute_layout_1, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.end_device_details, parent, false);
        }

        return new AttrsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttrsViewHolder holder, int position) {
        if (position == attributes.size()) {
            if (!hasMap) setMapView(holder);
            return;
        }

        Attribute attr = attributes.get(position);
        String name = Utils.formatString(attr.getName());
        String value = attr.getValueString();
        String type = attr.getType();
        String validatedType = Utils.formatString(attr.getType());

        holder.ib_star.setEnabled(attr.canShowValue(type));
        holder.ib_star.setImageTintList(ColorStateList.valueOf(attr.canShowValue(type) ? Utils.getColor(ctx, R.color.bg) : Utils.getColor(ctx, R.color.darker_grey)));
        holder.ib_star.setImageResource(attr.isInWidgets(ctx, deviceId) ? R.drawable.ic_star_fill : R.drawable.ic_star_border);

        holder.tv_name.setText(name);

        if (value.equals("")) holder.tv_value.setText(R.string.no_value);
        else holder.tv_value.setText(attr.canShowValue(type) ? value : validatedType);

        holder.linear_menu.setVisibility(attr.isExpanded() ? View.VISIBLE : View.GONE);

        holder.linear_attribute.setOnClickListener(view -> {
            setExpandedView(holder, attr.isExpanded());
            attr.setExpanded(!attr.isExpanded());
        });

        holder.ib_edit.setOnClickListener(view -> attributeListener.onEditClicked(holder.getBindingAdapterPosition()));

        holder.ib_star.setOnClickListener(view -> {
            String widgetString = Utils.getPreferences(ctx, GlobalVars.WIDGET_KEY);
            JsonArray widgets = TextUtils.isEmpty(widgetString) ? new JsonArray() : JsonParser.parseString(widgetString).getAsJsonArray();
            JsonElement widgetInfo = JsonParser.parseString(String.join("-", deviceId, attr.getName()));

            String toastMsg;
            if (widgets.contains(widgetInfo)) {
                widgets.remove(widgetInfo);
                holder.ib_star.setImageResource(R.drawable.ic_star_border);
                toastMsg = "Removed widget!";
            } else {
                widgets.add(widgetInfo);
                holder.ib_star.setImageResource(R.drawable.ic_star_fill);
                toastMsg = "New widget added!";
            }

            Utils.savePreferences(ctx, GlobalVars.WIDGET_KEY, widgets.toString());

            Toast.makeText(ctx, toastMsg, Toast.LENGTH_SHORT).show();
        });
    }

    private void setExpandedView(AttrsViewHolder holder, boolean isExpanded) {
        if (isExpanded) {
            holder.iv_expand.setRotation(0);
            holder.linear_menu.setVisibility(View.GONE);
        } else {
            holder.iv_expand.setRotation(90);
            holder.linear_menu.setVisibility(View.VISIBLE);
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
        private final ImageView iv_expand;
        private final LinearLayout linear_attribute;
        private final LinearLayout linear_menu;
        private final MapView mapView;
        private final ImageButton ib_edit;
        private final ImageButton ib_star;
//        private final ImageButton ib_delete;

        public AttrsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_value = itemView.findViewById(R.id.tv_value);
            linear_attribute = itemView.findViewById(R.id.linear_attribute);
            linear_menu = itemView.findViewById(R.id.linear_menu);
            mapView = itemView.findViewById(R.id.mapView);
            iv_expand = itemView.findViewById(R.id.iv_expand);
            ib_edit = itemView.findViewById(R.id.ib_edit);
            ib_star = itemView.findViewById(R.id.ib_star);
//            ib_delete = itemView.findViewById(R.id.ib_delete);
        }
    }
}
