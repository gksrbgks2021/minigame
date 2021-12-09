import java.awt.Graphics;
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

public class Avoid extends JPanel implements Runnable { // ǥ�� ���߱� ����

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
	private int Limtime = 20; // �ð� ���� 20��.
	private int countO;
	private int gamelife;
	private Timer t[];
	java.util.Timer booking;
	private JLabel[] life;
	// volatile Ű���带 ���������μ� ���� �б� �۾��� ���� �޸𸮿��� �̷���.

	Avoid(MyFrame myFrame) {
		setLayout(null);
		this.myframe = myFrame;
		this.stickman = new Stickman();
		gamelife = 3;
		oblist = new Hashtable<>();
		removelist = new LinkedBlockingQueue<>(10);
		avoid = this;
		backgroundImage = new ImageIcon(Main.class.getResource("images/Jumpbackground.png")).getImage();

		letter = new RemoveBackground("Letter/avoid.png").getImage();
		Lpass = new RemoveBackground("Letter/passed.png").getImage();
		Lfail = new RemoveBackground("Letter/failed.png").getImage();
		booking = new java.util.Timer(false);
		add(stickman);

		life = new JLabel[this.gamelife];
		t = new Timer[2];
		t[0] = new Timer(50, (e) -> {
			istimeout();
			checklife();// ������ �˻�.
			if (!lock && isrun)
				removeobject();
		});

		t[1] = new Timer(3000, (e) -> {
			initobject();
		});

	}

	public void init() {
		gamelife = 3;
		yap = false;
		isrun = false;
		isdown = false;
		lock = false;
		finish = false;
		keybind();
		requestFocus();
		addlife();
	}

	public void keybind() {
		addKeyBindingP(this, KeyEvent.VK_RIGHT, "rightpress", (evt) -> {
			stickman.Right();
		});

		addKeyBindingL(this, KeyEvent.VK_RIGHT, "rightrelease", (evt) -> {
			stickman.stop_R();
		});

		addKeyBindingP(this, KeyEvent.VK_ESCAPE, "END_GAME", (evt) -> {
			System.exit(0);// ����ȴ�.
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
			startT();// ������ ����.
		}, (long) 1500);
	}

	public void stopT() {
		isrun = false;
		for (int i = 0; i < t.length; i++) {
			t[i].stop();
		}
	}

	public void close() {
		// ������ ����
		stopT();
		// Ű���ε� ����
		this.getInputMap().clear();
		this.getActionMap().clear();

		while (removelist.size() > 0) {
			removeobject();
			oblist.clear();
			sleep((long) 50);
		}
	}

	public void startT() {
		startime = System.currentTimeMillis();
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

	public void crash(int num) {// �浹������?
		if (!isrun)
			return;// ���ӳ������� ���.
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
	public void initobject() {
		System.out.println("jump Ŭ�������� ��ֹ� ����");
		obf = new ObstacleF22(avoid, countO++);
		addobject(obf);// ����Ʈ�� �߰�.
		obf.setOb_run(true);
		new Thread(obf).start();
	}

	public synchronized void addobject(ObstacleF22 f) {
		add(f);
		oblist.put(f.Num, f);
	}

	public void removeobject(int Num) {
		removelist.add(Num);
	}

	public void removeobject() { // �����ε�
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

	// �׸��׸���
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

	// �� jcomponent�� 1�� actionMap 3�� inputMap �� ������.
	public void addKeyBindingP(JComponent comp, int keyCode, String id, ActionListener actionListener) {
		InputMap im = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap ap = comp.getActionMap();
		// getKeyStroke ( keyCode, ��� x�� 0 �ٸ� modifier�� �����Ϸ��� �����Ѵ�. true�� Ű�� press ����
		// ��������. ����)
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

		// getKeyStroke ( keyCode, ��� x�� 0 �ٸ� modifier�� �����Ϸ��� �����Ѵ�. true�� Ű�� press ����
		// ��������. ����)
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
	private int polx[] = { 132, 99, 132, 152, 153 };
	private int poly[] = { 10, 34, 80, 55, 22 };
	private boolean Ob_isrun;
	private boolean iscrash;// �浹 üũ ����
	private boolean lock;
	java.util.Timer booking;
	Timer t1;
	Timer t2;

	ObstacleF22(Avoid avoid, int n) {
		this.avoid = avoid;
		this.Num = n;
		x = 100; // ���߿� ���� �ڵ� �ۼ�.
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
		mSpeed = Main.GAME_SPEED + 1;
		r[0] = new Rectangle(x, y, 70, 70);
		r[1] = new Rectangle(avoid.getmanX(), avoid.getmanY(), 70, 140);
		t1 = new Timer(50, e -> {
			CheckCrash();
			moveOB();
		});
		booking = new java.util.Timer(false);
		count = 0;
	}

	@Override
	public void run() { // ��ֹ��� �����ϰ� add�մϴ�~
		Ob_isrun = true;
		setLocation(x, 10);
		startThread();// 1
	}

	public void startThread() {// 1
		// �浹 üũ
		// ���ٽ��� �޼ҵ带 ��ü�� ����ϴ� ����̴�.
		// ���ٽ��� ����� ���� Ŭ������ �����ϰ� ǥ���ߴ�.
		// timer Ŭ������ actionPerformed() �޼ҵ带 ȣ���Ѵ�. ACtionListener �������̽��� �����Ѵ�.
		t1.start();
		setTimer(() -> {
			if (!iscrash)// �浹��������
				finish();
		}, 522 * 50 / mSpeed); // ���̵��Ÿ� 512 = time* 1000/(50) * mSpeed
	}

	public void setOb_run(boolean isrun) { // main���� �����ϴ°�.
		this.Ob_isrun = isrun;
	}

	public boolean Ob_isrun() {
		return Ob_isrun;
	}

	public void moveOB() { // �Ʒ��� �̵��մϴ�.
		y += mSpeed;
		count++;
		this.setLocation(x, y);
	}

	public void CheckCrash() {// �浹�� üũ�մϴ�.
		if (!lock) {
			lock = true;
			if (iscrash)// �浹������ �ƹ��͵� ����.
			{
				t1.stop();
				return;
			}

			r[1] = new Rectangle(avoid.getmanX(), avoid.getmanY(), 70, 140);
			for (int i = 0; i < 5; i++) {// �帱 ������.
				if (r[1].contains(polx[i], poly[i] + (count * mSpeed))) {
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

	// ������ ���� ������û.
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