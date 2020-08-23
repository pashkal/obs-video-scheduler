package util;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonValue;

public class SimpleScheduleEntry extends ScheduleEntry {
    public SimpleScheduleEntry(String uuid, long start, String itemName) {
        super(uuid, start, itemName);
    }

    @Override
    public String toString() {
        return "ScheduleEntry [id=" + uuid + ", start=" + start + ", itemName=" + itemName + "]";
    }

    public JsonValue toJsonValue() {
        return Json.createObjectBuilder().add("uuid", this.uuid).add("start_timestamp", this.start).add("name", itemName)
                .build();
    }
    
    public void process(Map<String, Item> allItems, long time) {
        try {
            if (!allItems.get(this.itemName).isVideo)
                return;
            long start = this.start;
            long stop = this.start + allItems.get(this.itemName).duration + Disclaimer.getDuration();
            
            if (time > start - 2000 && time < start) {
                Thread.sleep(start - time);
                System.err.println("Launching " + this.itemName);

                new OBSApi().launchVideo(this.itemName);

                Thread.sleep(1500);
            }
            
            if (time > stop - 2000 && time < stop) {
                Thread.sleep(stop - time);
                System.err.println("Stopping " + this.itemName);
                new OBSApi().removeScheduledVideo();
                Thread.sleep(1500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.toString());
        }
    }
}
