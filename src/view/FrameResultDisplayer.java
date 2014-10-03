package view;

import hochberger.utilities.gui.EDTSafeFrame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class FrameResultDisplayer implements ResultDisplayer {

	public FrameResultDisplayer() {
		super();
	}

	@Override
	public void displayResult(final Image resultImage) {
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
				final JPanel panel = new JPanel() {
					private static final long serialVersionUID = -1511079494393691518L;

					@Override
					protected void paintComponent(final Graphics g) {
						super.paintComponent(g);
						final int width = resultImage.getWidth(null);
						final int height = resultImage.getHeight(null);
						setPreferredSize(new Dimension(width, height));
						setSize(getPreferredSize());
						final Graphics2D graphics = (Graphics2D) g.create();
						graphics.drawImage(resultImage, 0, 0, width, height, null);
					}
				};
				final JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				setContentPane(scrollPane);
			}
		};
		frame.show();
	}
}
