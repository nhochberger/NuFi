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

import java.awt.image.BufferedImage;

import model.ResultImageGenerator;
import model.serialization.ResultImageSerializer;
import model.serialization.ResultSerializerFactory;
import model.serialization.StatisticsSerializer;
import model.serialization.TargetPointSerializer;
import model.statistics.ImageStatistics;
import model.statistics.ImageStatisticsFactory;
import model.statistics.StatisticsResult;
import model.targetdetection.ImageAnalysisResults;
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
	private final StatisticsSerializer distanceSerializer;
	private final ResultDisplayer displayer;
	private final ImageStatistics imageStatistics;
	private final Timing applicationTimer;

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
		this.applicationTimer = new Timing();
		this.applicationTimer.start();
		this.session = new BasicSession(properties, new SimpleEventBus(), logger());
		final TargetFinderFactory targetFinderFactory = new TargetFinderFactory(this.session, configuration);
		this.targetFinder = targetFinderFactory.getTargetFinder();
		final ResultSerializerFactory resultSerializerFactory = new ResultSerializerFactory(this.session, configuration);
		final ImageStatisticsFactory distanceMeasurerFactory = new ImageStatisticsFactory(this.session);
		this.imageStatistics = distanceMeasurerFactory.getDistanceMeasurer();
		this.targetPointserializer = resultSerializerFactory.getTargetPointSerializer();
		this.resultImageSerializer = resultSerializerFactory.getImageSerializer();
		this.distanceSerializer = resultSerializerFactory.getDistanceSerializer();
		final ResultDisplayFactory displayFactory = new ResultDisplayFactory(this.session);
		this.displayer = displayFactory.getResultDisplayer();
	}

	@Override
	public void start() {
		logger().info("Application started");
		findTargets();
		postProcessing();
		stop();
	}

	private void postProcessing() {
		logger().info("Beginning post-processing.");
		final ImageAnalysisResults results = this.targetFinder.getResults();
		this.targetPointserializer.serialize(results);
		final StatisticsResult statistics = this.imageStatistics.performMeasurements(results);
		if (this.imageStatistics.isReal()) {
			this.distanceSerializer.serializeStatistics(statistics);
		}
		final BufferedImage resultImage = new ResultImageGenerator().createResultImageFrom(results);
		this.resultImageSerializer.serializeResultImage(resultImage);
		this.displayer.displayResult(resultImage);
		logger().info("Post-processing finished.");
	}

	private void findTargets() {
		logger().info("Beginning target detection.");
		final Timing timing = new Timing();
		timing.start();
		this.targetFinder.findTargets();
		timing.stop();
		logger().info("Target detection took " + timing.getMilis() + " miliseconds.");
	}

	@Override
	public void stop() {
		this.applicationTimer.stop();
		logger().info("Application finished. Runtime: " + this.applicationTimer.getMilis() + " miliseconds.");
	}
}
