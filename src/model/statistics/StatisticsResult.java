package model.statistics;

public interface StatisticsResult {

	public int nucleiCount();

	public int targetCount();

	public double nucleoliNucleioliRatio();

	public double meanDistance();

	Iterable<Double> distances();

	public Iterable<Double> nucleusAreas();

	public double meanNucleusArea();

	public Iterable<Double> nucleolusAreas();

	public double meanNucleolusArea();
}
