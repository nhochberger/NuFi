package model.serialization;

import java.util.List;

import model.targetdetection.TargetPoint;

public interface TargetPointSerializer {

	public void serialize(List<TargetPoint> targets);
}
