package controller.configuration;

import java.util.Arrays;
import java.util.List;

public interface NuFiConfigurationConstants {

	public final static String SOURCE_FOLDER = "source.folder";
	public final static String USED_CHANNELS = "used.channels";
	public final static String CHANNEL_FILETYPE = "channel.filetype";
	public final static String CHANNEL_SEPARATOR = ",";
	public final static String NUCLEOLUS_AVERAGE_SIZE = "nucleolus.average";
	public final static String NUCLEOLUS_MIN_SIZE_PERCENTAGE = "nucleolus.min";
	public final static String NUCLEOLUS_MAX_SIZE_PERCENTAGE = "nucleolus.max";
	public static final String IN_DEPTH_RANGE = "indepth.range";

	public final static List<String> MANDATORY_ENTRIES = Arrays.asList(SOURCE_FOLDER, USED_CHANNELS, CHANNEL_FILETYPE);
}
