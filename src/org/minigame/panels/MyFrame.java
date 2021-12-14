package org.minigame.panels;

import java.awt.CardLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JFrame;

import org.minigame.main.Main;

public class MyFrame extends JFrame implements MouseListener {
	private MyFrame gameFrame = this;
	private MainP mainP;
	private ShootingGame shoot;
	private JumpGame jump;
	private GameOver over;
	private RhythmGame rh;
	private Avoid avoid;

	public boolean isrun; // ���ӽ���
	private int CurPoint = 0;
	private int CurLife = 5;
	private int GameIndex;
	private int once1 = 0;
	private int once2 = 0;
	private Random rd;
	private int count = 0;
	Thread thread1;
	Thread thread2;
	Thread thread3;
	Thread thread4;
	CardLayout cardLayout = new CardLayout();

	public MyFrame() {
		setF();
		init();
		changepanel("MainP", CurLife);
	}

	public void init() {
		CurPoint = 0;
		CurLife = 1;
		isrun = false;
		GameIndex = -1;
		addMouseListener(this);
		rd = new Random();
	}
	// set Frame

	public void setF() {
		setUndecorated(true); // �޴��� ����
		setTitle("GameSwitch");
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);// ����ȭ�� ũ�� ����.
		setResizable(false); // ��ũ�� ũ�� ����.
		setLocationRelativeTo(null); // ������ â�� ȭ���� ����� ���� ����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(cardLayout);
		setVisible(true);

		mainP = new MainP(gameFrame);// 0
		shoot = new ShootingGame(gameFrame);// 1
		jump = new JumpGame(gameFrame);// 2
		rh = new RhythmGame(gameFrame);// 3
		avoid = new Avoid(gameFrame);// 4
		over = new GameOver(gameFrame, CurPoint);// 5

		add(mainP, "0");
		add(shoot, "1");
		add(jump, "2");
		add(rh, "3");
		add(avoid, "4");
		add(over, "5");
	}

	// �г� �̸��� �����ͼ� ȭ�� ��ȯ�� ���ݴϴ�. panel name, cur life
	public void changepanel(String PN, int life) {
		if (PN.equals("MainP")) { // �� �� ȭ ��
			// show (�θ� �����̳� , ī�� ���� id)
			StopThread();
			cardLayout.show(getContentPane(), "0");
			mainP.run();
			// mainP = new MainP(gameFrame);
//			getContentPane().removeAll();
//			getContentPane().add(mainP); 
			repaint();
//			new Thread(mainP).start();
		} else if (PN.equals("Shoot")) { // 1 �� �� �� ��
			GameIndex = 0;
			StopThread();
			cardLayout.show(getContentPane(), "1");
			shoot.run();
		} else if (PN.equals("Jump")) { // 2 �� �� �� ��
			GameIndex = 1;
			StopThread();
			cardLayout.show(getContentPane(), "2");
			jump.run();
			addKeyListener(jump);
		} else if (PN.equals("Music")) { // 3 �� �� �� ��
			GameIndex = 1;
			StopThread();
			cardLayout.show(getContentPane(), "3");
			rh.run();
		} else if (PN.equals("Avoid")) { // 4 �� �� �� ��
			// show (�θ� �����̳� , ī�� ���� id)
			GameIndex = 1;
			StopThread();
			cardLayout.show(getContentPane(), "4");
			avoid.run();
			requestFocus();
		} else if (PN.equals("GameOver")) { // �� �� �� ��
			// show (�θ� �����̳� , ī�� ���� id)
			GameIndex = -1;
			StopThread();
			cardLayout.show(getContentPane(), "5");
			revalidate();
			repaint();
			// is just sum of both. It marks the container as invalid and performs layout of
			// the container.
			// call invalidate() and validate()
		}
	}

	public void nextgame() {
		System.out.println("���� ������ : " + CurLife + "��������~");

		int a = rd.nextInt(3);
		if (GameIndex == 0) {
			removeMouseListener(shoot);
		}
		a = (count++) % 4;
		switch (a) {
		case 0:
			changepanel("Shoot", CurLife);
			break;
		case 1:
			changepanel("Jump", CurLife);
			break;
		case 2:
			changepanel("Avoid", CurLife);
			break;
		case 3:
			changepanel("Music", CurLife);
			break;
		}
	}

	public void gamepassed() {
		this.CurPoint += 1;
		nextgame();
	}

	public void gamefailed() {
		this.CurLife--;
		// ������ ���̻� �����ٰ��̸�.
		if (this.CurLife <= 0) {
			over.setpoint(this.CurPoint); // ���� ���� ����.
			changepanel("GameOver", CurLife);
		}
		else
			nextgame();
	}

	public void StopThread() {
		if (thread1 != null) {
			thread1.interrupt();
			System.out.println("����");
		}
	}

	public int life() {
		return CurLife;
	}

	public int getpoint() {
		return this.CurPoint;
	}

	public void restart() {
		this.CurLife = 5;
		nextgame();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (GameIndex == 0)
			shoot.mousePressed(e);
		// avoid.mousepress(e);
		// System.out.println(","+e.getX()+","+e.getY());
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
}
