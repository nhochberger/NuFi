package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.AutoThresholder.Method;
import ij.process.ImageProcessor;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import model.NuFiImage;
import controller.configuration.NuFiConfiguration;

public class ImprovedImageJTargetFinder extends SessionBasedObject implements TargetFinder {

	private static final String AREA = "Area";

	private final static String DIVIDE = "div";

	private final NuFiConfiguration configuration;
	private final List<TargetPoint> nucleoliTargets;
	private final List<TargetPoint> nucleiTargets;
	private Roi[] rois;
	private double[] nucleusAreas;
	private final List<double[]> nucleolusAreas;
	private final ImageCalculator calculator;
	private final SelectionHelper selectionHelper;

	public ImprovedImageJTargetFinder(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
		this.nucleoliTargets = new LinkedList<>();
		this.nucleiTargets = new LinkedList<>();
		this.calculator = new ImageCalculator();
		this.nucleolusAreas = new LinkedList<>();
		this.selectionHelper = new SelectionHelper();
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
		final ImagePlus background = channel3.duplicate();
		background.getProcessor().blurGaussian(this.configuration.getNucleusBackgroundBlur());
		this.calculator.run(DIVIDE, channel3, background);
		channel3.getProcessor().multiply(178d);
		channel3.getProcessor().blurGaussian(this.configuration.getNucleusThresholdingBlur());
		channel3.getProcessor().setAutoThreshold(Method.Default, true);
		final RoiManager manager = new RoiManager(true);
		final ResultsTable table = new ResultsTable();
		final int options = ParticleAnalyzer.ADD_TO_MANAGER | ParticleAnalyzer.IN_SITU_SHOW;
		final int measurements = 0;
		ParticleAnalyzer.setResultsTable(table);
		ParticleAnalyzer.setRoiManager(manager);
		final ParticleAnalyzer analyzer = new ParticleAnalyzer(options, measurements, table, this.configuration.getMinimumNucleusSize(), this.configuration.getMaximumNucleusSize(),
				this.configuration.getNucleusMinCircularity(), 1);
		final boolean analysisResult = analyzer.analyze(channel3);
		logger().info("Particle analysis result: " + analysisResult);
		logger().info("Particle analysis found " + manager.getCount() + " ROIs.");
		this.nucleusAreas = table.getColumnAsDoubles(table.getColumnIndex(AREA));
		return manager;
	}

	private void findTargetsIn(final NuFiImage nuFiImage, final RoiManager roiManager) {
		final ImagePlus channel1 = IJ.openImage(nuFiImage.getChannel1().getAbsolutePath());
		final int minSize = this.configuration.getMinimumNucleolusSize();
		final int maxSize = this.configuration.getMaximumNucleolusSize();
		logger().info("Using minimum nucleolus size of " + minSize + " and maximum nucleolus size of " + maxSize);
		for (int i = 0; i < roiManager.getCount(); i++) {
			performAnalysisOfRoi(i, roiManager, channel1, minSize, maxSize);
		}
	}

	private void performAnalysisOfRoi(final int indexOfRoi, final RoiManager roiManager, final ImagePlus channel1, final int minSize, final int maxSize) {
		logger().info("Beginning analysis of roi " + (indexOfRoi + 1));
		roiManager.select(channel1, indexOfRoi);
		final Rectangle roiBounds = roiManager.getRoi(indexOfRoi).getBounds();
		final int xOffset = (int) roiBounds.getX();
		final int yOffset = (int) roiBounds.getY();
		final ImagePlus workingImage = this.selectionHelper.deleteSurrouding(channel1.duplicate(), roiManager.getRoi(indexOfRoi), xOffset, yOffset);
		roiManager.select(workingImage, indexOfRoi);
		workingImage.getProcessor().blurGaussian(this.configuration.getNucleolusThresholdingBlur());
		workingImage.getProcessor().setAutoThreshold(Method.MaxEntropy, true);
		final ResultsTable roiResults = new ResultsTable();
		ParticleAnalyzer.setResultsTable(roiResults);
		final int roiOptions = ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
		final int roiMeasurements = Measurements.CENTROID | Measurements.AREA;
		final ParticleAnalyzer roiAnalyzer = new ParticleAnalyzer(roiOptions, roiMeasurements, roiResults, minSize, maxSize);
		final boolean roiAnalysisResult = roiAnalyzer.analyze(workingImage);
		logger().info("Result of analysis: " + roiAnalysisResult);
		logger().info("Found " + roiResults.getCounter() + " targets using basic analysis (threshold was " + workingImage.getProcessor().getAutoThreshold() + ").");
		if (0 == roiResults.getCounter()) {
			performInDepthAnalysis(workingImage, roiResults, roiAnalyzer);
		}
		if (0 == roiResults.getCounter()) {
			setCenterOfNucleusAsTarget(indexOfRoi, roiBounds);
			return;
		}
		this.nucleolusAreas.add(roiResults.getColumnAsDoubles(roiResults.getColumnIndex(AREA)));
		int indexOfLargest = 0;
		for (int j = 0; j < roiResults.getCounter(); j++) {
			if (roiResults.getValue(AREA, indexOfLargest) < roiResults.getValue(AREA, j)) {
				indexOfLargest = j;
			}
		}
		logger().info("Found largest target with an area of " + roiResults.getValue(AREA, indexOfLargest));
		final double largestTargetX = roiResults.getValue("X", indexOfLargest) + xOffset;
		final double largestTargetY = roiResults.getValue("Y", indexOfLargest) + yOffset;
		if (!roiManager.getRoi(indexOfRoi).getPolygon().contains(largestTargetX, largestTargetY)) {
			logger().error("Found target was outside nucleus. Falling back to center of nucleus. (ROI #" + (indexOfRoi + 1) + ")");
			setCenterOfNucleusAsTarget(indexOfRoi, roiBounds);
			return;
		}
		final int x = (int) (largestTargetX);
		final int y = (int) (largestTargetY);
		this.nucleoliTargets.add(new TargetPoint(x, y));
	}

	private void setCenterOfNucleusAsTarget(final int i, final Rectangle roiBounds) {
		logger().info("Found no targets in ROI " + i + ". Using center of ROI as target.");
		this.nucleiTargets.add(new TargetPoint((int) roiBounds.getCenterX(), (int) roiBounds.getCenterY()));
	}

	private void performInDepthAnalysis(final ImagePlus workingImage, final ResultsTable roiResults, final ParticleAnalyzer roiAnalyzer) {
		logger().info("Performing in-depth analysis");
		final int autoThreshold = workingImage.getProcessor().getAutoThreshold();
		final int minThreshold = minThreshold(autoThreshold);
		final int maxThreshold = maxThreshold(autoThreshold);
		logger().info("Threshold range: [" + minThreshold + "; " + maxThreshold + "]");
		for (int threshold = maxThreshold; threshold >= minThreshold; threshold--) {
			workingImage.getProcessor().setThreshold(threshold, 255f, ImageProcessor.RED_LUT);
			roiAnalyzer.analyze(workingImage);
			if (0 < roiResults.getCounter()) {
				logger().info("Found target with threshold: " + threshold + ". Exiting in-depth analysis.");
				break;
			}
		}
		logger().info("In-depth analysis finished. Found " + roiResults.getCounter() + " targets.");
	}

	private int maxThreshold(final int autoThreshold) {
		final int result = autoThreshold + this.configuration.getInDepthRange();
		if (255 < result) {
			return 255;
		}
		return result;
	}

	private int minThreshold(final int autoThreshold) {
		final int result = autoThreshold - this.configuration.getInDepthRange();
		if (0 > result) {
			return 0;
		}
		return result;
	}

	@Override
	public ImageAnalysisResults getResults() {
		return new ImageAnalysisResults(this.configuration.getNuFiImage().getChannel1(), RoiToPolygonConverter.convert(this.rois), this.nucleoliTargets, this.nucleiTargets, this.nucleusAreas,
				this.nucleolusAreas);
	}
}
