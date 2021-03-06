package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import controller.configuration.NuFiConfiguration;

public class RandomTargetFinder extends SessionBasedObject implements TargetFinder {

	private final List<TargetPoint> targets;

	public RandomTargetFinder(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.targets = new ArrayList<TargetPoint>();
	}

	@Override
	public void findTargets() {
		logger().info("Generating random targets");
		generateRandomTargets(10);
	}

	private void generateRandomTargets(final int amount) {
		final Random targetGenerator = new Random();
		for (int i = 1; i <= amount; i++) {
			final TargetPoint newTarget = new TargetPoint(targetGenerator.nextInt(1000), targetGenerator.nextInt(1000));
			logger().info("Found target: " + newTarget);
			this.targets.add(newTarget);
		}
		logger().info("Found " + this.targets.size() + " targets");
	}

	@Override
	public ImageAnalysisResults getResults() {
		return new ImageAnalysisResults(null, new LinkedList<Polygon>(), this.targets, new LinkedList<TargetPoint>(), new double[0], new LinkedList<double[]>());
	}
}
