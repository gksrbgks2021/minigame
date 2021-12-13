package org.minigame.objects;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.minigame.panels.ShootingGame;
import org.minigame.main.Main;
public class Circle extends JLabel implements Runnable {
	private int radius;
	private int circleX;// x��ǥ y ��ǥ
	private int circleY;
	private int countClick;
	private int type;
	private Circle circle;
	private boolean isrun = true;
	int CN; // circleNumber.
	private ShootingGame shoot;
	private java.util.Timer booking;
	ImageIcon img1; // ���� ��
	ImageIcon img2; // ���� ��
	Random rand = new Random();

	public Circle(ShootingGame shootinggame, int Onum,int r) {
		this.shoot = shootinggame;
		this.CN = Onum;
		this.radius = 70;
		
		type = r;
		initCircle();
		
	}

	public void initCircle() {
		img1 = new ImageIcon(getClass().getResource("../images/redcircle.png"));
		img2 = new ImageIcon(getClass().getResource("../images/Circle2.png"));
		
		if (type == 0) {
			setIcon(img1);
			countClick =1 ;
		}
		if (type == 1) {
			setIcon(img2);
			countClick =2 ;
		}
		setSize(radius, radius);
		
		this.circleX = rand.nextInt(851) + 150; // [ 150 .. 1000 ] ���� ��ǥ
		this.circleY = rand.nextInt(421) + 150; // [ 150 .. 570 ] ���� ��ǥ
		setVisible(true);
		setLocation(circleX, circleY);
		
		circle = this;
		countClick = type+1;
		booking = new java.util.Timer(false);
	}

	// Circle �׸��ϴ�~
//	public void paintCircle(Graphics g, List<Circle> c) {
//		c.forEach(a -> {
//			g.drawImage(img1.getImage(), a.getcircleX(), a.getcircleY(), null);
//		});
//	}

	@Override
	public void run() {
		 setTimer(()->{
			 if(isrun) //������ �ȉ�ٸ� ~ 
					shoot.Requestremovecircle(CN,true);// 1�ʵ� ���� ��û��.
					shoot = null;
		 },1000);
	}

	public double getdistance(int mX, int mY, int cX, int cY) {
		return Math.sqrt((mX - cX) * (mX - cX) + (mY - cY) * (mY - cY));
	}

	public int getradius() {
		return radius;
	}

	public int getcircleX() {
		return circleX;
	}

	public int getcircleY() {
		return circleY;
	}

	public int getcircleN() {
		return CN;
	}
	
	public void Clicked() {
		countClick--;
		if(countClick <=0) {
			isrun = false;
			System.out.println(CN+"��ȣ ���ſ�û");
			if(shoot != null)
			shoot.Requestremovecircle(CN,false);
		}
	}
	public void setTimer(Runnable runnable, long delay) {
		booking.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				runnable.run();
			}
		}, delay);
	}
}