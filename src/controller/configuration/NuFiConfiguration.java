package controller.configuration;

import hochberger.utilities.properties.LoadProperties;
import hochberger.utilities.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import model.NuFiImage;

public class NuFiConfiguration {

	private final Properties properties;

	private NuFiConfiguration(final Properties properties) {
		super();
		this.properties = properties;
	}

	public File getSourceFolder() {
		return new File(getProperty(NuFiConfigurationConstants.SOURCE_FOLDER));
	}

	public String getChannelSeparator() {
		return getProperty(NuFiConfigurationConstants.CHANNEL_SEPARATOR);
	}

	public Iterable<String> getChannelDesignators() {
		final String channelsProperty = getProperty(NuFiConfigurationConstants.USED_CHANNELS);
		final String channelSeparator = NuFiConfigurationConstants.CHANNEL_SEPARATOR;
		return Text.trimAll(Text.toIterable(channelsProperty, channelSeparator));
	}

	public String getImageFiletype() {
		return getProperty(NuFiConfigurationConstants.CHANNEL_FILETYPE);
	}

	public NuFiImage getNuFiImage() {
		final ChannelFileBuilder fileBuilder = new ChannelFileBuilder(this.properties);
		return fileBuilder.getNuFiImage();
	}

	public int getMinimumNucleolusSize() {
		final int defaultSize = Integer.valueOf(getProperty(NuFiConfigurationConstants.NUCLEOLUS_AVERAGE_SIZE));
		final int minPercentage = Integer.valueOf(getProperty(NuFiConfigurationConstants.NUCLEOLUS_MIN_SIZE_PERCENTAGE));
		return (int) (defaultSize * (minPercentage / 100f));
	}

	public int getMaximumNucleolusSize() {
		final int defaultSize = Integer.valueOf(getProperty(NuFiConfigurationConstants.NUCLEOLUS_AVERAGE_SIZE));
		final int maxPercentage = Integer.valueOf(getProperty(NuFiConfigurationConstants.NUCLEOLUS_MAX_SIZE_PERCENTAGE));
		return (int) (defaultSize * (maxPercentage / 100f));
	}

	public int getInDepthRange() {
		return Integer.valueOf(getProperty(NuFiConfigurationConstants.IN_DEPTH_RANGE));
	}

	public String getProperty(final String key) {
		if (!this.properties.containsKey(key)) {
			throw new MissingConfigurationEntryException(key);
		}
		return this.properties.getProperty(key);
	}

	public static NuFiConfiguration createFrom(final String filePath) throws IOException, ConfigurationException {
		final Properties properties = LoadProperties.fromExtern(filePath);
		NuFiConfigurationValidator validator = new NuFiConfigurationValidator(properties);
		validator.validate();
		return new NuFiConfiguration(properties);
	}

	public static class MissingConfigurationEntryException extends RuntimeException {

		private static final long serialVersionUID = 3933516086547968600L;

		public MissingConfigurationEntryException(final String key) {
			super("Configuration lacks desired key: '" + key + "'.");
		}
	}
}
