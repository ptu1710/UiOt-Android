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

    public Device() { }

    public Device(String type) {
        this.type = type;
    }

    public static boolean devicesLoaded = false;

    private static final List<Device> deviceList = new ArrayList<>();

    public static List<Device> getDevicesList() {
        return deviceList;
    }

    public static void setDevicesList(List<Device> list) {
        if (list == null) return;

        deviceList.clear();

        if (list.get(0).type.equals("GroupAsset") && list.get(0).name.equals("Consoles")) list.remove(0);

        for (Device device : list) {
            if (!device.type.contains("ConsoleAsset")) {
                deviceList.add(device);
            }
        }
    }

    public static Device getDeviceById(String id) {
        for (Device device : deviceList) {
            if (device.id.equals(id)) {
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

    // Get device parent device
    public Device getParent() {
        if (path.size() > 1) {
            return getDeviceById(path.get(path.size() - 2));
        }

        return null;
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
    public int getIconRes() {

        switch (type) {
            case "GroupAsset":
                return R.drawable.ic_folder;
            case "PeopleCounterAsset":
                return R.drawable.ic_account_multiple;
            case "ElectricityBatteryAsset":
                return R.drawable.ic_battery_charging;
            case "ElectricVehicleAsset":
                return R.drawable.ic_car_electric;
            case "ElectricVehicleFleetGroupAsset":
                return R.drawable.ic_car_multiple;
            case "CityAsset":
                return R.drawable.ic_city;
            case "ThingAsset":
                return R.drawable.ic_cube_outline;
            case "ElectricityChargerAsset":
                return R.drawable.ic_ev_station;
            case "VentilationAsset":
                return R.drawable.ic_fan;
            case "ShipAsset":
                return R.drawable.ic_ferry;
            case "ElectricityProducerAsset":
                return R.drawable.ic_flash;
            case "MicrophoneAsset":
                return R.drawable.ic_microphone;
            case "BuildingAsset":
                return R.drawable.ic_office_building;
            case "ParkingAsset":
                return R.drawable.ic_parking;
            case "ElectricityConsumerAsset":
                return R.drawable.ic_power_plug;
            case "PlugAsset":
                return R.drawable.ic_power_socket_eu;
            case "ThermostatAsset":
                return R.drawable.ic_thermostat;
            case "GatewayAsset":
                return R.drawable.ic_router_wireless;
            case "WeatherAsset":
                return R.drawable.ic_weather_partly_cloudy;
            case "RoomAsset":
                return R.drawable.ic_room;
            case "DoorAsset":
                return R.drawable.ic_door;
            case "GroundwaterSensorAsset":
                return R.drawable.ic_water_outline;
            case "PVSolarAsset":
                return R.drawable.ic_white_balance_sunny;
            case "WindTurbineAsset":
                return R.drawable.ic_wind_turbine;
            case "PresenceSensorAsset":
                return R.drawable.ic_eye_circle;
            case "LightAsset":
                return R.drawable.ic_lightbulb;
            case "EnvironmentSensorAsset":
                return R.drawable.ic_molecule_co2;
            case "ElectricitySupplierAsset":
                return R.drawable.ic_upload_network;
        }

        return R.drawable.ic_iot;
    }

    //Get color of icon
    public int getColorId(Context ctx){

        switch (type) {
            case "GroupAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Group_Asset, null);
            case "PeopleCounterAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.People_Counter_Asset, null);
            case "ElectricityBatteryAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Electricity_Battery_Asset, null);
            case "ElectricVehicleAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Electric_Vehicle_Asset, null);
            case "ElectricVehicleFleetGroupAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Electric_Vehicle_Fleet_Group_Asset, null);
            case "CityAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.City_Asset, null);
            case "ThingAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Thing_Asset, null);
            case "ElectricityChargerAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Electricity_Charger_Asset, null);
            case "VentilationAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Ventilation_Asset, null);
            case "ShipAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Ship_Asset, null);
            case "ElectricityProducerAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Electricity_Producer_Asset, null);
            case "MicrophoneAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Microphone_Asset, null);
            case "BuildingAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Building_Asset, null);
            case "ParkingAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Parking_Asset, null);
            case "ElectricityConsumerAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Electricity_Consumer_Asset, null);
            case "PlugAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Plug_Asset, null);
            case "ThermostatAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Thermostat_Asset, null);
            case "GatewayAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Gateway_Asset, null);
            case "WeatherAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Weather_Asset, null);
            case "RoomAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Room_Asset, null);
            case "DoorAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Door_Asset, null);
            case "GroundwaterSensorAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Groundwater_Sensor_Asset, null);
            case "PVSolarAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.PV_Solar_Asset, null);
            case "WindTurbineAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Wind_Turbine_Asset, null);
            case "PresenceSensorAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Presence_Sensor_Asset, null);
            case "LightAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Light_Asset, null);
            case "EnvironmentSensorAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Environment_Sensor_Asset, null);
            case "ElectricitySupplierAsset":
                return ResourcesCompat.getColor(ctx.getResources(), R.color.Electricity_Supplier_Asset, null);
        }

        return ResourcesCompat.getColor(ctx.getResources(), R.color.white, null);
    }

    // Get icon drawable
    public Drawable getIconDrawable(Context context) {
        int resId = getIconRes();
        Drawable icon = ResourcesCompat.getDrawable(context.getResources(), resId, null);
        assert icon != null;
        icon.setTint(getColorId(context));

        return icon;
    }

    // Get icon bitmap (show on Maps)
    public Bitmap getIconPinBitmap(Context context) {

        // Get icon drawable from resId
        Drawable drawable = getIconDrawable(context);
        assert drawable != null;
        drawable.setTint(getColorId(context));

        // Get pin drawable
        Drawable pin_drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_pin_green, null);
        assert pin_drawable != null;
        pin_drawable.setTint(getColorId(context));

        // Draw icon into pin
        Bitmap pin = Bitmap.createBitmap(pin_drawable.getIntrinsicWidth(), pin_drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(pin);
        pin_drawable.setBounds(0, 0, pin_drawable.getIntrinsicWidth(), pin_drawable.getIntrinsicHeight());
        pin_drawable.draw(canvas);

        int start = pin_drawable.getIntrinsicWidth() * 10 / 100;
        int end1 = pin_drawable.getIntrinsicWidth() * 90 / 100;
        int end2 = pin_drawable.getIntrinsicHeight() * 40 / 100;

        drawable.setBounds(start, start, end1, end2);
        drawable.draw(canvas);

        return pin;
    }

    public List<String> getStoredAttributes() {
        List<String> attributeList = new ArrayList<>();

        for (String key : attributes.keySet()) {
            JsonObject o = attributes.get(key).getAsJsonObject();
            Attribute attribute = new Gson().fromJson(o, Attribute.class);
            if (attribute.meta != null && attribute.getMetaValue("storeDataPoints").equals("true")) {
                attributeList.add(key);
            }
        }

        return attributeList;
    }
}
