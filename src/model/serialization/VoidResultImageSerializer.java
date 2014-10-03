package model.serialization;

import java.awt.image.BufferedImage;

public class VoidResultImageSerializer implements ResultImageSerializer {

	public VoidResultImageSerializer() {
		super();
	}

	@Override
	public void serializeResultImage(final BufferedImage image) {
		// do nothing on pupose
	}
}
