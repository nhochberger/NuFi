package view;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.gui.EDTSafeFrame;
import ij.gui.Roi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
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

		public void displayResult(File channel1, Roi[] roisAsArray, List<TargetPoint> targets);
	}

	public class VoidResultDisplayer implements ResultDisplayer {

		public VoidResultDisplayer() {
			super();
		}

		@Override
		public void displayResult(final File channel1, final Roi[] roisAsArray, final List<TargetPoint> targets) {
			// do nothing on purpose here
		}
	}

	public class ImageJFrameResultDisplayer implements ResultDisplayer {

		public ImageJFrameResultDisplayer() {
			super();
		}

		@Override
		public void displayResult(final File imageFile, final Roi[] roisAsArray, final List<TargetPoint> targets) {
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
					final Image image = createImageFrom(imageFile);
					JPanel panel = new JPanel() {
						private static final long serialVersionUID = -1511079494393691518L;

						@Override
						protected void paintComponent(final Graphics g) {
							super.paintComponent(g);
							int width = image.getWidth(null);
							int height = image.getHeight(null);
							setPreferredSize(new Dimension(width, height));
							setSize(getPreferredSize());
							Graphics2D graphics = (Graphics2D) g.create();
							graphics.drawImage(image, 0, 0, width, height, null);
							for (Roi roi : roisAsArray) {
								graphics.setColor(Color.YELLOW);
								graphics.drawPolygon(roi.getConvexHull());
							}
							int number = 0;
							for (TargetPoint target : targets) {
								drawCross(graphics, (target.getxCoordinate()), (target.getyCoordinate()));
								drawNumber(graphics, ++number, (target.getxCoordinate()), (target.getyCoordinate()));
							}
						}
					};
					JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
					setContentPane(scrollPane);
				}

				private Image createImageFrom(final File imageFile) {
					Image image;
					try {
						image = ImageIO.read(imageFile);
					} catch (IOException e) {
						image = new BufferedImage(0, 0, BufferedImage.TYPE_3BYTE_BGR);
					}
					return image;
				}

				private void drawNumber(final Graphics2D graphics, final int number, final int x, final int y) {
					graphics.setColor(Color.RED);
					graphics.setFont(graphics.getFont().deriveFont(8f));
					graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
					graphics.drawString(String.valueOf(number), x + 2, y - 2);
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
