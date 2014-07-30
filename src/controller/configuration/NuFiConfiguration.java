package controller.configuration;

import hochberger.utilities.files.checker.FileChecker;
import hochberger.utilities.files.checker.aspects.ExistingFolderFileAspect;
import hochberger.utilities.properties.LoadProperties;
import hochberger.utilities.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import controller.NuFiApplication;

public class NuFiConfiguration {

	private final Properties properties;

	private NuFiConfiguration(final Properties properties) {
		super();
		this.properties = properties;
	}

	public static NuFiConfiguration createFrom(final String filePath) throws IOException, ConfigurationException {
		Properties properties = LoadProperties.fromExtern(filePath);
		validateProperties(properties);
		return new NuFiConfiguration(properties);
	}

	private static void validateProperties(final Properties properties) throws ConfigurationException {
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
		NuFiApplication.getLogger().info("Configuration keys are okay.");
	}

	private static void validateSourceFolderExistence(final Properties properties) throws MissingSourceFolderException {
		String filename = properties.getProperty(NuFiConfigurationConstants.SOURCE_FOLDER);
		FileChecker fileChecker = new FileChecker(new File(filename));
		fileChecker.addFileAspect(new ExistingFolderFileAspect());
		if (!fileChecker.check()) {
			throw new MissingSourceFolderException(Text.fromIterable(fileChecker.getResultDescriptions(), ", "));
		}
		NuFiApplication.getLogger().info("Source folder is okay.");
	}

	private static void validateChannelExistence(final Properties properties) {
		// TODO Auto-generated method stub

	}

	public static class ConfigurationException extends Exception {

		public ConfigurationException(final String message) {
			super(message);
		}
	}

	public static class InvalidConfigurationException extends ConfigurationException {

		private static final long serialVersionUID = 3651037404940742635L;

		public InvalidConfigurationException(final String missingKeys) {
			super("There are missing keys in the configuration: " + missingKeys);
		}
	}

	public static class MissingSourceFolderException extends ConfigurationException {

		private static final long serialVersionUID = 5203928823071388289L;

		public MissingSourceFolderException(final String description) {
			super("Source folder error: " + description);
		}
	}
}
