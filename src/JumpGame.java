import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class JumpGame extends JPanel implements Runnable, KeyListener { // 표적 맞추기 게임
	int gamelife;
	private MyFrame myframe;
	private JumpGame jump;
	private Image backgroundImage;
	private volatile boolean isrun;
	private boolean isdown;
	private boolean isThread1;
	private Stickman stickman;
	private int time = 0;
	private int Limtime = 20; // 시간 제한 20초.
	private int x;
	private int y;
	// volatile 키워드를 선언함으로서 쓰기 읽기 작업은 메인 메모리에서 이뤄짐.
	private volatile boolean isremoveAll;
	ObstacleF obf;
	List<ObstacleF> oblist;
	private Image dbImage;
	private Graphics dbg;

	JumpGame(MyFrame myFrame) {
		setLayout(null);
		oblist = Collections.synchronizedList(new ArrayList<>());
		this.myframe = myFrame;
		jump = this;
		backgroundImage = new ImageIcon(Main.class.getResource("images/Jumpbackground.png")).getImage();
		this.stickman = new Stickman();
		add(stickman);
		repaint();
	}

	public void init() {
		gamelife = 3;
		isrun = true;
		isThread1 = true;
		isdown = false;
		Main.addKeyBinding(this, KeyEvent.VK_ESCAPE, "END_GAME", (evt) -> {
			System.exit(0);// 종료된다.
		});
		isremoveAll = false;
		addKeyListener(this);
		requestFocus();
	}

	@Override
	public void run() {
		init();
		try {
			Thread.sleep(1000);
			startT();// 스레드 동작.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopT() {
		isrun = false;
	}

	public void close() {
		// 스레드 종료
		stopT();
		while (!isremoveAll)
			; // 다 종료할떄까지 busy waiting
	}

	public void startT() {
		new Thread(() -> { // 스레드 한 개
			while (isrun) {
				try {
					if (!isrun)
						break;
					repaint();
					Thread.sleep(30);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(() -> {
			while (isrun) {
				try {
					if (!isrun)
						break;
					istimeout();
					time++;
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(() -> {
			while (isrun) {
				try {
					if (!isrun)
						break;
					checklife();// 라이프 검사.
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		// 원 생성

		new Thread(() -> {
			while (isrun) {
				try {
					if (!isrun)
						break;
					initobject();
					Thread.sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(() -> {
			while (isrun) {
				try {
					if (!isrun)
						break;
					repaint();
					Thread.sleep(30);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void istimeout() {
		if (time >= Limtime) {
			stopAll();
			checklife();
			close();
		}
	}

	// 모든 스레드 종료
	public void stopAll() {
		isrun = false;
	}

	public void crash() {// 충돌했으면?
		this.gamelife -= 1;
		System.out.println("충돌함 라이프 : " + gamelife);
		checklife();
	}

	public void checklife() {
		if (gamelife <= 0) // 게임종료시
		{
			close();
			myframe.gamefailed(); // 실패
		} else if (isrun == false && gamelife > 0) {// 게임 통과시.
			close();
			myframe.gamepassed();
		}
	}

	public void initobject() {
		isremoveAll = false;
		System.out.println("jump 클래스에서 장애물 생성");
		obf = new ObstacleF(jump);
		obf.setOb_run(true);
		new Thread(obf).start();
	}

	public void addobject(ObstacleF f) {
		add(f);
		oblist.add(f);
	}

	public void removeobject(ObstacleF f) {
		remove(f);
		oblist.remove(f);
		if (oblist.size() == 0)// 마지막일떄
			isremoveAll = true;
	}

	// 더블 버퍼링.
	public void doublebuffer(Graphics g) {

	}

	// 그림그리기
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, null);
	}

	/*public void paint(Graphics g) {
		dbImage = createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		dbg = dbImage.getGraphics();
		paintComponent(dbg);
		g.drawImage(dbImage, 0, 0, this);
	}
*/
	@Override
	public void keyTyped(KeyEvent e) {
	}

	public boolean isrun() {
		return this.isrun;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
			if (!stickman.isUp() && !stickman.isgoDown())// 이미 점프하고있으면 호출 x
				stickman.Up();
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			stickman.Down();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			stickman.wakeUp();
		}
	}

	public int getmanX() {
		return stickman.getX();
	}

	public int getmanY() {
		return stickman.getY();
	}
}

//===================================================================================================================
//===================================================================================================================
//====================================================================================================================

class ObstacleF extends JLabel implements Runnable {
	private JumpGame jump;
	private ObstacleF obstaclef;

	int x;
	int y;
	private int hitbox_X;
	private int hitbox_Y;
	ImageIcon imageF;
	private boolean Ob_isrun;
	private boolean iscrash;// 충돌 체크 여부
	Timer t1;
	Timer t2;

	ObstacleF(JumpGame jump) {
		this.jump = jump;
		init();
		x = 1210;
		y = 523;
	}

	public void init() {
		imageF = new ImageIcon(Main.class.getResource("images/obstacleF.png"));
		setSize(53, 70);
		setLocation(x, y);
		setIcon(imageF);
		Ob_isrun = false;
		obstaclef = this;
		iscrash = false;
	}

	public void addObstacle() {
		jump.addobject(obstaclef); // 장애물을 생성합니다~.
	}

	@Override
	public void run() { // 장애물을 생성하고 add합니다~
		Ob_isrun = true;
		startThread();// 1
	}

	public void startThread() {// 1
		addObstacle();
		moveOB();
		// 충돌 체크
		// 람다식은 메소드를 객체로 취급하는 기능이다.
		// 람다식을 사용해 무명 클래스를 간결하게 표현했다.
		// timer 클래스는 actionPerformed() 메소드를 호출한다. ACtionListener 인터페이스를 구현한다.
		t1 = new Timer(50, e -> {
			CheckCrash();
		});
		t1.setInitialDelay(1000); // 1초 뒤에 타이머를 시작합니다.
		t2 = new Timer(50, e -> {
			if (!jump.isrun()) {
				finish();
			}
		});
		t1.start();
		t2.start();
		try {
			Thread.sleep(7000);// 5초뒤에 종료 요청.
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setOb_run(boolean isrun) { // main에서 조정하는거.
		this.Ob_isrun = isrun;
	}

	public boolean Ob_isrun() {
		return Ob_isrun;
	}

	public void moveOB() { // 왼쪽으로 이동합니다.

		new Thread(() -> {
			for (int i = 0; i < 400; i++) {
				if (!Ob_isrun())// 실행종료했으면
					break;
				x -= Main.GAME_SPEED;
				setLocation(x, y);
				try {
					Thread.sleep(30);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public void CheckCrash() {// 충돌을 체크합니다.
		// 장애물의 4 꼭짓점을 조사합니다.
		int x1 = jump.getmanX();
		int x2 = this.x;
		int y1 = jump.getmanY();
		int y2 = this.y;
		if (iscrash)// 충돌했으면 아무것도 안함.
			return;

		if ((x2 >= x1 && x2 <= x1 + 70) && (y2 >= y1 && y2 <= y1 + 140)// 왼쪽위 꼭짓점.
				|| (x2 + 53 >= x1 && x2 + 53 <= x1 + 70) && (y2 >= y1 && y2 <= y1 + 140)// 오른쪽위꼭짓점.
				|| (x2 >= x1 && x2 <= x1 + 70) && (y2 + 70 >= y1 && y2 + 70 <= y1 + 140)// 왼쪽아래
				|| (x2 + 53 >= x1 && x2 + 53 <= x1 + 70) && (y2 + 70 >= y1 && y2 + 70 <= y1 + 140))// 오른쪽아래
		{
			iscrash = true;
			jump.crash();
		}
	}

	// 스레드 끄고 삭제요청.
	public void finish() {
		Ob_isrun = false;
		t1.stop();
		t2.stop();
		jump.removeobject(obstaclef);
	}
}