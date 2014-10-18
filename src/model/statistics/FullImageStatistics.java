package model.statistics;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

import java.awt.Polygon;
import java.util.LinkedList;
import java.util.List;

import model.targetdetection.ImageAnalysisResults;
import model.targetdetection.TargetPoint;

public class FullImageStatistics extends SessionBasedObject implements ImageStatistics {

	public FullImageStatistics(final BasicSession session) {
		super(session);
	}

	@Override
	public double determinMeanDistance(final ImageAnalysisResults results) {
		final List<TargetPoint> targets = new LinkedList<>(results.getNucleoliTargets());
		double sumOfDistances = 0;
		for (final Polygon roi : results.getRois()) {
			for (final TargetPoint target : targets) {
				if (roi.contains(target.getxCoordinate(), target.getyCoordinate())) {
					final double roiCenterX = roi.getBounds().getCenterX();
					final double roiCenterY = roi.getBounds().getCenterY();
					sumOfDistances += calculateDistance(roiCenterX, roiCenterY, target.getxCoordinate(), target.getyCoordinate());

				}
			}
		}
		final double result = sumOfDistances / targets.size();
		logger().info("Mean distance: " + result);
		return result;
	}

	private double calculateDistance(final double x1, final double y1, final double x2, final double y2) {
		final double deltaX = Math.abs(x1 - x2);
		final double deltaY = Math.abs(y1 - y2);
		final double result = Math.sqrt(square(deltaX) + square(deltaY));
		logger().info("Calculated distance between (" + x1 + "|" + y1 + ") and (" + x2 + "|" + y2 + "): " + result);
		return result;
	}

	private double square(final double value) {
		return value * value;
	}

	@Override
	public boolean isReal() {
		return true;
	}
}
