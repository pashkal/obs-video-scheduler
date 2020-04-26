package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import util.Config;
import util.DataProvider;
import util.ScheduleEntry;
import util.Item;
import util.OBSApi;

public class VideoLaunchService implements Runnable {

	private ServletContext sc;

	// OBSApi api = new OBSApi();

	public VideoLaunchService(ServletContext servletContext) {
		this.sc = servletContext;
	}

	static int currentId = 0;

	@Override
	public void run() {
		sc.log("Launcher started");

		while (true) {
			boolean longSleep = false;
			try {
				List<ScheduleEntry> schedule = DataProvider.getSchedule();
				Map<String, Item> items = DataProvider.getMapByName();
				String videoDir = Config.getOBSVideoDir();
				long time = new Date().getTime() - new Date().getTimezoneOffset() * 60 * 1000;
				for (ScheduleEntry e : schedule) {
					try {
						if (!items.get(e.itemName).isVideo)
							continue;
						long start = e.start;
						long stop = e.start + items.get(e.itemName).duration;
						System.err.println(start + " " + time);
						if (time > start - 1000 && Math.abs(start - time) < 2000) {
							sc.log("Launching " + e.itemName);
							System.err.println("Launching " + e.itemName);
							System.err.println(videoDir + e.itemName);

							new OBSApi().launchVideoByPath(videoDir + e.itemName);
							longSleep = true;

						}
						if (time > stop - 500 && time - stop < 1000) {
							System.err.println("Stopping " + e.itemName);
							new OBSApi().removeSource(Config.getSourceName());
							longSleep = true;
						}

					} catch (Exception ee) {
						ee.printStackTrace();
						System.err.println(e.toString());
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			// sc.log("Launcher iteration");
			try {
				Thread.sleep(longSleep ? 5000 : 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				// api.close();
			}
		}
	}

}
