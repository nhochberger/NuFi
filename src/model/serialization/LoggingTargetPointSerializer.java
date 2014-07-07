package model.serialization;

import hochberger.utilities.application.session.BasicSession;

import java.util.List;

import model.targetdetection.TargetPoint;

import org.apache.log4j.Logger;

public class LoggingTargetPointSerializer implements TargetPointSerializer {

	private Logger logger;

	public LoggingTargetPointSerializer(BasicSession session) {
		super();
		this.logger = session.getLogger();
	}

	@Override
	public void serialize(List<TargetPoint> targets) {
		logger.info("Serializing " + targets.size() + " targets");
		TargetPointFormatter formatter = new TargetPointFormatter();
		int i = 1;
		for (TargetPoint point : targets) {
			logger.info(formatter.format(i, point));
			i++;
		}
		logger.info("Serializing finished");
	}
}
