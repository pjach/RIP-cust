import java.util.*;

public class Node {
	
	private String id;
	private List<Node> neighb;
	private List<DVRResult> routingTable;

	public Node (String id) {
		
		this.id = id;
		neighb = new ArrayList<Node>();
		routingTable = new ArrayList<DVRResult>();
		routingTable.add(new DVRResult(id,0,id));
		
	}
	
	public synchronized List<Node> getNeighbours(){
		
		return neighb;
	}

	public synchronized String getId() {
		
		return id;
	}

	public synchronized List<DVRResult> getRoutingTable(){
		
		return routingTable;
	}
	
	public synchronized void addNeighbour(Node node) {
		
		if(node != null && !neighb.contains(node)) {
			neighb.add(node);
			routingTable.add(new DVRResult(node.getId(),1,node.getId()));
			node.getRoutingTable().add(new DVRResult(this.id,1,this.id));
			node.getNeighbours().add(this);
		}
	}

	public synchronized void removeNode() {
		
		for (Node node : neighb) {
			
			if(node.getNeighbours().contains(this)) {
				
				node.getNeighbours().remove(this);
			}
		}
		neighb.clear();
	}
	
	public synchronized void sendPacket(int check, boolean first, String dest, String text) throws InterruptedException {
		
		check = 0;
		if(dest.equals(id)) {
			
			this.receivePacket(text);
		}
		else {
			
			for(DVRResult res : routingTable) {
				
				if(res.destination.equals(dest)) {

					for(Node node : neighb) {
						
						if(node.getId().equals(res.nextHop)) {
							check = 1;
							if(first) {
								first = false;
								System.out.println("Node: " + this.id + 
										" is sending packet to: " + node.getId());
							}
							else {
								System.out.println("Node: " + this.id + 
										" is routing packet to: " + node.getId());
								
							}
							node.sendPacket(check, first, dest, text);
						}
					}				
				}
			}
			if(check == 0) {
				
				for(DVRResult res : routingTable) {
					
					if(res.destination.equals(dest)) {
						
						res.hopCount = 16;
					}
				}
				System.out.println("Packet dropped at node :" + this.id);
			}
		}
		
	}
	
	public synchronized void receivePacket(String text) {
		
		System.out.println("Node " + id + " received text: " + text);
	}
		
	public synchronized void sendTable() {
		
		for(Node node : neighb) {
			
			node.receiveTable(routingTable, id);
		}
	}
	
	public synchronized void receiveTable(List<DVRResult> temp, String nodeId) {
		
		this.updateRTable(temp,nodeId);
	}
	
	public synchronized void updateRTable(List<DVRResult> temp, String nodeId) {
		
		boolean found = false;
		for(DVRResult ext : temp) {
			
			found = false;
			for(DVRResult inter : routingTable) {
				
				if(ext.destination.equals(this.id)) {
					
					found = true;
				}
				if(inter.destination.equals(ext.destination)) {
					
					found = true;
					if(ext.hopCount == 16) {
						
						if(inter.hopCount != 16) {

							inter.hopCount = 16;
						}
						
					}
					else if(((ext.hopCount+1) < inter.hopCount) && inter.hopCount < 15) {
						
						inter.hopCount = (ext.hopCount+1);
						inter.nextHop = nodeId;
					}
				}
			}
			if(found == false && ext.hopCount < 15) {
				
				DVRResult copy = new DVRResult(nodeId,ext.hopCount+1,ext.destination);
				routingTable.add(copy);
			}
		}
	}
	
	public synchronized void checkTable() {
		
		List<DVRResult> temp = new ArrayList<DVRResult>();
		boolean found = false;
		for(DVRResult res : routingTable) {
			
			if(res.destination.equals(this.id) || res.nextHop.equals(this.id)) {
				
				res.hopCount = 0;
			}
			else if(res.hopCount == 16) {
				
				temp.add(res);
			}
		}
		for(DVRResult res : temp) {
			
			routingTable.remove(res);
		}
		for(DVRResult res : routingTable) {
			
			if(!res.destination.equals(this.id) && !res.nextHop.equals(this.id)) {
				
				found = false;
				for(Node node : neighb) {
					
					if(res.nextHop.equals(node.getId())) {found = true;}
				}
				if(found == false) {res.hopCount = 16;}
			}
		}
		
	}
	
}
