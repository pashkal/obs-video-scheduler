package util;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
	static Properties pr;
	
	static String getConfigValue(String name) throws FileNotFoundException, IOException {
		if (pr == null) {
			pr = new Properties();
			pr.load(new FileInputStream("../../sched.properties"));
		}
		
//		System.err.println(name + " = " + pr.getProperty(name));
		
		return pr.getProperty(name);
	}

	public static String getDefaultLayer() throws FileNotFoundException, IOException {
		return getConfigValue("layer-name");
	}

	public static String getVideoList() throws FileNotFoundException, IOException {
		return getConfigValue("video-list-file");
	}

	public static String getScheduleFile() throws FileNotFoundException, IOException {
		return getConfigValue("schedule-file");
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

	public static String getConnectionFile() throws FileNotFoundException, IOException {
		return getConfigValue("connection-file");
	}

	public static String getScriptDir() throws FileNotFoundException, IOException {
		return getConfigValue("perl-script-dir");
	}
	
	public static String getDefaultShotName() throws FileNotFoundException, IOException {
		return getConfigValue("default-shot-name");
	}

	public static String getActivityList() throws FileNotFoundException, IOException {
		return getConfigValue("activity-list-file");
	}
}
