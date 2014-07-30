package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.files.Closer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.targetdetection.TargetPoint;
import controller.configuration.NuFiConfiguration;

public class FileWritingTargetPointSerializer extends SessionBasedObject implements TargetPointSerializer {

	private final NuFiConfiguration configuration;

	public FileWritingTargetPointSerializer(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
	}

	@Override
	public void serialize(final List<TargetPoint> targets) throws IOException {
		logger().info("Beginning to serialize " + targets.size() + " targets");
		BufferedWriter writer = null;
		try {
			File destination = getDestinationFile();
			logger().info("Destination: " + destination.getAbsolutePath());
			writer = new BufferedWriter(new FileWriter(destination));
			writeTargets(targets, writer);
		} finally {
			Closer.close(writer);
		}
		logger().info("Serializing finished");
	}

	private void writeTargets(final List<TargetPoint> targets, final BufferedWriter writer) throws IOException {
		TargetPointFormatter formatter = new TargetPointFormatter();
		int i = 1;
		for (TargetPoint targetPoint : targets) {
			logger().info("Serializing target " + i + ": " + targetPoint);
			writer.write(formatter.format(i, targetPoint));
			writer.newLine();
			i++;
		}
	}

	private File getDestinationFolder() {
		String destinationFolderPath = this.configuration.getSourceFolder().getAbsolutePath() + "/results";
		File destinationFile = new File(destinationFolderPath);
		destinationFile.mkdirs();
		return destinationFile;
	}

	private File getDestinationFile() {
		String destinationFileName = getDestinationFolder().getAbsolutePath() + "/targets.txt";
		File destination = new File(destinationFileName);
		return destination;
	}
}
