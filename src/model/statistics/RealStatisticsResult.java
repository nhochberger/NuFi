package model.statistics;

public class RealStatisticsResult implements StatisticsResult {

	private final int nucleiCount;
	private final int nucleoliCount;
	private final double meanDistance;
	private final Iterable<Double> distances;
	private final Iterable<Double> nucleusAreas;
	private final double meanNucleusArea;
	private final Iterable<Double> nucleolusAreas;
	private final double meanNucleolusAreas;

	public RealStatisticsResult(final int nucleiCount, final int nucleoliCount, final Iterable<Double> distances, final double meanDistance, final Iterable<Double> nucleusAreas,
			final double meanNucleusArea, final Iterable<Double> nucleolusAreas, final double meanNucleolusAreas) {
		super();
		this.nucleiCount = nucleiCount;
		this.nucleoliCount = nucleoliCount;
		this.distances = distances;
		this.meanDistance = meanDistance;
		this.nucleusAreas = nucleusAreas;
		this.meanNucleusArea = meanNucleusArea;
		this.nucleolusAreas = nucleolusAreas;
		this.meanNucleolusAreas = meanNucleolusAreas;
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
	public Iterable<Double> distances() {
		return this.distances;
	}

	@Override
	public double meanDistance() {
		return this.meanDistance;
	}

	@Override
	public Iterable<Double> nucleusAreas() {
		return this.nucleusAreas;
	}

	@Override
	public double meanNucleusArea() {
		return this.meanNucleusArea;
	}

	@Override
	public Iterable<Double> nucleolusAreas() {
		return this.nucleolusAreas;
	}

	@Override
	public double meanNucleolusArea() {
		return this.meanNucleolusAreas;
	}
}
