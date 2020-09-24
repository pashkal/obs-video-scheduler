package util;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Disclaimer {
    public static boolean exists() throws FileNotFoundException, IOException {
        String name = Config.getDisclaimerFileName();
        return DataProvider.getVideosByName().containsKey(name);
    }

    public static String getFileName() throws FileNotFoundException, IOException {
        if (Disclaimer.exists()) {
            return Config.getDisclaimerFileName();
        } else {
            return null;
        }
    }

    public static long getDuration() throws FileNotFoundException, IOException {
        if (Disclaimer.exists()) {
            return DataProvider.getVideosByName().get(Config.getDisclaimerFileName()).duration;
        } else {
            return 0;
        }
    }
    
    public static long getTransitionTime() throws FileNotFoundException, IOException {
        if (Disclaimer.exists()) {
            return ((long) Config.getDisclaimerTransitionTime()) * 1000;
        } else {
            return 0;
        }
    }
}
