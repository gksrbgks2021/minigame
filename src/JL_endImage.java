import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class JL_endImage extends JLabel{
	private ImageIcon GamefailedIcon =new ImageIcon(Main.class.getResource("images/gamefailed.png"));
	private ImageIcon GamepassedIcon =new ImageIcon(Main.class.getResource("images/gamepassed.png"));
	int fail = 0;
	int pass = 1;
	
	JL_endImage(int a){
		this.setVisible(true);
		this.setSize(getPreferredSize());
		if(a == fail) {
			setIcon(GamefailedIcon);
		}
		else if(a == pass) {
			setIcon(GamepassedIcon);
		}
	}
}
