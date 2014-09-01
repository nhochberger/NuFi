package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.AutoThresholder.Method;
import ij.process.ImageProcessor;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import model.NuFiImage;
import model.display.ResultDisplayFactory;
import controller.configuration.NuFiConfiguration;

public class ImagejTargetFinder extends SessionBasedObject implements TargetFinder {

	private final NuFiConfiguration configuration;
	private final List<TargetPoint> targets;
	private final ResultDisplayFactory displayFactory;

	public ImagejTargetFinder(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
		this.targets = new LinkedList<>();
		this.displayFactory = new ResultDisplayFactory(session);
	}

	@Override
	public void findTargets() {
		NuFiImage nuFiImage = this.configuration.getNuFiImage();
		RoiManager rois = findRoisIn(nuFiImage);
		ImagePlus channel1 = findTargetsIn(nuFiImage, rois);
		this.displayFactory.getResultDisplayer().displayResult(channel1, rois.getRoisAsArray(), this.targets);
	}

	private RoiManager findRoisIn(final NuFiImage image) {
		ImagePlus channel3 = IJ.openImage(image.getChannel3().getAbsolutePath());
		channel3.getProcessor().setAutoThreshold(Method.Default, true);
		RoiManager manager = new RoiManager(true);
		ResultsTable table = new ResultsTable();
		int options = ParticleAnalyzer.ADD_TO_MANAGER | ParticleAnalyzer.IN_SITU_SHOW | ParticleAnalyzer.SHOW_OUTLINES | ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
		int measurements = 0;
		ParticleAnalyzer.setResultsTable(table);
		ParticleAnalyzer.setRoiManager(manager);
		ParticleAnalyzer analyzer = new ParticleAnalyzer(options, measurements, table, 5000d, 25000d);
		boolean analysisResult = analyzer.analyze(channel3);
		logger().info("Particle analysis result: " + analysisResult);
		logger().info("Particle analysis found " + manager.getCount() + " ROIs.");
		return manager;
	}

	private ImagePlus findTargetsIn(final NuFiImage nuFiImage, final RoiManager rois) {
		ImagePlus channel1 = IJ.openImage(nuFiImage.getChannel1().getAbsolutePath());
		for (int i = 0; i < rois.getCount(); i++) {
			logger().info("Beginning analysis of roi #" + i);
			rois.select(channel1, i);
			Rectangle roiBounds = rois.getRoi(i).getBounds();
			int xOffset = (int) roiBounds.getX();
			int yOffset = (int) roiBounds.getY();
			ImagePlus workingImage = channel1.duplicate();
			ResultsTable roiResults = new ResultsTable();
			analyzeSingleRoi(workingImage, roiResults);
			int roiParticleCounter = roiResults.getCounter();
			logger().info("Found " + roiParticleCounter + " targets.");
			if (2 > roiParticleCounter) {
				performInDepthAnalysisOf(workingImage, roiResults);
			}
			for (int j = 0; j < roiResults.getCounter(); j++) {
				int x = (int) (roiResults.getValue("XM", j) + xOffset);
				int y = (int) (roiResults.getValue("YM", j) + yOffset);
				this.targets.add(new TargetPoint(x, y));
			}
		}
		return channel1;
	}

	private void performInDepthAnalysisOf(final ImagePlus workingImage, final ResultsTable roiResults) {
		int autoThreshold = workingImage.getProcessor().getAutoThreshold();
		logger().info("Found no targets with automatic approach (auto threshold: " + autoThreshold + "). Performing indepth analysis.");
		int highestThreshold = (int) (autoThreshold * 1.3);
		highestThreshold = highestThreshold <= 255 ? highestThreshold : 255;
		int lowestThreshold = (int) (autoThreshold * 0.7);
		logger().info("Beginning indepth analysis with threshold between " + lowestThreshold + " and " + highestThreshold);
		for (int threshold = highestThreshold; lowestThreshold < threshold; threshold -= 10) {
			ImagePlus duplicate = workingImage.duplicate();
			duplicate.getProcessor().setThreshold(threshold, 255f, ImageProcessor.RED_LUT);
			int roiOptions = ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
			int roiMeasurements = Measurements.CENTER_OF_MASS;
			ParticleAnalyzer.setResultsTable(roiResults);
			ParticleAnalyzer roiAnalyzer = new ParticleAnalyzer(roiOptions, roiMeasurements, roiResults, 25, 500);
			boolean roiAnalysisResult = roiAnalyzer.analyze(duplicate);
			logger().info("Result of analysis: " + roiAnalysisResult);
			System.err.println("count: " + roiResults.getCounter());
		}
	}

	private void analyzeSingleRoi(final ImagePlus workingImage, final ResultsTable resultTable) {
		workingImage.getProcessor().setAutoThreshold(Method.MaxEntropy, true);
		int roiOptions = ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
		int roiMeasurements = Measurements.CENTER_OF_MASS;
		ParticleAnalyzer.setResultsTable(resultTable);
		ParticleAnalyzer roiAnalyzer = new ParticleAnalyzer(roiOptions, roiMeasurements, resultTable, 50, 500);
		boolean roiAnalysisResult = roiAnalyzer.analyze(workingImage);
		logger().info("Result of analysis: " + roiAnalysisResult);
	}

	@Override
	public List<TargetPoint> getTargets() {
		return this.targets;
	}
}
