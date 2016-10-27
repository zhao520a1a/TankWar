import java.awt.*;

public class Blood {
	private static int WIDTH = 10;
	private static int HEIGHT = 10;
	
	int x, y;
	TankClient tc;
	
	private boolean live = true;
	
	private int[][] pos ={ 
			{600, 100}, {500, 50}, {300, 260}, {200, 130}, {240, 400}, {300, 350}, {330, 440}, {400, 560}
	};
	int step = 0;
	
	public Blood() {
		this.x = pos[0][0];
		this.y = pos[0][1];
	}
	
	public void draw (Graphics g) {
		if(!live) {
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.RED);
		g.fillRect(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		move();
	}
	
	private void move() { 
		step ++;
		if(step == pos.length) {
			step = 0;
		}
		x = pos[step][0];
		y = pos[step][1];
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	
}
