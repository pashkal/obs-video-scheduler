package util;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class Item implements Comparable<Item> {
    public String name;
    public long duration;
    public boolean isVideo;
    public String uuid;

    public Item(String uuid, String name, long duration, boolean isVideo) {
        super();
        this.name = name;
        this.duration = duration;
        this.isVideo = isVideo;
        this.uuid = uuid;
    }

    public Item(String name, long duration, boolean isVideo) {
        super();
        this.name = name;
        this.duration = duration;
        this.isVideo = isVideo;
        this.uuid = UUID.nameUUIDFromBytes(name.getBytes()).toString();
    }

    public static Item fromJsonObject(JsonObject o) {
        return new Item(o.getString("uuid"), o.getString("name"), o.getInt("duration"), o.getBoolean("isVideo"));
    }

    @Override
    public int compareTo(Item o) {
        return this.name.compareTo(o.name);
    }

    public JsonObject toJsonObject() {
        return Json.createObjectBuilder().add("uuid", uuid).add("name", name).add("duration", duration)
                .add("isVideo", isVideo).build();
    }
}
