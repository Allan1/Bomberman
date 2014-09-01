package bomberman;
import java.lang.Thread;
public class Clock implements Runnable {
	
	private int time;
	private boolean blocked = true;
	
	public Clock(int t){
		this.time = t;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	public boolean isBlocked() {
		return blocked;
	}
    public void run() {
    	while(getTime()>0){	
    	try {		
			Thread.sleep(1000);
    		
		} catch (InterruptedException e) {}
    	oneSecondTime(); // Passa um segundo no relogio.
    	}
    	setBlocked(false);
    }
    public int getTime(){
    	return this.time;
    }
    public void oneSecondTime(){
    	this.time = time -1;
    }
}
