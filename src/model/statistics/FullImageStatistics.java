package model.statistics;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

import java.awt.Polygon;
import java.util.LinkedList;
import java.util.List;

import model.targetdetection.ImageAnalysisResults;
import model.targetdetection.TargetPoint;

import com.google.common.collect.Iterables;

public class FullImageStatistics extends SessionBasedObject implements ImageStatistics {

	public FullImageStatistics(final BasicSession session) {
		super(session);
	}

	public Iterable<Double> determineDistances(final ImageAnalysisResults results) {
		final List<TargetPoint> targets = new LinkedList<>(results.getNucleoliTargets());
		final List<Double> result = new LinkedList<>();
		for (final Polygon roi : results.getRois()) {
			for (final TargetPoint target : targets) {
				if (roi.contains(target.getxCoordinate(), target.getyCoordinate())) {
					final double roiCenterX = roi.getBounds().getCenterX();
					final double roiCenterY = roi.getBounds().getCenterY();
					result.add(calculateDistance(roiCenterX, roiCenterY, target.getxCoordinate(), target.getyCoordinate()));
				}
			}
		}
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

	private double meanValue(final Iterable<Double> values) {
		final int size = Iterables.size(values);
		if (0 == size) {
			return 0;
		}
		double sum = 0;
		for (final Double value : values) {
			sum += value;
		}
		return sum / size;
	}

	@Override
	public StatisticsResult performMeasurements(final ImageAnalysisResults imageAnalysisResults) {
		final int nucleiCount = imageAnalysisResults.getRois().size();
		final int nucleoliCount = imageAnalysisResults.getNucleoliTargets().size();
		final Iterable<Double> distances = determineDistances(imageAnalysisResults);
		final double meanDistance = meanValue(distances);
		final Iterable<Double> nucleusAreas = imageAnalysisResults.getNucleusAreas();
		final double meanNucleusArea = meanValue(nucleusAreas);
		final Iterable<Double> nucleolusAreas = imageAnalysisResults.getNucleolusAreas();
		final double meanNucleolusAreas = meanValue(nucleolusAreas);
		return new RealStatisticsResult(nucleiCount, nucleoliCount, distances, meanDistance, nucleusAreas, meanNucleusArea, nucleolusAreas, meanNucleolusAreas);
	}

	@Override
	public boolean isReal() {
		return true;
	}

}
