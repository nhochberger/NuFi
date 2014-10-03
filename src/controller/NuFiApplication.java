package controller;

import hochberger.utilities.application.ApplicationProperties;
import hochberger.utilities.application.BasicLoggedApplication;
import hochberger.utilities.application.parameter.ParameterException;
import hochberger.utilities.application.parameter.checker.ParameterChecker;
import hochberger.utilities.application.parameter.checker.aspects.ExistingFiles;
import hochberger.utilities.application.parameter.checker.aspects.SingleParameter;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.eventbus.SimpleEventBus;
import hochberger.utilities.text.Text;
import hochberger.utilities.timing.Timing;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import model.ResultImageGenerator;
import model.serialization.ResultImageSerializer;
import model.serialization.ResultSerializerFactory;
import model.serialization.TargetPointSerializer;
import model.targetdetection.TargetFinder;
import model.targetdetection.TargetFinderFactory;
import view.ResultDisplayFactory;
import view.ResultDisplayer;
import controller.configuration.NuFiConfiguration;

public class NuFiApplication extends BasicLoggedApplication {

	private final BasicSession session;
	private final TargetFinder targetFinder;
	private final TargetPointSerializer targetPointserializer;
	private final ResultImageSerializer resultImageSerializer;
	private final ResultDisplayer displayer;

	public static void main(final String[] args) {
		setUpLoggingServices(NuFiApplication.class);
		try {
			getLogger().info("Preparing application start");
			checkParams(args);
			final NuFiConfiguration configuration = NuFiConfiguration.createFrom(args[0]);
			final ApplicationProperties properties = new ApplicationProperties();
			final NuFiApplication application = new NuFiApplication(properties, configuration);
			application.start();
		} catch (final Exception e) {
			getLogger().fatal(e.getCause(), e);
		}
	}

	private static void checkParams(final String[] args) throws ParameterException {
		final ParameterChecker paramChecker = new ParameterChecker(args);
		paramChecker.addParameterAspect(new SingleParameter());
		paramChecker.addParameterAspect(new ExistingFiles());
		final boolean parameterResult = paramChecker.check();
		final String resultDescription = Text.fromIterable(paramChecker.getResultDescription(), Text.newLine());
		if (!parameterResult) {
			throw new ParameterException("Check application arguments:\n" + resultDescription);
		}
		getLogger().info(resultDescription);
	}

	public NuFiApplication(final ApplicationProperties properties, final NuFiConfiguration configuration) {
		super();
		this.session = new BasicSession(properties, new SimpleEventBus(), getLogger());
		final TargetFinderFactory targetFinderFactory = new TargetFinderFactory(this.session, configuration);
		this.targetFinder = targetFinderFactory.getTargetFinder();
		final ResultSerializerFactory resultSerializerFactory = new ResultSerializerFactory(this.session, configuration);
		this.targetPointserializer = resultSerializerFactory.getTargetPointSerializer();
		this.resultImageSerializer = resultSerializerFactory.getImageSerializer();
		final ResultDisplayFactory displayFactory = new ResultDisplayFactory(this.session);
		this.displayer = displayFactory.getResultDisplayer();
	}

	@Override
	public void start() {
		getLogger().info("Application started");
		findTargets();
		postProcessing();
	}

	private void postProcessing() {
		serializeTargets();
		BufferedImage resultImage = new ResultImageGenerator().createResultImageFrom(this.targetFinder.getResults());
		displayResults(resultImage);
		this.resultImageSerializer.serializeResultImage(resultImage);
	}

	private void findTargets() {
		final Timing timing = new Timing();
		timing.start();
		this.targetFinder.findTargets();
		timing.stop();
		getLogger().info("Target detection took " + timing.getMilis() + " miliseconds.");
	}

	private void displayResults(final Image resultImage) {
		this.displayer.displayResult(resultImage);
	}

	private void serializeTargets() {
		try {
			this.targetPointserializer.serialize(this.targetFinder.getResults());
		} catch (final IOException e) {
			getLogger().error("Unable to serialize targets", e);
		}
	}

	@Override
	public void stop() {
		getLogger().info("Application stopped");
	}
}
