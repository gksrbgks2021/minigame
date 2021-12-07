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

public class Avoid extends JPanel implements Runnable { // ǥ�� ���߱� ����
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
	private int Limtime = 20; // �ð� ���� 20��.
	private int x;
	private int y;
	private int countO;
	private Timer t1, t2, t3;
	// volatile Ű���带 ���������μ� ���� �б� �۾��� ���� �޸𸮿��� �̷���.
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
		// �� ���� ������ �����ս�
		this.setDoubleBuffered(true);

		t1 = new Timer(50, (e) -> {
			istimeout();
			checklife();// ������ �˻�.
			if (!lock && isrun)
				removeobject();
			Thread.currentThread().setName("66�� ������");
			curtime = System.currentTimeMillis() - startime;
		});

		t2 = new Timer(3000, (e) -> {
			initobject();
			Thread.currentThread().setName("72�� ������");
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
		startime = System.currentTimeMillis();
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
		t1.stop();
		t2.stop();
	}

	public void close() {
		// ������ ����
		stopT();
		//Ű���ε� ����
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
		obf = new ObstacleF22(avoid,countO++);
		addobject(obf);// ����Ʈ�� �߰�.
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

	public  void removeobject() { // �����ε�
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

	// �׸��׸���
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
	private boolean iscrash;// �浹 üũ ����
	Timer t1;
	Timer t2;

	ObstacleF22(Avoid avoid , int n) {
		this.avoid = avoid;
		this.Num= n;
		init();
		x = 100; // ���߿� ���� �ڵ� �ۼ�.
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
	public void run() { // ��ֹ��� �����ϰ� add�մϴ�~
		Ob_isrun = true;
		startThread();// 1
	}

	public void startThread() {// 1

		// �浹 üũ
		// ���ٽ��� �޼ҵ带 ��ü�� ����ϴ� ����̴�.
		// ���ٽ��� ����� ���� Ŭ������ �����ϰ� ǥ���ߴ�.
		// timer Ŭ������ actionPerformed() �޼ҵ带 ȣ���Ѵ�. ACtionListener �������̽��� �����Ѵ�.
		t1 = new Timer(50, e -> {
			CheckCrash();
			moveOB();
			if (!avoid.isrun() || y >= 523) {
				finish();
			}
		});
		t1.setInitialDelay(1000); // 1�� �ڿ� Ÿ�̸Ӹ� �����մϴ�.
		t1.start();
		while (Ob_isrun) {
			try {
			
				Thread.sleep(60);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setOb_run(boolean isrun) { // main���� �����ϴ°�.
		this.Ob_isrun = isrun;
	}

	public boolean Ob_isrun() {
		return Ob_isrun;
	}

	public void moveOB() { // �Ʒ��� �̵��մϴ�.
		y += Main.GAME_SPEED;
		this.setLocation(x, y);
	}

	public void CheckCrash() {// �浹�� üũ�մϴ�.
		if (iscrash)// �浹������ �ƹ��͵� ����.
			return;
		// ��ֹ��� 4 �������� �����մϴ�.
		int x1 = avoid.getmanX();
		int x2 = this.x;
		int y1 = avoid.getmanY();
		int y2 = this.y;
		Rectangle r1 = new Rectangle(x1, y1, 70, 140);
		Rectangle r2 = new Rectangle(x2, y2, 70, 70);
		// System.out.println("���� ��ǥ (" +this.x + ","+this.y+") ����:"
		// +image.getRGB(x2,y2));

		if (r1.intersects(r2) && !iscrash)// ��ġ��
		{iscrash = true;
			System.out.println("����� ��ǥ: " + avoid.getmanX() + " " + avoid.getmanY());
			finish();
			avoid.crash();
		}
	}

	// ������ ���� ������û.
	public void finish() {
		t1.stop();
		if(Ob_isrun)//�ߺ� ȣ�� ����. 
		avoid.removeobject(this.Num);
		setOb_run(false);
	}

}