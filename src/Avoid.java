import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Queue;
import java.util.Random;
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

	private MyFrame myframe;
	private Avoid avoid;
	private Stickman stickman;

	ObstacleF22 obf;
	ObstacleF22 temp;
	Queue<Integer> removelist;
	Hashtable<Integer, ObstacleF22> oblist;
	InputMap im;
	ActionMap am;
	private Image backgroundImage;
	private Image letter;
	private Image Lpass;
	private Image Lfail;

	private boolean isrun;
	private boolean isdown;
	private boolean lock;
	private boolean finish;
	private boolean yap;
	private long startime = 0;
	private long curtime = 0;
	private int Limtime = 20; // 시간 제한 20초.
	private int countO;
	private int gamelife;
	private int temp1[];
	private Timer t[];
	java.util.Timer booking;
	private JLabel[] life;
	// volatile 키워드를 선언함으로서 쓰기 읽기 작업은 메인 메모리에서 이뤄짐.

	Avoid(MyFrame myFrame) {
		setLayout(null);
		this.myframe = myFrame;
		this.stickman = new Stickman();
		oblist = new Hashtable<>();
		removelist = new LinkedBlockingQueue<>(30);
		avoid = this;
		backgroundImage = new ImageIcon(Main.class.getResource("images/Jumpbackground.png")).getImage();
		
		letter = new RemoveBackground("Letter/avoid.png").getImage();
		Lpass = new RemoveBackground("Letter/passed.png").getImage();
		Lfail = new RemoveBackground("Letter/failed.png").getImage();
		booking = new java.util.Timer(false);
		Random r = new Random();
		boolean[] appear = new boolean[24];//1280/53 = 
		temp1 = new int[11]; //총 12개 장애물 생성. 
		t = new Timer[2];
		t[0] = new Timer(50, (e) -> {
			istimeout();
			checklife();// 라이프 검사.
			if (!lock && isrun)
				removeobject();
		});

		t[1] = new Timer(3000, (e) -> {
			for(int i =0 ; i <temp1.length;i++) {
				temp1[i] = r.nextInt(24);
				appear[temp1[i]] = true;
			}
			for(int i =0 ; i<appear.length ;i++) {
					if(appear[i])
						makeobject(i*53);
			}
			for(int i =0 ; i < temp1.length;i++) {
				appear[temp1[i]] = false; //초기화.
			}
		});
	}

	public void init() {
		gamelife = 3;
		yap = false;
		isrun = false;
		isdown = false;
		lock = false;
		finish = false;
		add(stickman);
		keybind();
		requestFocus();
		addlife();
	}

	public void mousepress(MouseEvent e) {// 좌표찍기용.
		System.out.print(System.currentTimeMillis() - startime);
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
		init();
		sleep((long) 60);
		repaint();

		setTimer(() -> {
			startT();// 스레드 동작.
		}, (long) 1500);
	}

	public void stopT() {
		isrun = false;
		for (int i = 0; i < t.length; i++) {
			t[i].stop();
		}
	}

	public void close() {
		// 스레드 종료
		stopT();
		// 키바인딩 해제
		this.getInputMap().clear();
		this.getActionMap().clear();
		stickman.stopAll();
		remove(stickman);
		clearlife();
		while (removelist.size() > 0) {
			removeobject();
			oblist.clear();

		}
		sleep((long) 5);
	}

	public void startT() {
		startime = System.currentTimeMillis();
		System.out.println("시작{");
		isrun = true;
		repaint();
		for (int i = 0; i < t.length; i++) {
			t[i].start();
		}
	}

	public void istimeout() {
		if ((System.currentTimeMillis() - startime) / 1000 >= Limtime) {
			stopT();
			checklife();
			close();
		}
	}

	public void crash(int num) {// 충돌했으면?
		if (!isrun)
			return;// 게임끝났으면 블락.
		this.gamelife -= 1;
		minuslifeImage();
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

	public void makeobject(int x) {
		obf = new ObstacleF22(avoid, countO++,x);
		addobject(obf);// 리스트에 추가.
		obf.run();
	}

	public synchronized void addobject(ObstacleF22 f) {
		add(f);
		oblist.put(f.Num, f);
	}

	public void removeobject(int Num) {
		removelist.add(Num);
	}

	public void removeobject() { // 오버로딩
		try {
			lock = true;
			int a;
			while (!removelist.isEmpty()) {
				a = removelist.poll();
				temp = oblist.get(a);
				remove(temp);
				oblist.remove(temp.Num);
				repaint();
			}
			lock = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 그림그리기
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, null);

		if (!isrun && !finish) {
			g.drawImage(letter, 535, 210, null);
		}
		if (yap) {
			if (Lpass != null)
				g.drawImage(Lpass, 535, 210, null);
			if (Lfail != null)
				g.drawImage(Lfail, 535, 210, null);
		}
	}

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

	public void sleep(long i) {
		try {
			Thread.sleep(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addlife() {
		life = new JLabel[this.gamelife];
		for (int i = 0; i < this.gamelife; i++) {
			life[i] = new JL_Life();
			life[i].setLocation(100 + i * 80, 600);
			this.add(life[i]);
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

	public void clearlife() {
		for (int i = 0; i < this.gamelife; i++) {
			this.remove(life[i]);
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
}

//===================================================================================================================
//===================================================================================================================
//====================================================================================================================

class ObstacleF22 extends JLabel implements Runnable {
	private Avoid avoid;
	private ObstacleF22 ObstacleF22;
	private ImageIcon imageF;
	private Rectangle[] r;
	int Num;
	int x;
	int y;
	int count;
	private int mSpeed;
	private int sumSpeed;
	private int polx[] = { 32, 0, 32, 52, 53 };
	private int poly[] = { 10, 34, 80, 55, 22 };
	private boolean Ob_isrun;
	private boolean iscrash;// 충돌 체크 여부
	private boolean lock;
	java.util.Timer booking;
	Timer t1;
	Timer t2;

	ObstacleF22(Avoid avoid, int n, int x) {
		this.avoid = avoid;
		this.Num = n;
		this.x = x; // 나중에 가변 코드 작성.
		y = 0;
		r = new Rectangle[2];
		init();
	}

	public void init() {
		imageF = new ImageIcon(Main.class.getResource("images/ruby.png"));
		setSize(53, 70);
		setIcon(imageF);
		Ob_isrun = false;
		ObstacleF22 = this;
		iscrash = false;
		lock = false;
		mSpeed = 1;
		r[0] = new Rectangle(x, y, 70, 70);
		r[1] = new Rectangle(avoid.getmanX(), avoid.getmanY(), 70, 140);
		t1 = new Timer(50, e -> {
			CheckCrash();
			moveOB();
		});
		booking = new java.util.Timer(false);
		sumSpeed=  0;
	}

	@Override
	public void run() { // 장애물을 생성하고 add합니다~
		Ob_isrun = true;
		setLocation(x, 10);
		startThread();// 1
	}

	public void startThread() {// 1
		// 충돌 체크
		// 람다식은 메소드를 객체로 취급하는 기능이다.
		// 람다식을 사용해 무명 클래스를 간결하게 표현했다.
		// timer 클래스는 actionPerformed() 메소드를 호출한다. ACtionListener 인터페이스를 구현한다.
		t1.start();
		setTimer(() -> {
			if (!iscrash)// 충돌안했으면
				finish();
		}, 50*33 ); 
		// 1. 1씩 줄어들때총이동거리 512 = time* 1000/(50) * mSpeed
		// 522 * 50 / mSpeed 초
		//2. 미끄러질때 
		//-1 + Math.sqrt(1045) 32.3264..--> 32
		//522 = tiem * 32 * 1000/50
	}

	public void setOb_run(boolean isrun) { // main에서 조정하는거.
		this.Ob_isrun = isrun;
	}

	public boolean Ob_isrun() {
		return Ob_isrun;
	}

	public void moveOB() { // 아래로 이동합니다.
		sumSpeed += mSpeed;
		y +=sumSpeed;
		this.setLocation(x, y);
	}

	public void CheckCrash() {// 충돌을 체크합니다.
		if (!lock) {
			lock = true;
			if (iscrash)// 충돌했으면 아무것도 안함.
			{
				t1.stop();
				return;
			}

			r[1].setLocation(avoid.getmanX(), avoid.getmanY());
			for (int i = 0; i < polx.length; i++) {// 드릴 꼭짓점.
				if (r[1].contains(polx[i]+x, poly[i] +y)) {
					iscrash = true;
					finish();
					break;
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
		avoid.removeobject(this.Num);
	}

	// 스레드 끄고 삭제요청.
	public void finish() {
		Ob_isrun = false;
		t1.stop();
		if (iscrash) {
			avoid.crash(this.Num);
		} else {
			avoid.removeobject(this.Num);
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

}