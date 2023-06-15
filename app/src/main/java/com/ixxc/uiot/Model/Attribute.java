package com.ixxc.uiot.Model;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ixxc.uiot.GlobalVars;
import com.ixxc.uiot.Utils;

import java.util.ArrayList;

public class Attribute {
    private final String name;
    private final String type;
    private JsonObject meta;
    private JsonElement value = JsonParser.parseString("");
    private final ArrayList<String> units = new ArrayList<>();
    private final long timestamp;
    private final boolean optional;
    private boolean isExpanded = false;

    public String getUnits() { return String.join(" ", units); }
    public String getName() { return name; }
    public String getType() { return type; }
    public void setMeta(JsonObject meta) { this.meta = meta; }
    public JsonObject getMeta() { return meta; }
    public void setValue(JsonElement value) { this.value = value; }
    public JsonElement getValue() { return value; }
    public boolean isOptional() { return optional; }
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { this.isExpanded = expanded; }

    public Attribute(String name, String type) {
        this.name = name;
        this.type = type;
        this.optional = false;
        this.timestamp = System.currentTimeMillis();
    }

    public static String formatJsonValue(String text){
        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    if (i != 0) {
                        json.append("\n");
                    }
                    json.append(indentString).append(letter).append("\n");
                    indentString = indentString + "\t\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t\t", "");
                    json.append("\n").append(indentString).append(letter);
                    break;
                case ',':
                    json.append(letter).append("\n").append(indentString);
                    break;
                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }

    public String getMetaValue(String name) {
        if (meta == null) return "";

        JsonElement element = this.meta.get(name);

        if (element == null || element.isJsonNull()) return "";
        else if (element.isJsonObject()) return Attribute.formatJsonValue(String.valueOf(element.getAsJsonObject()));
        else if (element.isJsonArray()) return Attribute.formatJsonValue(String.valueOf(element.getAsJsonArray()));
        else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) return String.valueOf(element.getAsInt());
        else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) return String.valueOf(element.getAsBoolean());
        else return element.getAsString();
    }

    public String getValueString() {
        if (value == null || value.isJsonNull()) return "";
        else if (value.isJsonObject()) return Attribute.formatJsonValue(String.valueOf(value.getAsJsonObject()));
        else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) return String.valueOf(value.getAsInt());
        else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) return String.valueOf(value.getAsInt());
        else return value.getAsString();
    }

    public String getUnit(String unitString) {
        switch (unitString) {
            case "kilo watt":
                return "kW";
            case "kilo metre":
                return "km";
            case "milli metre":
                return "mm";
            case "EUR per kilo watt hour":
                return "€/kWh";
            case "micro gram per metre cubed":
                return "µg/m³";
            case "kilo metre per hour":
                return "km/h";
            case "decibel":
                return "(dB)";
            case "knot":
                return "kn";
            case "kilo watt hour":
                return "kWh";
            case "metre cubed per hour":
                return "m³/h";
            case "EUR":
                return "€";
            case "celsius":
                return "°C";
            case "kilo gram per kilo watt hour":
                return "kg/kWh";
            case "percentage":
                return "%";
            case "metre per second":
                return "m/s";
            case "kelvin":
                return "K";
            case "metre squared":
                return "m²";
            case "metre":
                return "m";
            case "kilo gram":
                return "kg";
            case "degree":
                return "°";
            default:
                return unitString;
        }
    }

    public boolean canShowValue(String type) {
        switch (type) {
            case "timestamp":
            case "timestampISO8601":
            case "dateAndTime":
            case "timeDurationISO8601":
            case "periodDurationISO8601":
            case "timeAndPeriodDurationISO8601":
            case "integer":
            case "long":
            case "bigInteger":
            case "number":
            case "bigNumber":
            case "TCP_IPPortNumber":
            case "positiveInteger":
            case "positiveNumber":
            case "negativeInteger":
            case "negativeNumber":
            case "integerByte":
            case "byte":
            case "boolean":
                return true;
        }

        return false;
    }

    public boolean isInWidgets(Context ctx, String deviceId) {
        String widgetString = Utils.getPreferences(ctx, GlobalVars.WIDGET_KEY);
        JsonArray widgets = TextUtils.isEmpty(widgetString) ? new JsonArray() : JsonParser.parseString(widgetString).getAsJsonArray();
        return widgets.contains(JsonParser.parseString(String.join("-", deviceId, name)));
    }

    public JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("type", type);
        o.addProperty("name", name);
        o.addProperty("timestamp", timestamp);

        if (meta != null)  o.add("meta", meta);
        if (value != null && !value.isJsonNull()) o.add("value", value);

        return o;
    }
}
