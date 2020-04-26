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

	public void launchVideoByPath(String filePath)
			throws FileNotFoundException, IOException {
		try {
			for (String source : Config.getSourcesToMute()) {
				client.muteSource(source);
			}
			client.launchVideo(filePath, Config.getSourceLayer(), Config.getSceneName(), Config.getSourceName(),
					new SourceDimensions());
			transport.close();
		} catch (TException e) {
			e.printStackTrace();
		}
	}

	public void removeSource(String name) throws IOException {
		try {
			client.removeSource(Config.getSceneName(), Config.getSourceName());
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
