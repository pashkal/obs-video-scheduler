package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class Config {

	private static String CONFIG_FILE = "../../data/config.json";

	public static String getVideoList() throws FileNotFoundException, IOException {
		return getConfigValue("video-list-file");
	}

	public static String getContestTimestampFile() throws FileNotFoundException, IOException {
		return getConfigValue("contest-start-file");
	}

	public static String getScheduleSaveDir() throws FileNotFoundException, IOException {
		return getConfigValue("schedule-save-dir");
	}

	public static String getVideoDir() throws FileNotFoundException, IOException {
		return getConfigValue("video-sync-dir");
	}

	public static String getVideoServerDir() throws FileNotFoundException, IOException {
		return getConfigValue("video-server-dir");
	}

	public static String getActivityList() throws FileNotFoundException, IOException {
		return getConfigValue("activity-list-file");
	}

	private static Map<String, String> readData() throws FileNotFoundException {
		JsonReader jr = Json.createReader(new FileReader(new File(CONFIG_FILE)));
		JsonObject jo = jr.readObject();
		HashMap<String, String> data = new HashMap<>();
		for (String key : jo.keySet()) {
			data.put(key, jo.getString(key));
		}
		return data;
	}

	private static String getConfigValue(String name) throws FileNotFoundException, IOException {
		return readData().get(name);
	}

}
