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
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

public class Config {

    private static String CONFIG_FILE = "../../data/config.json";

    public static String getDisclaimerFileName() throws FileNotFoundException, IOException {
        return getStringConfigValue("disclaimer-file-name");
    }

    public static String getServerVideoDir() throws FileNotFoundException, IOException {
        return getStringConfigValue("server-video-dir");
    }

    public static String getOBSVideoDir() throws FileNotFoundException, IOException {
        return getStringConfigValue("obs-video-dir");
    }

    public static String getOBSHost() throws FileNotFoundException, IOException {
        return getStringConfigValue("obs-host");
    }

    public static int getSourceLayer() throws FileNotFoundException, IOException {
        return getIntConfigValue("source-layer");
    }

    public static int getVideoTopMargin() throws FileNotFoundException, IOException {
        return getIntConfigValue("video-top-margin");
    }

    public static int getVideoLeftMargin() throws FileNotFoundException, IOException {
        return getIntConfigValue("video-left-margin");
    }

    public static int getVideoWidth() throws FileNotFoundException, IOException {
        return getIntConfigValue("video-width");
    }

    public static int getVideoHeight() throws FileNotFoundException, IOException {
        return getIntConfigValue("video-height");
    }

    public static String getSceneName() throws FileNotFoundException, IOException {
        return getStringConfigValue("scene-name");
    }

    public static String getSourceName() throws FileNotFoundException, IOException {
        return getStringConfigValue("source-name");
    }

    public static ArrayList<String> getSourcesToMute() throws FileNotFoundException, IOException {
        return getStringListConfigValue("sources-to-mute");
    }

    public static void writeData(Map<String, String> data) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File(CONFIG_FILE));

        List<String> sortedKeys = new ArrayList<String>(data.keySet());
        Collections.sort(sortedKeys);

        pw.println("{\n");
        for (int i = 0; i < sortedKeys.size(); i++) {
            String value = data.get(sortedKeys.get(i));

            // TODO: this really needs some more generic validation / processing for
            // different fields
            // and pretty json printing instead of this crap
            if (sortedKeys.get(i).equals("sources-to-mute")) {
                String[] sources = value.split("\n");
                value = "[";
                for (int j = 0; j < sources.length; j++) {
                    String sourceName = sources[j].replaceAll("\r", "");
                    if (!sourceName.equals("")) {
                        if (!value.equals("[")) {
                            value = value + ", ";
                        }
                        value = value + "\"" + sourceName + "\"";
                    }
                }
                value = value + "]";
            } else {
                if (sortedKeys.get(i).equals("obs-video-dir")) {
                    if (!value.endsWith("/")) {
                        value = value + "/";
                    }
                }

                // TODO: can check if the directory exists on the server and throw if it doesn't
                // once there's some nice way of highlighting errors on the settings page
                if (sortedKeys.get(i).equals("server-video-dir")) {
                    if (!value.endsWith("/")) {
                        value = value + "/";
                    }
                }

                value = "\"" + value + "\"";
            }

            pw.printf("\t\"%s\":\t%s", sortedKeys.get(i), value);
            if (i < sortedKeys.size() - 1) {
                pw.println(",");
            } else {
                pw.println();
            }
        }
        pw.println("}");
        pw.close();
    }

    private static Map<String, JsonValue> readData() throws FileNotFoundException {
        JsonReader jr = Json.createReader(new FileReader(new File(CONFIG_FILE)));
        JsonObject jo = jr.readObject();
        HashMap<String, JsonValue> data = new HashMap<>();
        for (String key : jo.keySet()) {
            data.put(key, jo.get(key));
        }
        return data;
    }

    private static String getStringConfigValue(String name) throws FileNotFoundException, IOException {
        return ((JsonString) (readData().get(name))).getString();
    }

    private static int getIntConfigValue(String name) throws FileNotFoundException, IOException {
        return Integer.valueOf(((JsonString) (readData().get(name))).getString());
    }

    private static ArrayList<String> getStringListConfigValue(String name) throws FileNotFoundException, IOException {
        JsonArray array = readData().get(name).asJsonArray();
        ArrayList<String> values = new ArrayList<>();
        array.forEach((value) -> values.add(((JsonString) value).getString()));
        return values;
    }

}
