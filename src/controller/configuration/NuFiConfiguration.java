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

	public int getMinimumNucleusSize() {
		final int defaultSize = Integer.valueOf(getProperty(NuFiConfigurationConstants.NUCLEUS_AVERAGE_SIZE));
		final double minFactor = Double.valueOf(getProperty(NuFiConfigurationConstants.NUCLEUS_MIN_SIZE_FACTOR));
		return (int) (defaultSize * minFactor);
	}

	public int getMaximumNucleusSize() {
		final int defaultSize = Integer.valueOf(getProperty(NuFiConfigurationConstants.NUCLEUS_AVERAGE_SIZE));
		final double maxFactor = Double.valueOf(getProperty(NuFiConfigurationConstants.NUCLEUS_MAX_SIZE_FACTOR));
		return (int) (defaultSize * maxFactor);
	}

	public int getMinimumNucleolusSize() {
		final int defaultSize = Integer.valueOf(getProperty(NuFiConfigurationConstants.NUCLEOLUS_AVERAGE_SIZE));
		final double minFactor = Double.valueOf(getProperty(NuFiConfigurationConstants.NUCLEOLUS_MIN_SIZE_FACTOR));
		return (int) (defaultSize * minFactor);
	}

	public int getMaximumNucleolusSize() {
		final int defaultSize = Integer.valueOf(getProperty(NuFiConfigurationConstants.NUCLEOLUS_AVERAGE_SIZE));
		final double maxFactor = Double.valueOf(getProperty(NuFiConfigurationConstants.NUCLEOLUS_MAX_SIZE_FACTOR));
		return (int) (defaultSize * maxFactor);
	}

	public int getInDepthRange() {
		return Integer.valueOf(getProperty(NuFiConfigurationConstants.IN_DEPTH_RANGE));
	}

	public double getNucleusBackgroundBlur() {
		return Double.valueOf(getProperty(NuFiConfigurationConstants.NUCLEUS_BACKGROUND_BLUR));
	}

	public double getNucleusThresholdingBlur() {
		return Double.valueOf(getProperty(NuFiConfigurationConstants.NUCLEUS_THRESHOLDING_BLUR));
	}

	public double getNucleusMinCircularity() {
		return Double.valueOf(getProperty(NuFiConfigurationConstants.NUCLEUS_MIN_CIRCULARITY));
	}

	public int getNucleusBoundaryWidth() {
		return Integer.valueOf(getProperty(NuFiConfigurationConstants.NUCLEUS_BOUNDARY_WIDTH));
	}

	public double getNucleolusBackgroundBlur() {
		return Double.valueOf(getProperty(NuFiConfigurationConstants.NUCLEOLUS_BACKGROUND_BLUR));
	}

	public double getNucleolusThresholdingBlur() {
		return Double.valueOf(getProperty(NuFiConfigurationConstants.NUCLEOLUS_THRESHOLDING_BLUR));
	}

	public String getProperty(final String key) {
		if (!this.properties.containsKey(key)) {
			throw new MissingConfigurationEntryException(key);
		}
		return this.properties.getProperty(key);
	}

	public static NuFiConfiguration createFrom(final String filePath) throws IOException, ConfigurationException {
		final Properties properties = LoadProperties.fromExtern(filePath);
		final NuFiConfigurationValidator validator = new NuFiConfigurationValidator(properties);
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
