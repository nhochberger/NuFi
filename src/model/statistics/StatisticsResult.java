package model.statistics;

public interface StatisticsResult {

	public int nucleiCount();

	public int targetCount();

	public double nucleoliNucleioliRatio();

	public double meanDistance();

	Iterable<Double> distances();

}
