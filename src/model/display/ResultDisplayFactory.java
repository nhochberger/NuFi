package model.display;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.Roi;

import java.awt.Polygon;

public class ResultDisplayFactory extends SessionBasedObject {

	private static final String DISPLAY_MODE = "display.mode";
	private final static String FRAME_STRING = "frame";

	public ResultDisplayFactory(final BasicSession session) {
		super(session);
	}

	public ResultDisplayer getResultDisplayer() {
		String configuredMode = session().getProperties().otherProperty(DISPLAY_MODE);
		if (FRAME_STRING.equalsIgnoreCase(configuredMode)) {
			logger().info("Using ImageJ frame to show result image.");
			return new ImageJFrameResultDisplayer();
		}
		logger().info("Not showing resulting image.");
		return new VoidResultDisplayer();
	}

	public interface ResultDisplayer {
		public void displayResult(ImagePlus image, Polygon polygon);
	}

	public class VoidResultDisplayer implements ResultDisplayer {

		public VoidResultDisplayer() {
			super();
		}

		@Override
		public void displayResult(final ImagePlus image, final Polygon polygon) {
			// do nothing on purpose
		}
	}

	public class ImageJFrameResultDisplayer implements ResultDisplayer {

		public ImageJFrameResultDisplayer() {
			super();
		}

		@Override
		public void displayResult(final ImagePlus image, final Polygon polygon) {
			logger().info("Displaying result");
			Roi roi = new PointRoi(polygon);
			Overlay overlay = new Overlay(roi);
			image.setOverlay(overlay);
			image.show("channel1");
		}
	}
}
