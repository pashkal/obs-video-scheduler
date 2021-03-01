package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import scheduler.ObsThriftServer;
import scheduler.SourceDimensions;

public class OBSApi {
    ObsThriftServer.Client client;
    TTransport transport;

    public OBSApi() {
        try {
            transport = new TSocket(Config.getOBSHost(), 9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            client = new ObsThriftServer.Client(protocol);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getCause() instanceof ConnectException) {
                System.err.println(
                    "Can't connect to OBS!\n1) Make sure OBS is running\n2) Make sure obs-thrift-api plugin initialized successfully");
            }
        }
    }

    public void startPlayback(ScheduleEntry entry, boolean hasNext)
            throws FileNotFoundException, IOException, InterruptedException {
        System.err.println("Launching " + entry.itemName);

        try {
            for (String source : Config.getSourcesToMute()) {
                client.muteSource(source);
            }

            if (Disclaimer.exists()) {
                client.launchVideo(
                    Config.getOBSVideoDir() + Disclaimer.getFileName(),
                    Config.getSourceLayer(),
                    Config.getSceneName(),
                    entry.getDisclaimerSourceName(),
                    new SourceDimensions(
                        Config.getVideoLeftMargin(),
                        Config.getVideoTopMargin(),
                        Config.getVideoWidth() * 1.0 / 100,
                        Config.getVideoHeight() * 1.0 / 100),
                    false);
                Thread.sleep(
                    Disclaimer.getDuration() - Disclaimer.getTransitionTime());
            }

            int videoLayer = Config.getSourceLayer();
            if (Disclaimer.exists() && Disclaimer.getTransitionTime() > 0) {
                videoLayer++;
            }

            boolean clearOnEnd = true;
            if (hasNext && !Disclaimer.exists()) {
                clearOnEnd = false;
            }
            if (Disclaimer.exists() && Disclaimer.getTransitionTime() == 0) {
                clearOnEnd = false;
            }

            client.launchVideo(
                Config.getOBSVideoDir() + entry.itemName,
                videoLayer,
                Config.getSceneName(),
                entry.getSourceName(),
                new SourceDimensions(
                    Config.getVideoLeftMargin(),
                    Config.getVideoTopMargin(),
                    Config.getVideoWidth() * 1.0 / 100,
                    Config.getVideoHeight() * 1.0 / 100),
                clearOnEnd);

            if (Disclaimer.exists()) {
                Thread.sleep(Disclaimer.getTransitionTime() + 500);
                client.removeSource(
                    Config.getSceneName(),
                    entry.getDisclaimerSourceName());
            }

            transport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public void endPlayback(ScheduleEntry entry)
            throws IOException, InterruptedException {
        System.err.println("Stopping " + entry.itemName);
        try {
            if (Disclaimer.exists()) {
                if (Disclaimer.exists()) {
                    client.launchVideo(
                        Config.getOBSVideoDir() + Disclaimer.getFileName(),
                        Config.getSourceLayer(),
                        Config.getSceneName(),
                        entry.getDisclaimerSourceName(),
                        new SourceDimensions(
                            Config.getVideoLeftMargin(),
                            Config.getVideoTopMargin(),
                            Config.getVideoWidth() * 1.0 / 100,
                            Config.getVideoHeight() * 1.0 / 100),
                        true);
                }
                Thread.sleep(Disclaimer.getDuration());
            }

            client.removeSource(Config.getSceneName(), entry.getSourceName());

            if (Disclaimer.exists()) {
                client.removeSource(
                    Config.getSceneName(),
                    entry.getDisclaimerSourceName());
            }
            for (String source : Config.getSourcesToMute()) {
                client.unmuteSource(source);
            }
            transport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public void switchPlayback(ScheduleEntry prev, ScheduleEntry next, boolean hasNext) throws FileNotFoundException, IOException, InterruptedException {
        System.err.println("Switching from " + prev.itemName + " to " + next.itemName);
        try {
            if (Disclaimer.exists()) {
                client.launchVideo(
                    Config.getOBSVideoDir() + Disclaimer.getFileName(),
                    Config.getSourceLayer(),
                    Config.getSceneName(),
                    prev.getDisclaimerSourceName(),
                    new SourceDimensions(
                        Config.getVideoLeftMargin(),
                        Config.getVideoTopMargin(),
                        Config.getVideoWidth() * 1.0 / 100,
                        Config.getVideoHeight() * 1.0 / 100),
                    false);
                Thread.sleep(Disclaimer.getDuration());
                client.launchVideo(
                    Config.getOBSVideoDir() + Disclaimer.getFileName(),
                    Config.getSourceLayer(),
                    Config.getSceneName(),
                    next.getDisclaimerSourceName(),
                    new SourceDimensions(
                        Config.getVideoLeftMargin(),
                        Config.getVideoTopMargin(),
                        Config.getVideoWidth() * 1.0 / 100,
                        Config.getVideoHeight() * 1.0 / 100),
                    false);
                Thread.sleep(Disclaimer.getDuration() - Disclaimer.getTransitionTime());
            }

            int videoLayer = Config.getSourceLayer();
            if (Disclaimer.exists() && Disclaimer.getTransitionTime() > 0) {
                videoLayer++;
            }

            boolean clearOnEnd = true;
            if (hasNext && !Disclaimer.exists()) {
                clearOnEnd = false;
            }
            if (Disclaimer.exists() && Disclaimer.getTransitionTime() == 0) {
                clearOnEnd = false;
            }
            client.launchVideo(
                Config.getOBSVideoDir() + next.itemName,
                videoLayer,
                Config.getSceneName(),
                next.getSourceName(),
                new SourceDimensions(
                    Config.getVideoLeftMargin(),
                    Config.getVideoTopMargin(),
                    Config.getVideoWidth() * 1.0 / 100,
                    Config.getVideoHeight() * 1.0 / 100),
                clearOnEnd);
            Thread.sleep(500);
            client.removeSource(Config.getSceneName(), prev.getSourceName());

            if (Disclaimer.exists()) {
                client.removeSource(
                    Config.getSceneName(),
                    prev.getDisclaimerSourceName());
                client.removeSource(
                    Config.getSceneName(),
                    next.getDisclaimerSourceName());
            }
            transport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public boolean heartbeat() {
        try {
            client.heartbeat();
            transport.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
