package model.statistics;

import model.targetdetection.ImageAnalysisResults;

public interface ImageStatistics {

	public StatisticsResult performMeasurements(ImageAnalysisResults imageAnalysisResults);

	public boolean isReal();
}
