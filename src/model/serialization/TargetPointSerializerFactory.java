package model.serialization;

import hochberger.utilities.application.session.BasicSession;
import controller.configuration.NuFiConfiguration;

public class TargetPointSerializerFactory {
	private static final String SERIALIZATION_MODE_KEY = "serialization.mode";
	private static final String SERIAL_STRING = "serial";

	private enum SerializationMode {
		SERIAL {
			@Override
			public TargetPointSerializer getSerializer(final BasicSession session, final NuFiConfiguration configuration) {
				return new LoggingTargetPointSerializer(session);
			}
		},
		LOGGED {
			@Override
			public TargetPointSerializer getSerializer(final BasicSession session, final NuFiConfiguration configuration) {
				return new FileWritingTargetPointSerializer(session, configuration);
			}
		};

		public abstract TargetPointSerializer getSerializer(BasicSession session, NuFiConfiguration configuration);

	}

	private final BasicSession session;
	private final NuFiConfiguration configuration;

	public TargetPointSerializerFactory(final BasicSession session, final NuFiConfiguration configuration) {
		super();
		this.session = session;
		this.configuration = configuration;
	}

	public TargetPointSerializer getTargetFinder() {
		SerializationMode mode = getMode();
		this.session.getLogger().info("Using Serializer: " + mode);
		return mode.getSerializer(this.session, this.configuration);
	}

	public SerializationMode getMode() {
		String configuredMode = this.session.getProperties().otherProperty(SERIALIZATION_MODE_KEY);
		if (SERIAL_STRING.equalsIgnoreCase(configuredMode)) {
			return SerializationMode.SERIAL;
		}
		return SerializationMode.LOGGED;
	}
}
