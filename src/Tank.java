
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;//Random类所在包，生成随机数产生器；


public class Tank {
	public static final int XSPEED = 5;
	public static final int YSPEED = 5;
	
	private static int life = 100;//主战坦克生命值；
	
	private static Random r = new Random();//用static让随机数产生器被共享
	private int step = r.nextInt(12)+ 3;//生成一个随机步数（3~14），为了避免坦克在一定范围打转（因每走一步就改方向）
	
	private int x, y;
	private int oldX, oldY;//用来记录Tank的上一步，解决tank撞墙黏住的问题
	
	private boolean bL = false,  bU = false,   bR =  false, bD = false;
	
	private Direction dir = Direction.STOP;
	private Direction ptDir = Direction.D;//炮筒方向
	
	TankClient tc;
	
	private boolean good;
	
	private boolean live = true;

	private BloodBar bb = new BloodBar();//创建小血条对象；
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();//拿到当前操作系统提供给我们的Toolkit对象；
	
	private static Image[] tankImages = null;
	private static Map<String, Image> imgs = new HashMap<String, Image>();
	
	//第二种加载图片的方法；定义一个static（静态代码区），当这个类被load进内存后，首先执行的一段代码；
	//主要区别：它其中可以写代码语句，第一种方法（在Explode类中）只能声明属性；所以这种方式更灵活；这种方法最适合做变量的初始化工作；
	static {
		tankImages = new Image[] {
				//把硬盘上的图片拿到内存里来；指定图片的位置；这里用的是 tk.getImage(URL url)方法；
				tk.getImage(Tank.class.getClassLoader().getResource("images/tankL.gif")),
				tk.getImage(Tank.class.getClassLoader().getResource("images/tankLU.gif")),
				tk.getImage(Tank.class.getClassLoader().getResource("images/tankU.gif")),
				tk.getImage(Tank.class.getClassLoader().getResource("images/tankRU.gif")),
				tk.getImage(Tank.class.getClassLoader().getResource("images/tankR.gif")),
				tk.getImage(Tank.class.getClassLoader().getResource("images/tankRD.gif")),
				tk.getImage(Tank.class.getClassLoader().getResource("images/tankD.gif")),
				tk.getImage(Tank.class.getClassLoader().getResource("images/tankLD.gif")),		
		};
		
		imgs.put("L", tankImages[0]);
		imgs.put("LU", tankImages[1]);
		imgs.put("U", tankImages[2]);
		imgs.put("RU", tankImages[3]);
		imgs.put("R", tankImages[4]);
		imgs.put("RD", tankImages[5]);
		imgs.put("D", tankImages[6]);
		imgs.put("LD", tankImages[7]);
	}
	
	public static final int LURD_WIDTH = 37;//tankImages[0].getWidth(null);//因为左上右下的方向图片已经大小剪裁一致了；
	public static final int LURD_HEIGHT = 37;//tankImages[0].getWidth(null);虽然理论上这样写是可以的，但是由于实际上由于当用到该数据时，数据还没从硬盘上加载过来，所以画面会出现异常；
	
	public static final int xie_WIDTH = 53;//tankImages[1].getWidth(null);;//因为斜方向图片已经大小剪裁一致了；
	public static final int xie_HEIGHT = 52;//tankImages[1].getWidth(null);;
	
	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.good = good;
	}
	
	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public Tank(int x, int y, boolean good, Direction dir,TankClient tc){
		this(x, y, good);
		this.dir = dir;
		this.tc = tc;
	}
	
	void draw (Graphics g){
		if(!live){ 
			if(!good){
				tc.tanks.remove(this);
			}
			return;
		}
		
		if(isGood())      bb.draw(g);
		
		move();
		switch(ptDir){
		case L: 
			g.drawImage(imgs.get("L"), x, y, null);
			break;
		case LU:
			g.drawImage(imgs.get("LU"), x, y, null);
			break;
		case U:
			g.drawImage(imgs.get("U"), x, y, null);
			break;
		case RU:
			g.drawImage(imgs.get("RU"), x, y, null);
			break; 
		case R:
			g.drawImage(imgs.get("R"), x, y, null);
			break;
		case RD:
			g.drawImage(imgs.get("RD"), x, y, null);
			break;
		case D:
			g.drawImage(imgs.get("D"), x, y, null);
			break;
		case LD:
			g.drawImage(imgs.get("LD"), x, y, null);
			break;
		}
		
	}
	
	void move(){
		this.oldX = x;
		this.oldY = y;
		
		switch(dir){
		case L: 
			x -= XSPEED;
			break;
		case LU:
			x -= XSPEED;
			y -= YSPEED;
			break;
		case U:
			y -= YSPEED;
			break;
		case RU:
			x += XSPEED;
			y -= YSPEED;
			break;
		case R:
			x += XSPEED;
			break;
		case RD:
			x += XSPEED;
			y += YSPEED;
			break;
		case D:
			y += YSPEED;
			break;
		case LD:
			x -= XSPEED;
			y += YSPEED; 
			break;
		case STOP:
			break;
		}
		
		if(dir != Direction.STOP){
			this.ptDir = dir;
		}
		
		if(x < 0) x = 0;
		if(y < 30) y = 30;
		
		if(x + Tank.LURD_WIDTH > TankClient.GAME_WIDTH) x = TankClient.GAME_WIDTH - Tank.LURD_WIDTH;
		if(y + Tank.LURD_HEIGHT > TankClient.GAME_HEIGHT) y = TankClient.GAME_HEIGHT - Tank.LURD_HEIGHT;;
		
		if(!good){
			Direction[] dirs = Direction.values();//要先将方向转换成数组；
			if(step == 0) { // 只有当step=0时再转方向
				step = r.nextInt(12) + 3;
				int rn = r.nextInt(dirs.length); // 生成一个随机数；
				dir = dirs[rn];//随机从数组中取一个数；
			}
			step --;
			
			if(r.nextInt(40) > 38){//控制敌人的炮火猛烈度
				this.fire();
			}
		}
	}
	
	void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		switch(key){
		case KeyEvent.VK_F2:
			if(!this.live) {
				this.live = true;
				this.life = 100;
			}
			break;
		case KeyEvent.VK_LEFT :
			bL = true;
			break;
		case KeyEvent.VK_UP :
			bU = true;
			break;
		case KeyEvent.VK_RIGHT :
			bR = true;
			break;
		case KeyEvent.VK_DOWN :
			bD = true;
			break;
		}
		locationDirection();
	}
	
	void locationDirection() {
		if(bL && !bU && !bR && !bD == true)  dir = Direction.L;
		else if(bL &&  bU && !bR && !bD == true)  dir = Direction.LU;
		else if(!bL && bU && !bR && !bD == true)  dir = Direction.U;
		else if(!bL && bU && bR && !bD == true)   dir = Direction.RU;
		else if(!bL && !bU && bR && !bD == true)  dir = Direction.R;
		else if(!bL && !bU && bR && bD == true)   dir = Direction.RD;
		else if(!bL && !bU && !bR && bD == true)  dir = Direction.D;
		else if(bL && !bU && !bR && bD == true)   dir = Direction.LD;
		else if(!bL && !bU && !bR && !bD == true)   dir = Direction.STOP;
	}


	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key){
		case KeyEvent.VK_CONTROL:
			fire();
			break;
		case KeyEvent.VK_A:
			superFire();
			break;
		case KeyEvent.VK_LEFT :
			bL = false;
			break;
		case KeyEvent.VK_UP :
			bU = false;
			break;
		case KeyEvent.VK_RIGHT :
			bR = false;
			break;
		case KeyEvent.VK_DOWN :
			bD = false;
			break;
		}
		locationDirection();
		
	}
	
	public Missile fire(){
		if(!live) {//myTank死了就不要发炮弹了
			return null;
		}
		//通过计算让子弹从坦克的中间打出
		int x, y;
		if(ptDir == Direction.L || ptDir == Direction.U || ptDir == Direction.R || ptDir == Direction.D ){
			x = this.x + Tank.LURD_WIDTH/2 - Missile.WIDTH/2;
			y = this.y + Tank.LURD_HEIGHT/2 - Missile.HEIGHT/2;
		}
		else{
			x = this.x + Tank.xie_WIDTH/2 - Missile.WIDTH/2;
			y = this.y + Tank.xie_HEIGHT/2 - Missile.HEIGHT/2;
		}
		Missile m = new Missile(x, y, good, ptDir, this.tc);//这里“good”是将坦克本身的好坏直接传递为所属的子弹好坏；根据炮筒的方向来发子弹；
		tc.missiles.add(m);
		return m;
	}
	
	//朝某个方向打一发炮弹
	public Missile fire(Direction dir) {
		if(!live) {
			return null;
		}
		int x, y;
		if(ptDir == Direction.L || ptDir == Direction.U || ptDir == Direction.R || ptDir == Direction.D ){
			x = this.x + Tank.LURD_WIDTH/2 - Missile.WIDTH/2;
			y = this.y + Tank.LURD_HEIGHT/2 - Missile.HEIGHT/2;
		}
		else{
			x = this.x + Tank.xie_WIDTH/2 - Missile.WIDTH/2;
			y = this.y + Tank.xie_HEIGHT/2 - Missile.HEIGHT/2;
		}
		Missile m = new Missile(x, y, good, dir, this.tc);//dir是指定的某个方向；
		tc.missiles.add(m);
		return m;
	}
	
	private void superFire() {
		Direction[] dirs = Direction.values();
		for(int i = 0; i<8; i++){
			fire(dirs[i]);
		}
	}
	
	public Rectangle getRect (){
		return new Rectangle(x, y, tankImages[0].getWidth(null), tankImages[0].getHeight(null));
	}

	public boolean isGood() {
		return good;
	}
	
	private void stay() {
		x = oldX;
		y = oldY;
	} 
	
	public boolean collidesWithWall(Wall w) {
		if(this.live && this.getRect().intersects(w.getRect())) {
			this.stay();
			return true;
		}
		return false;
	}
	
	public boolean collidesWithTank (Tank t) {
		if(this != t) {
			if(this.live && t.live && this.getRect().intersects(t.getRect())) {
				this.stay();
				t.stay();
				return true;
			}
		}
		return false;
	}
	
	public boolean collidesWithTanks (java.util.List<Tank> tanks){
		for(int i=0; i<tanks.size(); i++) {
			if(collidesWithTank(tanks.get(i))) {
				return true;
			}
		}
		return false;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}
	
	//因为小血条是属于tank类的，但又比较独立，所以将其声明为内部类；
	private class BloodBar {
		public void draw(Graphics g) {
			Color c = g.getColor();
			g.setColor(Color.RED);
			g.drawRect(x, y-12,40,7);//血条外框；  
			int w = 40 * life/100;
			g.fillRect(x, y-12, w, 7);//血条里框；
			g.setColor(c);
		}
	}
	
	public boolean eatBlood(Blood b) {
		if(this.live && b.isLive() && this.getRect().intersects(b.getRect())) {
				this.life = 100;
				b.setLive(false);
				return true;
		}
		return false;	
		
	}
	
}
