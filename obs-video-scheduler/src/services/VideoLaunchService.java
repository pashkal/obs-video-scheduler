package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import util.Config;
import util.DataProvider;
import util.Disclaimer;
import util.ScheduleEntry;
import util.Item;
import util.OBSApi;

public class VideoLaunchService implements Runnable {

	private ServletContext sc;

	public VideoLaunchService(ServletContext servletContext) {
		this.sc = servletContext;
	}

	static int currentId = 0;

	@Override
	public void run() {
		sc.log("Launcher started");

		while (true) {
			try {
				List<ScheduleEntry> schedule = DataProvider.getSchedule();
				Map<String, Item> items = DataProvider.getAllItems();
				long time = new Date().getTime() - new Date().getTimezoneOffset() * 60 * 1000;
				for (ScheduleEntry e : schedule) {
					try {
						if (!items.get(e.itemName).isVideo)
							continue;
						long start = e.start;
						long stop = e.start + items.get(e.itemName).duration + Disclaimer.getDuration();
						if (time > start - 2000 && time < start) {
							Thread.sleep(start - time);
							sc.log("Launching " + e.itemName);
							System.err.println("Launching " + e.itemName);

							new OBSApi().launchVideo(e.itemName);
							
							Thread.sleep(1500);
						}
						if (time > stop - 2000 && time < stop) {
							Thread.sleep(stop - time);
							System.err.println("Stopping " + e.itemName);
							new OBSApi().removeScheduledVideo();
							Thread.sleep(1500);
						}

					} catch (Exception ee) {
						ee.printStackTrace();
						System.err.println(e.toString());
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
