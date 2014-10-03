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
		BufferedImage image = createImageFrom(result.getImageFile());
		Graphics2D graphics = (Graphics2D) image.getGraphics().create();
		graphics.setColor(Color.YELLOW);
		for (Polygon polygon : result.getRois()) {
			graphics.drawPolygon(polygon);
		}
		graphics.setColor(Color.RED);
		drawTargets(result.getNucleoliTargets(), graphics, 1);
		graphics.setColor(Color.GREEN);
		drawTargets(result.getNucleiTargets(), graphics, result.getNucleoliTargets().size() + 1);
		graphics.dispose();
		return image;
	}

	private BufferedImage createImageFrom(final File imageFile) {
		BufferedImage image;
		try {
			BufferedImage readImage = ImageIO.read(imageFile);
			image = new BufferedImage(readImage.getWidth(), readImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			image.getGraphics().create().drawImage(readImage, 0, 0, null);
		} catch (final IOException e) {
			image = new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
		}
		return image;
	}

	private void drawTargets(final List<TargetPoint> targets, final Graphics2D graphics, final int startingIndex) {
		int i = startingIndex;
		for (TargetPoint target : targets) {
			int x = target.getxCoordinate();
			int y = target.getyCoordinate();
			drawCross(graphics, x, y);
			drawNumber(graphics, i, x, y);
		}
	}

	private void drawNumber(final Graphics2D graphics, final int number, final int x, final int y) {
		graphics.setFont(graphics.getFont().deriveFont(8f));
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
		graphics.drawString(String.valueOf(number), x + 2, y - 2);
	}

	private void drawCross(final Graphics2D graphics, final int x, final int y) {
		graphics.drawLine(x - 2, y, x + 2, y);
		graphics.drawLine(x, y - 2, x, y + 2);
	}
}
