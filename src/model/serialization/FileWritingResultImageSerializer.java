package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import controller.configuration.NuFiConfiguration;

public class FileWritingResultImageSerializer extends SessionBasedObject implements ResultImageSerializer {

	private final NuFiConfiguration configuration;

	public FileWritingResultImageSerializer(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
	}

	@Override
	public void serializeResultImage(final BufferedImage image) {
		final File destinationFile = new DestinationFileBuilder(this.configuration).buildDestinationFileFrom("targets", "png");
		try {
			ImageIO.write(image, this.configuration.getImageFiletype(), destinationFile);
		} catch (final IOException e) {
			logger().error("Unable to serialize result image", e);
		}
	}
}
