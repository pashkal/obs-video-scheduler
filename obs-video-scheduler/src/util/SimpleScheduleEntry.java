package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonValue;

public class SimpleScheduleEntry extends ScheduleEntry {
    protected SimpleScheduleEntry(String uuid, long start, String itemName) {
        super(uuid, start, itemName);
    }

    public SimpleScheduleEntry(long start, String itemName) {
        super(start, itemName);
    }

    @Override
    public String toString() {
        return "ScheduleEntry [id=" + uuid + ", start=" + start + ", itemName=" + itemName + "]";
    }

    public JsonValue toJsonValue() {
        return Json.createObjectBuilder().add("type", "simple").add("uuid", this.uuid).add("start_timestamp", this.start).add("name", itemName)
                .build();
    }
    
    public JsonValue toClientJsonValue(Map<String, Item> allItems) throws IOException {
        long stop = (this.start + allItems.get(this.itemName).duration + Disclaimer.getDuration() * 2) - Disclaimer.getTransitionTime() * 2;
        return Json.createObjectBuilder().add("_id", this.uuid).add("start", this.start).add("stop", stop)
                .add("name", this.itemName).build();
    }
    
    public void process(Map<String, Item> allItems, long time) {
        try {
            if (!allItems.get(this.itemName).isVideo)
                return;
            long start = this.start;
            long stop = this.start + allItems.get(this.itemName).duration + Disclaimer.getDuration() - Disclaimer.getTransitionTime() * 2;
            
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

    @Override
    public void reschedule(long newStart) {
        this.start = newStart;
        
    }

    @Override
    public long getStopTime(Map<String, Item> allItems) throws IOException {
        return this.start + allItems.get(this.itemName).duration + Disclaimer.getDuration() * 2 - Disclaimer.getTransitionTime() * 2;
    }
}
