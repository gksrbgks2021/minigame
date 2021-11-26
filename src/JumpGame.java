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

public class JumpGame extends JPanel implements Runnable, KeyListener { // ǥ�� ���߱� ����
	int gamelife;
	private MyFrame myframe;
	private JumpGame jump;
	private Image backgroundImage;
	private volatile boolean isrun;
	private boolean isdown;
	private boolean isThread1;
	private Stickman stickman;
	private int time = 0;
	private int Limtime = 20; // �ð� ���� 20��.
	private int x;
	private int y;
	// volatile Ű���带 ���������μ� ���� �б� �۾��� ���� �޸𸮿��� �̷���.
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
			System.exit(0);// ����ȴ�.
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
			startT();// ������ ����.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopT() {
		isrun = false;
	}

	public void close() {
		// ������ ����
		stopT();
		while (!isremoveAll)
			; // �� �����ҋ����� busy waiting
	}

	public void startT() {
		new Thread(() -> { // ������ �� ��
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
					checklife();// ������ �˻�.
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		// �� ����

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

	// ��� ������ ����
	public void stopAll() {
		isrun = false;
	}

	public void crash() {// �浹������?
		this.gamelife -= 1;
		System.out.println("�浹�� ������ : " + gamelife);
		checklife();
	}

	public void checklife() {
		if (gamelife <= 0) // ���������
		{
			close();
			myframe.gamefailed(); // ����
		} else if (isrun == false && gamelife > 0) {// ���� �����.
			close();
			myframe.gamepassed();
		}
	}

	public void initobject() {
		isremoveAll = false;
		System.out.println("jump Ŭ�������� ��ֹ� ����");
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
		if (oblist.size() == 0)// �������ϋ�
			isremoveAll = true;
	}

	// ���� ���۸�.
	public void doublebuffer(Graphics g) {

	}

	// �׸��׸���
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
			if (!stickman.isUp() && !stickman.isgoDown())// �̹� �����ϰ������� ȣ�� x
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
	private boolean iscrash;// �浹 üũ ����
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
		jump.addobject(obstaclef); // ��ֹ��� �����մϴ�~.
	}

	@Override
	public void run() { // ��ֹ��� �����ϰ� add�մϴ�~
		Ob_isrun = true;
		startThread();// 1
	}

	public void startThread() {// 1
		addObstacle();
		moveOB();
		// �浹 üũ
		// ���ٽ��� �޼ҵ带 ��ü�� ����ϴ� ����̴�.
		// ���ٽ��� ����� ���� Ŭ������ �����ϰ� ǥ���ߴ�.
		// timer Ŭ������ actionPerformed() �޼ҵ带 ȣ���Ѵ�. ACtionListener �������̽��� �����Ѵ�.
		t1 = new Timer(50, e -> {
			CheckCrash();
		});
		t1.setInitialDelay(1000); // 1�� �ڿ� Ÿ�̸Ӹ� �����մϴ�.
		t2 = new Timer(50, e -> {
			if (!jump.isrun()) {
				finish();
			}
		});
		t1.start();
		t2.start();
		try {
			Thread.sleep(7000);// 5�ʵڿ� ���� ��û.
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setOb_run(boolean isrun) { // main���� �����ϴ°�.
		this.Ob_isrun = isrun;
	}

	public boolean Ob_isrun() {
		return Ob_isrun;
	}

	public void moveOB() { // �������� �̵��մϴ�.

		new Thread(() -> {
			for (int i = 0; i < 400; i++) {
				if (!Ob_isrun())// ��������������
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

	public void CheckCrash() {// �浹�� üũ�մϴ�.
		// ��ֹ��� 4 �������� �����մϴ�.
		int x1 = jump.getmanX();
		int x2 = this.x;
		int y1 = jump.getmanY();
		int y2 = this.y;
		if (iscrash)// �浹������ �ƹ��͵� ����.
			return;

		if ((x2 >= x1 && x2 <= x1 + 70) && (y2 >= y1 && y2 <= y1 + 140)// ������ ������.
				|| (x2 + 53 >= x1 && x2 + 53 <= x1 + 70) && (y2 >= y1 && y2 <= y1 + 140)// ��������������.
				|| (x2 >= x1 && x2 <= x1 + 70) && (y2 + 70 >= y1 && y2 + 70 <= y1 + 140)// ���ʾƷ�
				|| (x2 + 53 >= x1 && x2 + 53 <= x1 + 70) && (y2 + 70 >= y1 && y2 + 70 <= y1 + 140))// �����ʾƷ�
		{
			iscrash = true;
			jump.crash();
		}
	}

	// ������ ���� ������û.
	public void finish() {
		Ob_isrun = false;
		t1.stop();
		t2.stop();
		jump.removeobject(obstaclef);
	}
}