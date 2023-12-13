package com.ixxc.uiot.Model;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class MetaItem {
    public String name;
    public JsonObject type;

    private static List<MetaItem> metaItemList;

    public static List<MetaItem> getMetaItemList() { return metaItemList; }

    public static void setMetaItemList(JsonObject list) {
        metaItemList = list.entrySet().stream()
                .map(item -> {
                    MetaItem metaItem = new MetaItem();
                    metaItem.name = item.getKey();
                    metaItem.type = item.getValue().getAsJsonObject();
                    return metaItem;
                })
                .collect(Collectors.toList());
    }

    public static String getMetaType(String name) {
        return metaItemList.stream().filter(metaItem -> metaItem.name.equals(name)).collect(Collectors.toList()).get(0).type.get("type").getAsString();
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.add(name,null);
        return object;
    }
}
