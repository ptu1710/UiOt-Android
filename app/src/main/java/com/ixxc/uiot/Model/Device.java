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
    public int getIconRes(String deviceType) {
        switch (deviceType) {
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
            case "Electricity Producer Asset":
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
            case "Gateway Asset":
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
    public int getColorIcon(String deviceType){

        switch (deviceType) {
            case "GroupAsset":
                return R.color.Group_Asset;
            case "PeopleCounterAsset":
                return R.color.People_Counter_Asset;
            case "ElectricityBatteryAsset":
                return R.color.Electricity_Battery_Asset;
            case "ElectricVehicleAsset":
                return R.color.Electric_Vehicle_Asset;
            case "ElectricVehicleFleetGroupAsset":
                return R.color.Electric_Vehicle_Fleet_Group_Asset;
            case "CityAsset":
                return R.color.City_Asset;
            case "ThingAsset":
                return R.color.Thing_Asset;
            case "ElectricityChargerAsset":
                return R.color.Electricity_Charger_Asset;
            case "VentilationAsset":
                return R.color.Ventilation_Asset;
            case "ShipAsset":
                return R.color.Ship_Asset;
            case "ElectricityProducerAsset":
                return R.color.Electricity_Producer_Asset;
            case "MicrophoneAsset":
                return R.color.Microphone_Asset;
            case "BuildingAsset":
                return R.color.Building_Asset;
            case "ParkingAsset":
                return R.color.Parking_Asset;
            case "ElectricityConsumerAsset":
                return R.color.Electricity_Consumer_Asset;
            case "PlugAsset":
                return R.color.Plug_Asset;
            case "ThermostatAsset":
                return R.color.Thermostat_Asset;
            case "GatewayAsset":
                return R.color.Gateway_Asset;
            case "WeatherAsset":
                return R.color.Weather_Asset;
            case "RoomAsset":
                return R.color.Room_Asset;
            case "DoorAsset":
                return R.color.Door_Asset;
            case "GroundwaterSensorAsset":
                return R.color.Groundwater_Sensor_Asset;
            case "PVSolarAsset":
                return R.color.PV_Solar_Asset;
            case "WindTurbineAsset":
                return R.color.Wind_Turbine_Asset;
            case "PresenceSensorAsset":
                return R.color.Presence_Sensor_Asset;
            case "LightAsset":
                return R.color.Light_Asset;
            case "EnvironmentSensorAsset":
                return R.color.Environment_Sensor_Asset;
            case "ElectricitySupplierAsset":
                return R.color.Electricity_Supplier_Asset;

        }

        return R.color.white;
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
}
