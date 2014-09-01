package bomberman;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Player {

	public Rectangle rect;
	public String name;
	public Image img;
	public String[] imgs;
	
	public Player(String n, Rectangle r, String []imgs){
		this.name = n;
		this.rect = r;
		this.imgs = imgs;
		ImageIcon icon = new ImageIcon(this.getClass().getResource(imgs[3]));
		this.img = icon.getImage();
	}
	
	public void setImage(String img){
		ImageIcon icon = new ImageIcon(this.getClass().getResource(img));
		this.img = icon.getImage();
	}
}
