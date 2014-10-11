package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import controller.configuration.NuFiConfiguration;

public class ResultSerializerFactory {
	private static final String SERIALIZATION_MODE_KEY = "serialization.mode";
	private static final String SERIAL_STRING = "serial";

	private enum SerializationMode {
		SERIAL {
			@Override
			public TargetPointSerializer getTargetPointSerializer(final BasicSession session, final NuFiConfiguration configuration) {
				return new FileWritingTargetPointSerializer(session, configuration);
			}

			@Override
			public ResultImageSerializer getResultImageSerializer(final BasicSession session, final NuFiConfiguration configuration) {
				return new FileWritingResultImageSerializer(session, configuration);
			}

			@Override
			public DistanceSerializer getDistanceSerializer(final BasicSession session, final NuFiConfiguration configuration) {
				return new FileWritingDistanceSerializer(session, configuration);
			}
		},
		LOGGED {
			@Override
			public TargetPointSerializer getTargetPointSerializer(final BasicSession session, final NuFiConfiguration configuration) {
				return new LoggingTargetPointSerializer(session);
			}

			@Override
			public ResultImageSerializer getResultImageSerializer(final BasicSession session, final NuFiConfiguration configuration) {
				return new VoidResultImageSerializer();
			}

			@Override
			public DistanceSerializer getDistanceSerializer(final BasicSession session, final NuFiConfiguration configuration) {
				return new LoggingDistanceSerializer(session);
			}
		};

		public abstract TargetPointSerializer getTargetPointSerializer(BasicSession session, NuFiConfiguration configuration);

		public abstract ResultImageSerializer getResultImageSerializer(BasicSession session, NuFiConfiguration configuration);

		public abstract DistanceSerializer getDistanceSerializer(BasicSession session, NuFiConfiguration configuration);
	}

	private final BasicSession session;
	private final NuFiConfiguration configuration;

	public ResultSerializerFactory(final BasicSession session, final NuFiConfiguration configuration) {
		super();
		this.session = session;
		this.configuration = configuration;
		this.session.getLogger().info("Result serialization mode: " + getMode());
	}

	public TargetPointSerializer getTargetPointSerializer() {
		final SerializationMode mode = getMode();
		return mode.getTargetPointSerializer(this.session, this.configuration);
	}

	public ResultImageSerializer getImageSerializer() {
		final SerializationMode mode = getMode();
		return mode.getResultImageSerializer(this.session, this.configuration);
	}

	public DistanceSerializer getDistanceSerializer() {
		return getMode().getDistanceSerializer(this.session, this.configuration);
	}

	public SerializationMode getMode() {
		final String configuredMode = this.session.getProperties().otherProperty(SERIALIZATION_MODE_KEY);
		if (SERIAL_STRING.equalsIgnoreCase(configuredMode)) {
			return SerializationMode.SERIAL;
		}
		return SerializationMode.LOGGED;
	}
}
