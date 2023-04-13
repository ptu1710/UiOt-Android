package com.ixxc.uiot.Model;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class MetaItem {
    public String name;
    public String type;

    private static List<MetaItem> metaItemList;

    public static List<MetaItem> getMetaItemList() { return metaItemList; }

    public static void setMetaItemList(List<MetaItem> list) {
        metaItemList = list;
    }

    public static String getMetaType(String name) {
        return metaItemList.stream().filter(metaItem -> metaItem.name.equals(name)).collect(Collectors.toList()).get(0).type;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.add(name,null);
        return object;
    }
}
