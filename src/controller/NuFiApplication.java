package controller;

import hochberger.utilities.application.ApplicationProperties;
import hochberger.utilities.application.BasicLoggedApplication;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.eventbus.SimpleEventBus;
import hochberger.utilities.timing.Timing;

import java.io.IOException;

import model.ParameterExtractor;
import model.serialization.FileWritingTargetPointSerializer;
import model.serialization.TargetPointSerializer;
import model.targetdetection.RandomTargetFinder;
import model.targetdetection.TargetFinder;

public class NuFiApplication extends BasicLoggedApplication {

	private final BasicSession session;
	private final TargetFinder targetFinder;
	private final TargetPointSerializer serializer;

	public static void main(final String[] args) {
		setUpLoggingServices(NuFiApplication.class);
		try {
			final ApplicationProperties properties = new ApplicationProperties();
			final NuFiApplication application = new NuFiApplication(properties, args);
			application.start();
		} catch (final Exception e) {
			getLogger().fatal(e.getCause(), e);
		}
	}

	public NuFiApplication(final ApplicationProperties properties, final String[] args) {
		super();
		this.session = new BasicSession(properties, new SimpleEventBus(), getLogger());
		ParameterExtractor.extractFrom(args).to(this.session);
		this.targetFinder = new RandomTargetFinder(this.session);
		this.serializer = new FileWritingTargetPointSerializer(this.session);
	}

	@Override
	public void start() {
		getLogger().info("Application started");
		Timing timing = new Timing();
		timing.start();
		this.targetFinder.findTargets();
		timing.stop();
		getLogger().info("Target detetion took " + timing.getMilis() + " miliseconds.");
		serializeTargets();
	}

	private void serializeTargets() {
		try {
			this.serializer.serialize(this.targetFinder.getTargets());
		} catch (final IOException e) {
			getLogger().error("Unable to serialize targets", e);
		}
	}

	@Override
	public void stop() {
		getLogger().info("Application stopped");
	}
}
