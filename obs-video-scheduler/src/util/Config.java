package util;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.json.*;
import javax.json.stream.JsonGenerator;

public class Config {

    private static String CONFIG_FILE = "../../data/config.json";

    public static String getDisclaimerFileName() throws FileNotFoundException, IOException {
        return getStringConfigValue("disclaimer-file-name");
    }

    public static double getDisclaimerTransitionTime() throws FileNotFoundException, IOException {
        return getDoubleConfigValue("disclaimer-transition-time");
    }

    public static Path getServerVideoDirPath() throws FileNotFoundException, IOException {
        return Paths.get(getServerVideoDir());
    }

    public static Path getOBSVideoDirPath() throws FileNotFoundException, IOException {
        return Paths.get(getOBSVideoDir());
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

    public static ArrayList<String> getSourcesToMute() throws FileNotFoundException, IOException {
        return getStringListConfigValue("sources-to-mute");
    }

    public static void writeData(Map<String, String> data) throws IOException {
        final Map<String, Object> jsonWriterConfig = new HashMap<>();
        jsonWriterConfig.put(JsonGenerator.PRETTY_PRINTING, true);

        final JsonObjectBuilder configObjectBuilder = Json.createObjectBuilder(new HashMap<>(data));

        final String[] sourcesToMute = data.get("sources-to-mute").replaceAll("\r", "")
                .split("\n");
        configObjectBuilder.add("sources-to-mute", Json.createArrayBuilder(Arrays.asList(sourcesToMute)).build());

        try (final Writer printWriter = new PrintWriter(CONFIG_FILE);
             final JsonWriter writer = Json.createWriterFactory(jsonWriterConfig).createWriter(printWriter)) {
            writer.write(configObjectBuilder.build());
        }
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

    private static double getDoubleConfigValue(String name) throws FileNotFoundException, IOException {
        return Double.valueOf(((JsonString) (readData().get(name))).getString());
    }

    private static ArrayList<String> getStringListConfigValue(String name) throws FileNotFoundException, IOException {
        JsonArray array = readData().get(name).asJsonArray();
        ArrayList<String> values = new ArrayList<>();
        array.forEach((value) -> values.add(((JsonString) value).getString()));
        return values;
    }

}
