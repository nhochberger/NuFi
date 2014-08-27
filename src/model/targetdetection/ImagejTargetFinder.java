package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.ContrastEnhancer;
import ij.plugin.filter.MaximumFinder;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.AutoThresholder.Method;

import java.awt.Polygon;
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
		ContrastEnhancer contrastEnhancer = new ContrastEnhancer();
		contrastEnhancer.stretchHistogram(channel3.getProcessor(), 0.3);
		channel3.getProcessor().setAutoThreshold(Method.Default, true);
		int options = ParticleAnalyzer.ADD_TO_MANAGER | ParticleAnalyzer.IN_SITU_SHOW | ParticleAnalyzer.SHOW_OUTLINES | ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
		int measurements = Measurements.AREA | Measurements.CIRCULARITY;
		ResultsTable table = new ResultsTable();
		ParticleAnalyzer.setResultsTable(table);
		RoiManager manager = new RoiManager(true);
		ParticleAnalyzer.setRoiManager(manager);
		ParticleAnalyzer analyzer = new ParticleAnalyzer(options, measurements, table, 6400d, 15000d);
		boolean analysisResult = analyzer.analyze(channel3);
		logger().info("Particle analysis result: " + analysisResult);
		logger().info("Particle analysis found " + manager.getCount() + " ROIs.");
		MaximumFinder finder = new MaximumFinder();
		for (int i = 0; i < manager.getCount(); i++) {
			manager.select(channel1, i);
			Polygon maxima = finder.getMaxima(channel1.getProcessor(), 25, true);
			for (int j = 0; j < maxima.npoints; j++) {
				this.targets.add(new TargetPoint(maxima.xpoints[j], maxima.ypoints[j]));
			}
		}
		this.displayFactory.getResultDisplayer().displayResult(channel1, manager.getRoisAsArray(), this.targets);
	}

	@Override
	public List<TargetPoint> getTargets() {
		return this.targets;
	}
}
