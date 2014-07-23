package controller.configuration;

import hochberger.utilities.files.FileChecker;
import hochberger.utilities.properties.LoadProperties;
import hochberger.utilities.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class NuFiConfiguration {

	private final Properties properties;

	private NuFiConfiguration(final Properties properties) {
		super();
		this.properties = properties;
	}

	public static NuFiConfiguration createFrom(final String filePath) throws IOException, InvalidConfigurationException {
		Properties properties = LoadProperties.fromExtern(filePath);
		validateProperties(properties);
		return new NuFiConfiguration(properties);
	}

	private static void validateProperties(final Properties properties) throws InvalidConfigurationException {
		validateEntryExistence(properties);
		validateSourceFolderExistence(properties);
		validateChannelExistence(properties);
	}

	private static void validateEntryExistence(final Properties properties) throws InvalidConfigurationException {
		List<String> missingEntries = new LinkedList<String>();
		for (String key : NuFiConfigurationConstants.MANDATORY_ENTRIES) {
			if (Text.empty().equals(properties.getProperty(key, Text.empty()))) {
				missingEntries.add(key);
			}
		}
		if (!missingEntries.isEmpty()) {
			throw new InvalidConfigurationException(Text.fromIterable(missingEntries, ", "));
		}
	}

	private static void validateSourceFolderExistence(final Properties properties) {
		String filename = properties.getProperty(NuFiConfigurationConstants.SOURCE_FOLDER);
		FileChecker fileChecker = new FileChecker(new File(filename));
		// TODO
	}

	private static void validateChannelExistence(final Properties properties) {
		// TODO Auto-generated method stub

	}

	public static class InvalidConfigurationException extends Exception {

		public InvalidConfigurationException(final String missingKeys) {
			super("There are missing keys in the configuration: " + missingKeys);
		}
	}
}
