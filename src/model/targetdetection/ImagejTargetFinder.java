package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.MaximumFinder;
import ij.process.ImageProcessor;

import java.awt.Polygon;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

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
		for (File image : this.configuration.getSourceFiles()) {
			ImagePlus imagePlus = IJ.openImage(image.getAbsolutePath());
			MaximumFinder maxFinder = new MaximumFinder();
			ImageProcessor processor = imagePlus.getProcessor();
			Polygon polygon = maxFinder.getMaxima(processor, 100, true);
			for (int i = 0; i < polygon.xpoints.length; i++) {
				this.targets.add(new TargetPoint(polygon.xpoints[i], polygon.ypoints[i]));
			}
			break;
		}
	}

	@Override
	public List<TargetPoint> getTargets() {
		return this.targets;
	}
}
