package model.statistics;

import model.targetdetection.ImageAnalysisResults;

public interface ImageStatistics {

	public double determinMeanDistance(ImageAnalysisResults results);

	public boolean isReal();

	public static class DoNothingImageStatistics implements ImageStatistics {

		public DoNothingImageStatistics() {
			super();
		}

		@Override
		public double determinMeanDistance(final ImageAnalysisResults results) {
			// do nothing on purpose
			return -1.0;
		}

		@Override
		public boolean isReal() {
			return false;
		}
	}
}
