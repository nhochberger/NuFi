package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

public class LoggingStatisticsSerializer extends SessionBasedObject implements StatisticsSerializer {

	public LoggingStatisticsSerializer(final BasicSession session) {
		super(session);
	}

	@Override
	public void serializeStatistics(final double distance) {
		logger().info("Mean distance between center of nucleus and targeted nucleolus: " + distance + "pixels.");
	}
}
