package controller;

import hochberger.utilities.application.ApplicationProperties;
import hochberger.utilities.application.BasicLoggedApplication;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.eventbus.SimpleEventBus;

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

	public static void main(String[] args) {
		setUpLoggingServices(NuFiApplication.class);
		try {
			ApplicationProperties properties = new ApplicationProperties();
			NuFiApplication application = new NuFiApplication(properties, args);
			application.start();
		} catch (Exception e) {
			getLogger().fatal(e);
		}
	}

	public NuFiApplication(ApplicationProperties properties, String[] args) {
		super();
		this.session = new BasicSession(properties, new SimpleEventBus(),
				getLogger());
		ParameterExtractor.extractFrom(args).to(session);
		this.targetFinder = new RandomTargetFinder(session);
		this.serializer = new FileWritingTargetPointSerializer(session);
	}

	@Override
	public void start() {
		getLogger().info("Application started");
		targetFinder.findTargets();
		serializeTargets();
	}

	private void serializeTargets() {
		try {
			serializer.serialize(targetFinder.getTargets());
		} catch (IOException e) {
			getLogger().error("Unable to serialize targets", e);
		}
	}

	@Override
	public void stop() {
		getLogger().info("Application stopped");
	}
}
