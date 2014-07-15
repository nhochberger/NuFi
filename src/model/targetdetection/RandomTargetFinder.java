package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTargetFinder extends SessionBasedObject implements TargetFinder {

	private final List<TargetPoint> targets;

	public RandomTargetFinder(BasicSession session) {
		super(session);
		targets = new ArrayList<TargetPoint>();
	}

	@Override
	public void findTargets() {
		logger().info("Generating random targets");
		generateRandomTargets(10);
	}

	private void generateRandomTargets(int amount) {
		Random targetGenerator = new Random();
		for (int i = 1; i <= amount; i++) {
			TargetPoint newTarget = new TargetPoint(targetGenerator.nextInt(1000), targetGenerator.nextInt(1000));
			logger().info("Found target: " + newTarget);
			targets.add(newTarget);
		}
		logger().info("Found " + targets.size() + " targets");
	}

	@Override
	public List<TargetPoint> getTargets() {
		return targets;
	}
}
