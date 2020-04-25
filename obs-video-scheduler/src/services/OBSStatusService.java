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
import util.OBSStatus;

public class OBSStatusService implements Runnable {

	private ServletContext sc;

	public OBSStatusService(ServletContext servletContext) {
		this.sc = servletContext;
	}

	@Override
	public void run() {
		sc.log("Service monitor started");
		
		while (true) {
			OBSStatus.set(new OBSApi().heartbeat());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
