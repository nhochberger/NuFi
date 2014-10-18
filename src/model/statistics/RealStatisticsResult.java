package model.statistics;

public class RealStatisticsResult implements StatisticsResult {

	private final int nucleiCount;
	private final int nucleoliCount;
	private final double meanDistance;

	public RealStatisticsResult(final int nucleiCount, final int nucleoliCount, final double meanDistance) {
		super();
		this.nucleiCount = nucleiCount;
		this.nucleoliCount = nucleoliCount;
		this.meanDistance = meanDistance;
	}

	@Override
	public int nucleiCount() {
		return this.nucleiCount;
	}

	@Override
	public int targetCount() {
		return this.nucleoliCount;
	}

	@Override
	public double nucleoliNucleioliRatio() {
		return ((double) this.nucleoliCount) / this.nucleiCount;
	}

	@Override
	public double meanDistance() {
		return this.meanDistance;
	}
}
