package controller;

import hochberger.utilities.application.ApplicationProperties;
import hochberger.utilities.application.BasicLoggedApplication;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.eventbus.SimpleEventBus;
import model.serialization.LoggingTargetPointSerializer;
import model.serialization.TargetPointSerializer;
import model.targetdetection.RandomTargetFinder;
import model.targetdetection.TargetFinder;

public class NuFiApplication extends BasicLoggedApplication {
	
	private final BasicSession session;
	private final TargetFinder targetFinder;
	private final TargetPointSerializer serializer;

	public NuFiApplication(ApplicationProperties properties) {
		super();
		this.session = new BasicSession(properties, new SimpleEventBus(), getLogger());
		this.targetFinder = new RandomTargetFinder();
		this.serializer = new LoggingTargetPointSerializer(session);
	}

	public static void main(String[] args) {
		setUpLoggingServices(NuFiApplication.class);
		try {
			ApplicationProperties properties = new ApplicationProperties();
			NuFiApplication application = new NuFiApplication(properties);
			application.start();
		}
		catch(Exception e) {
			getLogger().fatal(e);
		}
	}

	@Override
	public void start() {
		getLogger().info("Application started");
		targetFinder.findTargets();
		serializer.serialize(targetFinder.getTargets());
	}

	@Override
	public void stop() {
		getLogger().info("Application stopped");
	}
	
	public BasicSession session() {
		return session;
	}
}
