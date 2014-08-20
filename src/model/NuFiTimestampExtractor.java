package model;

import hochberger.utilities.text.Text;

import com.google.common.collect.Iterables;

import controller.configuration.NuFiConfiguration;

public class NuFiTimestampExtractor {

	private final NuFiConfiguration configuration;

	public NuFiTimestampExtractor(final NuFiConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	public String getTimestamp() {
		String channel1Filename = this.configuration.getNuFiImage().getChannel1().getName();
		String channel1Designator = Iterables.get(this.configuration.getChannelDesignators(), 0);
		String filetype = this.configuration.getImageFiletype();
		String noFiletype = remove(channel1Filename, filetype);
		noFiletype = remove(noFiletype, ".");
		String result = remove(noFiletype, channel1Designator);
		return result;
	}

	private String remove(final String original, final String toRemove) {
		return original.replace(toRemove, Text.empty());
	}
}
