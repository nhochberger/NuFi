package controller.configuration;

import java.util.Arrays;
import java.util.List;

public interface NuFiConfigurationConstants {

	public final static String SOURCE_FOLDER = "source.folder";
	public final static String USED_CHANNELS = "used.channels";
	public final static String CHANNEL_FILETYPE = "channel.filetype";
	public final static String CHANNEL_SEPARATOR = ",";
	public final static String NUCLEUS_AVERAGE_SIZE = "nucleus.average";
	public final static String NUCLEUS_MIN_SIZE_FACTOR = "nucleus.min.factor";
	public final static String NUCLEUS_MAX_SIZE_FACTOR = "nucleus.max.factor";
	public final static String NUCLEOLUS_AVERAGE_SIZE = "nucleolus.average";
	public final static String NUCLEOLUS_MIN_SIZE_FACTOR = "nucleolus.min.factor";
	public final static String NUCLEOLUS_MAX_SIZE_FACTOR = "nucleolus.max.factor";
	public static final String IN_DEPTH_RANGE = "indepth.range";
	public static final String NUCLEUS_BACKGROUND_BLUR = "nucleus.background.blur";
	public static final String NUCLEUS_THRESHOLDING_BLUR = "nucleus.thresholding.blur";
	public static final String NUCLEUS_MIN_CIRCULARITY = "nucleus.min.circularity";
	public static final String NUCLEUS_BOUNDARY_WIDTH = "nucleus.boundary.width";
	public static final String NUCLEOLUS_BACKGROUND_BLUR = "nucleolus.background.blur";
	public static final String NUCLEOLUS_THRESHOLDING_BLUR = "nucleolus.thresholding.blur";

	public final static List<String> MANDATORY_ENTRIES = Arrays.asList(SOURCE_FOLDER, USED_CHANNELS, CHANNEL_FILETYPE, NUCLEUS_AVERAGE_SIZE, NUCLEUS_MIN_SIZE_FACTOR, NUCLEUS_MAX_SIZE_FACTOR,
			NUCLEOLUS_AVERAGE_SIZE, NUCLEOLUS_MIN_SIZE_FACTOR, NUCLEOLUS_MAX_SIZE_FACTOR, IN_DEPTH_RANGE, NUCLEUS_BACKGROUND_BLUR, NUCLEUS_THRESHOLDING_BLUR, NUCLEOLUS_BACKGROUND_BLUR,
			NUCLEOLUS_THRESHOLDING_BLUR, NUCLEUS_MIN_CIRCULARITY);
}
