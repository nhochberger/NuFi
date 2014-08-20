package controller.configuration;

import hochberger.utilities.files.filenamefilter.AndFilenameFilter;
import hochberger.utilities.files.filenamefilter.FileNameContainsFilter;
import hochberger.utilities.files.filenamefilter.FilenameEndsWithFilter;
import hochberger.utilities.text.Text;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.Properties;

import model.NuFiImage;

import com.google.common.collect.Iterables;

public class ChannelFileBuilder {

	private final File sourceFolder;
	private final Iterable<String> channelDesignator;
	private final String filetype;

	// TODO: needs heavy refactoring! do not pass properties in here!!
	public ChannelFileBuilder(final Properties properties) {
		super();
		String channelsProperty = properties.getProperty(NuFiConfigurationConstants.USED_CHANNELS);
		String channelSeparator = NuFiConfigurationConstants.CHANNEL_SEPARATOR;
		this.channelDesignator = Text.trimAll(Text.toIterable(channelsProperty, channelSeparator));
		this.sourceFolder = new File(properties.getProperty(NuFiConfigurationConstants.SOURCE_FOLDER));
		this.filetype = properties.getProperty(NuFiConfigurationConstants.CHANNEL_FILETYPE);
	}

	public NuFiImage getNuFiImage() {
		Iterable<File> channelFiles = getChannelFiles();
		return new NuFiImage(Iterables.get(channelFiles, 0), Iterables.get(channelFiles, 1), Iterables.get(channelFiles, 2));
	}

	public Iterable<File> getChannelFiles() {
		LinkedList<File> channelFiles = new LinkedList<>();
		for (FilenameFilter filter : buildFilenameFilters()) {
			// TODO needs refactoring. just an ugly workaround
			channelFiles.add(this.sourceFolder.listFiles(filter)[0]);
		}
		return channelFiles;
	}

	private Iterable<FilenameFilter> buildFilenameFilters() {
		LinkedList<FilenameFilter> filters = new LinkedList<>();
		FilenameFilter pngFilter = new FilenameEndsWithFilter(this.filetype);
		for (String infix : this.channelDesignator) {
			FilenameFilter containsFilter = new FileNameContainsFilter(infix);
			FilenameFilter andFilter = new AndFilenameFilter(pngFilter, containsFilter);
			filters.add(andFilter);
		}
		return filters;
	}
}
