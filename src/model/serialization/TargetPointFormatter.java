package model.serialization;

import model.targetdetection.TargetPoint;

public class TargetPointFormatter {

	public TargetPointFormatter() {
		super();
	}
	
	public String format(int number, TargetPoint point) {
		StringBuilder result = new StringBuilder();
		result.append(String.valueOf(number))
			.append(" [")
			.append(String.valueOf(point.getxCoordinate()))
			.append(":")
			.append(String.valueOf(point.getyCoordinate()))
			.append("]");
		return result.toString();
	}
}
