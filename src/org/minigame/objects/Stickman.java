package org.minigame.objects;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import org.minigame.main.Main;
public class Stickman extends JLabel {
	// 좌표.
	private int x;
	private int y;
	private int height;
	private boolean Up;
	private boolean Down;
	private boolean Right;
	private boolean Left;
	private boolean goDown;
	private int stickanime;
	private int Speed;
	private int sumSpeed;
	private int jumpstart;
	private int status; // 현재 상태. 1이면 기본., . 2면 움크리기. 3이면 오른쪽 진행, 4면 왼쪽.
//속도 상태 
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
		// 초기화 합니다.
		x = 54;
		y = 453; // 바닥은 593
		Up = false;
		Down = false;
		goDown = false;
		Speed = 3;
		jumpstart = -30;
		status = 1;
		t = new Timer[5];
		stickanime = 0;
		sumSpeed = 0;
	}

	public void setlabel() {

		manUp = new ImageIcon[5];
		manUp[0] = new ImageIcon(getClass().getResource("../images/Player2.png"));
		manUp[1] = new ImageIcon(getClass().getResource("../images/Player2_1.png"));
		manUp[2] = new ImageIcon(getClass().getResource("../images/Player2_2.png"));
		manUp[3] = new ImageIcon(getClass().getResource("../images/Player2_3.png"));
		manUp[4] = new ImageIcon(getClass().getResource("../images/Player2_4.png"));
		manDown = new ImageIcon(getClass().getResource("../images/playerdown1.png"));
		manJump = new ImageIcon(getClass().getResource("../images/jump_1.png"));
		manleft1 = new ImageIcon(getClass().getResource("../images/Player2_l.png"));
		height = 140;
		setSize(70, height);
		setIcon(manUp[stickanime]);
		setLocation(x, y);

		t[0] = new Timer(30, e -> { // t1은 오른쪽으로 이동하는거다.
			
			sumSpeed += Speed;
		});

		t[1] = new Timer(30, e -> { // t1은 왼쪽으로 이동하는거다.
			
			sumSpeed -= Speed;
		});

		t[2] = new Timer(50, e -> {
			y += jumpstart;
			jumpstart += Speed;
			checkfloor();
			setLocation(x, y);
		});

		t[3] = new Timer(50, e -> {
			if (status == 1 && !Up)// 일어나있는 상태이면
				setIcon(manUp[(stickanime++) % 5]);
			if (stickanime == Integer.MAX_VALUE)// 충돌방지.
				stickanime = 0;
		});

		t[4] = new Timer(30, e -> {
			if (getX() >= 1208) {
				t[0].stop();
				t[4].stop();
				sumSpeed = 0;
				x = 1207;
			}
			if (getX() <= 0) {
				t[1].stop();
				t[4].stop();
				sumSpeed = 0;
				x =1 ;
			}
			sumSpeed += (0 - ZeroExceptionSpeed(sumSpeed));
			x += sumSpeed;
			setLocation(x, y);
		});
	}

	public int ZeroExceptionSpeed(int s) {
		if (s == 0)
			return 0;
		else if (s > 0)
			return s / s;
		else
			return s / s * (-1);
	}

	public void runStickman() { // 움직임 구현.
		t[3].start();
	}

	public void stopStickman() { // 움직임 구현.
		t[3].stop();
		stickanime = 0;
		setIcon(manUp[stickanime]);//초기화
	}

	public void stopAll() {
		for (int i = 0; i < t.length; i++) {
			t[i].stop();
		}
	}

	public void checkfloor() {// 바닥인지아닌지.

		if (getY() + height >= 593) {
			setLocation(x, getY() + height);
			Up = false;
			t[2].stop();
		}
	}

	public void Up() {
		if (!Up) {
			Speed = 10;
			jumpstart = -50;
			Up = true;
			if (!Down)
				setIcon(manJump);
			t[2].start();
		}
	}

	// jump 한다.
//	public void Up() {
//		Up = true;
//		// 한번 점프하면
//		new Thread(() -> {
//			try {
//				// y좌표 80만큼 올라갔다 내려온다.
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
				for (int i = 0; i < 180 / Speed; i++) {
					y = y + Speed;
					setLocation(x, y);
					Thread.sleep(60);
					// 점프하는데 걸리는 시간 4800 / jumpspeed.
				}
				goDown = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void Down() {
		// 엎드린 상태 일때 그리는 좌표, 사이즈 최신화
		if (Down == false && status == 1) {
			height = 70;
			setSize(70, height);
			this.y += 70;
			status = 2;// 엎드린 상태.
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
			setIcon(manUp[0]);
			Right = true;
			if (getX() <= 1210) {
				t[0].start();
				t[4].start();
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
			setIcon(manleft1);
			Left = true;
			if (getX() >= 0) {
				t[1].start();
				t[4].start();
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

	public int height() { // 1이면 기본, 2면 엎드린거.
		return height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}