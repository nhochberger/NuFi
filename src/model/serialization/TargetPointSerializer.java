package model.serialization;

import model.targetdetection.ImageAnalysisResults;

public interface TargetPointSerializer {

	public void serialize(ImageAnalysisResults results);
}
