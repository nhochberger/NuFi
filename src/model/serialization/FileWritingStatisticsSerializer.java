package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.files.Closer;
import hochberger.utilities.text.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import model.statistics.StatisticsResult;
import controller.configuration.NuFiConfiguration;

public class FileWritingStatisticsSerializer extends SessionBasedObject implements StatisticsSerializer {

	private final NuFiConfiguration configuration;

	public FileWritingStatisticsSerializer(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
	}

	@Override
	public void serializeStatistics(final StatisticsResult statistcs) {
		logger().info("Serializing mean distance.");
		final DestinationFileBuilder fileBuilder = new DestinationFileBuilder(this.configuration);
		BufferedWriter writer = null;
		try {
			final File destination = fileBuilder.buildDestinationFileFrom("statistics", "txt");
			logger().info("Destination: " + destination.getAbsolutePath());
			writer = writeStatistics(statistcs, destination);
		} catch (final IOException e) {
			logger().error("Unable to serialize mean distance", e);
		} finally {
			Closer.close(writer);
			logger().info("Serializing finsihed.");
		}
	}

	private BufferedWriter writeStatistics(final StatisticsResult statistcs, final File destination) throws IOException {
		BufferedWriter writer;
		DecimalFormat doubleFormatter = new DecimalFormat("0.00");
		doubleFormatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
		writer = new BufferedWriter(new FileWriter(destination));
		writeLine(writer, "Nuclei count: \n\t" + statistcs.nucleiCount());
		writeLine(writer, "Target count: \n\t" + statistcs.targetCount());
		writeLine(writer, "Nuclei to target ratio: \n\t" + doubleFormatter.format(statistcs.nucleoliNucleioliRatio() * 100) + "%");
		writeLine(writer, "Mean distance: \n\t" + doubleFormatter.format(statistcs.meanDistance()) + " pixels");
		writeLine(writer, "Distances [pixels]: \n\t" + Text.fromIterable(statistcs.distances(), "\n\t"));
		writer.newLine();
		return writer;
	}

	private void writeLine(final BufferedWriter writer, final String line) throws IOException {
		writer.write(line);
		writer.newLine();
		writer.newLine();
	}
}
