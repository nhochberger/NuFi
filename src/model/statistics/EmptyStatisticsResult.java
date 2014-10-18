package model.statistics;

public class EmptyStatisticsResult implements StatisticsResult {

	public EmptyStatisticsResult() {
		super();
	}

	@Override
	public int nucleiCount() {
		return -1;
	}

	@Override
	public int targetCount() {
		return -1;
	}

	@Override
	public double nucleoliNucleioliRatio() {
		return -1.0;
	}

	@Override
	public double meanDistance() {
		return -1;
	}
}
