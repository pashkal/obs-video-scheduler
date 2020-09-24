package util;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.json.JsonObject;
import javax.json.JsonValue;

public abstract class ScheduleEntry {
    public String uuid;
    public long start;
    public String itemName;

    protected ScheduleEntry(String uuid, long start, String itemName) {
        this.uuid = uuid;
        this.start = start;
        this.itemName = itemName;
    }
    
    public ScheduleEntry(long start, String itemName) {
        this.uuid = UUID.randomUUID().toString();
        this.start = start;
        this.itemName = itemName;
    }
    
    public static ScheduleEntry fromJsonObject(JsonObject o) {
        if (o.getString("type").equals("simple")) {
            return new SimpleScheduleEntry(o.getString("uuid"),
                o.getJsonNumber("start_timestamp").longValue(), o.getString("name"));
        } else {
            return new ShuffledDirectoryScheduleEntry(o.getString("uuid"), o.getJsonNumber("start_timestamp").longValue(), o.getJsonNumber("stop_timestamp").longValue(), o.getString("name"));
        }
    }

    @Override
    public abstract String toString();
    
    public abstract void reschedule(long newStart);
    public abstract long getStopTime(Map<String, Item> allItems) throws IOException;

    public abstract JsonValue toJsonValue();
    public abstract JsonValue toClientJsonValue(Map<String, Item> items) throws IOException;
    
    public abstract void process(Map<String, Item> allItems, long time) throws IOException, InterruptedException;
}
