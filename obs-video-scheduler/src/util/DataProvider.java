package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;

public class DataProvider {

    private static String VIDEO_LIST_FILE = "../../data/filelist.txt";
    private static String ACTIVITY_LIST_FILE = "../../data/alist.txt";
    private static String SCHEDULE_FILE = "../../data/schedule.json";
    private static String EVENT_START_TIMESTAMP_FILE = "../../data/timestamp";
    private static String SCHEDULE_SAVE_DIR = "../../data/schedules/";

    public static long getContestStart() throws IOException {
        Scanner s = new Scanner(new File(EVENT_START_TIMESTAMP_FILE));
        long res = s.nextLong();
        s.close();
        return res;
    }

    public static Map<String, Item> getVideosByName() throws IOException {
        return DataProvider.getItemsByName(VIDEO_LIST_FILE);
    }

    public static Map<String, Item> getVideosByUUID() throws IOException {
        return DataProvider.getItemsByUUID(VIDEO_LIST_FILE);
    }

    public static Map<String, Item> getActivitiesByName() throws IOException {
        return DataProvider.getItemsByName(ACTIVITY_LIST_FILE);
    }

    public static Map<String, Item> getActivitiesByUUID() throws IOException {
        return DataProvider.getItemsByUUID(ACTIVITY_LIST_FILE);
    }

    public static void writeVideos(Collection<Item> items) throws IOException {
        writeItems(VIDEO_LIST_FILE, items);
    }

    public static void writeActivities(Collection<Item> items) throws IOException {
        writeItems(ACTIVITY_LIST_FILE, items);
    }

    public static Map<String, Item> getAllItemsByName() throws IOException {
        Map<String, Item> result = DataProvider.getVideosByName();
        result.putAll(DataProvider.getActivitiesByName());
        return result;
    }

    public static Map<String, Item> getAllItemsByUUID() throws IOException {
        Map<String, Item> result = DataProvider.getVideosByUUID();
        result.putAll(DataProvider.getActivitiesByUUID());
        return result;
    }

    public static List<ScheduleEntry> getSchedule() throws FileNotFoundException, IOException {
        JsonReader jr = Json.createReader(new FileInputStream(SCHEDULE_FILE));
        JsonArray ja = jr.readArray();
        ArrayList<ScheduleEntry> schedule = new ArrayList<>();
        for (int i = 0; i < ja.size(); i++) {
            JsonObject o = ja.getJsonObject(i);
            schedule.add(ScheduleEntry.fromJsonObject(o));
        }
        Collections.sort(schedule);
        return schedule;
    }

    public static void startContest(long time) throws IOException {
        time -= new Date().getTimezoneOffset() * 60 * 1000;
        long diff = time - getContestStart();

        PrintWriter pw = new PrintWriter(EVENT_START_TIMESTAMP_FILE);
        pw.println(time);
        pw.close();

        List<ScheduleEntry> sc = getSchedule();
        for (ScheduleEntry sce : sc) {
            sce.start += diff;
        }

        updateSchedule(sc);
    }

    public static void startContest() throws IOException {
        startContest(new Date().getTime());
    }

    public static long getCurrentTime() {
        return new Date().getTime() - new Date().getTimezoneOffset() * 60 * 1000;
    }

    public static void saveSchedule(String fileName) throws FileNotFoundException, IOException {
        int cnt = getScheduleVersions(fileName);

        FileReader fr = new FileReader(new File(SCHEDULE_FILE));
        JsonReader jr = Json.createReader(fr);
        JsonArray schedule = jr.readArray();
        fr.close();

        JsonObject savedSchedule = Json.createObjectBuilder().add("start_timestamp", getContestStart())
                .add("schedule", schedule).build();

        fileName = SCHEDULE_SAVE_DIR + fileName + "." + cnt;
        prettyPrintJsonObject(fileName, savedSchedule);

    }

    private static int getScheduleVersions(String fileName) throws FileNotFoundException, IOException {
        File dir = new File(SCHEDULE_SAVE_DIR);
        File[] all = dir.listFiles();
        int cnt = 0;
        for (File f : all) {
            if (f.getName().startsWith(fileName))
                cnt++;
        }
        return cnt;
    }

    public static List<String> getScheduleList() throws FileNotFoundException, IOException {
        HashSet<String> scheduleList = new HashSet<String>();
        File dir = new File(SCHEDULE_SAVE_DIR);
        File[] files = dir.listFiles();
        for (File f : files) {
            String name = f.getName();
            int pos = name.lastIndexOf(".");
            String cName = name.substring(0, pos);
            scheduleList.add(cName);
        }

        return new ArrayList<String>(scheduleList);
    }

    public static void loadSchedule(String fileName) throws IOException {
        int cnt = getScheduleVersions(fileName);

        FileReader fr = new FileReader(new File(SCHEDULE_SAVE_DIR + fileName + "." + (cnt - 1)));
        JsonObject savedSchedule = Json.createReader(fr).readObject();
        fr.close();

        long timestamp = savedSchedule.getInt("start_timestamp");
        JsonArray schedule = savedSchedule.getJsonArray("schedule");

        Date contestStart = new Date();
        Date loadedContestStart = new Date(timestamp);
        adjustDate(contestStart, loadedContestStart);
        System.out.println(contestStart + " " + loadedContestStart);
        startContest(contestStart.getTime() + new Date().getTimezoneOffset() * 60 * 1000);

        JsonArrayBuilder jab = Json.createArrayBuilder();
        System.out.println(schedule);
        for (int i = 0; i < schedule.size(); i++) {
            JsonObject jo = schedule.getJsonObject(i);
            System.out.println(i);
            try {
                long oldTimestamp = jo.getJsonNumber("start_timestamp").longValueExact();
                Date oldDate = new Date(oldTimestamp);
                Date newDate = new Date();
                adjustDate(newDate, oldDate);
                ScheduleEntry se = ScheduleEntry.fromJsonObject(jo);
                se.start = newDate.getTime();
                jab.add(se.toJsonValue());
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }

        String newSchedule = jab.build().toString();
        System.out.println(newSchedule);
        updateSchedule(newSchedule);
    }

    private static void adjustDate(Date contestStart, Date loadedContestStart) {
        contestStart.setHours(loadedContestStart.getHours());
        contestStart.setMinutes(loadedContestStart.getMinutes());
        contestStart.setSeconds(loadedContestStart.getSeconds());
        contestStart
                .setTime(contestStart.getTime() - contestStart.getTime() % 1000 + loadedContestStart.getTime() % 1000);
    }

    public static void updateSchedule(Collection<ScheduleEntry> newSchedule) throws IOException {
        JsonArrayBuilder ab = Json.createArrayBuilder();

        for (ScheduleEntry se : newSchedule) {
            ab.add(se.toJsonValue());
        }

        prettyPrintJsonArray(SCHEDULE_FILE, ab.build());
    }

    public static void writeScheduleToClient(HttpServletResponse r)
            throws IOException {
        r.setContentType("application/json");

        Map<String, Item> videoMap = DataProvider.getAllItemsByName();

        JsonObjectBuilder result = Json.createObjectBuilder().add("contest_timestamp", DataProvider.getContestStart());

        JsonArrayBuilder scheduleBuilder = Json.createArrayBuilder();
        
        List<ScheduleEntry> schedule = DataProvider.getSchedule();

        for (ScheduleEntry e : schedule) {
            long stop = e.getStopTime(videoMap);
            scheduleBuilder.add(Json.createObjectBuilder().add("_id", e.uuid).add("start", e.start).add("stop", stop)
                    .add("name", e.itemName).build());
        }

        result.add("schedule", scheduleBuilder.build());
        r.getWriter().print(result.build().toString());
    }

    private static void updateSchedule(String newSchedule) throws IOException {
        newSchedule = URLDecoder.decode(newSchedule, StandardCharsets.UTF_8.name());

        JsonReader jr = Json.createReader(new StringReader(newSchedule));
        JsonArray a = jr.readArray();

        prettyPrintJsonArray(SCHEDULE_FILE, a);
    }

    private static void prettyPrintJsonArray(String fileName, JsonArray a) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(map);
        FileWriter w = new FileWriter(fileName);
        JsonWriter jsonWriter = writerFactory.createWriter(w);
        jsonWriter.writeArray(a);
        jsonWriter.close();
        w.close();
    }

    private static void prettyPrintJsonObject(String fileName, JsonObject o) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(map);
        FileWriter w = new FileWriter(fileName);
        JsonWriter jsonWriter = writerFactory.createWriter(w);
        jsonWriter.writeObject(o);
        jsonWriter.close();
        w.close();
    }

    private static JsonArray readJsonArray(String fileName) throws IOException {
        FileReader fr = new FileReader(new File(fileName));
        JsonArray array = Json.createReader(fr).readArray();
        fr.close();
        return array;
    }

    private static Map<String, Item> getItemsByUUID(String fileName) throws IOException {
        JsonArray a = readJsonArray(fileName);
        HashMap<String, Item> result = new HashMap<>();
        for (Object o : a.toArray()) {
            if (o instanceof JsonObject) {
                JsonObject v = (JsonObject) o;
                result.put(v.getString("uuid"), Item.fromJsonObject(v));
            }
        }
        return result;
    }

    private static Map<String, Item> getItemsByName(String fileName) throws IOException {
        JsonArray a = readJsonArray(fileName);

        HashMap<String, Item> result = new HashMap<>();
        for (Object o : a.toArray()) {
            if (o instanceof JsonObject) {
                JsonObject v = (JsonObject) o;
                result.put(v.getString("name"), Item.fromJsonObject(v));
            }
        }
        return result;
    }

    private static void writeItems(String fileName, Collection<Item> list) throws FileNotFoundException, IOException {
        JsonArrayBuilder b = Json.createArrayBuilder();
        for (Item i : list) {
            b.add(i.toJsonObject());
        }

        prettyPrintJsonArray(fileName, b.build());
    }
}
