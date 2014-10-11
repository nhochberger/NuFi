package model.serialization;

import java.io.File;

import com.google.common.collect.Iterables;

import controller.configuration.NuFiConfiguration;

public class DestinationFileBuilder {

	private final NuFiConfiguration configuration;

	public DestinationFileBuilder(final NuFiConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	public File buildDestinationFileFrom(final String fileIdentificator, final String filetype) {
		final String destinationFolderPath = this.configuration.getSourceFolder().getAbsolutePath() + "/results";
		final File destinationFolder = new File(destinationFolderPath);
		destinationFolder.mkdirs();
		final String channel1Filename = this.configuration.getNuFiImage().getChannel1().getName();
		final String channel1Designator = Iterables.get(this.configuration.getChannelDesignators(), 0);
		final String imageFiletype = this.configuration.getImageFiletype();
		final String resultFilename = channel1Filename.replace(channel1Designator, fileIdentificator).replace(imageFiletype, filetype);
		final String destinationFileName = destinationFolder.getAbsolutePath() + "/" + resultFilename;
		final File destination = new File(destinationFileName);
		return destination;
	}
}
