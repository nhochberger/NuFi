package model.serialization;

import java.io.IOException;

import model.targetdetection.ImageAnalysisResults;

public interface TargetPointSerializer {

	public void serialize(ImageAnalysisResults results) throws IOException;
}
