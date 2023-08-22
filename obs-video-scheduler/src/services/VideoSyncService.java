package services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                Map<String, Item> oldList = DataProvider.getVideosByName();

                List<String> allFiles = getAllChildren(Config.getServerVideoDirPath());
                HashSet<String> toAdd = new HashSet<String>();
                for (String fileName : allFiles) {
                    if (!oldList.containsKey(fileName)) {
                        toAdd.add(fileName);
                    }
                }

                HashSet<String> toRemove = new HashSet<String>();
                for (String fileName : oldList.keySet()) {
                    if (!allFiles.contains(fileName)) {
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


    private static boolean isVideoFile(Path file) {
        return Files.isRegularFile(file) &&
                (file.toString().endsWith("mkv") || file.toString().endsWith("mp4")
                        || file.toString().endsWith("avi") || file.toString().endsWith("mov"));
    }

    private List<String> getAllChildren(Path dir) {
        try (final Stream<Path> files = Files.walk(dir)) {
            return files.filter(VideoSyncService::isVideoFile)
                    .map((p) -> dir.relativize(p).toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to sync list of videos on sever", e);
        }
        return new ArrayList<>();
    }

    private long getDuration(String fileName) throws IOException {
        final Path filePath = Config.getServerVideoDirPath().resolve(fileName);
        Process p = Runtime.getRuntime().exec("ffmpeg -i \"" + filePath + "\"");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            String line = br.readLine();
            do {
                if (!line.contains("Duration")) {
                    line = br.readLine();
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line, " :,.");
                String t = st.nextToken();
                while (!t.equals("Duration")) {
                    t = st.nextToken();
                }
                return ffmpegDurationToLong(st.nextToken(), st.nextToken(), st.nextToken(), st.nextToken());
            } while (line != null);
        } finally {
            p.destroy();
        }
        throw new IllegalArgumentException("Failed to load duration of file " + filePath);
    }

    private long ffmpegDurationToLong(String hours, String minutes, String seconds, String hundredths) {
        return ((Long.parseLong(hours) * 60 + Long.parseLong(minutes)) * 60
                + Long.parseLong(seconds)) * 1000 + Long.parseLong(hundredths) * 10;
    }

    private static Logger logger = LoggerFactory.getLogger(VideoSyncService.class);
}
