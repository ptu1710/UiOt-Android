package com.ixxc.myuit.Model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.ixxc.myuit.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Device {
    @SerializedName("id")
    public String id;
    @SerializedName("version")
    public String version;
    @SerializedName("name")
    public String name;
    @SerializedName("type")
    public String type;
    @SerializedName("attributes")
    public JsonObject attributes;

    private static List<Device> deviceList;

    public static List<Device> getAllDevices() {
        return deviceList;
    }

    public static void setDevicesList(List<Device> list) {
        if (list == null) return;

        if (deviceList != null) {
            deviceList.clear();
        } else {
            deviceList = new ArrayList<>();
        }

        for (Device device : list) {
            if (!device.type.contains("ConsoleAsset")) {
                deviceList.add(device);
            }
        }
    }

//    public LatLng getMarkerLatLng() {
//        float lat = attributes.location.get("value").getAsJsonObject().get("coordinates").getAsJsonArray().get(1).getAsFloat();
//        float lng = attributes.location.get("value").getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsFloat();
//        return new LatLng(lat, lng);
//    }

//    public HashMap<String, String> getNotNullAttrs() {
//        HashMap<String, String> attrs = new HashMap();
//        for (JsonObject obj : attributes.getAllObjects()) {
//            try {
//                JsonElement element = obj.get("value");
//                if (element != JsonNull.INSTANCE) {
//                    String key = obj.get("name").getAsString();
//                    String value = element.getAsString();
//                    attrs.put(key, value);
//                }
//            } catch (UnsupportedOperationException ignored) { }
//        }
//
//        return attrs;
//    }

    public static Device getDeviceById(String id) {
        for (Device device : deviceList) {
            if (Objects.equals(device.id, id)) {
                return device;
            }
        }
        return null;
    }

    public List<JsonObject> getDeviceAttribute() {
        List<JsonObject> attributeList = new ArrayList<>();

        for (String key : attributes.keySet()) {
            JsonObject attribute = attributes.get(key).getAsJsonObject();
            Log.d("API LOG", attribute.get("name").getAsString());
            attributeList.add(attribute);
        }

        return attributeList;
    }

//    public JsonObject getWeatherData() {
//        return attributes.weatherData.get("value").getAsJsonObject();
//    }

    public static String getUnit(String name) {
        String rtnUnit;
        switch (name) {
            case "temperature":
                rtnUnit = "°C";
                break;
            case "humidity":
            case "percent":
                rtnUnit = "%";
                break;
            case "sunset":
                rtnUnit = "PM";
                break;
            case "sunrise":
                rtnUnit = "AM";
                break;
            case "pressure":
                rtnUnit = "hPa";
                break;
            case "wind":
            case "windSpeed":
                rtnUnit = "km/h";
                break;
            case "visibility":
                rtnUnit = "km";
                break;
            default:
                rtnUnit = "";
                break;
        }
        return rtnUnit;
    }

    public static int getIcon(String name) {
        int res;

        switch (name) {
//            case "temperature":
//                res = R.drawable.ic_heat;
//                break;
//            case "humidity":
//                res = R.drawable.ic_humidity;
//                break;
//            case "sunset":
//                res = R.drawable.ic_sunset;
//                break;
//            case "sunrise":
//                res = R.drawable.ic_sunrise;
//                break;
//            case "pressure":
//                res = R.drawable.ic_pressure;
//                break;
//            case "wind":
//            case "windSpeed":
//            case "windDirection":
//                res = R.drawable.ic_wind;
//                break;
//            case "visibility":
//                res = R.drawable.ic_visibility;
//                break;
            default:
//                res = R.drawable.ic_weather;
                res = R.drawable.ic_lock;
                break;
        }
        return res;
    }

    public static String NumToWindDirection(int num) {
        if (num >= 350 && num <= 10) {
            // North
            return "North";
        }
        else if (num >= 80 && num <= 100) {
            // East
            return "East";
        }
        else if (num >= 170 && num <= 190) {
            // South
            return "South";
        }
        else if (num >= 260 && num <= 280) {
            // West
            return "West";
        }
        else if (num > 10 && num < 80) {
            // North East
            return "North East";
        }
        else if (num > 100 && num < 170) {
            // South East
            return "South East";
        }
        else if (num > 190 && num < 260) {
            // South West
            return "South West";
        }
        else {
            // North West
            return "North West";
        }
    }
}
