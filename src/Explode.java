
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;


public class Explode {
	
	int x, y;
	
	private boolean live = true;

	private boolean init = false;//一个表示图片是否被初始化的量；
	
	int step = 0;
	
	private TankClient tc;
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();//拿到当前操作系统提供给我们的Toolkit对象；
	
	//第一种加载图片的方法，静态初始化；
	private static Image[] imgs = {
		//把硬盘上的图片拿到内存里来；指定图片的位置；这里用的是 tk.getImage(URL url)方法；
		tk.getImage(Explode.class.getClassLoader().getResource("images/0.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/1.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/2.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/3.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/4.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/5.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/6.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/7.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/8.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/9.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/10.gif"))
	};
	
	public Explode(int x, int y, TankClient tc) {
		super();
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	
	public void draw(Graphics g) {
		
		//此小段代码是为了解决运行时，当主坦克第一次打敌军坦克时，不会有爆炸图片产生的问题；
		if(!init){
			
			for (int i = 0; i < imgs.length; i++) {
				g.drawImage(imgs[i], -100, -100, null);
			}
			init = true;
		}
		
		if(!live) { 	
			tc.explodes.remove(this);
			return;
		}
		if(step == imgs.length){
			live = false;
			step =0;
			return;
		}
		
		g.drawImage(imgs[step], x, y, null);
		
		step++;
	}

	
}
