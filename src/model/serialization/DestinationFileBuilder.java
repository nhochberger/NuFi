package model.serialization;

import java.io.File;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.google.common.collect.Iterables;

import controller.configuration.NuFiConfiguration;

public class DestinationFileBuilder {

	private static final String TIMESTAMP_SEPARATOR = "_";
	private final NuFiConfiguration configuration;
	private final DateTime dateTime;
	private final DateTimeFormatter formatter;

	public DestinationFileBuilder(final NuFiConfiguration configuration) {
		super();
		this.configuration = configuration;
		this.dateTime = new DateTime();
		this.formatter = new DateTimeFormatterBuilder().appendYear(4, 4).appendLiteral(TIMESTAMP_SEPARATOR).appendMonthOfYear(2).appendLiteral(TIMESTAMP_SEPARATOR).appendDayOfMonth(2)
				.appendLiteral(TIMESTAMP_SEPARATOR).appendHourOfDay(2).appendLiteral(TIMESTAMP_SEPARATOR).appendMinuteOfHour(2).appendLiteral(TIMESTAMP_SEPARATOR).appendSecondOfMinute(2)
				.toFormatter();
	}

	public File buildDestinationFileFrom(final String fileIdentificator, final String filetype) {
		final String destinationFolderPath = this.configuration.getSourceFolder().getAbsolutePath() + "/results";
		final File destinationFolder = new File(destinationFolderPath);
		destinationFolder.mkdirs();
		final String channel1Filename = this.configuration.getNuFiImage().getChannel1().getName();
		final String channel1Designator = Iterables.get(this.configuration.getChannelDesignators(), 0);
		final String imageFiletype = this.configuration.getImageFiletype();
		final String timestamp = this.formatter.print(this.dateTime);
		final String resultFilename = channel1Filename.replace(channel1Designator, fileIdentificator + "_" + timestamp).replace(imageFiletype, filetype);
		final String destinationFileName = destinationFolder.getAbsolutePath() + "/" + resultFilename;
		final File destination = new File(destinationFileName);
		return destination;
	}
}
