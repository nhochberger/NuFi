package view;

import hochberger.utilities.gui.EDTSafeFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import model.targetdetection.ImageAnalysisResults;
import model.targetdetection.TargetPoint;

public class FrameResultDisplayer implements ResultDisplayer {

	public FrameResultDisplayer() {
		super();
	}

	@Override
	public void displayResult(final ImageAnalysisResults results) {
		final EDTSafeFrame frame = new EDTSafeFrame("Result") {

			@Override
			protected void buildUI() {
				notResizable();
				final int width = 800;
				final int height = 600;
				setSize(width, height);
				center();
				exitOnClose();
				useLayoutManager(new BorderLayout());
				final Image image = createImageFrom(results.getImageFile());
				final JPanel panel = new JPanel() {
					private static final long serialVersionUID = -1511079494393691518L;

					@Override
					protected void paintComponent(final Graphics g) {
						super.paintComponent(g);
						final int width = image.getWidth(null);
						final int height = image.getHeight(null);
						setPreferredSize(new Dimension(width, height));
						setSize(getPreferredSize());
						final Graphics2D graphics = (Graphics2D) g.create();
						graphics.drawImage(image, 0, 0, width, height, null);
						for (final Polygon polygon : results.getRois()) {
							graphics.setColor(Color.YELLOW);
							graphics.drawPolygon(polygon);
						}
						int number = 0;
						for (final TargetPoint target : results.getNucleoliTargets()) {
							drawCross(graphics, (target.getxCoordinate()), (target.getyCoordinate()));
							drawNumber(graphics, ++number, (target.getxCoordinate()), (target.getyCoordinate()));
						}
					}
				};
				final JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				setContentPane(scrollPane);
			}

			private Image createImageFrom(final File imageFile) {
				Image image;
				try {
					image = ImageIO.read(imageFile);
				} catch (final IOException e) {
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
}
