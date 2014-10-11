package model.distancemeasurement;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

import java.awt.Polygon;
import java.util.LinkedList;
import java.util.List;

import model.targetdetection.ImageAnalysisResults;
import model.targetdetection.TargetPoint;
import controller.configuration.NuFiConfiguration;

public class SingleNucleolusDistanceMeasurer extends SessionBasedObject implements DistanceMeasurer {

	private final NuFiConfiguration configuration;

	protected SingleNucleolusDistanceMeasurer(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
	}

	@Override
	public double determinMeanDistance(final ImageAnalysisResults results) {
		final List<TargetPoint> targets = new LinkedList<>(results.getNucleoliTargets());
		final double result = 0;
		for (final Polygon roi : results.getRois()) {
			for (final TargetPoint target : targets) {
				if (roi.contains(target.getxCoordinate(), target.getyCoordinate())) {
					System.err.println("sdfksjdhfksjhdfshdjf");
				}
			}
		}
		return result;
	}
}
