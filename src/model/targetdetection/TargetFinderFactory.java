package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import controller.configuration.NuFiConfiguration;

public class TargetFinderFactory {

	private static final String DETECTION_MODE_KEY = "detection.mode";
	private static final String REAL_STRING = "real";

	private enum DetectionMode {
		REAL {
			@Override
			public TargetFinder getTargetFinder(final BasicSession session, final NuFiConfiguration configuration) {
				return new ImagejTargetFinder(session, configuration);
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

	private final BasicSession session;
	private final NuFiConfiguration configuration;

	public TargetFinderFactory(final BasicSession session, final NuFiConfiguration configuration) {
		super();
		this.session = session;
		this.configuration = configuration;
	}

	public TargetFinder getTargetFinder() {
		final DetectionMode mode = getMode();
		this.session.getLogger().info("Target detection mode: " + mode);
		return mode.getTargetFinder(this.session, this.configuration);
	}

	public DetectionMode getMode() {
		final String configuredMode = this.session.getProperties().otherProperty(DETECTION_MODE_KEY);
		if (REAL_STRING.equalsIgnoreCase(configuredMode)) {
			return DetectionMode.REAL;
		}
		return DetectionMode.RANDOM;
	}
}
