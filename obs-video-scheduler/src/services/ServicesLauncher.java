package services;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServicesLauncher implements ServletContextListener {

	Thread syncThread, launchThread;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			arg0.getServletContext().log("Launching Wirecast video synchronization");
			syncThread = new Thread(new VideoSyncService(arg0.getServletContext()));
			syncThread.start();

			launchThread = new Thread(new VideoLaunchService(arg0.getServletContext()));
			launchThread.start();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
