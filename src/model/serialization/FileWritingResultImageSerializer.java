package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.common.collect.Iterables;

import controller.configuration.NuFiConfiguration;

public class FileWritingResultImageSerializer extends SessionBasedObject implements ResultImageSerializer {

	private final NuFiConfiguration configuration;

	public FileWritingResultImageSerializer(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
	}

	@Override
	public void serializeResultImage(final BufferedImage image) {
		File destinationFolder = new DestinationFolderBuilder().buildDestinationFolderFrom(this.configuration);
		String channel1Filename = this.configuration.getNuFiImage().getChannel1().getName();
		String channel1Designator = Iterables.get(this.configuration.getChannelDesignators(), 0);
		String resultFilename = channel1Filename.replace(channel1Designator, "targets");
		File resultImageFile = new File(destinationFolder, resultFilename);
		try {
			ImageIO.write(image, this.configuration.getImageFiletype(), resultImageFile);
		} catch (IOException e) {
			logger().error("Unable to serialize result image", e);
		}
	}
}
