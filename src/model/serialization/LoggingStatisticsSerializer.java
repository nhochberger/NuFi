package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.text.Text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import model.statistics.StatisticsResult;

public class LoggingStatisticsSerializer extends SessionBasedObject implements StatisticsSerializer {

	public LoggingStatisticsSerializer(final BasicSession session) {
		super(session);
	}

	@Override
	public void serializeStatistics(final StatisticsResult statistics) {
		logger().info("Number of detected nuclei: " + statistics.nucleiCount());
		logger().info("Number of targets: " + statistics.targetCount());
		DecimalFormat doubleFormatter = new DecimalFormat("0.00");
		doubleFormatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
		logger().info(doubleFormatter.format(statistics.nucleoliNucleioliRatio() * 100) + "% of the detected nulcei contain targets.");
		logger().info("Distances: " + Text.fromIterable(statistics.distances()));
		logger().info("Mean distance between center of nucleus and targeted nucleolus: " + doubleFormatter.format(statistics.meanDistance()) + " pixels.");
	}
}
