package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class ScheduleEntry implements Comparable<ScheduleEntry> {
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
        return new ScheduleEntry(o.getString("uuid"),
                o.getJsonNumber("start_timestamp").longValue(), o.getString("name"));
    }
    
    @Override
    public int compareTo(ScheduleEntry o) {
        return Long.compare(start, o.start);
    }

    @Override
    public String toString() {
        return "ScheduleEntry [id=" + uuid + ", start=" + start + ", itemName=" + itemName + "]";
    }

    public JsonValue toJsonValue() {
        return Json.createObjectBuilder().add("uuid", this.uuid).add("start_timestamp", this.start).add("name", itemName)
                .build();
    }

    public long getStopTime(Map<String, Item> allItems) throws FileNotFoundException, IOException {
        return this.start + allItems.get(this.itemName).duration + Disclaimer.getDuration() * 2 - Disclaimer.getTransitionTime() * 2;
    }

    public String getSourceName() {
        return "Scheduler: " + itemName + " [" + uuid + "]";
    }
    
    public String getDisclaimerSourceName() {
        return "Scheduler: disclaimer [" + uuid + "]";
    }
}
