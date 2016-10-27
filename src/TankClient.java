/*  功能：在classpath中添加资源；为Explode,Tank,Missile加图片；
 * 反射的初步概念
 * 		对于classloader，每一个.class实际上就是一个Class对象；
 * 		Class是对类信息的表述，是类的metainfo/metadata(元数据)
 * 		换句话说它是表述类中数据的数据；
*/
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

public class TankClient extends Frame {
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	
	Tank myTank = new Tank(50, 50, true, Direction.STOP, this);

	Wall w1 = new Wall(100, 200, 20, 300, this), w2 = new Wall(200, 100, 300, 20, this);
	
	Blood b = new Blood();
	
	List<Missile> missiles = new ArrayList<Missile>();
	List<Explode> explodes = new ArrayList<Explode>();
	List<Tank> tanks = new ArrayList<Tank>();
	
	Image offScreenImage = null;
	
	public void paint(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.ORANGE);
		g.drawString("missiles count:" + missiles.size(), 10, 40);
		g.drawString("explodes count:" + explodes.size(), 10, 60);
		g.drawString("enemytanks    coutn:" + tanks.size(), 10, 80);
		g.drawString("mytanks    life :" + myTank.getLife(), 10, 100);
		g.setColor(c);
		
		if(tanks.size() <= 0) {
			for(int i=0; i<5; i++) {
				tanks.add(new Tank(50 + 40*(i+1), 50, false, Direction.D, this));
			}
		}
		
		for(int i=0; i<missiles.size(); i++){
			Missile m = missiles.get(i);
			m.hitTanks(tanks);
			m.hitTank(myTank);//让别人可以打我
			m.hitWall(w1);
			m.hitWall(w2);
			m.draw(g);
		}
		
		for(int i=0; i<explodes.size(); i++){
			Explode e = explodes.get(i);
			e.draw(g);
		}
		
		for(int i=0; i<tanks.size(); i++){
			Tank t = tanks.get(i);
			t.collidesWithWall(w1);//enemyTank不能穿墙
			t.collidesWithWall(w2);
			t.collidesWithTanks(tanks);
			t.draw(g);
		}
		
		myTank.draw(g);
		myTank.eatBlood(b);
		w1.draw(g);
		w2.draw(g);
		b.draw(g);
	
	}
	
	public void update(Graphics g) {
		if(offScreenImage == null){
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		//要先刷新背景
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.BLACK);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		//将所有的东西画在“虚拟图片”上
		paint(gOffScreen);
		//再将“虚拟图片”一下子放在屏幕上
		g.drawImage(offScreenImage, 0, 0, null);
	}

	public void lanchFrame () {
		
		for(int i=0; i<10; i++){ //增加敌人Tank
			tanks.add(new Tank(50 + 40*(i+1), 50, false, Direction.D, this ));
		}
		
		//this.setLocation(400,300);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("TankWar");
		this.setBackground(Color.BLACK);
		this.addWindowListener( new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
		});
		this.addKeyListener(new KeyMonitor());
		setVisible(true);
		setResizable(false);
		
		new Thread(new PaintThread()).start();
		
		
	}
	

	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.lanchFrame();
		
	}
	
	private class PaintThread implements Runnable{

		public void run() {
			while(true){
				repaint();
				try {
					Thread.sleep(30);//图片刷新时间
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}	
		}
	}
	
	private class KeyMonitor extends KeyAdapter{

		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}

		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);
		}
	}

}
