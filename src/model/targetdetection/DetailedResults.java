package model.targetdetection;

import java.awt.Polygon;
import java.io.File;
import java.util.List;

public class DetailedResults {

	private final File imageFile;
	private final List<Polygon> rois;
	private final List<TargetPoint> targets;

	public DetailedResults(final File imageFile, final List<Polygon> rois, final List<TargetPoint> targets) {
		super();
		this.imageFile = imageFile;
		this.rois = rois;
		this.targets = targets;
	}

	public File getImageFile() {
		return this.imageFile;
	}

	public List<Polygon> getRois() {
		return this.rois;
	}

	public List<TargetPoint> getTargets() {
		return this.targets;
	}
}
