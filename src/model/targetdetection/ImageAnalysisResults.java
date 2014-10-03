package model.targetdetection;

import java.awt.Polygon;
import java.io.File;
import java.util.List;

public class ImageAnalysisResults {

	private final File imageFile;
	private final List<Polygon> rois;
	private final List<TargetPoint> nucleoliTargets;
	private final List<TargetPoint> nucleiTargets;

	public ImageAnalysisResults(final File imageFile, final List<Polygon> rois, final List<TargetPoint> nucleoliTargets, final List<TargetPoint> nucleiTargets) {
		super();
		this.imageFile = imageFile;
		this.rois = rois;
		this.nucleoliTargets = nucleoliTargets;
		this.nucleiTargets = nucleiTargets;
	}

	public File getImageFile() {
		return this.imageFile;
	}

	public List<Polygon> getRois() {
		return this.rois;
	}

	public List<TargetPoint> getNucleoliTargets() {
		return this.nucleoliTargets;
	}

	public List<TargetPoint> getNucleiTargets() {
		return this.nucleiTargets;
	}
}
