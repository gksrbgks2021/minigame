import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GameOver extends JPanel {
	private MyFrame mf;
	private int point;
	InputMap im;
	ActionMap ap;
	
	GameOver(MyFrame myframe, int point) {
		mf = myframe;
		this.point = point;
		keybinding();
		repaint();
	}

	public void keybinding() {
		im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ap = this.getActionMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "restart");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "end");
		ap.put("restart", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mf.restart();
			}
		});
		ap.put("end", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);// 게임종료
			}
		});
	}

	public void paintComponent(Graphics g) {
		g.setColor(new Color(128, 128, 128));
		g.setFont(new Font("Gulim", Font.BOLD, 100));
		g.drawString("최종점수 : " + point + "점", 300, 150);
		g.drawString("최종점수 : " + point + "점", 300, 150);
		g.drawString("종료하기 : ESC", 300, 300);
		g.drawString("다시시작 : SPACE", 300, 450);
	}
}
