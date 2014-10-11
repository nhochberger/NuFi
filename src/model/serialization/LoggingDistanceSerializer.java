package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

public class LoggingDistanceSerializer extends SessionBasedObject implements DistanceSerializer {

	public LoggingDistanceSerializer(final BasicSession session) {
		super(session);
	}

	@Override
	public void serializeDistance(final double distance) {
		logger().info("Mean distance between center of nucleus and targeted nucleolus: " + distance + "pixels.");
	}
}
