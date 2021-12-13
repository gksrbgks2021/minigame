package org.minigame.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GameOver extends JPanel {
	private MyFrame mf;
	private int point;
	private int Bp;
	InputMap im;
	ActionMap ap;
	int bestpoint;
	File scorenote;
	GameOver(MyFrame myframe, int point) {
		mf = myframe;
		this.point = point;
		fileio();
		keybinding();
		repaint();
	}

	public void fileio() {
		try {
			scorenote = new File(getClass().getResource("../tools/Score.txt").getPath());
			BufferedReader br = new BufferedReader(
					new FileReader(getClass().getResource("../").getPath() + "/tools/Score.txt"));

			String s = br.readLine();
			if(s == null)System.out.println("파일 읽기 오류=====");
			bestpoint =  Integer.parseInt(s);
			if( bestpoint < point)//최고기록갱신하면 수정.
			{bestpoint = point;
				BufferedWriter bw = new BufferedWriter(new FileWriter(scorenote));
				bw.write(Integer.toString(bestpoint) ,0,1 );//0번쨰부터 길이 1 문자 씀.
				bw.flush();
				bw.close();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void keybinding() {
		im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ap = this.getActionMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "restart");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "end");
		ap.put("restart", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Thread.sleep(10);
				} catch (Exception a) {
					a.printStackTrace();
				}
				mf.restart();
			}
		});
		ap.put("end", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);// 게임종료
			}
		});
	}

	public void paintComponent(Graphics g) {
		g.setColor(new Color(128, 128, 128));
		g.setFont(new Font("Gulim", Font.BOLD, 80));
		g.drawString("최종점수 : " + point + "점", 300, 100);
		g.drawString("최고점수 : " + bestpoint + "점", 300, 200);
		g.drawString("종료하기 : ESC", 300, 300);
		g.drawString("다시시작 : SPACE", 300, 450);
	}

	public void setpoint(int a) {
		point = a;
	}

}
