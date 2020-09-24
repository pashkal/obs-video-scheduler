package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class ShuffledDirectoryScheduleEntry extends ScheduleEntry {

    static class State {
        String uuid;
        long nextSwitch;
        Map<String, Integer> played;

        private State(String uuid, long nextSwitch, Map<String, Integer> played) {
            super();
            this.uuid = uuid;
            this.nextSwitch = nextSwitch;
            this.played = played;
        }

        public State(String uuid, long start) {
            super();
            this.uuid = uuid;
            this.nextSwitch = start;
            this.played = new HashMap<>();
        }

        public static State load(String uuid) throws FileNotFoundException {
            File stateFile = new File("../../data/states/" + uuid);
            JsonReader r = Json.createReader(new FileInputStream(stateFile));
            JsonObject o = r.readObject();

            Map<String, Integer> played = new HashMap<>();
            for (String title : o.getJsonObject("played").keySet()) {
                played.put(title, o.getJsonObject("played").getInt(title));
            }

            return new State(uuid, o.getJsonNumber("nextSwitch").longValue(), played);
        }

        public void writeState() throws IOException {
            JsonObjectBuilder ob = Json.createObjectBuilder();
            ob.add("nextSwitch", nextSwitch);

            JsonObjectBuilder playedOb = Json.createObjectBuilder();
            for (String title : played.keySet()) {
                playedOb.add(title, played.get(title));
            }

            ob.add("played", playedOb.build());

            DataProvider.prettyPrintJsonObject("../../data/states/" + uuid, ob.build());
        }

        public int getNumberPlayed() {
            int result = 0;
            for (Integer count : this.played.values()) {
                result += count;
            }
            return result;
        }

        public String selectNext(Map<String, Item> allItems, String itemName, long stop) {
            Set<String> filtered = new HashSet<>();

            allItems.values().forEach((i) -> {
                if (i.isVideo && i.name.startsWith(itemName) && i.duration + nextSwitch <= stop)
                    filtered.add(i.name);
            });

            int min = Integer.MAX_VALUE;
            for (String title : filtered) {
                if (this.played.getOrDefault(title, 0) < min) {
                    min = this.played.getOrDefault(title, 0);
                }
            }

            ArrayList<String> candidates = new ArrayList<>();
            for (String title : filtered) {
                if (this.played.getOrDefault(title, 0) == min) {
                    candidates.add(title);
                }
            }
            ;

            if (candidates.isEmpty()) {
                return null;
            }

            String result = candidates.get(new Random().nextInt(candidates.size()));

            nextSwitch = nextSwitch + allItems.get(result).duration;
            played.put(result, played.getOrDefault(result, 0) + 1);
            return result;

        }
    }

    private long stop;

    protected ShuffledDirectoryScheduleEntry(String uuid, long start, long stop, String itemName) {
        super(uuid, start, itemName);
        this.stop = stop;
    }

    public ShuffledDirectoryScheduleEntry(long start, long stop, String itemName) {
        super(start, itemName);
        this.stop = stop;
    }

    @Override
    public String toString() {
        return "ScheduleEntry [id=" + uuid + ", start=" + start + ", itemName=" + itemName + "]";
    }

    public JsonValue toJsonValue() {
        return Json.createObjectBuilder().add("type", "shuffled").add("uuid", this.uuid)
                .add("start_timestamp", this.start).add("name", itemName).add("stop_timestamp", stop).build();
    }

    public JsonValue toClientJsonValue(Map<String, Item> allItems) throws IOException {
        return Json.createObjectBuilder().add("_id", this.uuid).add("start", this.start).add("stop", this.stop)
                .add("name", this.itemName).build();
    }

    public void process(Map<String, Item> allItems, long time) throws IOException, InterruptedException {
        if (time < this.start - 2000) {
            return;
        }
        if (time > this.start - 2000 && time < this.start) {
            State state = new State(this.uuid, this.start);
            state.writeState();
        }

        State state = State.load(this.uuid);
        if (time > state.nextSwitch - 2000 && time < state.nextSwitch) {
            Thread.sleep(state.nextSwitch - time);
            int number = state.getNumberPlayed();
            String newVideo = state.selectNext(allItems, this.itemName, this.stop);

            String oldSourceName = this.itemName + ": " + number;
            if (number == 0) {
                oldSourceName = null;
            }

            String newSourceName = this.itemName + ": " + (number + 1);
            if (newVideo == null) {
                newSourceName = null;
            }

            new OBSApi().switchVideo(Config.getServerVideoDir() + "/" + newVideo, newSourceName, oldSourceName);

            state.writeState();
            return;
        }
    }

    @Override
    public void reschedule(long newStart) {
        this.stop = this.stop + newStart - this.start;
        this.start = newStart;
    }

    @Override
    public long getStopTime(Map<String, Item> allItems) {
        return this.stop;
    }
}
