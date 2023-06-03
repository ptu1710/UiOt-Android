package com.ixxc.uiot.Model;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class MetaItem {
    private final String name;
    private final String type;
    private static List<MetaItem> metaItemList = new ArrayList<>();

    public MetaItem(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public static List<MetaItem> getMetaItemList() { return metaItemList; }

    public static void setMetaItemList(List<MetaItem> list) {
        metaItemList = list;
    }

    // find the metaItem by name
    public static MetaItem findMetaItemByName(String name) {
        return metaItemList.stream().filter(metaItem -> metaItem.getName().equals(name)).findFirst().orElse(null);
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.add(name,null);
        return object;
    }
}
