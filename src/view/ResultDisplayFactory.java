package view;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

import java.awt.Image;

public class ResultDisplayFactory extends SessionBasedObject {

	private static final String DISPLAY_MODE = "display.mode";
	private final static String FRAME_STRING = "frame";

	public ResultDisplayFactory(final BasicSession session) {
		super(session);
	}

	public ResultDisplayer getResultDisplayer() {
		final String configuredMode = session().getProperties().otherProperty(DISPLAY_MODE);
		if (FRAME_STRING.equalsIgnoreCase(configuredMode)) {
			logger().info("Using ImageJ frame to show result image.");
			return new FrameResultDisplayer();
		}
		logger().info("Not showing resulting image.");
		return new VoidResultDisplayer();
	}

	public class VoidResultDisplayer implements ResultDisplayer {

		public VoidResultDisplayer() {
			super();
		}

		@Override
		public void displayResult(final Image resultImage) {
			// TODO Auto-generated method stub

		}
	}
}
