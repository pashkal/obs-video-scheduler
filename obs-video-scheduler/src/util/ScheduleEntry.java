package util;

import javax.json.Json;
import javax.json.JsonValue;

public class ScheduleEntry {
    public long id;
    public long start;
    public String itemName;

    public ScheduleEntry(long id, long start, String itemName) {
        super();
        this.id = id;
        this.start = start;
        this.itemName = itemName;
    }

    @Override
    public String toString() {
        return "ScheduleEntry [id=" + id + ", start=" + start + ", itemName=" + itemName + "]";
    }
    
    public JsonValue toJsonValue() {
        return Json.createObjectBuilder().add("id",this.id).add("start_timestamp", this.start).add("name", itemName).build();
    }
}
