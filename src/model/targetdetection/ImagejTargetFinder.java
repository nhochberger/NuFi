package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import ij.plugin.filter.MaximumFinder;
import ij.process.ImageProcessor;

import java.awt.Polygon;
import java.util.LinkedList;
import java.util.List;

import model.NuFiImage;
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
		// System.err.println(nuFiImage.getTimestamp());
		channel1.getProcessor().smooth();
		channel1.getProcessor().smooth();
		ContrastEnhancer enhancer = new ContrastEnhancer();
		enhancer.setNormalize(true);
		enhancer.stretchHistogram(channel1, 3);

		MaximumFinder maxFinder = new MaximumFinder();
		ImageProcessor processor = channel1.getProcessor();
		logger().info("MaximumFinder starts");
		Polygon polygon = maxFinder.getMaxima(processor, 20, true);
		logger().info("MaximumFinder finished");
		for (int i = 0; i < polygon.xpoints.length; i++) {
			this.targets.add(new TargetPoint(polygon.xpoints[i], polygon.ypoints[i]));
		}
		logger().info("Found " + this.targets.size() + " targets.");
		this.displayFactory.getResultDisplayer().displayResult(channel1, polygon);
	}

	@Override
	public List<TargetPoint> getTargets() {
		return this.targets;
	}
}
