import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

public class Node extends JLabel implements Runnable {
	private RhythmGame game;
	private ImageIcon nodeImage;
	private ImageIcon nodeImageActived;
	private ImageIcon nodeImageDead;
	private int x, y;
	private String direction;
	private int Ontime;// ����� �����ϴ� �ð�.
	private int ObjectNum;
	private boolean isrun;
	private boolean click;
	private Timer work;
	// 3
	// 1 2
	// 4 
	 
	public Node(RhythmGame game, String direction, int time, int num) {
		this.setSize(new Dimension(70, 70));
		this.direction = direction;
		this.ObjectNum = num;
		this.game = game;
		Ontime = time;
		// �� ���⿡ �´� �̹��� �׸��ϴ�.
		nodeImage = new ImageIcon(Main.class.getResource("images/" + this.direction + ".png")); // 70 * 70
		nodeImageActived = new ImageIcon(Main.class.getResource("images/active.png"));
		nodeImageDead = new ImageIcon(Main.class.getResource("images/failed.png"));
		this.setIcon(nodeImage);
		switch (this.direction) {
		case "left": // ��
			// 10�и�������� speed��ŭ �ڷ� ���ָ�,
			x = 600 - 1000 / 10 * Main.GAME_SPEED; // 1���Ŀ� �������ɶ��ο� �����ϰԵȴ�.
			y = 325;
			break;
		case "right": // ��
			x = 605 + 1000 / 10 * Main.GAME_SPEED;
			y = 325;
			break;
		case "top": // ��
			x = 605;
			y = 320 - 1000 / 10 * Main.GAME_SPEED;
			break;
		case "bottom":// �Ʒ�
			x = 605;
			y = 327 + 1000 / 10 * Main.GAME_SPEED;
			break;
		}
		this.setVisible(true);
		setLocation(x, y);
		work = new Timer(10, (e) -> {
			move();// �����̰�
		});
	}

	public void move() {
		switch (this.direction) {
		case "left": // ��
			this.x += Main.GAME_SPEED;
			if (x >= 640 && isrun)// �������
				failed();
			break;
		case "right": // �����ʿ��� �����Ǽ� �������� �̵�.
			this.x -= Main.GAME_SPEED;
			if (x <= 590 && isrun)
				failed();
			break;
		case "top": // ��
			this.y += Main.GAME_SPEED;
			if (y >= 360 && isrun)
				failed();
			break;
		case "bottom":// �Ʒ�
			this.y -= Main.GAME_SPEED;
			if (y <= 300 && isrun)
				failed();
			break;
		}
		setLocation(x, y);
	}

	public int when() {
		return this.Ontime - 1000;// 1��.
	}

	@Override
	public void run() {
		isrun = true;
		click = false;
		work.start();
	}

	// ���� �޼ҵ�.
	public void react(int direction) {// ���� �߽� x + 35, y + 35
		switch (this.direction) {
		case "left": // �� 1
			if (x <= 560 || direction != 1) {// ���� ������ �ʾ���.
				game.loseL();
				break;
			}
			if (x <= 580)// ����
			{
				terminated();
				game.removeobject(getON(), 1, getDirection());
				break;
			}
			terminated();
			game.removeobject(getON(), 2, getDirection());
			break;
		case "right": // �� 2
			if (x >= 675 || direction != 2) {// ���� ������ �ʾ���.
				game.loseL();
				break;
			}
			if (x >= 618)// ����
			{
				terminated();
				game.removeobject(getON(), 1, getDirection());
				break;
			}
			terminated();
			game.removeobject(getON(), 2, getDirection());
			break;
		case "top": // �� 3
			if (direction != 3 || y <= 257) {// ���� ������ �ʾ���.
				game.loseL();
				break;
			}
			if (y <= 290)// ����
			{
				terminated();
				game.removeobject(getON(), 1, getDirection());
				break;
			}
			terminated();
			game.removeobject(getON(), 2, getDirection());
			break;
		case "bottom":// �Ʒ� 4
			if (y >= 390 || direction != 4) {// ���� ������ �ʾ���.
				game.loseL();
				break;
			}
			if (y >= 333)// ����
			{
				terminated();
				game.removeobject(getON(), 1, getDirection());
				break;
			}

			terminated();
			game.removeobject(getON(), 2, getDirection());

			break;
		}
	}

	public void failed() {// ������ ���� ?
		isrun = false;
		work.stop();
		if (!click) {
			setIcon(nodeImageDead);
			game.removeobject(getON(), -1, getDirection());
		}
		click = true;
	}

	public void terminated() {
		setIcon(nodeImageActived);
		work.stop();
		isrun = false;
		click = true;
	}

	public int getON() {
		return this.ObjectNum;
	}

	public int getDirection() {
		switch (this.direction) {
		case "left": // ��
			return 1;
		case "right": // �����ʿ��� �����Ǽ� �������� �̵�.
			return 2;
		case "top": // ��
			return 3;
		case "bottom":// �Ʒ�
			return 4;
		}
		System.out.println("==================����=================");
		return -1;
	}
}
