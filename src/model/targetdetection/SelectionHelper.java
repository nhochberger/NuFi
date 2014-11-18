package model.targetdetection;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.RoiEnlarger;
import ij.process.ImageProcessor;

public class SelectionHelper {

	private static final int BLACK = 0;

	public SelectionHelper() {
		super();
	}

	public Roi shrinkRoi(final Roi roi, final int pixelsToShrink) {
		return RoiEnlarger.enlarge(roi, -pixelsToShrink);
	}

	public ImagePlus deleteSurrouding(final ImagePlus image, final Roi roi, final int xOffset, final int yOffset) {
		final ImagePlus result = image.duplicate();
		result.setRoi(roi);
		final ImageProcessor processor = result.getProcessor();
		for (int x = 0; x < result.getWidth(); x++) {
			for (int y = 0; y < result.getHeight(); y++) {
				if (!roi.contains(x + xOffset, y + yOffset)) {
					processor.putPixel(x, y, BLACK);
				}
			}
		}
		return result;
	}
}
