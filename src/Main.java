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
	public static int GAME_SPEED = 6;
	public static final int SLEEP_TIME = 10;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 SwingUtilities.invokeLater(new Runnable() {  
			 public void run() {
				 
				 new MyFrame();
			 }
		 });
	}

	//Ű ���ε� ����ƽ �Լ�.
	//�� jcomponent�� 1�� actionMap 3�� inputMap �� ������. 
	
	public static void addKeyBinding(JComponent comp, int keyCode, String id, ActionListener actionListener)
	{
	    InputMap im = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    ActionMap ap = comp.getActionMap();
	    
	    //getKeyStroke ( keyCode, ��� x�� 0  �ٸ� modifier�� �����Ϸ��� �����Ѵ�. true�� Ű�� press ���� ��������. ����) 
	    im.put(KeyStroke.getKeyStroke(keyCode, 0, false), id);
	    ap.put(id, new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e){
	            actionListener.actionPerformed(e);
	        }
	    });
	}
	
	public void InitSpeed() {
		GAME_SPEED = 5;
	}

	public void SpeedUp() {
		GAME_SPEED += 1;
	}

}
