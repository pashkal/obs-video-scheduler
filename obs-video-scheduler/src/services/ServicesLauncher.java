package services;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServicesLauncher implements ServletContextListener {

    Thread syncThread, launchThread, statusThread;

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {

    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        try {
            syncThread = new Thread(new VideoSyncService(arg0.getServletContext()));
            syncThread.start();

            launchThread = new Thread(new VideoLaunchService(arg0.getServletContext()));
            launchThread.start();

            statusThread = new Thread(new OBSStatusService(arg0.getServletContext()));
            statusThread.start();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
