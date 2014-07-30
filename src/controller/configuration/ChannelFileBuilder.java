package controller.configuration;

import hochberger.utilities.files.FirstFilenameEndsWithFilter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.LinkedList;

public class ChannelFileBuilder {

	private final File sourceFolder;
	private final Iterable<String> channelEndings;
	private final String filetype;

	public ChannelFileBuilder(final File sourceFolder, final Iterable<String> channelEndings, final String filetype) {
		super();
		this.sourceFolder = sourceFolder;
		this.channelEndings = channelEndings;
		this.filetype = filetype;
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
			filters.add(new FirstFilenameEndsWithFilter(suffix + this.filetype));
		}
		return filters;
	}
}
