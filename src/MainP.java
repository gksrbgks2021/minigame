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
	private JButton scoreboard = new JButton(dancingmachine); // 클릭하면 점수판 보여주는거.
	private JButton startbtn = new JButton(startbtndefault);
	private JButton endbtn = new JButton(endbtndefault);
	
	private Music newbgm = new Music("Rhythm Heaven Fever.MP3", true);
	MainP(MyFrame myFrame){
		init();
		//myframe 메소드를 실행시키기 위해서 가져온다.
		this.myframe = myFrame;
		scoreboard.setBounds(662, 85, 600, 600);// x좌표 y좌표 크기이다.
		scoreboard.setBorderPainted(false); // 버튼 테두리를 지웁니다
		scoreboard.setContentAreaFilled(false); // 버튼의 초기 background 색상을 지운다.
		scoreboard.setVisible(true);
		scoreboard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) { // 마우스 포커싱일때 점수판이 보여진다.
				scoreboard.setIcon(null);
				/* set scoreboard */

			}

			@Override
			public void mouseExited(MouseEvent e) { // 나가면 점수판 삭제.
				scoreboard.setIcon(dancingmachine);
			}

			public void mouseMoved(MouseEvent e) {

			}
		});
		
		startbtn.setBounds(40, 200, 400, 100);
		startbtn.setBorderPainted(false);
		startbtn.setContentAreaFilled(false);
		startbtn.setFocusPainted(false);// 디폴트값을 변경시켜 내가 원하는 모양으로 나오게한당.
		startbtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				startbtn.setIcon(startbtndentered);
				startbtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
				//나중에 어려움 버튼 구성. 
			}
			@Override
			public void mouseExited(MouseEvent e) {
				startbtn.setIcon(startbtndefault);
				startbtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mousePressed(MouseEvent e) { // 마우스를 눌렀을때??
				Music buttonEnteredMusic = new Music("exitbgm.mp3", false);
				buttonEnteredMusic.start();
				newbgm.stop();//브금 종료~
				myframe.nextgame();//게임실행.
			}
		});
		endbtn.setBounds(40, 330, 400, 100);
		endbtn.setBorderPainted(false);
		endbtn.setContentAreaFilled(false);
		endbtn.setFocusPainted(false);// 디폴트값을 변경시켜 내가 원하는 모양으로 나오게한당.
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
			public void mousePressed(MouseEvent e) { // 마우스를 눌렀을때??
				Music buttonEnteredMusic = new Music("exitbgm.mp3", false);
				buttonEnteredMusic.start();
				try {
					Thread.sleep(1000);// 1초뒤에 종료
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				System.exit(0);// 종료된다.
			}
		});
		add(scoreboard);
		add(startbtn);
		add(endbtn);
	}
	public void init(){
		this.setLayout(null);
		backgroundImage = new ImageIcon(Main.class.getResource("images/Startbackground.png")).getImage(); // 초기 백그라운드 설정
	
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
