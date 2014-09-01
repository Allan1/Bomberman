package bomberman;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Bomb implements Runnable{
	int x;
	int y;
	int time;
	public Image img;
	
	public Bomb(int x, int y, int time){
		this.x = x;
		this.y = y;
		this.time = time;
		
		ImageIcon icon = new ImageIcon(this.getClass().getResource("bomb1.png"));
		this.img = icon.getImage();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(this.time >0){	
	    	try {		
				Thread.sleep(1000);
	    		
			} catch (InterruptedException e) {}
	    	this.time--; // Passa um segundo no relogio.
    	}
	}
	
	public void setImage(String img){
		ImageIcon icon = new ImageIcon(this.getClass().getResource(img));
		this.img = icon.getImage();
	}
}
