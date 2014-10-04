package controller.configuration;

import hochberger.utilities.files.checker.FileChecker;
import hochberger.utilities.files.checker.aspects.ExistingFolderFileAspect;
import hochberger.utilities.text.Text;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.google.common.collect.Iterables;

import controller.NuFiApplication;
import controller.configuration.ConfigurationException.InvalidConfigurationException;
import controller.configuration.ConfigurationException.MissingChannelFilesException;
import controller.configuration.ConfigurationException.MissingSourceFolderException;

public class NuFiConfigurationValidator {

	private final Properties properties;

	public NuFiConfigurationValidator(final Properties properties) {
		super();
		this.properties = properties;
	}

	public void validate() throws ConfigurationException {
		validateEntryExistence(this.properties);
		validateSourceFolderExistence(this.properties);
		validateChannelExistence(this.properties);
	}

	private void validateEntryExistence(final Properties properties) throws InvalidConfigurationException {
		final List<String> missingEntries = new LinkedList<String>();
		for (final String key : NuFiConfigurationConstants.MANDATORY_ENTRIES) {
			if (Text.empty().equals(properties.getProperty(key, Text.empty()))) {
				missingEntries.add(key);
			}
		}
		if (!missingEntries.isEmpty()) {
			throw new InvalidConfigurationException(Text.fromIterable(missingEntries, ", "));
		}
		NuFiApplication.getLogger().info("Configuration keys are okay.");
	}

	private void validateSourceFolderExistence(final Properties properties) throws MissingSourceFolderException {
		final FileChecker fileChecker = new FileChecker(sourceFolder(properties));
		fileChecker.addFileAspect(new ExistingFolderFileAspect());
		if (!fileChecker.check()) {
			throw new MissingSourceFolderException(Text.fromIterable(fileChecker.getResultDescriptions(), ", "));
		}
		NuFiApplication.getLogger().info("Source folder is okay.");
	}

	private static File sourceFolder(final Properties properties) {
		return new File(properties.getProperty(NuFiConfigurationConstants.SOURCE_FOLDER));
	}

	private void validateChannelExistence(final Properties properties) throws MissingChannelFilesException {
		final String channelsProperty = properties.getProperty(NuFiConfigurationConstants.USED_CHANNELS);
		final String channelSeparator = NuFiConfigurationConstants.CHANNEL_SEPARATOR;
		final Iterable<String> channels = Text.trimAll(Text.toIterable(channelsProperty, channelSeparator));
		final ChannelFileBuilder builder = new ChannelFileBuilder(properties);
		final Iterable<File> channelFiles = builder.getChannelFiles();
		if (Iterables.size(channelFiles) != Iterables.size(channels)) {
			throw new MissingChannelFilesException("Number of expected channels does not match actual number.\nExpected channels: " + channelsProperty + "\nfound: "
					+ Text.fromIterable(channelFiles, ", "));
		}
		NuFiApplication.getLogger().info("Channel files are okay.");
		NuFiApplication.getLogger().info("Using: " + Text.fromIterable(channelFiles, ", "));
	}
}
