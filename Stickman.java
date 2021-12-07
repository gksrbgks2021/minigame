import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

class Stickman extends JLabel {
	// ��ǥ.
	private int x;
	private int y;

	private boolean Up;
	private boolean Down;
	private boolean Right;
	private boolean Left;
	private boolean goDown;
	private int jumpSpeed;
	private int status; // ���� ����. 1�̸� �⺻., . 2�� ��ũ����. 3�̸� ������ ����, 4�� ����.
//�ӵ� ���� 
	private ImageIcon manUp, manDown;
	private ImageIcon manleft1, manleft2;
	private ImageIcon manright1, manright2;
	private Timer t1, t2;

	public Stickman() {
		init();
		setlabel();
		
	}

	public void init() {
		// �ʱ�ȭ �մϴ�.
		x = 54;
		y = 453;
		Up = false;
		Down = false;
		goDown = false;
		jumpSpeed = 10;
		status = 1;
	}

	public void setlabel() {

		manUp = new ImageIcon(Main.class.getResource("images/Player2.png"));
		manDown = new ImageIcon(Main.class.getResource("images/playerdown1.png"));
		manleft1 = new ImageIcon(Main.class.getResource("images/Player2_l.png"));

		setSize(70, 140);
		setIcon(manUp);
		setLocation(x, y);

		t1 = new Timer(30, e -> { // t1�� ���������� �̵��ϴ°Ŵ�.
			if(getX() >=1210)
				t1.stop();
			x += 5;
			setLocation(x, y);
		});
		
		t2 = new Timer(30, e -> { // t1�� �������� �̵��ϴ°Ŵ�.
			if(getX() <=0)
				t2.stop();
			x -= 5;
			setLocation(x, y);
		});
	}

	// jump �Ѵ�.
	public void Up() {
		Up = true;
		// �ѹ� �����ϸ�
		new Thread(() -> {
			try {
				// y��ǥ 80��ŭ �ö󰬴� �����´�.
				for (int i = 0; i < 130 / jumpSpeed; i++) {
					y = y - jumpSpeed;
					setLocation(x, y);
					Thread.sleep(60);
				}
				Up = false;
				goDown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void goDown() {
		goDown = true;
		new Thread(() -> {
			try {
				for (int i = 0; i < 130 / jumpSpeed; i++) {
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
			setSize(70, 70);
			this.y += 70;
			status = 2;// ���帰 ����.
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
		if(!Right) {
			Right = true;
			if(getX() <=1210 ) {
				setIcon(manUp);
				t1.start();	
			}	
		}
	}
	
	public void Left() {
//		if (Left == false) {
//			this.x += 70;
//			status = 3;
//			Right = true;
//		}
		if(!Left) {
			Left = true;
			if(getX() >= 0) {
				setIcon(manUp);
				t2.start();	
			}	
		}
	}
	
	public void wakeUp() {
		if (status == 2) {
			setSize(70, 140);
			this.y -= 70;
			status = 1;
		}
		Down = false;
		setIcon(manUp);
		setLocation(x, y);
	}

	public void stop_R() {
		Right = false;
		t1.stop();
	}

	public void stop_L() {
		Left = false;
		t2.stop();
	}

	public boolean isUp() {
		return this.Up;
	}

	public boolean isgoDown() {
		return this.goDown;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}