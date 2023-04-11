package com.ixxc.myuit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.Model.Map;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraBoundsOptions;
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
import com.mapbox.maps.plugin.attribution.AttributionPlugin;
import com.mapbox.maps.plugin.logo.LogoPlugin;
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MapsFragment extends Fragment {
    HomeActivity parentActivity;
    private MapView mapView;
    private Map mapData;
    private static MapboxMap mapboxMap;
    private TextView tvAssetName;
    private ImageView ivIcon;
    private LinearLayout detailsLayout;
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private ProgressBar pbLoading;

    public String lastSelectedId = "";
    private boolean firstTime = true;

    public MapsFragment() { }

    public MapsFragment(HomeActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InitViews(view);
        InitEvents();

        mapView.setVisibility(View.INVISIBLE);

        new Thread(() -> {
            while (!Map.isReady) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            parentActivity.runOnUiThread(this::setMapView);

        }).start();
    }

    private void InitViews(View view) {
        mapView = view.findViewById(R.id.mapView);
        detailsLayout = view.findViewById(R.id.bs_MoreDetails);
        tvAssetName = view.findViewById(R.id.tv_assetName);
        ivIcon = view.findViewById(R.id.iv_assetIcon);
        pbLoading = view.findViewById(R.id.pb_loading_4);
        sheetBehavior = BottomSheetBehavior.from(detailsLayout);
    }

    private void InitEvents() { }

    private void setMapView() {
        mapData = Map.getMapObj();

        // Get the scale bar plugin instance and disable it
        ScaleBarPlugin scaleBarPlugin = mapView.getPlugin(Plugin.MAPBOX_SCALEBAR_PLUGIN_ID);
        assert scaleBarPlugin != null;
        scaleBarPlugin.setEnabled(false);

        // Get the logo plugin instance and disable it
        LogoPlugin logoPlugin = mapView.getPlugin(Plugin.MAPBOX_LOGO_PLUGIN_ID);
        assert logoPlugin != null;
        logoPlugin.setEnabled(false);

        //Get the attribution plugin instance and disable it
        AttributionPlugin attributionPlugin = mapView.getPlugin(Plugin.MAPBOX_ATTRIBUTION_PLUGIN_ID);
        assert attributionPlugin != null;
        attributionPlugin.setEnabled(false);

        mapboxMap = mapView.getMapboxMap();

        // Load style and map data
        mapboxMap.loadStyleJson(Objects.requireNonNull(new Gson().toJson(mapData)), style -> {

            style.removeStyleLayer("poi-level-1");
            style.removeStyleLayer("highway-name-major");

            // Get the annotation plugin instance
            AnnotationPlugin annoPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
            AnnotationConfig annoConfig = new AnnotationConfig("map_annotation");
            PointAnnotationManager pointAnnoManager = (PointAnnotationManager) annoPlugin.createAnnotationManager(AnnotationType.PointAnnotation, annoConfig);

            // Add click listener to the annotation manager
            pointAnnoManager.addClickListener(pointAnnotation -> {
                Log.d(GlobalVars.LOG_TAG, "onAnnotationClick: " + pointAnnotation.getData());
                return true;
            });


            // Add device markers to the map
            ArrayList<PointAnnotationOptions> markerList = new ArrayList<>();

            Bitmap bitmap = convertDrawableToBitmap(Objects.requireNonNull(AppCompatResources.getDrawable(parentActivity, R.drawable.ic_iot)));

            for (Device device : Device.getAllDevices()) {
                JsonObject o = new JsonObject();
                o.addProperty("id", device.id);
                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                        .withPoint(device.getPoint())
                        .withData(o)
                        .withIconImage(bitmap);
                markerList.add(pointAnnotationOptions);
            }

            pointAnnoManager.create(markerList);
        });

        // Set camera position
        mapboxMap.setCamera(
                new CameraOptions.Builder()
                        .center(mapData.getCenter())
                        .zoom(mapData.getZoom())
                        .build()
        );

        // Set camera bounds
        mapboxMap.setBounds(
                new CameraBoundsOptions.Builder()
                        .minZoom(mapData.getMinZoom())
                        .maxZoom(mapData.getMaxZoom())
                        .bounds(mapData.getBounds())
                        .build()
        );

        pbLoading.setVisibility(View.GONE);
        if (!isHidden()) onHiddenChanged(false);
    }

    public void toggleBottomSheet(@NonNull String id) {
        Log.d(GlobalVars.LOG_TAG, String.valueOf(sheetBehavior.getState()));
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED && !id.equals("")) {
            setBottomSheet(id);
            parentActivity.navbar.animate().translationY(260).setDuration(260);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            if (!Objects.equals(lastSelectedId, id) && !id.equals("")) {
                setBottomSheet(id);
            } else {
                lastSelectedId = "";
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                parentActivity.navbar.animate().translationY(0).setDuration(260);
            }
        }
    }

    private void setBottomSheet(@NonNull String assetId) {
        Device device = Device.getDeviceById(assetId);
        HashMap<String, String> notNullAttrs;
        if (device != null) {
//            notNullAttrs = device.getNotNullAttrs();
//            NotNullAttrsAdapter adapter = new NotNullAttrsAdapter(notNullAttrs);
//            LinearLayoutManager layoutManager =  new LinearLayoutManager(getContext());

            tvAssetName.setText(device.name);
            ivIcon.setImageResource(R.drawable.ic_iot);
//            rvHighlightAttrs.setLayoutManager(layoutManager);
//            rvHighlightAttrs.setAdapter(adapter);
        }
        lastSelectedId = assetId;
    }

    // convert drawable to bitmap
    private Bitmap convertDrawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                72,
                72,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (firstTime && Map.isReady && !hidden) {
            firstTime = false;
            Utils.delayHandler.postDelayed(() -> {
                mapView.setVisibility(View.VISIBLE);
            }, 200);
        }
        super.onHiddenChanged(hidden);
    }
}