package org.minigame.panels;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.minigame.tools.Music;

public class MainP extends JPanel implements Runnable {
	private MyFrame myframe;
	private Image backgroundImage;

	private ImageIcon dancingmachine = new ImageIcon(getClass().getResource("../images/skudance.gif"));
	private ImageIcon startbtndefault = new ImageIcon(getClass().getResource("../images/startbtn.png"));
	private ImageIcon startbtndentered = new ImageIcon(getClass().getResource("../images/startbtnentered.png"));
	private ImageIcon endbtndefault = new ImageIcon(getClass().getResource("../images/endbtndefault.png"));
	private ImageIcon endbtndentered = new ImageIcon(getClass().getResource("../images/endbtnentered.png"));

	// Jbutton
	private JButton scoreboard = new JButton(dancingmachine); // Ŭ���ϸ� ������ �����ִ°�.
	private JButton startbtn = new JButton(startbtndefault);
	private JButton endbtn = new JButton(endbtndefault);

	private JLabel label[];
	private Music newbgm = new Music("Rhythm Heaven Fever.MP3", true);

	MainP(MyFrame myFrame) {
		init();
		// myframe �޼ҵ带 �����Ű�� ���ؼ� �����´�.
		label = new JLabel[2];
		label[0] = new JLabel("" + fileio());
		label[0].setFont(new Font("Verdana", Font.PLAIN, 70));
		label[0].setBounds(800,350 , 100, 100);
		label[0].setVisible(false);

		label[1] = new JLabel();
		label[1].setText("Best Score ");
		label[1].setFont(new Font("Verdana", Font.PLAIN, 70));
		label[1].setBounds(700, 100, 700, 400);
		label[1].setVisible(false);

		this.myframe = myFrame;
		scoreboard.setBounds(662, 85, 600, 600);// x��ǥ y��ǥ ũ���̴�.
		scoreboard.setBorderPainted(false); // ��ư �׵θ��� ����ϴ�
		scoreboard.setContentAreaFilled(false); // ��ư�� �ʱ� background ������ �����.
		scoreboard.setVisible(true);
		scoreboard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) { // ���콺 ��Ŀ���϶� �������� ��������.
				scoreboard.setIcon(null);
				/* set scoreboard */
				label[0].setVisible(true);
				label[1].setVisible(true);
			}

			@Override
			public void mouseExited(MouseEvent e) { // ������ ������ ����.
				scoreboard.setIcon(dancingmachine);
				label[0].setVisible(false);
				label[1].setVisible(false);
			}

			public void mouseMoved(MouseEvent e) {

			}
		});

		startbtn.setBounds(40, 200, 400, 100);
		startbtn.setBorderPainted(false);
		startbtn.setContentAreaFilled(false);
		startbtn.setFocusPainted(false);// ����Ʈ���� ������� ���� ���ϴ� ������� �������Ѵ�.
		startbtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				startbtn.setIcon(startbtndentered);
				startbtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
				// ���߿� ����� ��ư ����.
			}

			@Override
			public void mouseExited(MouseEvent e) {
				startbtn.setIcon(startbtndefault);
				startbtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) { // ���콺�� ��������??
				Music buttonEnteredMusic = new Music("exitbgm.mp3", false);
				buttonEnteredMusic.start();
				newbgm.stop();// ��� ����~
				myframe.nextgame();// ���ӽ���.
			}
		});
		endbtn.setBounds(40, 330, 400, 100);
		endbtn.setBorderPainted(false);
		endbtn.setContentAreaFilled(false);
		endbtn.setFocusPainted(false);// ����Ʈ���� ������� ���� ���ϴ� ������� �������Ѵ�.
		endbtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				endbtn.setIcon(endbtndentered);
				endbtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				endbtn.setIcon(endbtndefault);
				endbtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) { // ���콺�� ��������??
				Music buttonEnteredMusic = new Music("exitbgm.mp3", false);
				buttonEnteredMusic.start();
				try {
					Thread.sleep(1000);// 1�ʵڿ� ����
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				System.exit(0);// ����ȴ�.
			}
		});
		add(scoreboard);
		add(startbtn);
		add(endbtn);
		add(label[0]);
		add(label[1]);
	}

	public int fileio() {
		int a = -1;
		try {
			File scorenote = new File(getClass().getResource("../tools/Score.txt").getPath());
			BufferedReader br = new BufferedReader(
					new FileReader(getClass().getResource("../").getPath() + "/tools/Score.txt"));
			String s = br.readLine();
			if (s == null)
				System.out.println("���� �б� ����=====");
			br.close();
			a = Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;
	}

	public void init() {
		this.setLayout(null);
		backgroundImage = new ImageIcon(getClass().getResource("../images/Startbackground.png")).getImage(); // �ʱ� ��׶���
																												// ����

	}

	@Override
	protected void paintComponent(Graphics g) {
		// paint the BGI and scale it to fill the entire space
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, null);

	}

	@Override
	public void run() {
		newbgm = new Music("Rhythm Heaven Fever.MP3", true);
		newbgm.start();
	}
}
