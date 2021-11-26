import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ShootingGame extends JPanel implements MouseListener, Runnable { // ǥ�� ���߱� ����
	// ���� ��ǥ�� ǥ�� ����. (��Ÿ���� ������� ǥ��. ���̵��� �ð��� ����.)
	// �ð� �ȿ� ���콺 Ŭ�� �ؾ���.()
	// ���콺 Ŭ���ϸ�, ���� + 1 .
	private Image screenImage; // ������۸� ���.
	private Graphics screenGraphic;
	private ShootingGame shoot;
	JLabel circle;
	int gamelife;
	private int CN;
	private MyFrame myframe;
	private Image backgroundImage;
	private int time = 0;
	private int Limtime = 20; // �ð� ���� 20��.

	private Thread t1;
	private Thread t2;
	private Thread t3;
	private Thread t4;
	private boolean isthread1Run;
	private boolean isthread2Run;
	
	private Circle c;
	boolean isrun = false;
	Music music;
	List<Circle> circlelist = Collections.synchronizedList(new ArrayList<>());
	boolean isshow = true;
	
	ShootingGame(MyFrame myFrame) { // ȭ�� ����.
		backgroundImage = new ImageIcon(Main.class.getResource("images/Gamebackground.png")).getImage(); // �ʱ� ��׶��� ����
		this.myframe = myFrame;
		this.shoot = this;
	
	}
	@Override
	public void paintComponent(Graphics g) {
		// paint the BGI and scale it to fill the entire space
		super.paintComponent(g);
		// Creates an off-screen drawable image to be used for double buffering.
		// screenImage = createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		// Creates a graphics context for drawing to an off-screen image.
		// screenGraphic = screenImage.getGraphics();
		// doublebuffering((Graphics2D) screenGraphic);
		g.drawImage(backgroundImage, 0, 0, null);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
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

		if (!isrun || circlelist.size() == 0)// ������� �������� return.
			return;
		for (Circle c : circlelist) {
			if (getdistance(e.getX(), e.getY(), c.getcircleX()+35, c.getcircleY()+35) <= c.getradius()) {
				remove(c);
				circlelist.remove(c);
				return;
			}
		}
		loselife();
	}

	public void init() {
		isrun = true;
		music = new Music("Lets_Practice_-_Rhythm_Heaven_Fever.MP3", false);
		music.start();
		isshow = false;
		gamelife = 5;
		time = 0;
		isthread1Run = true;
		isthread2Run = true;
	}
	@Override
	public void run() { // ������ ����.
		try {
			init();
			checkT();
			Thread.sleep(2000);
			/*
			 * int timerDelay = 20; new Timer(timerDelay, new ActionListener(){ public void
			 * actionPerformed(ActionEvent e) {
			 * 
			 * } }).start();
			 */
			CN = 0;
			initT();
			//start All thread
			t1.start();
			t2.start();
			t3.start();
			t4.start();
		} catch (Exception e) {
			e.printStackTrace();// : ������ �߻��ٿ����� ã�Ƽ� �ܰ躰�� ������ ����մϴ�.
		}

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
	public void close() {
		stopAll();
		music.stop();
		circlelist.clear();// ����Ʈ �� ��
		removeAll();
		removeMouseListener(this);
		new Thread(() -> {
			try {
				Thread.sleep(2500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	public void checkT() {
		if(t1 != null && t1.isAlive())t1.interrupt();
		if(t2 != null && t2.isAlive())t2.interrupt();
		if(t3 != null && t3.isAlive())t3.interrupt();
		if(t4 != null && t4.isAlive())t4.interrupt();
	}
	
	public void initT() {
		// thread1 ������ �˻�.
		isthread1Run = true;
		isthread2Run = true;
		isrun = true;
		t1 = new Thread(() -> {
			while (isthread1Run) {
				try {
					if(!isthread1Run)
						break;
					checklife();// ������ �˻�.
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
		// thread 2 �ð��˻�
		t2 = new Thread(() -> {
			while (isthread2Run) {
				try {
					if(!isthread2Run)
						break;
					istimeout();
					time++;
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// �� ����
		t3 = new Thread(() -> {
			while (isrun) {
				try {
					if(!isrun)
						break;
					makecircle(CN++);// 1�ʿ� �ѹ� ���� �����Ѵ�.
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
	
		t4 = new Thread(() -> {
			while (isrun) {
				try {
					if(!isrun)
						break;
					repaint();
					Thread.sleep(30);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	//��� ������ ����
	public void stopAll() {
		isrun = false;
		isthread2Run = false;
		isthread1Run = false;
		shoot = null;
	}
	public void istimeout() {
		if (time >= Limtime) {
			stopAll();
			checklife();
		}
	}
	public double getdistance(int mX, int mY, int cX, int cY) {
		return Math.sqrt((mX - cX) * (mX - cX) + (mY - cY) * (mY - cY));
	}

//System.currenttiimeliies.
	// �� ���� Ȯ��
	public boolean isremovecircle(int num) {
		if (circlelist.size() <= 0)
			return true;
		if (circlelist.get(0).getcircleN() != num)
			return true;
		repaint();
		return false;
	}

	public void makecircle(int num) {
		Random rand = new Random();
		// x��ǥ�� 150 ~ 1000
		// y��ǥ�� 150 ~ 570 ���� ��ǥ ����.
		Circle c = new Circle(shoot, num);
		new Thread(c).start();
	}

	public void addcircle(Circle c) {
		circlelist.add(c);
		add(c);
	
		System.out.println("���� �� --> " + c.getcircleN() + "�� ����:" + circlelist.size());
	}

	public void loselife() {
		gamelife--;
		System.out.println("���� Ŭ���ϼ��� ���� ������ : " +gamelife);
		
		if (gamelife == 0)
			checklife();
	}

	public void removecircle(int num) {
		if (circlelist.size() == 0)
			return;
		Circle temp = circlelist.get(0);
		if (temp.getcircleN() == num) {
			remove(temp);
			circlelist.remove(temp);
			gamelife--;
			System.out.println("�����Ϸ� : " +gamelife);
		}
	}

}
