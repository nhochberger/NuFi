package model.targetdetection;

public class TargetPoint {

	private final int xCoordinate;
	private final int yCoordinate;

	public TargetPoint(final int xCoordinate, final int yCoordinate) {
		super();
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
	}

	public int getxCoordinate() {
		return this.xCoordinate;
	}

	public int getyCoordinate() {
		return this.yCoordinate;
	}

	@Override
	public String toString() {
		return "(" + this.xCoordinate + ":" + this.yCoordinate + ")";
	}

	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof TargetPoint)) {
			return false;
		}
		TargetPoint other = (TargetPoint) obj;
		return other.getxCoordinate() == getxCoordinate() && other.getyCoordinate() == getyCoordinate();
	}
}
