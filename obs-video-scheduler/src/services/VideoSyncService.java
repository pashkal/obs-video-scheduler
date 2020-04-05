package services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import util.Config;

public class VideoSyncService implements Runnable {
	ServletContext sc;

	public VideoSyncService(ServletContext servletContext) throws FileNotFoundException, IOException {
		sc = servletContext;
		Properties pr = new Properties();
		pr.load(new FileInputStream("../../sched.properties"));
	}

	public void run() {
		while (true) {
			try {
				HashSet<String> oldFileList = new HashSet<>();
				HashMap<String, Long> fileDuration = new HashMap<>();
				Scanner s = new Scanner(new File(Config.getVideoList()));
				while (s.hasNext()) {
					String name = s.nextLine();
					long duration = Long.parseLong(s.nextLine());
					fileDuration.put(name, duration);
				}
				s.close();
				HashSet<String> newFileList = new HashSet<>();
				File videoDirFile = new File(Config.getVideoDir());
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
					if (!oldFileList.contains(fileName)) {
						toAdd.add(fileName);
					}
				}

				HashSet<String> toRemove = new HashSet<String>();
				for (String fileName : oldFileList) {
					if (!newFileList.contains(fileName)) {
						toRemove.add(fileName);
					}
				}

				for (String fileName : toAdd) {
					try {
						long duration = getDuration(fileName);
						fileDuration.put(fileName, duration);
						oldFileList.add(fileName);
					} catch (Throwable e) {
						e.printStackTrace();
						System.err.println(fileName);
					}
				}

				for (String fileName : toRemove) {
					oldFileList.remove(fileName);
				}

				PrintWriter pw = new PrintWriter(Config.getVideoList());
				for (String fileName : oldFileList) {
					pw.println(fileName);
					pw.println(fileDuration.get(fileName));
				}
				pw.close();

			} catch (Throwable e) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(bos);
				e.printStackTrace(ps);
				sc.log(new String(bos.toByteArray()));
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private long getDuration(String fileName) throws IOException {
		Process p = Runtime.getRuntime().exec("ffmpeg -i \"" + Config.getVideoDir() + fileName + "\"");
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
			long time = Integer.parseInt(st.nextToken()) * 3600 + Integer.parseInt(st.nextToken()) * 60
					+ Integer.parseInt(st.nextToken());
			time *= 1000;
			return time;
		}

	}
}
