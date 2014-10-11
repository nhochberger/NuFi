package model.serialization;

import java.io.IOException;

public interface DistanceSerializer {

	public void serializeDistance(double distance) throws IOException;
}
