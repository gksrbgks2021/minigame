package org.minigame.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.ImageFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;
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

import org.minigame.main.Main;
import org.minigame.objects.JL_Life;
import org.minigame.objects.Node;
import org.minigame.tools.Music;
import org.minigame.tools.RemoveBackground;

public class RhythmGame extends JPanel implements Runnable {
	private MyFrame myframe;
	private RhythmGame game;
	private Image backgroundImage;
	private Image rough1Image;
	private Image rough2Image;
	private Image rough1EnterImage;
	private Image rough2EnterImage;
	private Image letter;
	private Image Lgood;
	private Image Lbad;
	private Image Lperfect;
	private Image Lpass;
	private Image Lfail;
	private JLabel[] life;
	Music music;
	boolean isshow = true;

	Queue<Node> waitinglist = new LinkedList<>(); // ready큐
	Queue<Node> removelist = new LinkedBlockingQueue<>(10);
	List<Node> nodeList_L = new ArrayList<>(); // 리스트 추가.
	List<Node> nodeList_R = new ArrayList<>();
	List<Node> nodeList_T = new ArrayList<>();
	List<Node> nodeList_D = new ArrayList<>();
	ImageFilter f;
	public Node node;
	private Timer t[];
	private java.util.Timer booking;
	private double startTime;
	private boolean isrun;
	boolean ispassed = true;
	boolean lock = false;
	private long startime;
	private long curtime = 0; // 노드 시간 밀리초단위
	private int time;
	private int Limtime = 20; // 시간 제한 20초.
	private int Index_L;
	private int Index_R;
	private int Index_T;
	private int Index_D;
	private int pushright;
	private int gamelife;
	private int calltime;
	private volatile boolean isremoveAll;
	private boolean up;
	private boolean down;
	private boolean left;
	private boolean right;
	private boolean finish;
	private boolean yap;

//===========
//...1
//4  3  2
//===========

	RhythmGame(MyFrame mf) {
		gamelife = 6;
		setLayout(null);
		this.myframe = mf;
		this.game = this;
		calltime = 0;
		backgroundImage = new ImageIcon(getClass().getResource("../images/game2back.png")).getImage();
		rough1Image = new ImageIcon(getClass().getResource("../images/rought1.png")).getImage();
		rough2Image = new ImageIcon(getClass().getResource("../images/rought2.png")).getImage();
		rough1EnterImage = new ImageIcon(getClass().getResource("../images/rought1enter.png")).getImage();
		rough2EnterImage = new ImageIcon(getClass().getResource("../images/rought2enter.png")).getImage();

		letter = new RemoveBackground("Letter/push.png").getImage();
		Lgood = new RemoveBackground("Letter/good.png").getImage();
		Lbad = new RemoveBackground("Letter/bad.png").getImage();
		Lperfect = new RemoveBackground("Letter/perfect.png").getImage();
		Lpass = new RemoveBackground("Letter/passed.png").getImage();
		Lfail = new RemoveBackground("Letter/failed.png").getImage();
		t = new Timer[2];
		life = new JLabel[this.gamelife];
		pushright = -1;

		repaint();
		addKeyBind();
		setTimer();

	}

	public void setTimer() {
		t[0] = new Timer(50, (e) -> {
			istimeout();
			if (!lock && isrun)
				removeobject();
			//현재 시간에서 startime 뺀값. 
			curtime = System.currentTimeMillis() - startime;
			repaint();
		});
		t[1] = new Timer(20, (e) -> {
			if (waitinglist.peek().when() <= curtime) {
				node = waitinglist.poll();
				switch (node.getDirection()) {// 1 왼 2 오 3 위 4 아래.
				case 1:
					nodeList_L.add(node);
					break;
				case 2:
					nodeList_R.add(node);
					break;
				case 3:
					nodeList_T.add(node);
					break;
				case 4:
					nodeList_D.add(node);
					break;
				}
				add(node);
				node.run();
			}
			if (waitinglist.peek() == null)
				t[1].stop();
		});
		
		booking = new java.util.Timer(false);
	}

	public void addKeyBind() {
		addKeyBindingP(this, KeyEvent.VK_UP, "up", (e) -> {
			// System.out.println(music.getTime() + " top "+(temp++));
			if (!up && isrun) {
				up = true;
				repaint();
				if (nodeList_T.size() <= 0 || Index_T >= nodeList_T.size()) {
					loseL();
					return;
				}
				nodeList_T.get(Index_T).react(3);
			}
		});
		addKeyBindingP(this, KeyEvent.VK_RIGHT, "right", (e) -> {
			// System.out.println(music.getTime() + " right "+(temp++));
			if (!right && isrun) {
				right = true;
				repaint();
				if (nodeList_R.size() <= 0 || Index_R == nodeList_R.size()) {
					loseL();
					return;
				}
				nodeList_R.get(Index_R).react(2);
			}
		});
		addKeyBindingP(this, KeyEvent.VK_DOWN, "down", (e) -> {
			// System.out.println(music.getTime() + " bottom "+(temp++));
			if (!down && isrun) {
				down = true;
				repaint();
				if (nodeList_D.size() <= 0 || Index_D == nodeList_D.size()) {
					loseL();
					return;
				}
				nodeList_D.get(Index_D).react(4);
			}
		});
		addKeyBindingP(this, KeyEvent.VK_LEFT, "left", (e) -> {
			// System.out.println(music.getTime() + " left "+(temp++));
			if (!left && isrun) {
				left = true;
				repaint();
				if (nodeList_L.size() <= 0 || Index_L == nodeList_L.size()) {
					loseL();
					return;
				}
				nodeList_L.get(Index_L).react(1);
			}
		});
		addKeyBindingP(this, KeyEvent.VK_ESCAPE, "exit", (e) -> {
			System.exit(1);
		});

		addKeyBindingL(this, KeyEvent.VK_UP, "upr", (e) -> {
			if (isrun) {
				up = false;
				repaint();

			}
		});
		addKeyBindingL(this, KeyEvent.VK_RIGHT, "rightr", (e) -> {
			if (isrun) {
				right = false;
				repaint();
			}
		});
		addKeyBindingL(this, KeyEvent.VK_DOWN, "downr", (e) -> {
			if (isrun) {
				down = false;
				repaint();
			}
		});
		addKeyBindingL(this, KeyEvent.VK_LEFT, "exitr", (e) -> {
			if (isrun) {
				left = false;
				repaint();
			}
		});
	}

	public void istimeout() {
		if ((System.currentTimeMillis() - startime) / 1000 >= Limtime) {
			stopT();
			isrun = false;
			checklife();
		}
	}

	public int getMusicTime() {
		return music.getTime();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(backgroundImage, 0, 0, null);
		g.drawImage(rough2Image, 0, 325, null);
		g.drawImage(rough1Image, 605, 0, null);

		g.setColor(Color.magenta);
		g.drawOval(605, 325, 70, 70);
		g.fillOval(605, 325, 70, 70);
		g.setColor(Color.CYAN);
		g.drawOval(610, 330, 60, 60);
		g.fillOval(610, 330, 60, 60);

		if (!isrun && !finish) {
			g.drawImage(letter, 535, 210, null);
		}
		if (up)
			g.drawImage(rough1EnterImage, 605, 0, null);
		if (down)
			g.drawImage(rough1EnterImage, 605, 395, null);
		if (left)
			g.drawImage(rough2EnterImage, 0, 325, null);
		if (right)
			g.drawImage(rough2EnterImage, 675, 325, null);

		if (pushright != -1 && isrun == true) {
			if (pushright == 0) {
				g.drawImage(Lbad, 750, 230, null);
			} else if (pushright == 1) {
				g.drawImage(Lgood, 750, 230, null);
			} else if (pushright == 2) {
				g.drawImage(Lperfect, 750, 230, null);
			}
		}
		// 끝났으면 글자 띄우기
		if (yap && finish) {
			if (Lpass != null)
				g.drawImage(Lpass, 535, 210, null);
			if (Lfail != null)
				g.drawImage(Lfail, 535, 210, null);
		}
		g.setFont(new Font("Gulim", Font.BOLD, 50));
		g.setColor(Color.black);
		g.drawString("남은목숨 : "+ myframe.life(), 900, 70);
		g.drawString("점수 : " + myframe.getpoint() , 20, 70);
		time = Limtime - (int) curtime/1000 ;
		if(time < 0)
			g.drawString("남은시간 : "+ 20, 900, 650);
		if(time>=0)
		g.drawString("남은시간 : "+time, 900, 650);
	}

	public void init() {

		gamelife = 6;
		isrun = false;
		finish = false;
		yap = false;
		isremoveAll = false;
		up = false;
		down = false;
		left = false;
		right = false;
		time = 0 ;
		repaint();
		addlife();
		requestFocus();
		Index_L = 0;// 이거 위에 있어서 오류.
		Index_R = 0;
		Index_T = 0;
		Index_D = 0;
		calltime++;
	}

	@Override
	public void run() { // 노드를 떨굽니다.
		init();
		loadNode();
		sleep(5);
		repaint();

		setTimer(() -> {
			isrun = true;
			repaint();
			startT();// 스레드 동작.
		}, (long) 2000);
	}

	public void loadNode() {
		try {
			// 상대 경로 지정.
			BufferedReader br = new BufferedReader(
					new FileReader(getClass().getResource("../").getPath() + "/Track/track1.txt"));
			StringTokenizer st;
			String temp;
			while ((temp = br.readLine()) != null) {
				// 노드 ( direction, time) 를 생성해서 대기열 큐에 넣는다.
				st = new StringTokenizer(temp, " ");
				loadN(st.nextToken(), st.nextToken(), st.nextToken());
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startT() {
		music = new Music("RGamebgm.mp3", false);
		startime = System.currentTimeMillis();
		t[1].start();
		t[0].start();
		music.start();
	}

	public void loadN(String time, String dir, String ON) {
		waitinglist.add(new Node(game, dir, Integer.parseInt(time), Integer.parseInt(ON), calltime));
	}

	public void close() {
		finish = true;
		repaint();
		stopT();
		isrun = false;
		lock = false; // 잠금 해제.
		// 키바인딩 해제
		allclear();
	}

	public void allclear() {

		this.removeAll();
		waitinglist.clear();
		nodeList_L.clear();
		nodeList_R.clear();
		nodeList_T.clear();
		nodeList_D.clear();
		clearlife();
		isremoveAll = true;
	}

	public void removekeyBind() {
		this.getInputMap().clear();
		this.getActionMap().clear();
	}

	public void stopT() {
		t[0].stop();
		t[1].stop();
		music.stop();
	}

	public void sleep(long i) {
		try {
			Thread.sleep(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 삭제.
	public void removeobject(int ObjectNumber, int judge, int direction) {
		if (judge == -1) {// System.out.print("방향 : "+direction);
			loseL();
		}
		if (judge == 1)
			pushright = 1;
		if (judge == 2)
			pushright = 2;

		switch (direction) {// 1 왼 2 오 3 위 4 아래.
		case 1:
			for (int i = Index_L; i < nodeList_L.size(); i++) {
				if (nodeList_L.get(i).getON() == ObjectNumber) {
					removelist.add(nodeList_L.get(i));
					Index_L++; // 삭제 연산은 O(n) 이 걸리므로 게임이 끝날때 진행
					break;
				}
			}
			break;
		case 2:
			for (int i = Index_R; i < nodeList_R.size(); i++) {
				if (nodeList_R.get(i).getON() == ObjectNumber) {
					removelist.add(nodeList_R.get(i));
					Index_R++; // 삭제 연산은 O(n) 이 걸리므로 게임이 끝날때 진행
					break;
				}
			}
			break;
		case 3:
			for (int i = Index_T; i < nodeList_T.size(); i++) {
				if (nodeList_T.get(i).getON() == ObjectNumber) {
					removelist.add(nodeList_T.get(i));
					Index_T++; // 삭제 연산은 O(n) 이 걸리므로 게임이 끝날때 진행
					break;
				}
			}
			break;
		case 4:
			for (int i = Index_D; i < nodeList_D.size(); i++) {
				if (nodeList_D.get(i).getON() == ObjectNumber) {
					removelist.add(nodeList_D.get(i));
					Index_D++; // 삭제 연산은 O(n) 이 걸리므로 게임이 끝날때 진행
					break;
				}
			}
			break;
		}
	}

	public void removeobject() { // 오버로딩
		Node temp;
		while (!removelist.isEmpty()) {
			temp = removelist.poll();

			if (temp != null) {
				remove(temp);
				repaint();
			}
		}
		isremoveAll = true;

	}

	public void loseL() {
		pushright = 0;
		this.gamelife--;
		if (gamelife > 0) {
			remove(life[gamelife]);
		}
		if (gamelife <= 0) {
			remove(life[0]);
		}
		checklife();
	}

	public void checklife() {
		if (gamelife <= 0 && !finish) // 짧은시간안에 여러번 반복 호출 막기위한 플래그.
		{
			finish = true;
			yap = true;
			Lpass = null;
			repaint();
			removeAll();
//			removekeyBind();
			setTimer(() -> {
				pushright = 5;
				repaint();
				close();
				myframe.gamefailed(); // 실패
			}, 1500);
		} else if (!isrun && gamelife > 0 && !finish) {// 게임 통과시.
			finish = true;
			yap = true;
			Lfail = null;
			repaint();
			removeAll();
			removekeyBind();
			setTimer(() -> {
				pushright = 5;
				repaint();
				close();
				myframe.gamepassed();
			}, 1500); // 1초뒤에 종료 요청

		}
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

	public void setTimer(Runnable runnable, long delay) {
		booking.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				runnable.run();
			}
		}, delay);
	}

	public void addlife() {
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

	public int getCallTime() {
		return calltime;
	}
}
