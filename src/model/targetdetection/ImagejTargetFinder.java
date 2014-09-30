package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.AutoThresholder.Method;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import model.NuFiImage;
import controller.configuration.NuFiConfiguration;

public class ImagejTargetFinder extends SessionBasedObject implements TargetFinder {

	private final NuFiConfiguration configuration;
	private final List<TargetPoint> targets;
	private Roi[] rois;

	public ImagejTargetFinder(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
		this.targets = new LinkedList<>();
	}

	@Override
	public void findTargets() {
		final NuFiImage nuFiImage = this.configuration.getNuFiImage();
		final RoiManager roiManager = findRoisIn(nuFiImage);
		this.rois = roiManager.getRoisAsArray();
		findTargetsIn(nuFiImage, roiManager);
	}

	private RoiManager findRoisIn(final NuFiImage image) {
		final ImagePlus channel3 = IJ.openImage(image.getChannel3().getAbsolutePath());
		channel3.getProcessor().setAutoThreshold(Method.Default, true);
		final RoiManager manager = new RoiManager(true);
		final ResultsTable table = new ResultsTable();
		final int options = ParticleAnalyzer.ADD_TO_MANAGER | ParticleAnalyzer.IN_SITU_SHOW;
		final int measurements = 0;
		ParticleAnalyzer.setResultsTable(table);
		ParticleAnalyzer.setRoiManager(manager);
		final ParticleAnalyzer analyzer = new ParticleAnalyzer(options, measurements, table, 5000d, 25000d);
		final boolean analysisResult = analyzer.analyze(channel3);
		logger().info("Particle analysis result: " + analysisResult);
		logger().info("Particle analysis found " + manager.getCount() + " ROIs.");
		return manager;
	}

	private void findTargetsIn(final NuFiImage nuFiImage, final RoiManager roiManager) {
		final ImagePlus channel1 = IJ.openImage(nuFiImage.getChannel1().getAbsolutePath());
		final int minSize = this.configuration.getMinimumNucleolusSize();
		final int maxSize = this.configuration.getMaximumNucleolusSize();
		logger().info("Using minimum nucleolus size of " + minSize + " and maximum nucleolus size of " + maxSize);
		for (int i = 0; i < roiManager.getCount(); i++) {
			logger().info("Beginning analysis of roi " + (i + 1));
			roiManager.select(channel1, i);
			final Rectangle roiBounds = roiManager.getRoi(i).getBounds();
			final int xOffset = (int) roiBounds.getX();
			final int yOffset = (int) roiBounds.getY();
			final ImagePlus workingImage = channel1.duplicate();
			roiManager.select(workingImage, i);
			workingImage.getProcessor().setAutoThreshold(Method.MaxEntropy, true);
			final ResultsTable roiResults = new ResultsTable();
			ParticleAnalyzer.setResultsTable(roiResults);
			final int roiOptions = ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
			final int roiMeasurements = Measurements.CENTROID | Measurements.AREA;
			final ParticleAnalyzer roiAnalyzer = new ParticleAnalyzer(roiOptions, roiMeasurements, roiResults, minSize, maxSize);
			final boolean roiAnalysisResult = roiAnalyzer.analyze(workingImage);
			logger().info("Result of analysis: " + roiAnalysisResult);
			logger().info("Found " + roiResults.getCounter() + " targets");
			for (int j = 0; j < roiResults.getCounter(); j++) {
				final int x = (int) (roiResults.getValue("X", j) + xOffset);
				final int y = (int) (roiResults.getValue("Y", j) + yOffset);
				this.targets.add(new TargetPoint(x, y));
			}
			if (0 == roiResults.getCounter()) {
				this.targets.add(new TargetPoint((int) roiBounds.getCenterX(), (int) roiBounds.getCenterY()));
			}
		}
	}

	@Override
	public List<TargetPoint> getTargets() {
		return this.targets;
	}

	@Override
	public DetailedResults getDetailedResults() {
		return new DetailedResults(this.configuration.getNuFiImage().getChannel1(), RoiToPolygonConverter.convert(this.rois), this.targets);
	}
}
