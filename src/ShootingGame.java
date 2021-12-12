import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class ShootingGame extends JPanel implements MouseListener, Runnable { // 표적 맞추기 게임
	// 랜덤 좌표에 표적 생성. (나타났다 사라지는 표현. 난이도별 시간초 있음.)
	// 시간 안에 마우스 클릭 해야함.()
	// 마우스 클릭하면, 점수 + 1 .
	private ShootingGame shoot;
	JLabel circle;
	int gamelife;
	private int CN;
	private MyFrame myframe;
	private Image backgroundImage;
	private int time = 0;
	private int Limtime = 20; // 시간 제한 20초.
	private int startime;

	private Timer t1;
	private Timer t2;
	private Timer t3;
	private Timer t4;
//	private Thread t4;
	private boolean finish;

	private boolean yap;
	private boolean lock; // 제거 메소드 락 .
	private boolean isremoveAll; // 제거 리스트 큐가 다 제거되면 OK
	private Image letter;
	private Image Lgood;
	private Image Lbad;
	private Image Lpass;
	private Image Lfail;
	private JLabel[] life;
	private int startIndex;
	private java.util.Timer booking;
	private Circle c;
	private Random rand;
	boolean isrun = false;
	Music music;
	List<Circle> circlelist;
	Queue<Circle> removelist;

	ShootingGame(MyFrame myFrame) { // 화면 구성.
		setLayout(null);
		this.myframe = myFrame;
		this.shoot = this;//close에 shoot == null이있어서 안돌아갔었음

		rand = new Random();
		backgroundImage = new ImageIcon(Main.class.getResource("images/Gamebackground.png")).getImage(); // 초기 백그라운드 설정

		letter = new RemoveBackground("Letter/click.png").getImage();
		Lgood = new RemoveBackground("Letter/good.png").getImage();
		Lbad = new RemoveBackground("Letter/bad.png").getImage();
		Lpass = new RemoveBackground("Letter/passed.png").getImage();
		Lfail = new RemoveBackground("Letter/failed.png").getImage();
		booking = new java.util.Timer(false);
		setT();
		circlelist = Collections.synchronizedList(new ArrayList<>());
		removelist = new LinkedBlockingQueue<>(10);
	}

	@Override
	protected void paintComponent(Graphics g) {
		// paint the BGI and scale it to fill the entire space
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, null);

		if (!isrun && !finish) {
			g.drawImage(letter, 535, 210, null);
		}
		// 끝났으면 글자 띄우기
		if (yap && finish) {
			if (Lpass != null)
				g.drawImage(Lpass, 535, 210, null);
			if (Lfail != null)
				g.drawImage(Lfail, 535, 210, null);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("마우스칼릭담");
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

	@Override
	public void mousePressed(MouseEvent e) {
		boolean isremove = false;
		if (!isrun || circlelist.size() == 0)// 실행되지 않을때는 return.
			return;
		for (int i = startIndex; i < circlelist.size(); i++) {
			Circle c = circlelist.get(i);
			if (getdistance(e.getX(), e.getY(), c.getcircleX() + 35, c.getcircleY() + 35) <= c.getradius()) {
				c.Clicked();
				isremove = true;
				return;
			}
		}
		if (!isremove)
			loselife();
	}

	public void init() {
		this.gamelife = 5;
		yap = false;
		lock = false;
		isrun = false;
		finish = false;
		CN = 0;
		startIndex = 0;
		gamelife = 5;
		time = 0;
		addlife();
		repaint();
	}

	@Override
	public void run() { // 스레드 동작.
		init();
		sleep(60);
		repaint();

		// start All thread
		setTimer(() -> {
			music = new Music("Lets_Practice_-_Rhythm_Heaven_Fever.MP3", false);
			startime = 0;
			isrun = true;
			t1.start();
			t2.start();
			music.start();
		}, (long) 1500);

		// t3.start();
		// t4.start();
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
			}, (long) 1300);
		} else if (isrun == false && gamelife > 0 && !finish) {// 게임 통과시.
			yap = true;
			Lfail = null;
			finish = true;
			repaint();
			setTimer(() -> {
				close();
				myframe.gamepassed();
			}, (long) 1300);
		}
	}

	public void close() {
	
		stopAll();
		music.stop();
		circlelist.clear();// 리스트 삭 제
		removeAll();

		clearlife();
	}

//	public void checkT() {
//		if (t != null && t.isAlive())
//			t.interrupt();
//		if (t2 != null && t2.isAlive())
//			t2.interrupt();
//		if (t3 != null && t3.isAlive())
//			t3.interrupt();
//		if (t4 != null && t4.isAlive())
//			t4.interrupt();
//	}

	public void setT() {
		t1 = new Timer(40, (e) -> {
			System.out.println("ㅁㅁㅁ");
			istimeout();
			removeobject();
			repaint();
		});
		t2 = new Timer(1000, (e) -> {
			makecircle(CN++);// 1초에 한번 원을 생성한다.
			startime++;
		});

	}

//타이머 종료
	public void stopAll() {
		t1.stop();
		t2.stop();
		isrun = false;
	}

	public void istimeout() {
		if (startime >= Limtime) {
			stopAll();
			checklife();
		}
	}

	public double getdistance(int mX, int mY, int cX, int cY) {
		return Math.sqrt((mX - cX) * (mX - cX) + (mY - cY) * (mY - cY));
	}

//System.currenttiimeliies.
	// 원 삭제 확인
//	public boolean isremovecircle(int num) {
//		if (circlelist.size() <= 0)
//			return true;
//		if (circlelist.get(0).getcircleN() != num)
//			return true;
//		repaint();
//		return false;
//	}

	public void makecircle(int num) {
		// x좌표는 150 ~ 1000
		// y좌표는 150 ~ 570 랜덤 좌표 생성.
		Circle c = new Circle(shoot, num, rand.nextInt(2));
		circlelist.add(c);
		add(c);
		c.run();
	}

//	public synchronized void addcircle(Circle c) {
//		circlelist.add(c);
//		add(c);
//	}

	public void loselife() {
		gamelife--;
		System.out.println("원을 클릭하세요 현재 라이프 : " + gamelife);
		minuslifeImage();
		if (gamelife == 0)
			checklife();
	}

	public void minuslifeImage() {
		if (gamelife > 0) {
			remove(life[gamelife]);
		}
		if (gamelife <= 0) {
			remove(life[0]);
		}
	}

	public void Requestremovecircle(int num, boolean check) {
		if (circlelist.size() == 0)
			return;
		removelist.add(circlelist.get(num));
		startIndex++;
		if (check) {
			gamelife--;
			minuslifeImage();
		}
//			System.out.println("삭제완료 : " + gamelife);
	}

	public void removeobject() {
		if (!lock) {
			lock = true;
			Circle temp;
			while (!removelist.isEmpty()) {
				temp = removelist.poll();
				if (temp != null) {
					remove(temp);
					repaint();
				}
			}
			lock = false;
			isremoveAll = true;
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

	public void sleep(long mili) {
		try {
			Thread.sleep(mili);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addlife() {
		life = new JLabel[this.gamelife];
		for (int i = 0; i < this.gamelife; i++) {
			life[i] = new JL_Life();
			life[i].setLocation(100 + i * 80, 600);
			add(life[i]);
		}
	}

	public void clearlife() {
		for (int i = 0; i < this.gamelife; i++) {
			this.remove(life[i]);
		}
	}
}