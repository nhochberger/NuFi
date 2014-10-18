package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.exceptions.NotYetImplementedException;
import controller.configuration.NuFiConfiguration;

public class TargetFinderFactory {

	private static final String DETECTION_MODE_KEY = "detection.mode";
	private static final String SIMPLE_STRING = "simple";
	private static final String IMPROVED_STRING = "improved";
	private static final String DCMETHOD_STRING = "dcmethod";

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
		final String configuredMode = this.session.getProperties().otherProperty(DETECTION_MODE_KEY).toLowerCase();
		for (DetectionMode mode : DetectionMode.values()) {
			if (mode.name().toLowerCase().equals(configuredMode)) {
				return mode;
			}
		}
		return DetectionMode.RANDOM;
	}
}
