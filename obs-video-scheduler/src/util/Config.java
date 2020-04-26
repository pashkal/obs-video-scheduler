package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class Config {

	private static String CONFIG_FILE = "../../data/config.json";

	public static String getServerVideoDir() throws FileNotFoundException, IOException {
		return getConfigValue("server-video-dir");
	}

	public static String getOBSVideoDir() throws FileNotFoundException, IOException {
		return getConfigValue("obs-video-dir");
	}

	public static String getOBSHost() throws FileNotFoundException, IOException {
		return getConfigValue("obs-host");
	}

	public static int getSourceLayer() throws FileNotFoundException, IOException {
		return Integer.valueOf(getConfigValue("source-layer"));
	}

	public static String getSceneName() throws FileNotFoundException, IOException {
		return getConfigValue("scene-name");
	}

	public static String getSourceName() throws FileNotFoundException, IOException {
		return getConfigValue("source-name");
	}

	public static void writeData(Map<String, String> data) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(CONFIG_FILE));
		
		List<String> sortedKeys = new ArrayList<String>(data.keySet());
		Collections.sort(sortedKeys);
		
		pw.println("{\n");
		for (int i = 0; i < sortedKeys.size(); i++) {
			pw.printf("\t\"%s\":\t\"%s\"", sortedKeys.get(i), data.get(sortedKeys.get(i)));
			if (i < sortedKeys.size() - 1) {
				pw.println(",");
			} else {
				pw.println();
			}
		}
		pw.println("}");
		pw.close();
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
