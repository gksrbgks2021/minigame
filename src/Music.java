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
				if(player == null) return 0; //0.001�� �������� �˷���
				return player.getPosition();//������ ���⶧ �̰� �̿��ؼ� �м���.
			} 
			//������ ���� ����ǵ� ȭ����ȯ�̳� ���α׷� �����ϸ������� �����Ѵ�. 
			public void close() {
				Continue=false;
				player.close();
			this.interrupt();
			}
			//�� ��������~
			@Override
			public void run() {
				try {
					do {
						fis = new FileInputStream(file);
						bis = new BufferedInputStream(fis);
						player = new Player(bis);
						player.play(); //���� ������ѿ�.
					}while(Continue);
				}catch(Exception e) {
					System.out.println(e.getMessage());
			}
		}
			}