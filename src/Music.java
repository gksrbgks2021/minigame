import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import javazoom.jl.player.Player;

public class Music extends Thread {
private Player player;
	private boolean Continue;
	private File file;
	private FileInputStream fis;
	private BufferedInputStream bis;
	
		public Music (String name, boolean Continue) {
			try {
				this.Continue = Continue ;
				file  = new File(Main.class.getResource("/music/" + name).toURI());
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				player = new Player(bis);
			}
			catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
			public int getTime() {
				if(player == null) return 0; //0.001초 단위까지 알려줌
				return player.getPosition();//음악을 맞출때 이걸 이용해서 분석함.
			} 
			//음악이 언제 실행되든 화면전환이나 프로그램 종료하면음악을 종료한다. 
			public void close() {
				Continue=false;
				player.close();
			this.interrupt();
			}
			//곡 강제종료~
			@Override
			public void run() {
				try {
					do {
						fis = new FileInputStream(file);
						bis = new BufferedInputStream(fis);
						player = new Player(bis);
						player.play(); //곡을 실행시켜요.
					}while(Continue);
				}catch(Exception e) {
					System.out.println(e.getMessage());
			}
		}
			}