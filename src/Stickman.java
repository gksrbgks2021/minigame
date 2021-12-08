import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

class Stickman extends JLabel {
	// ��ǥ.
	private int x;
	private int y;
	private int height;
	private boolean Up;
	private boolean Down;
	private boolean Right;
	private boolean Left;
	private boolean goDown;
	private int stickanime;
	private int jumpSpeed;
	private int jumpstart;
	private int status; // ���� ����. 1�̸� �⺻., . 2�� ��ũ����. 3�̸� ������ ����, 4�� ����.
//�ӵ� ���� 
	private ImageIcon[] manUp;
	private ImageIcon manDown;
	private ImageIcon manJump;
	private ImageIcon manleft1, manleft2;
	private ImageIcon manright1, manright2;
	private Timer t[];

	public Stickman() {
		init();
		setlabel();
	}

	public void init() {
		// �ʱ�ȭ �մϴ�.
		x = 54;
		y = 453; // �ٴ��� 593
		Up = false;
		Down = false;
		goDown = false;
		jumpSpeed = 1;
		jumpstart = -30;
		status = 1;
		t = new Timer[4];
		stickanime = 0;
	}

	public void setlabel() {

		manUp = new ImageIcon[5];
		manUp[0] = new ImageIcon(Main.class.getResource("images/Player2.png"));
		manUp[1] = new ImageIcon(Main.class.getResource("images/Player2_1.png"));
		manUp[2] = new ImageIcon(Main.class.getResource("images/Player2_2.png"));
		manUp[3] = new ImageIcon(Main.class.getResource("images/Player2_3.png"));
		manUp[4] = new ImageIcon(Main.class.getResource("images/Player2_4.png"));
		manDown = new ImageIcon(Main.class.getResource("images/playerdown1.png"));
		manJump = new ImageIcon(Main.class.getResource("images/jump_1.png"));
		manleft1 = new ImageIcon(Main.class.getResource("images/Player2_l.png"));
		height = 140;
		setSize(70, height);
		setIcon(manUp[stickanime]);
		setLocation(x, y);

		t[0] = new Timer(30, e -> { // t1�� ���������� �̵��ϴ°Ŵ�.
			if (getX() >= 1210)
				t[0].stop();
			x += 5;
			setLocation(x, y);
		});

		t[1] = new Timer(30, e -> { // t1�� �������� �̵��ϴ°Ŵ�.
			if (getX() <= 0)
				t[1].stop();
			x -= 5;
			setLocation(x, y);
		});

		t[2] = new Timer(50, e -> {
			y += jumpstart;
			jumpstart += jumpSpeed;
			checkfloor();
			setLocation(x, y);
		});
		t[3] = new Timer(50, e -> {
			if (status == 1 && !Up)// �Ͼ�ִ� �����̸�
				setIcon(manUp[(stickanime++) % 5]);
			if (stickanime == Integer.MAX_VALUE)// �浹����.
				stickanime = 0;
		});
	}

	public void runStickman() { // ������ ����.
		t[3].start();
	}

	public void stopStickman() { // ������ ����.
		t[3].stop();
	}

	public void checkfloor() {// �ٴ������ƴ���.

		if (getY() + height >= 593) {
			setLocation(x, getY() + height);
			Up = false;
			t[2].stop();
		}
	}

	public void Up() {
		if (!Up) {
			jumpSpeed = 10;
			jumpstart = -50;
			Up = true;
			if (!Down)
				setIcon(manJump);
			t[2].start();
		}
	}

	// jump �Ѵ�.
//	public void Up() {
//		Up = true;
//		// �ѹ� �����ϸ�
//		new Thread(() -> {
//			try {
//				// y��ǥ 80��ŭ �ö󰬴� �����´�.
//				for (int i = 0; i < 180 / jumpSpeed; i++) {
//					y = y - jumpSpeed;
//					setLocation(x, y);
//					Thread.sleep(60);
//				}
//				Up = false;
//				goDown();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}).start();
//	}
	public boolean a() {
		return Up;
	}

	public void goDown() {
		goDown = true;
		new Thread(() -> {
			try {
				for (int i = 0; i < 180 / jumpSpeed; i++) {
					y = y + jumpSpeed;
					setLocation(x, y);
					Thread.sleep(60);
					// �����ϴµ� �ɸ��� �ð� 4800 / jumpspeed.
				}
				goDown = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void Down() {
		// ���帰 ���� �϶� �׸��� ��ǥ, ������ �ֽ�ȭ
		if (Down == false && status == 1) {
			height = 70;
			setSize(70, height);
			this.y += 70;
			status = 2;// ���帰 ����.
			stopStickman();
		}
		Down = true;
		setIcon(manDown);
		setLocation(x, y);
	}

	public void Right() {
//		if (Right == false) {
//			this.x += 70;
//			status = 3;
//			Right = true;
//		}
		if (!Right) {
			Right = true;
			if (getX() <= 1210) {
				t[0].start();
			}
		}
	}

	public void Left() {
//		if (Left == false) {
//			this.x += 70;
//			status = 3;
//			Right = true;
//		}
		if (!Left) {
			Left = true;
			if (getX() >= 0) {
				t[1].start();
			}
		}
	}

	public void wakeUp() {
		if (status == 2) {
			height = 140;
			setSize(70, height);
			this.y -= 70;
			status = 1;
			this.runStickman();
			Down = false;
		}
		if (Up) {
			Down = false;
			setIcon(manJump);
		}

		setLocation(x, y);
	}

	public void stop_R() {
		Right = false;
		t[0].stop();
	}

	public void stop_L() {
		Left = false;
		t[1].stop();
	}

	public boolean isUp() {
		return this.Up;
	}

	public boolean isgoDown() {
		return this.goDown;
	}

	public int height() { // 1�̸� �⺻, 2�� ���帰��.
		return height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}