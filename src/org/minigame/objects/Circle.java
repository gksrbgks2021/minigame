package org.minigame.objects;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.minigame.panels.ShootingGame;
import org.minigame.main.Main;
public class Circle extends JLabel implements Runnable {
	private int radius;
	private int circleX;// xÁÂÇ¥ y ÁÂÇ¥
	private int circleY;
	private int countClick;
	private int type;
	private Circle circle;
	private boolean isrun = true;
	int CN; // circleNumber.
	private ShootingGame shoot;
	private java.util.Timer booking;
	ImageIcon img1; // »¡°£ ¿ø
	ImageIcon img2; // °ËÁ¤ ¿ø
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
		
		this.circleX = rand.nextInt(851) + 150; // [ 150 .. 1000 ] ·£´ý ÁÂÇ¥
		this.circleY = rand.nextInt(421) + 150; // [ 150 .. 570 ] ·£´ý ÁÂÇ¥
		setVisible(true);
		setLocation(circleX, circleY);
		
		circle = this;
		countClick = type+1;
		booking = new java.util.Timer(false);
	}

	// Circle ±×¸³´Ï´Ù~
//	public void paintCircle(Graphics g, List<Circle> c) {
//		c.forEach(a -> {
//			g.drawImage(img1.getImage(), a.getcircleX(), a.getcircleY(), null);
//		});
//	}

	@Override
	public void run() {
		 setTimer(()->{
			 if(isrun) //»èÁ¦°¡ ¾È‰ç´Ù¸é ~ 
					shoot.Requestremovecircle(CN,true);// 1ÃÊµÚ »èÁ¦ ¿äÃ»ÇÔ.
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
			System.out.println(CN+"¹øÈ£ Á¦°Å¿äÃ»");
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