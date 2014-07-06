package model.targetdetection;

public class TargetPoint {

	private final int xCoordinate;
	private final int yCoordinate;
	
	public TargetPoint(int xCoordinate, int yCoordinate) {
		super();
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
	}

	public int getxCoordinate() {
		return xCoordinate;
	}

	public int getyCoordinate() {
		return yCoordinate;
	}
}
