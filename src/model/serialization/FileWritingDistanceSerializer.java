package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.files.Closer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import controller.configuration.NuFiConfiguration;

public class FileWritingDistanceSerializer extends SessionBasedObject implements DistanceSerializer {

	private final NuFiConfiguration configuration;

	public FileWritingDistanceSerializer(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
	}

	@Override
	public void serializeDistance(final double distance) throws IOException {
		final DestinationFileBuilder fileBuilder = new DestinationFileBuilder(this.configuration);
		BufferedWriter writer = null;
		try {
			final File destination = fileBuilder.buildDestinationFileFrom("distance", "txt");
			writer = new BufferedWriter(new FileWriter(destination));
			writer.write("Mean distance between neuclei centers and targteed nucleoli: ");
			writer.newLine();
			writer.write(String.valueOf(distance));
			writer.newLine();
		} finally {
			Closer.close(writer);
		}
	}
}
