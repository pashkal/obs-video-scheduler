package util;

import java.util.Map;

import javax.json.JsonObject;
import javax.json.JsonValue;

public abstract class ScheduleEntry {
    public String uuid;
    public long start;
    public String itemName;

    public ScheduleEntry(String uuid, long start, String itemName) {
        this.uuid = uuid;
        this.start = start;
        this.itemName = itemName;
    }
    
    public static ScheduleEntry fromJsonObject(JsonObject o) {
        return new SimpleScheduleEntry(o.getString("uuid"),
                o.getJsonNumber("start_timestamp").longValue(), o.getString("name"));
    }

    @Override
    public abstract String toString();

    public abstract JsonValue toJsonValue();
    
    public abstract void process(Map<String, Item> allItems, long time);
}
