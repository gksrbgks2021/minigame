import javax.swing.ImageIcon;
import javax.swing.JLabel;

class Stickman extends JLabel {
	// 좌표.
	private int x;
	private int y;

	private boolean Up;
	private boolean Down;
	private boolean goDown;
	private int jumpSpeed;
	private int status;
//속도 상태 
	private ImageIcon manUp, manDown;
	
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
		jumpSpeed = 9;
		status = 1;
	}

	public void setlabel() {
		manUp = new ImageIcon(Main.class.getResource("images/Player2.png"));
		manDown = new ImageIcon(Main.class.getResource("images/playerdown1.png"));
		setSize(70, 140);
		setIcon(manUp);
		setLocation(x, y);
	}

	// jump 한다.
	public void Up() {
		Up = true;
		// 한번 점프하면
		new Thread(() -> {
			try {
				//y좌표 80만큼 올라갔다 내려온다. 
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
					//점프하는데 걸리는 시간 4800 / jumpspeed.
				}
				goDown = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

	}

	public void Down() {
		//엎드린 상태 일때 그리는 좌표, 사이즈 최신화
		if(Down == false && status == 1) { 
			setSize(70,70);
			this.y += 70;
			status = 2;
		}
		Down = true;
		setIcon(manDown);
		setLocation(x, y);
	}

	public void wakeUp() {
		if(status == 2 ) {
			setSize(70, 140);
			this.y-=70;
			status =1;
		}
		Down = false;
		setIcon(manUp);
		setLocation(x, y);
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