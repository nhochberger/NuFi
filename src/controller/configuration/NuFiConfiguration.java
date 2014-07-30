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

import com.google.common.collect.Iterables;

import controller.NuFiApplication;

public class NuFiConfiguration {

	private final Properties properties;

	private NuFiConfiguration(final Properties properties) {
		super();
		this.properties = properties;
	}

	public File getSourceFolder() {
		return new File(this.properties.getProperty(NuFiConfigurationConstants.SOURCE_FOLDER));
	}

	public String getChannelSeparator() {
		return this.properties.getProperty(NuFiConfigurationConstants.CHANNEL_SEPARATOR);
	}

	public Iterable<File> getSourceFiles() {
		Text.toIterable(this.properties.getProperty(NuFiConfigurationConstants.SOURCE_FOLDER), getChannelSeparator());
		ChannelFileBuilder fileBuilder = new ChannelFileBuilder(this.properties);
		return fileBuilder.getChannelFiles();
	}

	public String getCustomProperty(final String key) {
		return this.properties.getProperty(key, Text.empty());
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
		FileChecker fileChecker = new FileChecker(sourceFolder(properties));
		fileChecker.addFileAspect(new ExistingFolderFileAspect());
		if (!fileChecker.check()) {
			throw new MissingSourceFolderException(Text.fromIterable(fileChecker.getResultDescriptions(), ", "));
		}
		NuFiApplication.getLogger().info("Source folder is okay.");
	}

	private static File sourceFolder(final Properties properties) {
		return new File(properties.getProperty(NuFiConfigurationConstants.SOURCE_FOLDER));
	}

	private static void validateChannelExistence(final Properties properties) throws MissingChannelFilesException {
		String channelsProperty = properties.getProperty(NuFiConfigurationConstants.USED_CHANNELS);
		String channelSeparator = NuFiConfigurationConstants.CHANNEL_SEPARATOR;
		Iterable<String> channels = Text.trimAll(Text.toIterable(channelsProperty, channelSeparator));
		ChannelFileBuilder builder = new ChannelFileBuilder(properties);
		Iterable<File> channelFiles = builder.getChannelFiles();
		if (Iterables.size(channelFiles) != Iterables.size(channels)) {
			throw new MissingChannelFilesException("Number of expected channels does not match actual number.\nExpected channels: " + channelsProperty + "\nfound: "
					+ Text.fromIterable(channelFiles, ", "));
		}
		NuFiApplication.getLogger().info("Channel files are okay.");
		NuFiApplication.getLogger().info("Using: " + Text.fromIterable(channelFiles, ", "));
	}

	public static class ConfigurationException extends Exception {

		private static final long serialVersionUID = -4487918431701008904L;

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

	public static class MissingChannelFilesException extends ConfigurationException {

		private static final long serialVersionUID = 5203928823071388289L;

		public MissingChannelFilesException(final String description) {
			super("Channel files error: " + description);
		}
	}
}
