import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class Avoid extends JPanel implements Runnable { // 표적 맞추기 게임
	int gamelife;
	private MyFrame myframe;
	private Avoid avoid;
	private Image backgroundImage;
	private volatile boolean isrun;
	private boolean isdown;
	private boolean isThread1;
	private Stickman stickman;
	private long startime = 0;
	private long curtime = 0;
	private int Limtime = 20; // 시간 제한 20초.
	private int x;
	private int y;
	private int countO;
	private Timer t1, t2, t3;
	// volatile 키워드를 선언함으로서 쓰기 읽기 작업은 메인 메모리에서 이뤄짐.
	private volatile boolean isremoveAll;
	private boolean lock;
	private ObstacleF22 temp;
	ObstacleF22 obf;
	Queue<Integer> removelist;
	Hashtable<Integer,ObstacleF22> oblist;
	//LinkedBlockingQueue<ObstacleF22> removelist;
	private Image dbImage;
	private Graphics dbg;
	InputMap im;
	ActionMap am;

	Avoid(MyFrame myFrame) {
		setLayout(null);
		this.myframe = myFrame;
		oblist = new Hashtable<>();
		removelist = new LinkedBlockingQueue<>(10);
		avoid = this;
		backgroundImage = new ImageIcon(Main.class.getResource("images/Jumpbackground.png")).getImage();
		this.stickman = new Stickman();
		add(stickman);
	}

	public void init() {
		gamelife = 3;
		isrun = true;
		isThread1 = true;
		isdown = false;
		isremoveAll = false;
		lock = false;
		keybind();
		requestFocus();
		// 더 나은 렌더링 퍼포먼스
		this.setDoubleBuffered(true);

		t1 = new Timer(50, (e) -> {
			istimeout();
			checklife();// 라이프 검사.
			if (!lock && isrun)
				removeobject();
			Thread.currentThread().setName("66줄 스레드");
			curtime = System.currentTimeMillis() - startime;
		});

		t2 = new Timer(3000, (e) -> {
			initobject();
			Thread.currentThread().setName("72줄 스레드");
		});

	}

	public void keybind() {
		addKeyBindingP(this, KeyEvent.VK_RIGHT, "rightpress", (evt) -> {
			stickman.Right();
		});

		addKeyBindingL(this, KeyEvent.VK_RIGHT, "rightrelease", (evt) -> {
			stickman.stop_R();
		});

		addKeyBindingP(this, KeyEvent.VK_ESCAPE, "END_GAME", (evt) -> {
			System.exit(0);// 종료된다.
		});

		addKeyBindingP(this, KeyEvent.VK_LEFT, "lefttpress", (evt) -> {
			stickman.Left();
		});

		addKeyBindingL(this, KeyEvent.VK_LEFT, "leftrelease", (evt) -> {
			stickman.stop_L();
		});
	}

	@Override
	public void run() {
		startime = System.currentTimeMillis();
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
		t1.stop();
		t2.stop();
	}

	public void close() {
		// 스레드 종료
		stopT();
		//키바인딩 해제
		this.getInputMap().clear();
		this.getActionMap().clear();
		
		while (removelist.size() > 0) {
			removeobject();
			oblist.clear();
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void startT() {
		t1.start();
		t2.start();
	}

	public void istimeout() {
		if((System.currentTimeMillis() - startime)/1000 >= Limtime) {
			stopT();
			checklife();
			close();
		}
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
		obf = new ObstacleF22(avoid,countO++);
		addobject(obf);// 리스트에 추가.
		obf.setOb_run(true);
		new Thread(obf).start();
	}

	public synchronized void addobject(ObstacleF22 f) {
		add(f);
		oblist.put(f.Num,f);
	}

	public  void removeobject(int Num) {
		removelist.add(Num);
	}

	public  void removeobject() { // 오버로딩
		try {
			lock = true;
			int a;
			while (!removelist.isEmpty()) {
				 a = removelist.poll();
				temp = oblist.get(a);
				if(temp !=null) {
					remove(temp);
					temp.finish();
					oblist.remove(temp.Num);
					repaint();	
				}
			}
			lock = false;
			isremoveAll = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 그림그리기
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g.drawImage(backgroundImage, 0, 0, null);

	}

	/*
	 * public void paint(Graphics g) { dbImage = createImage(Main.SCREEN_WIDTH,
	 * Main.SCREEN_HEIGHT); dbg = dbImage.getGraphics(); paintComponent(dbg);
	 * g.drawImage(dbImage, 0, 0, this); }
	 */

	public boolean isrun() {
		return this.isrun;
	}

	public int getmanX() {
		return stickman.getX();
	}

	public int getmanY() {
		return stickman.getY();
	}

	// 각 jcomponent는 1개 actionMap 3개 inputMap 을 가진다.
	public void addKeyBindingP(JComponent comp, int keyCode, String id, ActionListener actionListener) {
		InputMap im = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap ap = comp.getActionMap();
		// getKeyStroke ( keyCode, 사용 x는 0 다른 modifier과 결합하려면 삽입한다. true는 키가 press 다음
		// 떼어질때. 동작)
		im.put(KeyStroke.getKeyStroke(keyCode, 0, false), id);
		ap.put(id, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionListener.actionPerformed(e);
			}
		});

	}

	public void addKeyBindingL(JComponent comp, int keyCode, String id, ActionListener actionListener) {
		InputMap im = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap ap = comp.getActionMap();

		// getKeyStroke ( keyCode, 사용 x는 0 다른 modifier과 결합하려면 삽입한다. true는 키가 press 다음
		// 떼어질때. 동작)
		im.put(KeyStroke.getKeyStroke(keyCode, 0, true), id);
		ap.put(id, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionListener.actionPerformed(e);
			}
		});
	}
}

//===================================================================================================================
//===================================================================================================================
//====================================================================================================================

class ObstacleF22 extends JLabel implements Runnable {
	private Avoid avoid;
	private ObstacleF22 ObstacleF22;
	int Num;
	int x;
	int y;
	ImageIcon imageF;
	private boolean Ob_isrun;
	private boolean iscrash;// 충돌 체크 여부
	Timer t1;
	Timer t2;

	ObstacleF22(Avoid avoid , int n) {
		this.avoid = avoid;
		this.Num= n;
		init();
		x = 100; // 나중에 가변 코드 작성.
		y = 0;
	}

	public void init() {
		imageF = new ImageIcon(Main.class.getResource("images/ruby.png"));
		setSize(53, 70);
		setLocation(x, y);
		setIcon(imageF);
		Ob_isrun = false;
		ObstacleF22 = this;
		iscrash = false;
	}

	@Override
	public void run() { // 장애물을 생성하고 add합니다~
		Ob_isrun = true;
		startThread();// 1
	}

	public void startThread() {// 1

		// 충돌 체크
		// 람다식은 메소드를 객체로 취급하는 기능이다.
		// 람다식을 사용해 무명 클래스를 간결하게 표현했다.
		// timer 클래스는 actionPerformed() 메소드를 호출한다. ACtionListener 인터페이스를 구현한다.
		t1 = new Timer(50, e -> {
			CheckCrash();
			moveOB();
			if (!avoid.isrun() || y >= 523) {
				finish();
			}
		});
		t1.setInitialDelay(1000); // 1초 뒤에 타이머를 시작합니다.
		t1.start();
		while (Ob_isrun) {
			try {
			
				Thread.sleep(60);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setOb_run(boolean isrun) { // main에서 조정하는거.
		this.Ob_isrun = isrun;
	}

	public boolean Ob_isrun() {
		return Ob_isrun;
	}

	public void moveOB() { // 아래로 이동합니다.
		y += Main.GAME_SPEED;
		this.setLocation(x, y);
	}

	public void CheckCrash() {// 충돌을 체크합니다.
		if (iscrash)// 충돌했으면 아무것도 안함.
			return;
		// 장애물의 4 꼭짓점을 조사합니다.
		int x1 = avoid.getmanX();
		int x2 = this.x;
		int y1 = avoid.getmanY();
		int y2 = this.y;
		Rectangle r1 = new Rectangle(x1, y1, 70, 140);
		Rectangle r2 = new Rectangle(x2, y2, 70, 70);
		// System.out.println("현재 좌표 (" +this.x + ","+this.y+") 색깔:"
		// +image.getRGB(x2,y2));

		if (r1.intersects(r2) && !iscrash)// 겹치면
		{iscrash = true;
			System.out.println("졸라맨 좌표: " + avoid.getmanX() + " " + avoid.getmanY());
			finish();
			avoid.crash();
		}
	}

	// 스레드 끄고 삭제요청.
	public void finish() {
		t1.stop();
		if(Ob_isrun)//중복 호출 방지. 
		avoid.removeobject(this.Num);
		setOb_run(false);
	}

}