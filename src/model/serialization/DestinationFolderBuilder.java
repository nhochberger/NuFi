package model.serialization;

import java.io.File;

import controller.configuration.NuFiConfiguration;

public class DestinationFolderBuilder {

	public DestinationFolderBuilder() {
		super();
	}

	public File buildDestinationFolderFrom(final NuFiConfiguration configuration) {
		String destinationFolderPath = configuration.getSourceFolder().getAbsolutePath() + "/results";
		File destinationFile = new File(destinationFolderPath);
		destinationFile.mkdirs();
		return destinationFile;
	}
}
