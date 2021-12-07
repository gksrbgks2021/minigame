import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

class Stickman extends JLabel {
	// 좌표.
	private int x;
	private int y;

	private boolean Up;
	private boolean Down;
	private boolean Right;
	private boolean Left;
	private boolean goDown;
	private int jumpSpeed;
	private int status; // 현재 상태. 1이면 기본., . 2면 움크리기. 3이면 오른쪽 진행, 4면 왼쪽.
//속도 상태 
	private ImageIcon manUp, manDown;
	private ImageIcon manleft1, manleft2;
	private ImageIcon manright1, manright2;
	private Timer t1, t2;

	public Stickman() {
		init();
		setlabel();
		
	}

	public void init() {
		// 초기화 합니다.
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

		t1 = new Timer(30, e -> { // t1은 오른쪽으로 이동하는거다.
			if(getX() >=1210)
				t1.stop();
			x += 5;
			setLocation(x, y);
		});
		
		t2 = new Timer(30, e -> { // t1은 왼쪽으로 이동하는거다.
			if(getX() <=0)
				t2.stop();
			x -= 5;
			setLocation(x, y);
		});
	}

	// jump 한다.
	public void Up() {
		Up = true;
		// 한번 점프하면
		new Thread(() -> {
			try {
				// y좌표 80만큼 올라갔다 내려온다.
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
			setSize(70, 70);
			this.y += 70;
			status = 2;// 엎드린 상태.
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