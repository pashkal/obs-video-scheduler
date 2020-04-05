import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import util.OBSApi;

public class Add {
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		new OBSApi().launchVideoByPath("D:/videos/all teams video.mp4", "Scheduled video", new ArrayList<String>());
		Thread.sleep(1000);

	}
}