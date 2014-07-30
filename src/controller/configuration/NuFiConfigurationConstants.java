package controller.configuration;

import java.util.Arrays;
import java.util.List;

public interface NuFiConfigurationConstants {

	public final static String SOURCE_FOLDER = "source.folder";
	public final static String USED_CHANNELS = "used.channels";
	public final static String CHANNEL_SEPARATOR = "channel.separator";
	public final static String CHANNEL_FILETYPE = "channel.filetype";

	public final static List<String> MANDATORY_ENTRIES = Arrays.asList(SOURCE_FOLDER, USED_CHANNELS, CHANNEL_SEPARATOR, CHANNEL_FILETYPE);
}
