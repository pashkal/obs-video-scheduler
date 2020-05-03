package util;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Disclaimer {
	public static boolean exists() throws FileNotFoundException, IOException {
		String name = Config.getDisclaimerFileName();
		return DataProvider.getVideos().containsKey(name);
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
			return DataProvider.getVideos().get(Config.getDisclaimerFileName()).duration;
		} else {
			return 0;
		}
		
	}
}
