package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.files.Closer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.targetdetection.ImageAnalysisResults;
import model.targetdetection.TargetPoint;
import controller.configuration.NuFiConfiguration;

public class FileWritingTargetPointSerializer extends SessionBasedObject implements TargetPointSerializer {

	private final NuFiConfiguration configuration;

	public FileWritingTargetPointSerializer(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
	}

	@Override
	public void serialize(final ImageAnalysisResults results) {
		final int targetAmount = results.getNucleiTargets().size() + results.getNucleoliTargets().size();
		logger().info("Beginning to serialize " + targetAmount + " targets");
		BufferedWriter writer = null;
		try {
			final File destination = getDestinationFile("targets", "txt");
			logger().info("Destination: " + destination.getAbsolutePath());
			writer = new BufferedWriter(new FileWriter(destination));
			writeTargets(results, writer);
		} catch (final IOException e) {
			logger().error("Unable to serialize targets", e);
		} finally {
			Closer.close(writer);
		}
		logger().info("Serializing finished");
	}

	private void writeTargets(final ImageAnalysisResults results, final BufferedWriter writer) throws IOException {
		final TargetPointFormatter formatter = new TargetPointFormatter();
		writer.write("# nucleoli targets");
		writer.newLine();
		writeFromIndex(results.getNucleoliTargets(), writer, formatter, 1);
		writer.write("# targets in center of empty nuclei");
		writer.newLine();
		writeFromIndex(results.getNucleiTargets(), writer, formatter, results.getNucleoliTargets().size() + 1);
	}

	private void writeFromIndex(final List<TargetPoint> targets, final BufferedWriter writer, final TargetPointFormatter formatter, final int startIndex) throws IOException {
		int i = startIndex;
		for (final TargetPoint targetPoint : targets) {
			logger().info("Serializing target " + i + ": " + targetPoint);
			writer.write(formatter.format(i, targetPoint));
			writer.newLine();
			i++;
		}
	}

	private File getDestinationFile(final String fileIdentificator, final String filetype) {
		return new DestinationFileBuilder(this.configuration).buildDestinationFileFrom(fileIdentificator, filetype);
	}
}
