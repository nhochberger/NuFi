package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

import java.util.List;

import model.targetdetection.TargetPoint;

public class LoggingTargetPointSerializer extends SessionBasedObject implements TargetPointSerializer {

	public LoggingTargetPointSerializer(BasicSession session) {
		super(session);
	}

	@Override
	public void serialize(List<TargetPoint> targets) {
		logger().info("Serializing " + targets.size() + " targets");
		TargetPointFormatter formatter = new TargetPointFormatter();
		int i = 1;
		for (TargetPoint point : targets) {
			logger().info(formatter.format(i, point));
			i++;
		}
		logger().info("Serializing finished");
	}
}
