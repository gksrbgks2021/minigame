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
	private int Ontime;// 가운데에 도달하는 시간.
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
		// 각 방향에 맞는 이미지 그립니다.
		nodeImage = new ImageIcon(Main.class.getResource("images/" + this.direction + ".png")); // 70 * 70
		nodeImageActived = new ImageIcon(Main.class.getResource("images/active.png"));
		nodeImageDead = new ImageIcon(Main.class.getResource("images/failed.png"));
		this.setIcon(nodeImage);
		switch (this.direction) {
		case "left": // 왼
			// 10밀리세컨드로 speed만큼 뒤로 뺴주면,
			x = 600 - 1000 / 10 * Main.GAME_SPEED; // 1초후에 판정가능라인에 도달하게된다.
			y = 325;
			break;
		case "right": // 오
			x = 605 + 1000 / 10 * Main.GAME_SPEED;
			y = 325;
			break;
		case "top": // 위
			x = 605;
			y = 320 - 1000 / 10 * Main.GAME_SPEED;
			break;
		case "bottom":// 아래
			x = 605;
			y = 327 + 1000 / 10 * Main.GAME_SPEED;
			break;
		}
		this.setVisible(true);
		setLocation(x, y);
		work = new Timer(10, (e) -> {
			move();// 움직이고
		});
	}

	public void move() {
		switch (this.direction) {
		case "left": // 왼
			this.x += Main.GAME_SPEED;
			if (x >= 640 && isrun)// 엔드라인
				failed();
			break;
		case "right": // 오른쪽에서 생성되서 왼쪽으로 이동.
			this.x -= Main.GAME_SPEED;
			if (x <= 590 && isrun)
				failed();
			break;
		case "top": // 위
			this.y += Main.GAME_SPEED;
			if (y >= 360 && isrun)
				failed();
			break;
		case "bottom":// 아래
			this.y -= Main.GAME_SPEED;
			if (y <= 300 && isrun)
				failed();
			break;
		}
		setLocation(x, y);
	}

	public int when() {
		return this.Ontime - 1000;// 1초.
	}

	@Override
	public void run() {
		isrun = true;
		click = false;
		work.start();
	}

	// 판정 메소드.
	public void react(int direction) {// 원의 중심 x + 35, y + 35
		switch (this.direction) {
		case "left": // 왼 1
			if (x <= 560 || direction != 1) {// 아직 오지도 않았음.
				game.loseL();
				break;
			}
			if (x <= 580)// 괜춘
			{
				terminated();
				game.removeobject(getON(), 1, getDirection());
				break;
			}
			terminated();
			game.removeobject(getON(), 2, getDirection());
			break;
		case "right": // 오 2
			if (x >= 675 || direction != 2) {// 아직 오지도 않았음.
				game.loseL();
				break;
			}
			if (x >= 618)// 괜춘
			{
				terminated();
				game.removeobject(getON(), 1, getDirection());
				break;
			}
			terminated();
			game.removeobject(getON(), 2, getDirection());
			break;
		case "top": // 위 3
			if (direction != 3 || y <= 257) {// 아직 오지도 않았음.
				game.loseL();
				break;
			}
			if (y <= 290)// 괜춘
			{
				terminated();
				game.removeobject(getON(), 1, getDirection());
				break;
			}
			terminated();
			game.removeobject(getON(), 2, getDirection());
			break;
		case "bottom":// 아래 4
			if (y >= 390 || direction != 4) {// 아직 오지도 않았음.
				game.loseL();
				break;
			}
			if (y >= 333)// 괜춘
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

	public void failed() {// 삭제에 실패 ?
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
		case "left": // 왼
			return 1;
		case "right": // 오른쪽에서 생성되서 왼쪽으로 이동.
			return 2;
		case "top": // 위
			return 3;
		case "bottom":// 아래
			return 4;
		}
		System.out.println("==================오류=================");
		return -1;
	}
}
