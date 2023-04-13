package com.ixxc.uiot.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.ixxc.uiot.R;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Device {
    @SerializedName("id")
    public String id;
    @SerializedName("version")
    public String version;
    @SerializedName("createdOn")
    public long createdOn;
    @SerializedName("name")
    public String name;
    @SerializedName("accessPublicRead")
    public Boolean accessPublicRead;
    @SerializedName("realm")
    public String realm;
    @SerializedName("type")
    public String type;
    @SerializedName("attributes")
    public JsonObject attributes;
    @SerializedName("path")
    public ArrayList<String> path;

    private List<Attribute> optional;

    public List<Attribute> getOptional() {
        return optional;
    }

    public void setOptional(List<Attribute> optional) {
        this.optional = optional;
    }

    private static final List<Device> deviceList = new ArrayList<>();
    private static final List<Device> assetDeviceList = new ArrayList<>();

    public static List<Device> getDevicesList() {
        return deviceList;
    }

    public static List<Device> getAssetDevices() {
        return assetDeviceList;
    }

    public static void setDevicesList(List<Device> list) {
        if (list == null) return;

        deviceList.clear();
        assetDeviceList.clear();

        if (list.get(0).type.equals("GroupAsset") && list.get(0).name.equals("Consoles")) list.remove(0);

        for (Device device : list) {
            if (!device.type.contains("ConsoleAsset")) {
                if (!device.type.contains("Agent")) {
                    assetDeviceList.add(device);
                }

                deviceList.add(device);
            }
        }
    }

    public static Device getDeviceById(String id) {
        for (Device device : deviceList) {
            if (Objects.equals(device.id, id)) {
                return device;
            }
        }
        return null;
    }

    public static List<String> getDeviceNames() {
        return deviceList.stream().map(d -> d.name + "(" + d.id + ")").collect(Collectors.toList());
    }

    public List<Attribute> getDeviceAttribute() {
        List<Attribute> attributeList = new ArrayList<>();

        for (String key : attributes.keySet()) {
            JsonObject o = attributes.get(key).getAsJsonObject();
            Attribute attribute = new Gson().fromJson(o, Attribute.class);
            attributeList.add(attribute);
        }

        return attributeList;
    }

    // Get required attributes
    public List<Attribute> getRequiredAttributes() {
        List<Attribute> attributeList = new ArrayList<>();

        for (String key : attributes.keySet()) {
            JsonObject o = attributes.get(key).getAsJsonObject();
            Attribute attribute = new Gson().fromJson(o, Attribute.class);
            if (!attribute.optional && attribute.value != null && !attribute.value.isJsonObject()) {
                attributeList.add(attribute);
            }
        }

        return attributeList;
    }

    // Get device parent id
    public String getParentId() {
        String parentId = "";

        for (String path : path) {
            if (!path.equals(id)) {
                parentId = path;
                break;
            }
        }

        return parentId;
    }

    // Get device location
    public Point getPoint() {
        try {
            float lng = attributes.get("location").getAsJsonObject().get("value").getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsFloat();
            float lat = attributes.get("location").getAsJsonObject().get("value").getAsJsonObject().get("coordinates").getAsJsonArray().get(1).getAsFloat();
            return Point.fromLngLat(lng, lat);
        } catch (Exception e) {
            return null;
        }
    }

    // Get icon resource id
    public int getIconRes(String deviceType) {
        switch (deviceType) {
            case "GroupAsset":
                break;
            case "WeatherAsset":
                return R.drawable.ic_weather;
            case "RoomAsset":
            case "DoorAsset":
                return R.drawable.ic_door;
        }

        return R.drawable.ic_iot;
    }

    // Get icon drawable
    public Drawable getIconDrawable(Context context, String deviceType, int colorTint) {
        int resId = getIconRes(deviceType);
        Drawable icon = ResourcesCompat.getDrawable(context.getResources(), resId, null);
        assert icon != null;
        icon.setTint(colorTint);

        return icon;
    }

    // Get icon bitmap (show on Maps)
    public Bitmap getIconPinBitmap(Context context, int resId) {
        // Get icon drawable from resId
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), resId, null);
        assert drawable != null;

        // Get pin drawable
        Drawable pin_drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_pin_green, null);
        assert pin_drawable != null;

        // Draw icon into pin
        Bitmap pin = Bitmap.createBitmap(pin_drawable.getIntrinsicWidth(), pin_drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(pin);
        pin_drawable.setBounds(0, 0, pin_drawable.getIntrinsicWidth(), pin_drawable.getIntrinsicHeight());
        pin_drawable.draw(canvas);

        drawable.setBounds(6, 6, drawable.getIntrinsicWidth() - 18, drawable.getIntrinsicHeight() - 18);
        drawable.draw(canvas);

        return pin;
    }

    // Get child level (1 = parent, 2 = child, 3 = grandchild, ...)
    public int getChildLevel() {
        return path.size();
    }
}
