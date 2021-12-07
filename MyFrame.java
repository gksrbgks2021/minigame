import java.awt.CardLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JFrame;

public class MyFrame extends JFrame  implements MouseListener{
	private MyFrame gameFrame = this;
	private MainP mainP;
	private ShootingGame shoot;
	private JumpGame jump;
	private GameOver over;
	private RhythmGame rh;
	private Avoid avoid; 
	
	public boolean isrun; // 게임실행
	private int CurPoint = 0;
	private int CurLife = 5;
	private int GameIndex;
	private int once1=0;
	private int once2=0;
	private Random rd;
	
	Thread thread1;
	Thread thread2;
	Thread thread3;
	Thread thread4;
	CardLayout cardLayout = new CardLayout();
	public MyFrame() {
		setF();
		init();
		changepanel("MainP", CurLife);
	}

	public void init() {
		CurPoint = 0;
		CurLife = 1;
		isrun = false;
		GameIndex = -1;
		addMouseListener(this);
	}
	// set Frame
	
	public void setF() {
		setUndecorated(true); // 메뉴바 삭제
		setTitle("GameSwitch");
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);// 메인화면 크기 조절.
		setResizable(false); // 스크린 크기 고정.
		setLocationRelativeTo(null); // 윈도우 창을 화면의 가운데에 띄우는 역할
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setBackground(new Color(0, 0, 0, 0));
		setLayout(cardLayout);
		setVisible(true);
	}

	// 패널 이름을 가져와서 화면 전환을 해줍니다. panel name, cur life
	public void changepanel(String PN, int life) {
		if (PN.equals("MainP")) { //메 인 화 면
			// show (부모 컨테이너 , 카드 패의 id)
			StopThread();
			//cardLayout.show(getContentPane(), "0");
			mainP = new MainP(gameFrame);
			getContentPane().removeAll();
			getContentPane().add(mainP); 
			revalidate();
			repaint(); 
			new Thread(mainP).start();
		}
		else if (PN.equals("Shoot")) { // 1 번 쨰 게 임
			GameIndex = 0;
			StopThread();
			shoot = new ShootingGame(gameFrame);
			getContentPane().removeAll();
			getContentPane().add(shoot); 
			revalidate();
			repaint();
			new Thread(shoot).start();
		}
		else if (PN.equals("Jump")) { // 2 번 쨰 게 임
			GameIndex = 1;
			StopThread();
			jump = new JumpGame(gameFrame);
			getContentPane().removeAll();
			getContentPane().add(jump); 
			revalidate();
			repaint(); 
			addKeyListener(jump);
			// 스레드 동작 기능
			new Thread(jump).start();
			//thread2.start();
		}
		else if (PN.equals("Music")) { // 3 번 쨰 패 널
			// show (부모 컨테이너 , 카드 패의 id)
			GameIndex = 1;
			StopThread();
			rh = new RhythmGame(gameFrame);
			getContentPane().removeAll();
			getContentPane().add(rh); 
			revalidate();
			repaint(); 
			//cardLayout.show(getContentPane(), "1");
			//requestFocus();
			// 스레드 동작 기능
			new Thread(rh).start();
			//thread2.start();
		}
		else if (PN.equals("Avoid")) { // 4 번 쨰 패 널
			// show (부모 컨테이너 , 카드 패의 id)
			GameIndex = 1;
			StopThread();
			avoid = new Avoid(gameFrame);
			getContentPane().removeAll();
			getContentPane().add(avoid); 
			revalidate();
			repaint(); 
			//cardLayout.show(getContentPane(), "1");
			requestFocus();
			// 스레드 동작 기능
			new Thread(avoid).start();
			//thread2.start();
		}
		else if (PN.equals("GameOver")) { // 게 임 오 버
			// show (부모 컨테이너 , 카드 패의 id)
			GameIndex = -1;
			StopThread();
			over = new GameOver(gameFrame, CurPoint);
			//패널을 만듭니다. 
			getContentPane().removeAll();
			getContentPane().add(over); 
			revalidate();
			// is just sum of both. It marks the container as invalid and performs layout of the container.
			// call invalidate() and validate()
			repaint(); 
		}
		/*
		 * else if (PN.equals("selectAPL")) { selectAPI = new SelectAPI(gameFrame);
		 * getContentPane().removeAll(); getContentPane().add(selectAPI); revalidate();
		 * repaint(); } else if (PN.equals("gameMap")) { gamePanel = new
		 * GamePanel(gameFrame); getContentPane().removeAll();
		 * getContentPane().add(gamePanel); revalidate(); repaint(); } else { mainP =
		 * null; selectAPI = null; gamePanel = null; isgame = false;
		 * getContentPane().removeAll(); revalidate(); repaint(); }
		 */
	}

	/*
	 * 게임 전환 방법. public Game getnextGame() { Random random = new Random(); int num =
	 * random.nextInt(4); int interruptN = 0; while (nextG[num] == 1 && interruptN <
	 * 4) { // 이미 한번 방문했으면 다음걸로 넘어간다. num++; interruptN++; if (num >= 4) num = 0; }
	 * if (interruptN >= 4) Arrays.fill(nextG, 0); // 다 방문했으면 초기화해준다. nextG[num] =
	 * 1; // 방문처리 // return gamelist.get(num); //리스트의 랜덤 i번쨰 리턴. return
	 * gamelist.get(1); // 우선 게임 1만 무한반복합니다. }
	 */
	
	public void nextgame() { // 다음 게임 실행.
		System.out.println("메인 라이프 : " + CurLife + "다음게임~");
		rd = new Random();
		int a = rd.nextInt(3);
		if(GameIndex == 0 ) {
			removeMouseListener(shoot);
		}
		//똥피하기만 돌리기~
	a=3;
		switch(a) {
		case 0:
			changepanel("Shoot", CurLife);
			break;
		case 1:
			changepanel("Jump",CurLife);
			break;
		case 2:
			changepanel("Avoid", CurLife);
			break;
		case 3:
			changepanel("Music",CurLife);
			break;
		}
	}

	public void gamepassed() {// 점수추가.
		this.CurPoint += 1;
		nextgame();
	}

	public void gamefailed() {
		this.CurLife--;
		// 게임이 더이상 진행줄가이면.
		if (this.CurLife <= 0)
			changepanel("GameOver",CurLife);
		else
			nextgame();
	}
	
	public void StopThread() {
		if (thread1 != null) {
			thread1.interrupt();
			System.out.println("중지");
		}
	}

	public void keylistener() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					//
					break;
				case KeyEvent.VK_RIGHT:
					//
					break;
				}
			}
		});
	}
	public void restart() {
		this.CurLife = 5;
	nextgame();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(GameIndex ==0 )
			shoot.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
