import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

class Circle extends JLabel implements Runnable{
	private int radius;
	private int circleX;// xÁÂÇ¥ y ÁÂÇ¥
	private int circleY;
	private Circle circle ;
	private boolean isrun = true;
	int CN; // circleNumber.
	private ShootingGame shoot;
	ImageIcon img1 ; //»¡°£ ¿ø
	ImageIcon img2; //°ËÁ¤ ¿ø
	Random rand = new Random();
	
	Circle(ShootingGame shootinggame, int num){
		this.shoot = shootinggame;
		this.CN = num;
		this.radius = 70;
		initCircle();
	}
	
	public void initCircle() {
		img1 = new ImageIcon(Main.class.getResource("images/redcircle.png"));
		img2 = new ImageIcon(Main.class.getResource("images/ruby.png"));
		setIcon(img1);
		setSize(radius,radius);
		this.circleX =	rand.nextInt(851) + 150; //[ 150 .. 1000 ] ·£´ý ÁÂÇ¥
		this.circleY =  rand.nextInt(421) + 150; //[ 150 .. 570 ]  ·£´ý ÁÂÇ¥
		setLocation(circleX,circleY);
		circle = this;
	}
	
	@Override
	public void run() {
		try {
			if(!isrun)
			{
				Thread.currentThread().interrupt();
			}
			shoot.addcircle(circle);
			Thread.sleep(1500);
			shoot.removecircle(getcircleN());//1ÃÊµÚ »èÁ¦ ¿äÃ»ÇÔ.
			shoot= null;
			Thread.currentThread().interrupt();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
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

}