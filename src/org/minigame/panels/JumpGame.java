package org.minigame.panels;
import java.awt.Color;
import java.awt.Font;
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

import org.minigame.main.Main;
import org.minigame.objects.JL_Life;
import org.minigame.objects.Stickman;
import org.minigame.tools.RemoveBackground;
public class JumpGame extends JPanel implements Runnable, KeyListener { // ǥ�� ���߱� ����
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
	private boolean lock; // ���� �޼ҵ� �� .
	private boolean yap;
	private boolean finish;
	private Random r;
	private long curtime = 0; // ��� �ð� �и��ʴ���
	private int Limtime = 20; // �ð� ���� 20��.
private int time=0;
	private int ON;
	private int calltime;//�θ�Ƚ��
	
	private long startime = 0;
	private Timer t[];
	private java.util.Timer booking;
	// volatile Ű���带 ���������μ� ���� �б� �۾��� ���� �޸𸮿��� �̷���.
	ObstacleF obf;
	Queue<Integer> removelist;
	Hashtable<Integer, ObstacleF> oblist;

	JumpGame(MyFrame myFrame) {
		setLayout(null);
		this.jump = this;
		this.myframe = myFrame;
		this.stickman = new Stickman();
		calltime =0 ;
		r = new Random();
		backgroundImage = new ImageIcon(getClass().getResource("../images/Jumpbackground.png")).getImage();
		letter = new RemoveBackground("Letter/jump.png").getImage();
		Lpass = new RemoveBackground("Letter/passed.png").getImage();
		Lfail = new RemoveBackground("Letter/failed.png").getImage();

		oblist = new Hashtable<>();
		removelist = new LinkedBlockingQueue<>(10);
		booking = new java.util.Timer(false);
		setT();
		
	}

	public void init() {
		gamelife = 3;
		yap = false;
		lock = false;
		ON = 0; //0���� �ʱ�ȭ
		isdown = false;
		addKeyListener(this);
		requestFocus();
		add(stickman);
		finish = false;
		addlife();
		repaint();
	}

	public void setT() {
		t = new Timer[2];
		t[0] = new Timer(30, (e) -> {
			istimeout();
			removeobject();
			checklife();// ������ �˻�.
			curtime = System.currentTimeMillis() - startime;
			repaint();
		});

		t[1] = new Timer(2000, (e) -> {
			makeobject();
		});
	}

	@Override
	public void run() {
		startime = 0;
		calltime++;
		yap = false;
		isrun = false;
		finish = false;
		sleep(60);

		repaint();
		setTimer(() -> {
			init();// �ʱ�ȭ�ϰ�
		}, (long) 1000);

		setTimer(() -> {
			startT();// ������ ����.
		}, (long) 1500);

	}

	public void stopT() {
		isrun = false;
		for (int i = 0; i < 2; i++) {
			t[i].stop();
		}
	}

	public void close() {
		// ������ ����
		finish = true;
		repaint();
		sleep(4); // ������ ����
		stickman.stopStickman();
		stopT();
		remove(stickman);
		clearlife();
		this.removeAll();
		this.removeKeyListener(this);
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

	// ��� ������ ����
	public void stopAll() {
		isrun = false;
	}

	public void crash(int num) {// �浹������?
		if (!isrun)
			return;// ���ӳ������� ����.
		this.gamelife -= 1;
		minuslifeImage();
		System.out.println("�浹�� ������ : " + gamelife);
		checklife();

		removelist.add(num);
	}

	public void checklife() {
		if (gamelife <= 0 && !finish) // ���������
		{
			yap = true;
			Lpass = null;
			finish = true; // �����Ѵ�. �ߺ�ȣ�� ����.
			repaint();
			setTimer(() -> {
				close();
			}, (long) 1600);
			setTimer(() -> {
				removelist.clear(); //�� ������������ ���� �д�. 
				oblist.clear();
				myframe.gamefailed(); // ����
			}, (long) 2000);
		} else if (isrun == false && gamelife > 0 && !finish) {// ���� �����.
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

	public void makeobject() { // ������Ʈ ����.
		obf = new ObstacleF(jump, ON, r.nextInt(2) , calltime);
		oblist.put(ON++, obf);
		add(obf);
		obf.run();
	}

	public void removeobject(int num) {
		removelist.add(num);
	}

	// �׸��׸���
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, null);

		if (!isrun && !finish) {
			g.drawImage(letter, 535, 210, null);
		}
		// �������� ���� ����
		if (yap) {
			if (Lpass != null)
				g.drawImage(Lpass, 535, 210, null);
			if (Lfail != null)
				g.drawImage(Lfail, 535, 210, null);
		}
		g.setFont(new Font("Gulim", Font.BOLD, 50));
		g.setColor(Color.black);
		g.drawString("������� : "+ myframe.life(), 900, 70);
		g.drawString("���� : " + myframe.getpoint() , 20, 70);
		time = Limtime - (int) curtime/1000 ;
		if(time < 0)
			g.drawString("�����ð� : "+ 20, 900, 650);
		if(time>=0)
		g.drawString("�����ð� : "+time, 900, 650);
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

	public int getheight() { // 1�̸� �⺻, 2�� ���帰��.
		return stickman.height();
	}

	public void removeobject() {
		if (!lock) {
			lock = true;
			int temp;
			while (!removelist.isEmpty()) {
				temp = removelist.poll();
				if(oblist.containsKey(temp))//���������. 
				remove(oblist.get(temp)); //cannot read field parent
				oblist.remove(temp);
				repaint();
			}
			lock = false;
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
	public void clearlife() {
		for (int i = 0; i < this.gamelife; i++) {
			this.remove(life[i]);
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
	//������ ������ removeAll ȣ���� ���� ���ӿ��� ��ü ���� �䱸�� ������ ����.
	public int getCallTime() { 
		return calltime;
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
	private boolean iscrash;// �浹 üũ ����
	private int type;
	private int calltime;
	Rectangle r[];
	Polygon p;
	Timer t1;
	Timer t2;
	java.util.Timer booking;

	public ObstacleF(JumpGame jump, int num, int type,int calltime) {
		count = 0;
		this.jump = jump;
		this.calltime = calltime;
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
		imageF[0] = new ImageIcon(getClass().getResource("../images/obstacleF.png"));
		imageF[1] = new ImageIcon(getClass().getResource("../images/drill.png"));

		Ob_isrun = false;
		obstaclef = this;
		iscrash = false;
		lock = false;

		booking = new java.util.Timer(false);

		t1 = new Timer(50, e -> {
			CheckCrash();
		});
		t1.setInitialDelay(500); // 0.5�� �ڿ� Ÿ�̸Ӹ� �����մϴ�.
		t2 = new Timer(30, e -> { // move.
			x -= (mSpeed);
			setLocation(x, y);
			count++;
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
	public void run() { // ��ֹ��� �����ϰ� add�մϴ�~
		Ob_isrun = true;
		startThread();// 1
	}

	public void startThread() {// 1
		t1.start();
		t2.start();
		setTimer(() -> {
			if (!iscrash)// �浹��������
				finish();
		}, 10000); // 10�ʵڿ� ���� ��û
		// �浹 üũ
		// ���ٽ��� �޼ҵ带 ��ü�� ����ϴ� ����̴�.
		// ���ٽ��� ����� ���� Ŭ������ �����ϰ� ǥ���ߴ�.
		// timer Ŭ������ actionPerformed() �޼ҵ带 ȣ���Ѵ�. ACtionListener �������̽��� �����Ѵ�.
	}

	public boolean Ob_isrun() {
		return Ob_isrun;
	}

	public void CheckCrash() {// �浹�� üũ�մϴ�.
		if (!lock) {
			lock = true;
			if (iscrash)// �浹������ �ƹ��͵� ����.
			{
				t1.stop();
				return;
			}
			if (type == 0) {
				r[0].setLocation(x, y);
				r[1].setBounds(jump.getmanX(), jump.getmanY(), 70, jump.getheight());
				if (r[0].intersects(r[1])) // �浹������
				{
					crash();
				}
			}

			if (type == 1) {
				r[1].setBounds(jump.getmanX(), jump.getmanY(), 70, jump.getheight());
				for (int i = 0; i < 5; i++) {// �帱 ������.
					if (r[1].contains(polx[i] - (count * mSpeed), poly[i])) {
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
		if(calltime == jump.getCallTime())
		jump.crash(ON);
	}

	// ������ ���� ������û.
	public void finish() {
		Ob_isrun = false;
		t1.stop();
		t2.stop();
		if(calltime == jump.getCallTime())
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