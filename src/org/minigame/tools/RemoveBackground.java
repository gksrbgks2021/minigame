package org.minigame.tools;
import org.minigame.main.Main;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import javax.swing.ImageIcon;

public class RemoveBackground {
	private String path;// path는 상대경로 url은 절대경로
	ImageIcon originalIcon;
	ImageProducer filteredImage;
	Image changeImage;
	ImageIcon changeImageIcon;
	ImageFilter filter = new RGBImageFilter() {

		int transparentColor = Color.white.getRGB() | 0xFF000000;

		@Override
		public final int filterRGB(int x, int y, int rgb) {
			if ((rgb | 0xFF000000) == transparentColor) {
				return 0x00FFFFFF & rgb;
			} else {
				return rgb;
			}
		}
	};

	public RemoveBackground(String path) {
		this.path = path;
		this.originalIcon = new ImageIcon(getClass().getResource("../"+path));
		filteredImage = new FilteredImageSource(originalIcon.getImage().getSource(), filter);
		changeImage = Toolkit.getDefaultToolkit().createImage(filteredImage);
		changeImageIcon = new ImageIcon(changeImage);
	}

	public Image getImage() {
		return this.changeImage;
	}

	public ImageIcon getImageIcon() {
		return changeImageIcon;
	}
}
