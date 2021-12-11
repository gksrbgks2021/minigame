import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Hashtable;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class JumpGame extends JPanel implements Runnable, KeyListener { // 표적 맞추기 게임
	int gamelife;
	private MyFrame myframe;
	private JumpGame jump;
	private Stickman stickman;

	private Image backgroundImage;
	private Image Lpass;
	private Image Lfail;
	private Image letter;
	private JLabel[] life;
	private volatile boolean isrun;
	private boolean isdown;
	private boolean lock; // 제거 메소드 락 .
	private boolean yap;
	private boolean finish;
	private Random r;
	private int time = 0;
	private int Limtime = 20; // 시간 제한 20초.
	private int x;
	private int y;
	private int ON;
	private long startime = 0;
	private Timer t[];
	private java.util.Timer booking;
	// volatile 키워드를 선언함으로서 쓰기 읽기 작업은 메인 메모리에서 이뤄짐.
	private volatile boolean isremoveAll;
	ObstacleF obf;
	Queue<Integer> removelist;
	Hashtable<Integer, ObstacleF> oblist;

	JumpGame(MyFrame myFrame) {
		setLayout(null);
		gamelife = 3;
		this.myframe = myFrame;
		this.stickman = new Stickman();
		lock = false;
		jump = this;
		r = new Random();
		backgroundImage = new ImageIcon(Main.class.getResource("images/Jumpbackground.png")).getImage();
		letter = new RemoveBackground("Letter/jump.png").getImage();
		Lpass = new RemoveBackground("Letter/passed.png").getImage();
		Lfail = new RemoveBackground("Letter/failed.png").getImage();

		oblist = new Hashtable<>();
		removelist = new LinkedBlockingQueue<>(10);
		booking = new java.util.Timer(false);

		add(stickman);
		addlife();
		repaint();
	}

	public void init() {
		ON = 0;
		isdown = false;
		isremoveAll = false;
		addKeyListener(this);
		requestFocus();
		setT();
		finish = false;
	}

	public void setT() {
		t = new Timer[2];
		t[0] = new Timer(30, (e) -> {
			istimeout();
			removeobject();
			checklife();// 라이프 검사.
			repaint();
		});

		t[1] = new Timer(2000, (e) -> {
			makeobject();
		});

	}

	@Override
	public void run() {
		sleep(60);

		repaint();
		setTimer(() -> {
			init();// 초기화하고
		}, (long) 1000);

		setTimer(() -> {
			startT();// 스레드 동작.
		}, (long) 1500);

	}

	public void stopT() {
		isrun = false;
		for (int i = 0; i < 2; i++) {
			t[i].stop();
		}
	}

	public void close() {
		// 스레드 종료
		finish = true;
		repaint();
		sleep(400); // 스레드 슬립
		stickman.stopStickman();
		oblist.clear();
		stopT();
		removelist.clear();
	}

	public void startT() {
		isrun = true;
		repaint();
		startime = System.currentTimeMillis();
		for (int i = 0; i < 2; i++) {
			t[i].start();
		}
		stickman.runStickman();

	}

	public void istimeout() {
		if ((System.currentTimeMillis() - startime) / 1000 >= Limtime) {
			isrun = false;
			checklife();
		}
	}

	// 모든 스레드 종료
	public void stopAll() {
		isrun = false;
	}

	public void crash(int num) {// 충돌했으면?
		if (!isrun)
			return;// 게임끝났으면 블락.
		this.gamelife -= 1;
		minuslifeImage();
		System.out.println("충돌함 라이프 : " + gamelife);
		checklife();

		removelist.add(num);
	}

	public void checklife() {
		if (gamelife <= 0 && !finish) // 게임종료시
		{
			yap = true;
			Lpass = null;
			finish = true; // 락을한다. 중복호출 방지.
			repaint();
			setTimer(() -> {
				close();
				myframe.gamefailed(); // 실패
			}, (long) 2000);
		} else if (isrun == false && gamelife > 0 && !finish) {// 게임 통과시.
			yap = true;
			Lfail = null;
			finish = true;
			repaint();
			setTimer(() -> {
				close();
				myframe.gamepassed();
			}, (long) 2000);
		}
	}

	public void makeobject() { // 오브젝트 생성.

		isremoveAll = false;
		obf = new ObstacleF(jump, ON, r.nextInt(2));
		oblist.put(ON++, obf);
		add(obf);
		obf.run();

	}

	public void removeobject(int num) {
		removelist.add(num);
	}

	// 그림그리기
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, null);

		if (!isrun && !finish) {
			g.drawImage(letter, 535, 210, null);
		}
		// 끝났으면 글자 띄우기
		if (yap) {
			if (Lpass != null)
				g.drawImage(Lpass, 535, 210, null);
			if (Lfail != null)
				g.drawImage(Lfail, 535, 210, null);
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public boolean isrun() {
		return this.isrun;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
			stickman.Up();
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			stickman.Down();
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(1);
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

	public int getheight() { // 1이면 기본, 2면 엎드린거.
		return stickman.height();
	}

	public void removeobject() {
		try {
			lock = true;
			int temp;
			while (!removelist.isEmpty()) {
				temp = removelist.poll();
				remove(oblist.get(temp));
				repaint();
			}
			lock = false;
			isremoveAll = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTimer(Runnable runnable, long delay) {
		booking.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				runnable.run();
			}
		}, delay);
	}

	public void addlife() {
		life = new JLabel[this.gamelife];
		for (int i = 0; i < this.gamelife; i++) {
			life[i] = new JL_Life();
			life[i].setLocation(100 + i * 80, 600);
			add(life[i]);
		}
	}

	public void minuslifeImage() {
		if (gamelife > 0) {
			remove(life[gamelife]);
		}
		if (gamelife <= 0) {
			remove(life[0]);
		}
	}

	public void sleep(long mili) {
		try {
			Thread.sleep(mili);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

//===================================================================================================================
//===================================================================================================================
//====================================================================================================================

class ObstacleF extends JLabel implements Runnable {
	private JumpGame jump;
	private ObstacleF obstaclef;
	int ON;
	int x;
	int y;
	int count;
	int polx[] = { 1244, 1210, 1245, 1250, 1250 };
	int poly[] = { 453, 469, 481, 477, 458 };
	private int mSpeed;
	ImageIcon imageF[];
	private boolean lock;
	private boolean Ob_isrun;
	private boolean iscrash;// 충돌 체크 여부
	private int type;
	Rectangle r[];
	Polygon p;
	Timer t1;
	Timer t2;
	java.util.Timer booking;

	public ObstacleF(JumpGame jump, int num, int type) {
		count = 0 ;
		this.jump = jump;
		x = 1210;
		y = 553;
		mSpeed = Main.GAME_SPEED + 7;
		init();
		ON = num;
		this.type = type;
		r = new Rectangle[2];
		if (type == 0)
			type1();
		if (type == 1)
			type2();
		r[1] = new Rectangle(jump.getmanX(), jump.getmanY(), 70, jump.getheight());
	}

	public void init() {
		imageF = new ImageIcon[2];
		imageF[0] = new ImageIcon(Main.class.getResource("images/obstacleF.png"));
		imageF[1] = new ImageIcon(Main.class.getResource("images/drill.png"));

		Ob_isrun = false;
		obstaclef = this;
		iscrash = false;
		lock = false;

		booking = new java.util.Timer(false);

		t1 = new Timer(50, e -> {
			CheckCrash();
		});
		t1.setInitialDelay(500); // 0.5초 뒤에 타이머를 시작합니다.
		t2 = new Timer(30, e -> { // move.
			x -= (mSpeed);
			setLocation(x, y);
			count ++;
		});
	}

	public void type1() {
		r[0] = new Rectangle(x, y, 30, 40);
		setSize(30, 40);
		setLocation(x, y);
		setIcon(imageF[0]);
		setVisible(true);
	}

	public void type2() {
		setSize(40, 30);
		y -= 100;
		setLocation(x, y);
		setIcon(imageF[1]);
		setVisible(true);
	}

	@Override
	public void run() { // 장애물을 생성하고 add합니다~
		Ob_isrun = true;
		startThread();// 1
	}

	public void startThread() {// 1
		t1.start();
		t2.start();
		setTimer(() -> {
			if (!iscrash)// 충돌안했으면
				finish();
		}, 10000); // 10초뒤에 종료 요청
		// 충돌 체크
		// 람다식은 메소드를 객체로 취급하는 기능이다.
		// 람다식을 사용해 무명 클래스를 간결하게 표현했다.
		// timer 클래스는 actionPerformed() 메소드를 호출한다. ACtionListener 인터페이스를 구현한다.
	}

	public boolean Ob_isrun() {
		return Ob_isrun;
	}

	public void CheckCrash() {// 충돌을 체크합니다.
		if (!lock) {
			lock = true;
			if (iscrash)// 충돌했으면 아무것도 안함.
			{
				t1.stop();
				return;
			}
			if (type == 0) {
				r[0].setLocation(x, y);
				r[1].setBounds(jump.getmanX(), jump.getmanY(), 70, jump.getheight());
				if (r[0].intersects(r[1])) // 충돌했으면
				{
					crash();
				}
			}

			if (type == 1) {
				r[1].setBounds(jump.getmanX(), jump.getmanY(), 70, jump.getheight());
				for (int i = 0; i < 5; i++) {//드릴 꼭짓점.
					if (r[1].contains(polx[i] - (count*mSpeed), poly[i])) {
						crash();
						break;
					}
				}
			}
			lock = false;
		}
	}

	public void crash() {
		Ob_isrun = false;
		t1.stop();
		t2.stop();
		iscrash = true;
		jump.crash(ON);
	}

	// 스레드 끄고 삭제요청.
	public void finish() {
		Ob_isrun = false;
		t1.stop();
		t2.stop();
		jump.removeobject(ON);
	}

	public void setTimer(Runnable runnable, long delay) {
		booking.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				runnable.run();
			}
		}, delay);
	}
}