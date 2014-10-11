package model.distancemeasurement;

import model.targetdetection.ImageAnalysisResults;

public interface DistanceMeasurer {

	double determinMeanDistance(ImageAnalysisResults results);

	public static class DoNothingDistanceMeasurer implements DistanceMeasurer {

		public DoNothingDistanceMeasurer() {
			super();
		}

		@Override
		public double determinMeanDistance(final ImageAnalysisResults results) {
			// do nothing on purpose
			return -1.0;
		}
	}
}
