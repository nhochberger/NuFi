package model.targetdetection;

import java.awt.Polygon;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ImageAnalysisResults {

	private final File imageFile;
	private final List<Polygon> rois;
	private final List<TargetPoint> nucleoliTargets;
	private final List<TargetPoint> nucleiTargets;
	// note that the mix ox arrays and lists is pretty ugly
	// and only done like this to avoid transformation of
	// data types during image analysis
	// transformation will be performed here to provide a
	// nice interface
	private final double[] nucleusAreas;
	private final List<double[]> nucleolusAreas;

	public ImageAnalysisResults(final File imageFile, final List<Polygon> rois, final List<TargetPoint> nucleoliTargets, final List<TargetPoint> nucleiTargets, final double[] nucleusAreas,
			final List<double[]> nucleolusAreas) {
		super();
		this.imageFile = imageFile;
		this.rois = rois;
		this.nucleoliTargets = nucleoliTargets;
		this.nucleiTargets = nucleiTargets;
		this.nucleusAreas = nucleusAreas;
		this.nucleolusAreas = nucleolusAreas;
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

	public List<Double> getNucleusAreas() {
		final List<Double> result = new LinkedList<>();
		for (final double area : this.nucleusAreas) {
			result.add(area);
		}
		return result;
	}

	public List<Double> getNucleolusAreas() {
		final List<Double> result = new LinkedList<>();
		for (final double[] areas : this.nucleolusAreas) {
			for (final double area : areas) {
				result.add(area);
			}
		}
		return result;
	}
}
