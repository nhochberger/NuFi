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
		};

		public abstract TargetPointSerializer getTargetPointSerializer(BasicSession session, NuFiConfiguration configuration);

		public abstract ResultImageSerializer getResultImageSerializer(BasicSession session, NuFiConfiguration configuration);
	}

	private final BasicSession session;
	private final NuFiConfiguration configuration;

	public ResultSerializerFactory(final BasicSession session, final NuFiConfiguration configuration) {
		super();
		this.session = session;
		this.configuration = configuration;
		this.session.getLogger().info("Using Serializer: " + getMode());
	}

	public TargetPointSerializer getTargetPointSerializer() {
		SerializationMode mode = getMode();
		return mode.getTargetPointSerializer(this.session, this.configuration);
	}

	public ResultImageSerializer getImageSerializer() {
		SerializationMode mode = getMode();
		return mode.getResultImageSerializer(this.session, this.configuration);
	}

	public SerializationMode getMode() {
		String configuredMode = this.session.getProperties().otherProperty(SERIALIZATION_MODE_KEY);
		if (SERIAL_STRING.equalsIgnoreCase(configuredMode)) {
			return SerializationMode.SERIAL;
		}
		return SerializationMode.LOGGED;
	}
}
