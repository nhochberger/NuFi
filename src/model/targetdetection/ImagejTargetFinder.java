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
import ij.process.ImageProcessor;

import java.awt.Rectangle;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
		final int options = ParticleAnalyzer.ADD_TO_MANAGER | ParticleAnalyzer.IN_SITU_SHOW | ParticleAnalyzer.SHOW_OUTLINES;
		final int measurements = 0;
		ParticleAnalyzer.setResultsTable(table);
		ParticleAnalyzer.setRoiManager(manager);
		final ParticleAnalyzer analyzer = new ParticleAnalyzer(options, measurements, table, 5000d, 25000d);
		final boolean analysisResult = analyzer.analyze(channel3);
		logger().info("Particle analysis result: " + analysisResult);
		logger().info("Particle analysis found " + manager.getCount() + " ROIs.");
		return manager;
	}

	private void findTargetsIn(final NuFiImage nuFiImage, final RoiManager rois) {
		final ImagePlus channel1 = IJ.openImage(nuFiImage.getChannel1().getAbsolutePath());
		for (int i = 0; i < rois.getCount(); i++) {
			logger().info("Beginning analysis of roi #" + i);
			rois.select(channel1, i);
			final Rectangle roiBounds = rois.getRoi(i).getBounds();
			final int xOffset = (int) roiBounds.getX();
			final int yOffset = (int) roiBounds.getY();
			final ImagePlus workingImage = channel1.duplicate();
			rois.select(workingImage, i);
			workingImage.getProcessor().setAutoThreshold(Method.MaxEntropy, true);
			final int autoThreshold = workingImage.getProcessor().getAutoThreshold();
			int highestThreshold = (int) (autoThreshold * 1.4);
			highestThreshold = highestThreshold <= 255 ? highestThreshold : 255;
			final int lowestThreshold = (int) (autoThreshold * 0.6);
			final SortedSet<ResultsTable> results = new TreeSet<>(new ResultsTableComparator());
			analyze(workingImage, autoThreshold + 1, highestThreshold, results);
			analyze(workingImage, lowestThreshold, autoThreshold, results);
			final ResultsTable maximumEntries = results.last();
			for (int j = 0; j < maximumEntries.getCounter(); j++) {
				final int x = (int) (maximumEntries.getValue("X", j) + xOffset);
				final int y = (int) (maximumEntries.getValue("Y", j) + yOffset);
				this.targets.add(new TargetPoint(x, y));
			}
		}
	}

	private void analyze(final ImagePlus workingImage, final int lowestThreshold, final int highestThreshold, final SortedSet<ResultsTable> results) {
		for (int threshold = highestThreshold; lowestThreshold <= threshold; threshold -= 5) {
			final ImagePlus duplicate = workingImage.duplicate();
			final ResultsTable roiResults = new ResultsTable();
			final int roiOptions = ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
			final int roiMeasurements = Measurements.CENTROID | Measurements.AREA;
			ParticleAnalyzer.setResultsTable(roiResults);
			final ParticleAnalyzer roiAnalyzer = new ParticleAnalyzer(roiOptions, roiMeasurements, roiResults, 100, 500);
			duplicate.getProcessor().setThreshold(threshold, 255f, ImageProcessor.RED_LUT);
			final boolean roiAnalysisResult = roiAnalyzer.analyze(duplicate);
			logger().info("Result of analysis: " + roiAnalysisResult);
			logger().info("Analysis with threshold " + threshold + " resulted in " + roiResults.getCounter() + " possible targets.");
			results.add(roiResults);
		}
	}

	// private void performInDepthAnalysisOf(final ImagePlus workingImage, final
	// ResultsTable roiResults) {
	// int autoThreshold = workingImage.getProcessor().getAutoThreshold();
	// logger().info("Found no targets with automatic approach (auto threshold: "
	// + autoThreshold + "). Performing indepth analysis.");
	// int highestThreshold = (int) (autoThreshold * 1.3);
	// highestThreshold = highestThreshold <= 255 ? highestThreshold : 255;
	// int lowestThreshold = (int) (autoThreshold * 0.7);
	// logger().info("Beginning indepth analysis with threshold between " +
	// lowestThreshold + " and " + highestThreshold);
	// for (int threshold = highestThreshold; lowestThreshold < threshold;
	// threshold -= 10) {
	// ImagePlus duplicate = workingImage.duplicate();
	// duplicate.getProcessor().setThreshold(threshold, 255f,
	// ImageProcessor.RED_LUT);
	// int roiOptions = ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
	// int roiMeasurements = Measurements.CENTER_OF_MASS;
	// ParticleAnalyzer.setResultsTable(roiResults);
	// ParticleAnalyzer roiAnalyzer = new ParticleAnalyzer(roiOptions,
	// roiMeasurements, roiResults, 25, 500);
	// boolean roiAnalysisResult = roiAnalyzer.analyze(duplicate);
	// logger().info("Result of analysis: " + roiAnalysisResult);
	// System.err.println("count: " + roiResults.getCounter());
	// }
	// }

	// private void analyzeSingleRoi(final ImageProcessor workingImage, final
	// ResultsTable resultTable) {
	// workingImage.getProcessor().setAutoThreshold(Method.MaxEntropy, true);
	// int roiOptions = ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
	// int roiMeasurements = Measurements.CENTER_OF_MASS;
	// ParticleAnalyzer.setResultsTable(resultTable);
	// ParticleAnalyzer roiAnalyzer = new ParticleAnalyzer(roiOptions,
	// roiMeasurements, resultTable, 50, 500);
	// boolean roiAnalysisResult = roiAnalyzer.analyze(workingImage);
	// logger().info("Result of analysis: " + roiAnalysisResult);
	// }

	@Override
	public List<TargetPoint> getTargets() {
		return this.targets;
	}

	public static class ResultsTableComparator implements Comparator<ResultsTable> {

		public ResultsTableComparator() {
			super();
		}

		@Override
		public int compare(final ResultsTable o1, final ResultsTable o2) {
			return o1.getCounter() - o2.getCounter();
		}
	}

	@Override
	public DetailedResults getDetailedResults() {
		return new DetailedResults(this.configuration.getNuFiImage().getChannel1(), RoiToPolygonConverter.convert(this.rois), this.targets);
	}
}
