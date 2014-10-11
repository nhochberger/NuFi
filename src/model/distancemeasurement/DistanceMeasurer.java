package model.distancemeasurement;

import model.targetdetection.ImageAnalysisResults;

public interface DistanceMeasurer {

	public double determinMeanDistance(ImageAnalysisResults results);

	public boolean isReal();

	public static class DoNothingDistanceMeasurer implements DistanceMeasurer {

		public DoNothingDistanceMeasurer() {
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
