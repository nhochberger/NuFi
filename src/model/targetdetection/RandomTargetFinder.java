package model.targetdetection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTargetFinder implements TargetFinder {
	
	private final List<TargetPoint> targets;

	public RandomTargetFinder() {
		super();
		targets = new ArrayList<TargetPoint>();
	}

	@Override
	public void findTargets() {
		generateRandomTargets(10);
	}

	private void generateRandomTargets(int amount) {
		Random targetGenerator = new Random();
		for(int i = 1; i <= amount; i++) {
			targets.add(new TargetPoint(targetGenerator.nextInt(1000), targetGenerator.nextInt(1000)));
		}
	}

	@Override
	public List<TargetPoint> getTargets() {
		return targets;
	}
}
