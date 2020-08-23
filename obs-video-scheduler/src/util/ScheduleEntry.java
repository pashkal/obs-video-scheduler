package util;

import javax.json.Json;
import javax.json.JsonValue;

public class ScheduleEntry {
    public String uuid;
    public long start;
    public String itemName;

    public ScheduleEntry(String uuid, long start, String itemName) {
        super();
        this.uuid = uuid;
        this.start = start;
        this.itemName = itemName;
    }

    @Override
    public String toString() {
        return "ScheduleEntry [id=" + uuid + ", start=" + start + ", itemName=" + itemName + "]";
    }

    public JsonValue toJsonValue() {
        return Json.createObjectBuilder().add("uuid", this.uuid).add("start_timestamp", this.start).add("name", itemName)
                .build();
    }
}
