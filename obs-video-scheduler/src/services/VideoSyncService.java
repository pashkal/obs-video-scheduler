package services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import util.Config;
import util.DataProvider;
import util.Item;

public class VideoSyncService implements Runnable {
	ServletContext sc;

	public VideoSyncService(ServletContext servletContext) throws FileNotFoundException, IOException {
		sc = servletContext;
	}

	public void run() {
		while (true) {
			try {
				Map<String, Item> oldList = DataProvider.getVideos();
				
				HashSet<String> newFileList = new HashSet<>();
				File videoDirFile = new File(Config.getServerVideoDir());
				File[] children = videoDirFile.listFiles();
				for (File f : children) {
					newFileList.add(f.getName());
				}
				sc.log(newFileList.toString());
				HashSet<String> toAdd = new HashSet<String>();
				for (String fileName : newFileList) {
					if (!fileName.endsWith("mkv") && !fileName.endsWith("mp4") && !fileName.endsWith("avi") && !fileName.endsWith("mov")) {
						continue;
					}
					if (!oldList.containsKey(fileName)) {
						toAdd.add(fileName);
					}
				}

				HashSet<String> toRemove = new HashSet<String>();
				for (String fileName : oldList.keySet()) {
					if (!newFileList.contains(fileName)) {
						toRemove.add(fileName);
					}
				}

				for (String fileName : toAdd) {
					try {
						long duration = getDuration(fileName);
						oldList.put(fileName, new Item(fileName, duration, true));
					} catch (Throwable e) {
						e.printStackTrace();
						System.err.println(fileName);
					}
				}

				for (String fileName : toRemove) {
					oldList.remove(fileName);
				}
				
				DataProvider.writeVideos(oldList.values());
			} catch (Throwable e) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(bos);
				e.printStackTrace(ps);
				sc.log(new String(bos.toByteArray()));
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private long getDuration(String fileName) throws IOException {
		Process p = Runtime.getRuntime().exec("ffmpeg -i \"" + Config.getServerVideoDir() + fileName + "\"");
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while (true) {
			String s = br.readLine();
			// sc.log(s);
			if (!s.contains("Duration")) {
				continue;
			}
			StringTokenizer st = new StringTokenizer(s, " :,.");
			String t = st.nextToken();
			while (!t.equals("Duration")) {
				t = st.nextToken();
			}
			long time = (Integer.parseInt(st.nextToken()) * 3600 + Integer.parseInt(st.nextToken()) * 60
					+ Integer.parseInt(st.nextToken())) * 1000 + Integer.parseInt(st.nextToken()) * 10;
			return time;
		}

	}
}
