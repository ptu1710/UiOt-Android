package com.ixxc.uiot.Model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;

public class Attribute implements Serializable {
    private final String name;
    private final String type;
    private JsonObject meta;
    private JsonElement value = JsonParser.parseString("");
    private final long timestamp;
    private final boolean optional;
    private boolean isExpanded = false;

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
