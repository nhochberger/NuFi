package model.serialization;

import hochberger.utilities.application.session.BasicSession;

import java.util.List;

import org.apache.log4j.Logger;

import model.targetdetection.TargetPoint;

public class LoggingTargetPointSerializer implements TargetPointSerializer {
	
	private Logger logger;

	public LoggingTargetPointSerializer(BasicSession session) {
		super();
		this.logger = session.getLogger();
	}

	@Override
	public void serialize(List<TargetPoint> targets) {
		int i = 1;
		for (TargetPoint point : targets) {
			logger.info(String.valueOf(i) + "[" + String.valueOf(point.getxCoordinate()) + ":" + String.valueOf(point.getyCoordinate()) + "]");
		}
	}
}
