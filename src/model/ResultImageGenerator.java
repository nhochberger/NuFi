package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import model.targetdetection.ImageAnalysisResults;
import model.targetdetection.TargetPoint;

public class ResultImageGenerator {

	public ResultImageGenerator() {
		super();
	}

	public BufferedImage createResultImageFrom(final ImageAnalysisResults result) {
		final BufferedImage image = createImageFrom(result.getImageFile());
		final Graphics2D graphics = (Graphics2D) image.getGraphics().create();
		drawRois(result, graphics);
		graphics.setColor(Color.RED);
		drawTargets(result.getNucleoliTargets(), graphics, 1);
		graphics.setColor(Color.GREEN);
		drawTargets(result.getNucleiTargets(), graphics, result.getNucleoliTargets().size() + 1);
		graphics.dispose();
		return image;
	}

	private void drawRois(final ImageAnalysisResults result, final Graphics2D oldGgraphics) {
		final Graphics2D graphics = (Graphics2D) oldGgraphics.create();
		graphics.setColor(Color.YELLOW);
		int i = 1;
		for (final Polygon polygon : result.getRois()) {
			graphics.drawPolygon(polygon);
			drawCross(graphics, (int) polygon.getBounds().getCenterX(), (int) polygon.getBounds().getCenterY());
			drawNumber(graphics, i, (int) polygon.getBounds().getMinX(), (int) polygon.getBounds().getMaxY());
			i++;
		}
		graphics.dispose();
	}

	private BufferedImage createImageFrom(final File imageFile) {
		BufferedImage image;
		try {
			final BufferedImage readImage = ImageIO.read(imageFile);
			image = new BufferedImage(readImage.getWidth(), readImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			image.getGraphics().create().drawImage(readImage, 0, 0, null);
		} catch (final IOException e) {
			image = new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
		}
		return image;
	}

	private void drawTargets(final List<TargetPoint> targets, final Graphics2D oldGraphics, final int startingIndex) {
		final Graphics2D graphics = (Graphics2D) oldGraphics.create();
		int i = startingIndex;
		for (final TargetPoint target : targets) {
			final int x = target.getxCoordinate();
			final int y = target.getyCoordinate();
			drawCross(graphics, x, y);
			drawNumber(graphics, i, x, y);
			i++;
		}
		graphics.dispose();
	}

	private void drawNumber(final Graphics2D oldGraphics, final int number, final int x, final int y) {
		final Graphics2D graphics = (Graphics2D) oldGraphics.create();
		graphics.setFont(graphics.getFont().deriveFont(8f));
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
		graphics.drawString(String.valueOf(number), x + 2, y - 2);
		graphics.dispose();
	}

	private void drawCross(final Graphics2D oldGraphics, final int x, final int y) {
		final Graphics2D graphics = (Graphics2D) oldGraphics.create();
		graphics.drawLine(x - 2, y, x + 2, y);
		graphics.drawLine(x, y - 2, x, y + 2);
		graphics.dispose();
	}
}
