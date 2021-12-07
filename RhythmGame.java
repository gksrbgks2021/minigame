import java.awt.Color;
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

public class RhythmGame extends JPanel implements Runnable {
	int gamelife ;
	boolean ispassed = true;
	boolean lock = false;
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
	private MyFrame myframe;
	private RhythmGame game;
	Queue<Node> waitinglist = new LinkedList<>(); // ready큐
	Queue<Node> removelist = new LinkedBlockingQueue<>(10);
	
	ImageFilter f;
	public Node node;
	private Timer t1;
	private Timer makeNode;
	private java.util.Timer terminate;
	private double startTime;
	private boolean isrun;
	private long startime;
	private long curtime = 0;
	private int Limtime = 20; // 시간 제한 20초.
	private int firstime;
	List<Node> nodeList_L = new ArrayList<>(); // 리스트 추가.
	List<Node> nodeList_R = new ArrayList<>();
	List<Node> nodeList_T = new ArrayList<>();
	List<Node> nodeList_D = new ArrayList<>();

	private int Index_L;
	private int Index_R;
	private int Index_T;
	private int Index_D;
	private volatile boolean isremoveAll;
	private boolean up;
	private boolean down;
	private boolean left;
	private boolean right;
	private boolean finish;
	private int pushright;
	private boolean yap;
	int temp;

//===========
//...1
//4  3  2
//===========

	RhythmGame(MyFrame mf) {
		gamelife = 6;
		setLayout(null);
		this.myframe = mf;
		this.game = this;
		isrun = false;
		finish = false;
		yap = false;
		isremoveAll = false;
		backgroundImage = new ImageIcon(Main.class.getResource("images/game2back.png")).getImage();
		rough1Image = new ImageIcon(Main.class.getResource("images/rought1.png")).getImage();
		rough2Image = new ImageIcon(Main.class.getResource("images/rought2.png")).getImage();
		rough1EnterImage = new ImageIcon(Main.class.getResource("images/rought1enter.png")).getImage();
		rough2EnterImage = new ImageIcon(Main.class.getResource("images/rought2enter.png")).getImage();

		letter = new RemoveBackground("Letter/push.png").getImage();
		Lgood = new RemoveBackground("Letter/good.png").getImage();
		Lbad = new RemoveBackground("Letter/bad.png").getImage();
		Lperfect = new RemoveBackground("Letter/perfect.png").getImage();
		Lpass = new RemoveBackground("Letter/passed.png").getImage();
		Lfail = new RemoveBackground("Letter/failed.png").getImage();

		life = new JLabel[this.gamelife];
		temp = 0;
		pushright = -1;
		music = new Music("RGamebgm.mp3", true);

		Index_L = 0;
		Index_R = 0;
		Index_T = 0;
		Index_D = 0;
		addlife();
		repaint();
		addKeyBind();
		setTimer();
	}

	public void setTimer() {
		t1 = new Timer(50, (e) -> {
			istimeout();
			if (!lock && isrun)
				removeobject();
			curtime = System.currentTimeMillis() - startime;
		});
		makeNode = new Timer(10, (e) -> {
			if (waitinglist.peek().when() <= music.getTime()) {
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
				makeNode.stop();
		});
		terminate = new java.util.Timer(false);

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

		if (pushright != -1) {
			if (pushright == 0) {
				g.drawImage(Lbad, 750, 230, null);
			} else if (pushright == 1) {
				g.drawImage(Lgood, 750, 230, null);
			} else if (pushright == 2) {
				g.drawImage(Lperfect, 750, 230, null);
			}
		}
		// 끝났으면 글자 띄우기
		if (yap) {
			if (Lpass != null)
				g.drawImage(Lpass, 535, 210, null);
			if (Lfail != null)
				g.drawImage(Lfail, 535, 210, null);
		}
	}

	@Override
	public void run() { // 노드를 떨굽니다.
		try {
			gamelife = 6;
			repaint();
			Thread.sleep(1000);
			startime = System.currentTimeMillis();
			isrun = true;

			repaint();
			Thread.sleep(1000); // 스레드 간 충돌 방지 .
			makeN();
			makeNode.start();
			t1.start();
			music.start();
		} catch (Exception e) {
			e.printStackTrace();// : 에러의 발생근원지를 찾아서 단계별로 에러를 출력합니다.
		}
	}

	public void makeN() {
		try {
			// 상대 경로 지정.
			BufferedReader br = new BufferedReader(
					new FileReader(Main.class.getResource("").getPath() + "/Track/track2.txt"));
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

	public void loadN(String time, String dir, String ON) {
		waitinglist.add(new Node(game, dir, Integer.parseInt(time), Integer.parseInt(ON)));
	}

	public void close() {
		finish = true;
		repaint();
		sleep(1000); // 스레드 슬립
		stopT();
		music.stop();
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
		isremoveAll = true;
	}
public void removekeyBind() {
	this.getInputMap().clear();
	this.getActionMap().clear();
}
	public void stopT() {
		t1.stop();
		makeNode.stop();
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
		try {
			lock = true;
			Node temp;
			while (!removelist.isEmpty()) {
				temp = removelist.poll();

				if (temp != null) {
					remove(temp);
					repaint();
				}
			}
			lock = false;
			isremoveAll = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		// System.out.println("=======================라 이 프 :" + this.gamelife);
		checklife();
	}

	public void checklife() {
		if (gamelife <= 0 && !finish) //짧은시간안에 여러번 반복 호출 막기위한 플래그.
		{
			yap = true;
			Lpass = null;
			finish = true;
			repaint();
			removeAll();
			removekeyBind();
			terminate.schedule(new java.util.TimerTask() { //1초뒤에 호출합니다. 
				@Override
				public void run() {
					pushright = 5;
					repaint();
					sleep(500);
					close();
					while(!isremoveAll)
					{
						sleep(10);
					}
					myframe.gamefailed(); // 실패
				}
			}, 1000);
		} else if (!isrun && gamelife > 0 && !finish) {// 게임 통과시.
		
			finish = true; 
			yap = true;
			Lfail = null;
			repaint();
			removeAll();
			removekeyBind();
			terminate.schedule(new java.util.TimerTask() {
				@Override
				public void run() {
					pushright = 5;
					repaint();
					sleep(500);
					close();
					while(!isremoveAll)
					{sleep(10);
					}
					myframe.gamepassed();
				}
			}, 1000);
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

	public void addlife() {
		for (int i = 0; i < this.gamelife; i++) {
			life[i] = new JL_Life();
			life[i].setLocation(100 + i * 80, 600);
			add(life[i]);
		}
	}
}
