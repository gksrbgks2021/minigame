import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

class Node extends JLabel implements Runnable {
	private Image nodeImage;
	private int x, y;
	private String direction;

	// 3
	// 1 2
	// 4
	public Node(String direction) {
		// x��ǥ�� �ٲ�.
		if (direction.equals("left")) {// ���� x, y��ǥ ����.
			y = 340;
			this.direction = direction;
		}
		if (direction.equals("right")) {
			y = 340;
			this.direction = direction;
		}
		// y��ǥ�� �ٲ�
		if (direction.equals("top")) {
			y = 580 - 1000 / Main.SLEEP_TIME * Main.GAME_SPEED;
			x = 600;
			this.direction = direction;
		}
		if (direction.equals("bottom")) {
			x = 600;
			this.direction = direction;
		}
		// �� ���⿡ �´� �̹��� �׸��ϴ�.
		nodeImage = new ImageIcon(Main.class.getResource("images/"+this.direction+".png")).getImage(); // 70 * 70
		
	}

	public void doublebuffering(Graphics2D g) {
		// ��� �׸��ϴ�.
		g.drawImage(nodeImage, x, y, null);
	}

	public void move() {
		switch (this.direction) {
		case "left": // ��
			this.x += Main.GAME_SPEED;
			break;
		case "right": // ��
			this.x -= Main.GAME_SPEED;
			break;
		case "top": // ��
			this.y += Main.GAME_SPEED;
			break;
		case "bottom":// �Ʒ�
			this.x -= Main.GAME_SPEED;
			break;
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				System.out.println("��Ʈ������߸��ϴ�.");
				move();
				Thread.sleep(10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

public class RhythmGame extends JPanel implements KeyListener,Runnable {
	int gamelife = 3;
	String gametitle;
	String musictitle;
	boolean ispassed = true;
	boolean lock = false;
	Music music;
	boolean isshow = true;
	private MyFrame mf;
	List<Node> nodeList = new ArrayList<>();

	RhythmGame(String gametitle, String musictitle) {
		this.gametitle = gametitle;
		this.musictitle = musictitle;
		music = new Music(musictitle, false);// ���� ���� ����.
		System.out.println("������");
	}
	
	RhythmGame(MyFrame mf){
		this.mf = mf;
		
	}
	public void doublebuffering(Graphics2D g) {
		for (int i = 0; i < nodeList.size(); i++) { // ��带 �׸��ϴ�.
			nodeList.get(i).doublebuffering(g);
		}
	}
	public void paintComponent(Graphics g) {
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void run() { // ������ ����.
		try {
			lock = true;
			System.out.println("����1");
			Timer t = new Timer(30000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("����2");
					close();
				}
			});
			
			t.start();
			makeN();
		} catch (Exception e) {
			e.printStackTrace();// : ������ �߻��ٿ����� ã�Ƽ� �ܰ躰�� ������ ����մϴ�.
		}
	}
	
	public void makeN() {
		int startTime = 0;
		Random r = new Random();
		int a = 3;
		// while (!isInterrupted()) {
		boolean dropped = false;
		try {
			Node n;
			switch(a ) {
			case 1:
			 n = new Node("left");	
			 new Thread(n).start();
				nodeList.add(n);
				break;
			case 2:
			 n = new Node("right");
			 new Thread(n).start();
				nodeList.add(n);
				break;
			case 3:
			 n = new Node("top");
			 new Thread(n).start();
				nodeList.add(n);
			 break;
			case 4:
				n = new Node("bottom");
				new Thread(n).start();
				nodeList.add(n);
				break;
			}
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }
	}

	public void close() {
		// ���� ����.
		music.close();
		lock = false; // ��� ����.
		Thread.currentThread().interrupt(); // ���� ����Ǵ� ������ ����.
	}
}
