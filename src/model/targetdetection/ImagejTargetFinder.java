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
		ImagePlus channel1 = IJ.openImage(nuFiImage.getChannel1().getAbsolutePath());
		ImagePlus channel3 = IJ.openImage(nuFiImage.getChannel3().getAbsolutePath());
		channel3.getProcessor().setAutoThreshold(Method.Default, true);
		int options = ParticleAnalyzer.ADD_TO_MANAGER | ParticleAnalyzer.IN_SITU_SHOW | ParticleAnalyzer.SHOW_OUTLINES | ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
		int measurements = 0;
		ResultsTable table = new ResultsTable();
		ParticleAnalyzer.setResultsTable(table);
		RoiManager manager = new RoiManager(true);
		ParticleAnalyzer.setRoiManager(manager);
		ParticleAnalyzer analyzer = new ParticleAnalyzer(options, measurements, table, 5000d, 25000d);
		boolean analysisResult = analyzer.analyze(channel3);
		logger().info("Particle analysis result: " + analysisResult);
		logger().info("Particle analysis found " + manager.getCount() + " ROIs.");
		// until here: determining ROIs

		// now: find nucleoli
		for (int i = 0; i < manager.getCount(); i++) {
			manager.select(channel1, i);
			Rectangle roiBounds = manager.getRoi(i).getBounds();
			int xOffset = (int) roiBounds.getX();
			int yOffset = (int) roiBounds.getY();
			ImagePlus workingImage = channel1.duplicate();
			workingImage.updateAndDraw();
			workingImage.getProcessor().setAutoThreshold(Method.MaxEntropy, true);
			int roiOptions = ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
			int roiMeasurements = Measurements.CENTER_OF_MASS;
			ResultsTable roiResults = new ResultsTable();
			ParticleAnalyzer.setResultsTable(roiResults);
			ParticleAnalyzer roiAnalyzer = new ParticleAnalyzer(roiOptions, roiMeasurements, roiResults, 50, 500);
			System.err.println("analysis of roi #" + i + ": " + roiAnalyzer.analyze(workingImage));
			for (int j = 0; j < roiResults.getCounter(); j++) {
				int x = (int) (roiResults.getValue("XM", j) + xOffset);
				int y = (int) (roiResults.getValue("YM", j) + yOffset);
				this.targets.add(new TargetPoint(x, y));
			}
		}
		this.displayFactory.getResultDisplayer().displayResult(channel1, manager.getRoisAsArray(), this.targets);
	}

	@Override
	public List<TargetPoint> getTargets() {
		return this.targets;
	}
}
