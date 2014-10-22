package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.exceptions.NotYetImplementedException;
import controller.configuration.NuFiConfiguration;

public class TargetFinderFactory extends SessionBasedObject {

	private static final String DETECTION_MODE_KEY = "detection.mode";

	private enum DetectionMode {
		SIMPLE {
			@Override
			public TargetFinder getTargetFinder(final BasicSession session, final NuFiConfiguration configuration) {
				return new ImagejTargetFinder(session, configuration);
			}
		},
		IMPROVED {
			@Override
			public TargetFinder getTargetFinder(final BasicSession session, final NuFiConfiguration configuration) {
				return new ImprovedImageJTargetFinder(session, configuration);
			}
		},
		DCMETHOD {
			@Override
			public TargetFinder getTargetFinder(final BasicSession session, final NuFiConfiguration configuration) {
				throw new NotYetImplementedException();
			}
		},
		RANDOM {
			@Override
			public TargetFinder getTargetFinder(final BasicSession session, final NuFiConfiguration configuration) {
				return new RandomTargetFinder(session, configuration);
			}
		};

		public abstract TargetFinder getTargetFinder(BasicSession session, NuFiConfiguration configuration);

	}

	private final NuFiConfiguration configuration;

	public TargetFinderFactory(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
	}

	public TargetFinder getTargetFinder() {
		final DetectionMode mode = getMode();
		logger().info("Target detection mode: " + mode);
		return mode.getTargetFinder(session(), this.configuration);
	}

	public DetectionMode getMode() {
		final String configuredMode = session().getProperties().otherProperty(DETECTION_MODE_KEY).toLowerCase();
		for (final DetectionMode mode : DetectionMode.values()) {
			if (mode.name().toLowerCase().equals(configuredMode)) {
				return mode;
			}
		}
		logger().error("Unable to map configured detection mode to existing one. Falling back to simple mode.");
		return DetectionMode.SIMPLE;
	}
}
