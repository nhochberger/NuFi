package model.targetdetection;

import ij.gui.Roi;

import java.awt.Polygon;
import java.util.LinkedList;
import java.util.List;

public class RoiToPolygonConverter {

	private RoiToPolygonConverter() {
		super();
	}

	public static List<Polygon> convert(final Roi[] rois) {
		final List<Polygon> result = new LinkedList<>();
		for (final Roi roi : rois) {
			result.add(roi.getPolygon());
		}
		return result;
	}

}
