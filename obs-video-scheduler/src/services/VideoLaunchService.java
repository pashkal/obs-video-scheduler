package services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

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

                processSchedule();

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void processSchedule() throws FileNotFoundException, IOException, InterruptedException {
        List<ScheduleEntry> schedule = DataProvider.getSchedule();
        Map<String, Item> items = DataProvider.getAllItemsByName();
        for (int i = 0; i < schedule.size(); i++) {
            long time = new Date().getTime() - new Date().getTimezoneOffset() * 60 * 1000;
            ScheduleEntry entry = schedule.get(i);
            if (!items.get(entry.itemName).isVideo)
                return;
            long start = entry.start;
            long stop = entry.getStopTime(items) - Disclaimer.getDuration();

            // Video from the future
            if (start - 2000 > time) {
                continue;
            }

            // Video from the past
            if (stop < time) {
                continue;
            }

            boolean hasNext = (i < schedule.size() - 1) && (entry.getStopTime(items) == schedule.get(i + 1).start);

            if (time > start - 2000 && time < start) {
                Thread.sleep(start - time);
                new OBSApi().startPlayback(entry, hasNext);
            }

            if (time > stop - 2000 && time < stop) {
                entry.getStopTime(items);
                Thread.sleep(stop - time);
                if (hasNext) {
                    ScheduleEntry next = schedule.get(i + 1);
                    boolean hasFurtherNext = (i < schedule.size() - 2) && (next.getStopTime(items) == schedule.get(i + 2).start);
                    new OBSApi().switchPlayback(entry, next, hasFurtherNext);
                } else {
                    new OBSApi().endPlayback(entry);
                }
            }
            return;
        }
    }
}