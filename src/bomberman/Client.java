package bomberman;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Client {

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Bomberman");
    String name;
    private static final int PORT = 9002;
    static final int d = 40;
    static final int offset = 4;
    public HashSet<Player> players = new HashSet<Player>();
    private HashSet<Bomb> bombs = new HashSet<Bomb>();
    private HashSet<Rectangle> obstacles = new HashSet<Rectangle>();
    public String []imgs1 = {"left1.png","up1.png","right1.png","down1.png"};
    public String []imgs2 = {"left2.png","up2.png","right2.png","down2.png"};
    String statusMessage = "";
    Image bg;
    
    public Client() {
        frame.pack();
    }
    
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Digite o endereço IP do servidor:",
            "Bomberman",
            JOptionPane.QUESTION_MESSAGE);
    }
    
    private String getServerAddress(String message) {
        return JOptionPane.showInputDialog(
            frame,
            message+" Digite o endereço IP do servidor:",
            "Bomberman",
            JOptionPane.QUESTION_MESSAGE);
    }

    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Digite seu nome:",
            "Seleção de nome",
            JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Conecta no servidor e entra no loop
     */
    private void run() throws IOException {
    	String serverAddress = "";
        // checagem do IP
    	while(serverAddress==null || serverAddress.isEmpty() || !InetAddress.getByName(serverAddress).isReachable(1000)){
    		if(serverAddress==null)
    			System.exit(1);
    		if(!InetAddress.getByName(serverAddress).isReachable(1000)){
    			serverAddress = getServerAddress("Endereço inválido.");
        	}
    		else
    			serverAddress = getServerAddress();
    	}
    	
        Socket socket = new Socket(serverAddress, PORT);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));//interface de entrada
        out = new PrintWriter(socket.getOutputStream(), true);//interface de saida
        
        loadObstacles(obstacles);
        JPanel panel = new JPanel(){
        	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
    	      super.paintComponent(g);
    	      Graphics2D g2 = (Graphics2D) g;
    	      ImageIcon icon = new ImageIcon(this.getClass().getResource("bg.png"));
		      bg = icon.getImage();
		      g2.drawImage(bg, 0, 0, null);
    	      for(Bomb b: bombs){
        	      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
        	      g2.drawImage(b.img, b.x, b.y, d, d, null);
    	      }
    	      for(Player p: players){
        	      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
        	      g2.drawImage(p.img, p.rect.x, p.rect.y, p.rect.width, p.rect.height, null);
    	      }
    	      if(statusMessage.length()>0){
    	    	  g2.setColor(Color.white);
    	    	  g2.drawString(statusMessage, 100, 70);  
    	      }    	      
        	}
        };

        frame.getContentPane().add(panel);

        panel.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
            	out.println(e.getKeyCode());
            }
        });
        panel.setFocusable(true);
        panel.requestFocusInWindow();

        frame.setSize(new Dimension(700, 630));
        frame.setVisible(true);
        
        //Processa mensagens do servidor
        while (true) {
            String line = in.readLine();//le mensagem do servidor
            System.out.println("recebeu:"+line);
            
            //Estoura bombas
            HashSet<Bomb> auxBombs = new HashSet<Bomb>();
            if(bombs.size()>0)
            for(Bomb b: bombs){
            	System.out.println(b.time);
            	if(b.time<=0){
            		explode(b);
            		auxBombs.add(b);
            	}
            }
            for(Bomb b: auxBombs){
            	bombs.remove(b);
            }
            //processa mensagem
            if (line.startsWith("REPAINT")){
            	panel.repaint();
            }
            else if (line.startsWith("INIT")){
            	String message = line.substring(5);
            	System.out.println(message);
            	String []args = message.split(":");
            	String user = args[0];
            	if(Integer.parseInt(args[1])==1)
            		players.add(new Player(user,new Rectangle(50, 50, (int)(d), (int)(d*1.1)),imgs1));
            	else
            		players.add(new Player(user,new Rectangle(606, 514, (int)(d), (int)(d*1.1)),imgs2));
            	statusMessage = "";
            }else if (line.startsWith("SUBMITNAME")) {
            	name = getName();
            	while(name==null ||name.isEmpty()){
            		if(name==null)
                		System.exit(1);
            		name = getName();
            	}
                out.println(name);
            } else if (line.startsWith("NAMEACCEPTED")) {
//            	names.add(name);
            } else if (line.startsWith("MESSAGE")) {
            	String message = line.substring(8);
            	System.out.println(message);
            	String []args = message.split(":");
            	String user = args[0];
            	int command = Integer.parseInt(args[1]);
            	switch(command){
            		case 37:{
            			for(Player p: players)
            				if(p.name.equals(user))
            					moveLeft(p);
            			break;
            		}
            		case 40:{
            			for(Player p: players)
            				if(p.name.equals(user))
            					moveDown(p);
            			break;
            		}
            		case 39:{
            			for(Player p: players)
            				if(p.name.equals(user))
            					moveRight(p);
            			break;
            		}
            		case 38:{
            			for(Player p: players)
            				if(p.name.equals(user))
            					moveUp(p);
            			break;
            		}
            		case 88:{
            			for(Player p: players)
            				if(p.name.equals(user)){
            					Bomb b = new Bomb(p.rect.x,p.rect.y,(5));
                				new Thread(b).start();
                				bombs.add(b);
            				}
            			break;
            		}
            		case 27:{
            			HashSet<Player> rm = new HashSet<Player>();
        				for(Player p: players){
            				if(p.name.equals(user)){
            					rm.add(p);
            					break;
            				}
        				}
        				for(Player p: rm){
            				players.remove(p);
        				}
        				for(Player p: players){
            				statusMessage = "Jogador '"+p.name+"' ganhou!";
        				}
        				players = new HashSet<Player>();
        				
            			if(user.equals(name)){
            				System.out.println("desconectado");
            				socket.close();
        					System.exit(1);           					         				
            			}
            			break;
            		}
            		default:{
            			break;
            		}            	
            	}
            }
            if(statusMessage.isEmpty())
	            if(players.size()<2)
	        		statusMessage = "Esperando oponente";
        	panel.repaint();
        }
    }

    public void moveLeft(Player p){
    	int pos = p.rect.x;
    	if(p.rect.x-offset>=50)
    		p.rect.x = p.rect.x - offset;
    	if(intersects(p.rect))
    		p.rect.x = pos;
    	p.setImage(p.imgs[0]);
    }
    
    public void moveRight(Player p){
    	int pos = p.rect.x;
    	if(p.rect.x+offset<=(606))
    		p.rect.x = p.rect.x + offset;
    	if(intersects(p.rect))
    		p.rect.x = pos;
    	p.setImage(p.imgs[2]);
    }
    
    public void moveUp(Player p){
    	int pos = p.rect.y;
    	if(p.rect.y-offset>=50)
    		p.rect.y = p.rect.y - offset;
    	if(intersects(p.rect))
    		p.rect.y = pos;
    	p.setImage(p.imgs[1]);
    }
    
    public void moveDown(Player p){
    	int pos = p.rect.y;
    	if(p.rect.y+offset<=(514))
    		p.rect.y = p.rect.y + offset;
    	if(intersects(p.rect))
    		p.rect.y = pos;
    	p.setImage(p.imgs[3]);
    }
    
    public void loadObstacles(HashSet<Rectangle> obstacles){
    	for(int i=0;i<6;i++){
    		for(int j=0;j<5;j++){
    			obstacles.add(new Rectangle((94+(i*93)),(94+(j*93)),d,(int)(d*0.5)));
    		}
    		
    	}
    }
    
    public boolean intersects(Rectangle r){
    	for(Rectangle obs: obstacles){
    		if(r.intersects(obs)){
//    			System.out.println("r x:"+r.x+" y:"+r.y+" obs x:"+obs.x+" y:"+obs.y);
    			return true;
    		}
    	}
    	return false;
    }
    
    public void explode(Bomb b){
    	HashSet<Player> deads = new HashSet<Player>();
    	for(Player p: players){
    		deads.add(p);
    		boolean narota = false;
    		if(b.x <= p.rect.x && p.rect.x <= (b.x+d)){//p está na rota vertical de b
    			narota = true;
    			System.out.println("rota");
    			for(Rectangle obs: obstacles){
    				if(
						b.x <= obs.x && obs.x <= (b.x+d) && 
						(
							(p.rect.y <= obs.y && obs.y <= b.y) ||
							(p.rect.y >= obs.y && obs.y >= b.y)
						)
					){
    					System.out.println("b.x:"+b.x+" obs.x:"+obs.x+" b.x+d:"+(b.x+d));
    					deads.remove(p);
    					break;
    				}
    				
    			}
    		}
    		if (b.y <= p.rect.y && p.rect.y <= (b.y+d)){//p está na rota horizontal de b
    			narota = true;
    			System.out.println("rota");
    			for(Rectangle obs: obstacles){
    				if(
						b.y <= obs.y && obs.y <= (b.y+d) &&
						(
							(p.rect.x <= obs.x && obs.x <= b.x) ||
							(p.rect.x >= obs.x && obs.x >= b.x)
						)
					){
    					System.out.println("b.y:"+b.y+" obs.y:"+obs.y+" b.y+d:"+(b.y+d));
	    				deads.remove(p);
	    				break;
    				}
    			}    			
    		}
    		if(!narota)
    			deads.remove(p);
    	}
    	for(Player d: deads){
    		if(d.name.equals(name))
    			out.println(27);
    		players.remove(d);
    	}
    }
    
    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}