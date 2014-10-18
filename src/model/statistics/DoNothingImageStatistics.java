package model.statistics;

import model.targetdetection.ImageAnalysisResults;

public class DoNothingImageStatistics implements ImageStatistics {

	public DoNothingImageStatistics() {
		super();
	}

	@Override
	public StatisticsResult performMeasurements(final ImageAnalysisResults imageAnalysisResults) {
		return null;
	}

	@Override
	public boolean isReal() {
		return false;
	}
}
