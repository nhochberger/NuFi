package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import model.targetdetection.ImageAnalysisResults;
import model.targetdetection.TargetPoint;

public class LoggingTargetPointSerializer extends SessionBasedObject implements TargetPointSerializer {

	public LoggingTargetPointSerializer(final BasicSession session) {
		super(session);
	}

	@Override
	public void serialize(final ImageAnalysisResults results) {
		logger().info("Serializing " + results.getNucleoliTargets().size() + " targets");
		TargetPointFormatter formatter = new TargetPointFormatter();
		int i = 1;
		logger().info("# nucleoli targets");
		for (TargetPoint point : results.getNucleoliTargets()) {
			logger().info(formatter.format(i, point));
			i++;
		}
		logger().info("# targets in center of empty nuclei");
		for (TargetPoint point : results.getNucleiTargets()) {
			logger().info(formatter.format(i, point));
			i++;
		}
		logger().info("Serializing finished");
	}
}
