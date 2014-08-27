package model.display;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.gui.EDTSafeFrame;
import ij.ImagePlus;
import ij.gui.Roi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import model.targetdetection.TargetPoint;

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

		public void displayResult(ImagePlus channel1, Roi[] roisAsArray, List<TargetPoint> targets);
	}

	public class VoidResultDisplayer implements ResultDisplayer {

		public VoidResultDisplayer() {
			super();
		}

		@Override
		public void displayResult(final ImagePlus channel1, final Roi[] roisAsArray, final List<TargetPoint> targets) {
			// do nothing on purpose here
		}
	}

	public class ImageJFrameResultDisplayer implements ResultDisplayer {

		public ImageJFrameResultDisplayer() {
			super();
		}

		@Override
		public void displayResult(final ImagePlus imagePlus, final Roi[] roisAsArray, final List<TargetPoint> targets) {
			EDTSafeFrame frame = new EDTSafeFrame("Result") {

				@Override
				protected void buildUI() {
					notResizable();
					int width = 800;
					int height = 600;
					setSize(width, height);
					center();
					exitOnClose();
					useLayoutManager(new BorderLayout());
					JPanel panel = new JPanel() {
						private static final long serialVersionUID = -1511079494393691518L;

						@Override
						protected void paintComponent(final Graphics g) {
							super.paintComponent(g);
							setPreferredSize(new Dimension(imagePlus.getWidth(), imagePlus.getHeight()));
							setSize(getPreferredSize());
							Graphics2D graphics = (Graphics2D) g.create();
							graphics.drawImage(imagePlus.getImage(), 0, 0, imagePlus.getWidth(), imagePlus.getHeight(), null);
							for (Roi roi : roisAsArray) {
								graphics.setColor(Color.YELLOW);
								graphics.drawPolygon(roi.getConvexHull());
							}
							for (TargetPoint target : targets) {
								drawCross(graphics, (target.getxCoordinate()), (target.getyCoordinate()));
							}
						}
					};
					JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
					setContentPane(scrollPane);
				}

				private void drawCross(final Graphics2D graphics, final int x, final int y) {
					graphics.setColor(Color.RED);
					graphics.drawLine(x - 2, y, x + 2, y);
					graphics.drawLine(x, y - 2, x, y + 2);
				}
			};
			frame.show();
		}
		// @Override
		// public void displayResult(final ImagePlus image, final Polygon
		// polygon) {
		// logger().info("Displaying result");
		// Roi roi = new PointRoi(polygon);
		// Overlay overlay = new Overlay(roi);
		// image.setOverlay(overlay);
		// image.show("channel1");
		// }
	}
}
