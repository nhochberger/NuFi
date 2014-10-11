package model.distancemeasurement;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

public class DistanceMeasurerFactory extends SessionBasedObject {

	private static final String MEASUREMENT_MODE_KEY = "distance.measurement.mode";
	private static final String REAL_STRING = "real";

	public DistanceMeasurerFactory(final BasicSession session) {
		super(session);
	}

	public DistanceMeasurer getDistanceMeasurer() {
		final String mode = session().getProperties().otherProperty(MEASUREMENT_MODE_KEY);
		logger().info("Distance measurement mode: " + mode.toUpperCase());
		if (REAL_STRING.equals(mode)) {
			return new SingleNucleolusDistanceMeasurer(session());
		}
		return new DistanceMeasurer.DoNothingDistanceMeasurer();
	}
}
