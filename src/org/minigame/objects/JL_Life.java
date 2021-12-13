package org.minigame.objects;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.minigame.main.Main;
public class JL_Life extends JLabel {
	private ImageIcon imgicon;
	int x, y;

	public JL_Life() {
		this.setVisible(true);
		this.setSize(new Dimension(50, 50));
		this.imgicon = new ImageIcon(getClass().getResource("../images/life.png"));
		this.setIcon(imgicon);
		x = 300;
		y = 600;
		this.setLocation(x,y);
	}
}
