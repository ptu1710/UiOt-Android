package com.ixxc.myuit.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.ixxc.myuit.GlobalVars;
import com.ixxc.myuit.R;
import com.mapbox.geojson.Point;

import java.io.IOException;
import java.io.InputStream;
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

    private static final List<Device> deviceListFiltered = new ArrayList<>();

    public static List<Device> getAllDevices() {
        return deviceListFiltered;
    }

    public static void setDevicesList(List<Device> list) {
        if (list == null) return;

        deviceListFiltered.clear();
        if (list.get(0).type.equals("GroupAsset") && list.get(0).name.equals("Consoles")) list.remove(0);

        for (Device device : list) {
            if (!device.type.contains("ConsoleAsset")) {
                deviceListFiltered.add(device);
            }
        }
    }

    public static Device getDeviceById(String id) {
        for (Device device : deviceListFiltered) {
            if (Objects.equals(device.id, id)) {
                return device;
            }
        }
        return null;
    }

    public static List<String> getDeviceNames() {
        return deviceListFiltered.stream().map(d -> d.name + "(" + d.id + ")").collect(Collectors.toList());
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

    public Point getPoint() {
        float lng = attributes.get("location").getAsJsonObject().get("value").getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsFloat();
        float lat = attributes.get("location").getAsJsonObject().get("value").getAsJsonObject().get("coordinates").getAsJsonArray().get(1).getAsFloat();
        return Point.fromLngLat(lng, lat);
    }

    public Drawable getIcon(Context context, String deviceType) {

        switch (deviceType) {
            case "GroupAsset":
                break;
            case "WeatherAsset":
                return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_weather, null);
            case "RoomAsset":
            case "DoorAsset":
                return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_door, null);
        }

        return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_iot, null);
    }

    public Bitmap getIconPin(Context context, Drawable drawable) {
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
}
