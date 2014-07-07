package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.files.Closer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.targetdetection.TargetPoint;

import org.apache.log4j.Logger;

public class FileWritingTargetPointSerializer implements TargetPointSerializer {

	private String filepath;
	private BasicSession session;

	public FileWritingTargetPointSerializer(BasicSession session) {
		super();
		this.filepath = String.valueOf(session
				.getSessionVariable("destination"));
		this.session = session;
	}

	private Logger logger() {
		return session.getLogger();
	}

	@Override
	public void serialize(List<TargetPoint> targets) throws IOException {
		logger().info("Beginning to serialize " + targets.size() + " targets");
		BufferedWriter writer = null;
		try {
			File destination = new File(filepath);
			logger().info("Destination: " + destination.getAbsolutePath());
			writer = new BufferedWriter(new FileWriter(destination));
			writeTargets(targets, writer);
		} finally {
			Closer.close(writer);
		}
		logger().info("Serializing finished");
	}

	private void writeTargets(List<TargetPoint> targets, BufferedWriter writer)
			throws IOException {
		TargetPointFormatter formatter = new TargetPointFormatter();
		int i = 1;
		for (TargetPoint targetPoint : targets) {
			logger().info("Serializing target " + i + ": " + targetPoint);
			writer.write(formatter.format(i, targetPoint));
			writer.newLine();
			i++;
		}
	}
}
