package services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import util.DataProvider;
import util.ScheduleEntry;
import util.Item;

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
                Map<String, Item> items = DataProvider.getAllItemsByName();
                long time = new Date().getTime() - new Date().getTimezoneOffset() * 60 * 1000;
                for (ScheduleEntry e : schedule) {
                    e.process(items, time);
                }
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

}
