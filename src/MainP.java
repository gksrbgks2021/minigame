import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MainP extends JPanel implements Runnable{
	private MyFrame myframe;
	private Image screenImage;
	private Graphics screenGraphic;
	private Image backgroundImage;
	private ImageIcon dancingmachine = new ImageIcon(Main.class.getResource("images/skudance.gif"));
	private ImageIcon startbtndefault = new ImageIcon(Main.class.getResource("images/startbtn.png"));
	private ImageIcon startbtndentered = new ImageIcon(Main.class.getResource("images/startbtnentered.png"));
	private ImageIcon endbtndefault = new ImageIcon(Main.class.getResource("images/endbtndefault.png"));
	private ImageIcon endbtndentered = new ImageIcon(Main.class.getResource("images/endbtnentered.png"));
	
	//Jbutton
	private JButton scoreboard = new JButton(dancingmachine); // Ŭ���ϸ� ������ �����ִ°�.
	private JButton startbtn = new JButton(startbtndefault);
	private JButton endbtn = new JButton(endbtndefault);
	
	private Music newbgm = new Music("Rhythm Heaven Fever.MP3", true);
	MainP(MyFrame myFrame){
		init();
		//myframe �޼ҵ带 �����Ű�� ���ؼ� �����´�.
		this.myframe = myFrame;
		scoreboard.setBounds(662, 85, 600, 600);// x��ǥ y��ǥ ũ���̴�.
		scoreboard.setBorderPainted(false); // ��ư �׵θ��� ����ϴ�
		scoreboard.setContentAreaFilled(false); // ��ư�� �ʱ� background ������ �����.
		scoreboard.setVisible(true);
		scoreboard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) { // ���콺 ��Ŀ���϶� �������� ��������.
				scoreboard.setIcon(null);
				/* set scoreboard */

			}

			@Override
			public void mouseExited(MouseEvent e) { // ������ ������ ����.
				scoreboard.setIcon(dancingmachine);
			}

			public void mouseMoved(MouseEvent e) {

			}
		});
		
		startbtn.setBounds(40, 200, 400, 100);
		startbtn.setBorderPainted(false);
		startbtn.setContentAreaFilled(false);
		startbtn.setFocusPainted(false);// ����Ʈ���� ������� ���� ���ϴ� ������� �������Ѵ�.
		startbtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				startbtn.setIcon(startbtndentered);
				startbtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
				//���߿� ����� ��ư ����. 
			}
			@Override
			public void mouseExited(MouseEvent e) {
				startbtn.setIcon(startbtndefault);
				startbtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mousePressed(MouseEvent e) { // ���콺�� ��������??
				Music buttonEnteredMusic = new Music("exitbgm.mp3", false);
				buttonEnteredMusic.start();
				newbgm.stop();//��� ����~
				myframe.nextgame();//���ӽ���.
			}
		});
		endbtn.setBounds(40, 330, 400, 100);
		endbtn.setBorderPainted(false);
		endbtn.setContentAreaFilled(false);
		endbtn.setFocusPainted(false);// ����Ʈ���� ������� ���� ���ϴ� ������� �������Ѵ�.
		endbtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				endbtn.setIcon(endbtndentered);
				endbtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				endbtn.setIcon(endbtndefault);
				endbtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) { // ���콺�� ��������??
				Music buttonEnteredMusic = new Music("exitbgm.mp3", false);
				buttonEnteredMusic.start();
				try {
					Thread.sleep(1000);// 1�ʵڿ� ����
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				System.exit(0);// ����ȴ�.
			}
		});
		add(scoreboard);
		add(startbtn);
		add(endbtn);
	}
	public void init(){
		this.setLayout(null);
		backgroundImage = new ImageIcon(Main.class.getResource("images/Startbackground.png")).getImage(); // �ʱ� ��׶��� ����
	
	}
	@Override
	protected void paintComponent(Graphics g) {
		//paint the BGI and scale it to fill the entire space
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0,null);
		
	}
	@Override
	public void run() {
		newbgm = new Music("Rhythm Heaven Fever.MP3", true);
		newbgm.start();
		System.out.println("s");
	}
}
