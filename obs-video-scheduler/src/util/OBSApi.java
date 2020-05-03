package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

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
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void launchVideo(String fileName) throws FileNotFoundException, IOException, InterruptedException {
		try {
			for (String source : Config.getSourcesToMute()) {
				client.muteSource(source);
			}

			if (Disclaimer.exists()) {
				client.launchVideo(Config.getOBSVideoDir() + Disclaimer.getFileName(), Config.getSourceLayer(),
						Config.getSceneName(), "Disclaimer",
						new SourceDimensions(Config.getVideoLeftMargin(), Config.getVideoTopMargin(),
								Config.getVideoWidth() * 1.0 / 100, Config.getVideoHeight() * 1.0 / 100),
						false);
				Thread.sleep(Disclaimer.getDuration());
			}

			client.launchVideo(Config.getOBSVideoDir() + fileName, Config.getSourceLayer(), Config.getSceneName(),
					Config.getSourceName(),
					new SourceDimensions(Config.getVideoLeftMargin(), Config.getVideoTopMargin(),
							Config.getVideoWidth() * 1.0 / 100, Config.getVideoHeight() * 1.0 / 100),
					!Disclaimer.exists());

			if (Disclaimer.exists()) {
				Thread.sleep(1000);
				client.removeSource(Config.getSceneName(), "Disclaimer");
			}

			transport.close();
		} catch (TException e) {
			e.printStackTrace();
		}
	}

	public void removeScheduledVideo() throws IOException, InterruptedException {
		try {
			if (Disclaimer.exists()) {
				if (Disclaimer.exists()) {
					client.launchVideo(Config.getOBSVideoDir() + Disclaimer.getFileName(), Config.getSourceLayer(),
							Config.getSceneName(), "Disclaimer",
							new SourceDimensions(Config.getVideoLeftMargin(), Config.getVideoTopMargin(),
									Config.getVideoWidth() * 1.0 / 100, Config.getVideoHeight() * 1.0 / 100),
							true);
				}
				Thread.sleep(Disclaimer.getDuration());
			}

			client.removeSource(Config.getSceneName(), Config.getSourceName());

			if (Disclaimer.exists()) {
				client.removeSource(Config.getSceneName(), "Disclaimer");
			}
			for (String source : Config.getSourcesToMute()) {
				client.unmuteSource(source);
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
