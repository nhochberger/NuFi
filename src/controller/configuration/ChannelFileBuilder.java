package controller.configuration;

import hochberger.utilities.files.FirstFilenameEndsWithFilter;
import hochberger.utilities.text.Text;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;

public class ChannelFileBuilder {

	private final File sourceFolder;
	private final Iterable<String> channelEndings;
	private final String filetype;

	public ChannelFileBuilder(final Properties properties) {
		super();
		String channelsProperty = properties.getProperty(NuFiConfigurationConstants.USED_CHANNELS);
		String channelSeparator = NuFiConfigurationConstants.CHANNEL_SEPARATOR;
		this.channelEndings = Text.trimAll(Text.toIterable(channelsProperty, channelSeparator));
		this.sourceFolder = new File(properties.getProperty(NuFiConfigurationConstants.SOURCE_FOLDER));
		this.filetype = properties.getProperty(NuFiConfigurationConstants.CHANNEL_FILETYPE);
	}

	public Iterable<File> getChannelFiles() {
		LinkedList<File> channelFiles = new LinkedList<>();
		for (FilenameFilter filter : buildFilenameFilters()) {
			channelFiles.addAll(Arrays.asList(this.sourceFolder.listFiles(filter)));
		}
		return channelFiles;
	}

	private Iterable<FilenameFilter> buildFilenameFilters() {
		LinkedList<FilenameFilter> filters = new LinkedList<>();
		for (String suffix : this.channelEndings) {
			filters.add(new FirstFilenameEndsWithFilter(suffix + "." + this.filetype));
		}
		return filters;
	}
}
