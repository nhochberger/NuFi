package model.targetdetection;

import java.util.List;

public interface TargetFinder {

	public void findTargets();

	public List<TargetPoint> getTargets();

	public DetailedResults getDetailedResults();
}
