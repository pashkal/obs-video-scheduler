package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

public class DataProvider {
	
	static String SCHEDULE_FILE = "../../schedule.json";

	public static long getContestStart() throws IOException {
		Scanner s = new Scanner(new File(Config.getContestTimestampFile()));
		long res = s.nextLong();
		s.close();
		return res;
	}

	public static ArrayList<Item> getList(boolean videos) throws IOException {
//		System.err.println("getting " + videos);
		ArrayList<Item> res = new ArrayList<>();
		Scanner s;
		if (videos)
			s = new Scanner(new File(Config.getVideoList()));
		else
			s = new Scanner(new File(Config.getActivityList()));
		while (s.hasNext()) {
			String name = s.nextLine();
			long duration = Long.parseLong(s.nextLine());
			res.add(new Item(name, duration, videos));
		}
		s.close();
//		System.err.println(res.size());
		return res;
	}

	public static Map<String, Item> getMapByName() throws IOException {
		ArrayList<Item> videoList = getList(true);
		ArrayList<Item> activityList = getList(false);
		HashMap<String, Item> res = new HashMap<>();
		for (Item v : videoList) {
			res.put(v.name, v);
		}
		for (Item v : activityList) {
			res.put(v.name, v);
		}
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

		PrintWriter pw = new PrintWriter(Config.getContestTimestampFile());
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
		File f = new File(Config.getScheduleSaveDir() + fileName + "." + cnt);
		PrintWriter pw = new PrintWriter(f);
		long contestStart = getContestStart();
		pw.println(contestStart);

		String schedule = new Scanner(new File(SCHEDULE_FILE)).nextLine();
		pw.println(schedule);
		pw.close();

	}

	private static int getScheduleVersions(String fileName) throws FileNotFoundException, IOException {
		File dir = new File(Config.getScheduleSaveDir());
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
		File dir = new File(Config.getScheduleSaveDir());
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
		Scanner s = new Scanner(new File(Config.getScheduleSaveDir() + fileName + "." + (cnt - 1)));
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
		PrintWriter pw = new PrintWriter(SCHEDULE_FILE);
		pw.println(newSchedule);
		pw.close();
		
	}

	public static void writeList(List<Item> list, boolean video) throws FileNotFoundException, IOException {
		String filename;
		if (video) {
			filename = Config.getVideoList();
		} else
			filename = Config.getActivityList();
		PrintWriter pw = new PrintWriter(filename);
		for (Item i : list) {
			pw.println(i.name);
			pw.println(i.duration);
		}
		pw.close();
	}
}
