import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import util.OBSApi;

public class Remove {
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		new OBSApi().removeSource("Scheduled video");
	}

}