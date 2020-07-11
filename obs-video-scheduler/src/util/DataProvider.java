package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
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

public class DataProvider {
	
	public static String VIDEO_LIST_FILE = "../../data/filelist.txt";
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

	public static Map<String, Item> getVideos() throws IOException {
		return DataProvider.getList(true);
	}
	
	public static Map<String, Item> getActivities() throws IOException {
		return DataProvider.getList(false);
	}
	
	public static Map<String, Item> getAllItems() throws IOException {
		Map<String, Item> result = DataProvider.getVideos();
		result.putAll(DataProvider.getActivities());
		return result;
	}
	
	
	private static Map<String, Item> getList(boolean videos) throws IOException {
		TreeMap<String, Item> res = new TreeMap<>();
		Scanner s;
		if (videos)
			s = new Scanner(new File(VIDEO_LIST_FILE));
		else
			s = new Scanner(new File(ACTIVITY_LIST_FILE));
		while (s.hasNext()) {
			String name = s.nextLine();
			long duration = Long.parseLong(s.nextLine());
			res.put(name, new Item(name, duration, videos));
		}
		s.close();
		return res;
	}

	public static List<ScheduleEntry> getSchedule() throws FileNotFoundException, IOException {
		JsonReader jr = Json.createReader(new FileInputStream(SCHEDULE_FILE));
		JsonArray ja = jr.readArray();
		ArrayList<ScheduleEntry> schedule = new ArrayList<>();
		for (int i = 0; i < ja.size(); i++) {
			JsonObject o = ja.getJsonObject(i);
			ScheduleEntry e = new ScheduleEntry(o.getJsonNumber("id").longValue(),
					o.getJsonNumber("start_timestamp").longValue(), o.getString("name"));
			schedule.add(e);
		}
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

		printSchedule(sc);
	}

	private static void printSchedule(List<ScheduleEntry> sc) throws IOException {
		JsonArrayBuilder a = Json.createArrayBuilder();
		for (ScheduleEntry sce : sc) {
			JsonObjectBuilder job = Json.createObjectBuilder();
			job.add("id", sce.id);
			job.add("start_timestamp", sce.start);
			job.add("name", sce.itemName);
			a.add(job);
		}
		String s = a.build().toString();
		PrintWriter pw = new PrintWriter(SCHEDULE_FILE);
		pw.println(s);
		pw.close();
	}

	public static void startContest() throws IOException {
		startContest(new Date().getTime());
	}

	public static long getCurrentTime() {
		return new Date().getTime() - new Date().getTimezoneOffset() * 60 * 1000;
	}

	public static void saveSchedule(String fileName) throws FileNotFoundException, IOException {
		int cnt = getScheduleVersions(fileName);
		File f = new File(SCHEDULE_SAVE_DIR + fileName + "." + cnt);
		PrintWriter pw = new PrintWriter(f);
		long contestStart = getContestStart();
		pw.println(contestStart);

		String schedule = new Scanner(new File(SCHEDULE_FILE)).nextLine();
		pw.println(schedule);
		pw.close();

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
		Scanner s = new Scanner(new File(SCHEDULE_SAVE_DIR + fileName + "." + (cnt - 1)));
		long timestamp = Long.parseLong(s.nextLine());
		String schedule = s.nextLine();
		s.close();

		Date contestStart = new Date();
		Date loadedContestStart = new Date(timestamp);
		adjustDate(contestStart, loadedContestStart);
		System.out.println(contestStart + " " + loadedContestStart);
		startContest(contestStart.getTime() + new Date().getTimezoneOffset() * 60 * 1000);

		JsonReader jr = Json.createReader(new StringReader(schedule));
		JsonArray ja = jr.readArray();

		JsonArrayBuilder jab = Json.createArrayBuilder();
		System.out.println(schedule);
		for (int i = 0; i < ja.size(); i++) {
			JsonObject jo = ja.getJsonObject(i);
			System.out.println(i);
			try {
				long oldTimestamp = jo.getJsonNumber("start_timestamp").longValueExact();
				Date oldDate = new Date(oldTimestamp);
				Date newDate = new Date();
				adjustDate(newDate, oldDate);
				jab.add(Json.createObjectBuilder().add("id", jo.getJsonNumber("id")).add("name", jo.get("name"))
						.add("start_timestamp", newDate.getTime()).build());
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

	public static void updateSchedule(String newSchedule) throws IOException {
		JsonReader jr = Json.createReader(new StringReader(newSchedule));
		JsonArray a = jr.readArray();
		FileWriter w = new FileWriter(SCHEDULE_FILE);

		prettyPrintJsonArray(w, a);

		w.close();
	}

	private static void prettyPrintJsonArray(Writer w, JsonArray a) {
		Map<String, Object> map = new HashMap<>();
        map.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(map);
        JsonWriter jsonWriter = writerFactory.createWriter(w);
        jsonWriter.writeArray(a);
        jsonWriter.close();
	}

	public static void writeList(List<Item> list, boolean video) throws FileNotFoundException, IOException {
		String filename;
		if (video) {
			filename = VIDEO_LIST_FILE;
		} else
			filename = ACTIVITY_LIST_FILE;
		PrintWriter pw = new PrintWriter(filename);
		for (Item i : list) {
			pw.println(i.name);
			pw.println(i.duration);
		}
		pw.close();
	}
}
