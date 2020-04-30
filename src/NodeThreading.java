import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class NodeThreading implements Runnable {
	
	private Node node;
	private Thread thread;
	private Random rand;
	private final AtomicBoolean running  = new AtomicBoolean(false);
	
	public NodeThreading(Node node) {
		
		this.node = node;
		rand = new Random();
	}

	public void stop() {
		
		running.set(false);
	}
	
	public void run() {
		
		running.set(true);
		int a = 0;
		try {
			Thread.sleep(500 + rand.nextInt(500));
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(a != 5 || running.get()) {
			
			if(a == 4) {
				
				node.checkTable();
				a = 0;
			}
			node.sendTable();
			try {
				Thread.sleep(10000 + rand.nextInt(4000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			a++;
		}
		
	}
	
	public void start() {
		
		if(thread == null) {
			
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public Node getNode() {
		
		return node;
	}

}
