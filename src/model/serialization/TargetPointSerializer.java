package model.serialization;

import java.io.IOException;
import java.util.List;

import model.targetdetection.ImageAnalysisResults;
import model.targetdetection.TargetPoint;

public interface TargetPointSerializer {

	public void serialize(ImageAnalysisResults results) throws IOException;
}
