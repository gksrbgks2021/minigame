import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.InputMap;

public class Main {
	public static final int SCREEN_WIDTH = 1280;
	public static final int SCREEN_HEIGHT = 720;
	public static int GAME_SPEED = 5;
	public static final int SLEEP_TIME = 10;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 SwingUtilities.invokeLater(new Runnable() {  
			 public void run() {
				 new MyFrame();
			 }});
	}
	
	public void InitSpeed() {
		GAME_SPEED = 5;
	}
	
	public void SpeedUp() {
		GAME_SPEED += 1;
	}

}
