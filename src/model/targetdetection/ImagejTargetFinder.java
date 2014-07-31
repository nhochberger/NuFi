package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import ij.plugin.filter.MaximumFinder;
import ij.process.ImageProcessor;

import java.awt.Polygon;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Iterables;

import controller.configuration.NuFiConfiguration;

public class ImagejTargetFinder extends SessionBasedObject implements TargetFinder {

	private final NuFiConfiguration configuration;
	private final List<TargetPoint> targets;

	public ImagejTargetFinder(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
		this.targets = new LinkedList<>();
	}

	@Override
	public void findTargets() {
		Iterable<File> sourceFiles = this.configuration.getSourceFiles();
		ImagePlus channel1 = IJ.openImage(Iterables.get(sourceFiles, 0).getAbsolutePath());
		channel1.getProcessor().smooth();
		channel1.getProcessor().smooth();
		ContrastEnhancer enhancer = new ContrastEnhancer();
		enhancer.setNormalize(true);
		enhancer.stretchHistogram(channel1, 3);

		MaximumFinder maxFinder = new MaximumFinder();
		ImageProcessor processor = channel1.getProcessor();
		Polygon polygon = maxFinder.getMaxima(processor, 20, true);
		for (int i = 0; i < polygon.xpoints.length; i++) {
			this.targets.add(new TargetPoint(polygon.xpoints[i], polygon.ypoints[i]));
		}
		channel1.show("channel1");
	}

	@Override
	public List<TargetPoint> getTargets() {
		return this.targets;
	}
}
