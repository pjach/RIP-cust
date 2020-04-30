import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
public class Simulation {

	public static void main(String[] args) throws InterruptedException {
		
		String command = null;
		List <Node> nodes = new ArrayList<Node>();
		List <NodeThreading> threads = new ArrayList<NodeThreading>();
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter command: ");
		initialize(nodes,threads);		
		
		while(true) {
			
			printMenu();
			try {
				command = input.readLine();
				switch(command) {
				
				case "1":
					System.out.println("Enter node id: ");
		            String node = input.readLine();
		            printTable(node,threads);
		            break;
		        case "2":
		        	System.out.println("Enter new node's id: ");
		            String id = input.readLine();
		            if(addNode(id,threads,nodes)) {
		            	
			            System.out.println("Node " + id + " has been added to network");
		            }
		            else {
		            	
			            System.out.println("Id already used!");
		            }
		            break;
		        case "3":
		        	System.out.println("Enter first node's id: ");
		            String fNode = input.readLine();
		            System.out.println("Enter second node's id: ");
		            String sNode = input.readLine();
		            if(addConnection(fNode, sNode, threads)) {
		            	
		            	System.out.println("Successful!");
		            }
		            else {
		            	
		            	System.out.println("Error!");
		            }
		            break;
		        case "4":
		           	 System.out.println("Enter node's id: ");
		             String delete = input.readLine();
		             if(removeNode(delete,threads)) {
		              	 System.out.println("Node " + delete + " has been deleted from network");
		               }
		             else {
		                	 
		               	 System.out.println("Wrong node id!");
		             }
	                 break;
		        case "5":
		        	 System.out.println("Enter source node's id: ");
		        	 String src = input.readLine();
		        	 System.out.println("Enter destination node's id: ");
			    	 String dest = input.readLine();
			    	 System.out.println("Enter message: ");
				   	 String msg = input.readLine();
				   	 if(!sendPacket(src,dest,msg,threads)) {
				   		 
				   		 System.out.println("Wrong data!");
				   	 }
				   	 
				   	 break;
		        case "0":
			    	 System.exit(0);
			    	 break;
			     default:
			    	 System.out.println("ERROR - Wrong command!");
			    	 printMenu();
			    	 break;
			}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private static void initialize(List<Node> nodes, List<NodeThreading> threads) {
		
		Node vienas = new Node("N1");
		Node du = new Node("N2");
		Node trys = new Node("N3");
		Node keturi = new Node("N4");
		Node penki = new Node("N5");
		nodes.add(vienas);
		nodes.add(du);
		nodes.add(trys);
		nodes.add(keturi);
		nodes.add(penki);
		vienas.addNeighbour(du);
		vienas.addNeighbour(penki);
		du.addNeighbour(trys);
		trys.addNeighbour(keturi);
		trys.addNeighbour(penki);
		keturi.addNeighbour(penki);
		
		NodeThreading n1 = new NodeThreading(vienas);
		NodeThreading n2 = new NodeThreading(du);
		NodeThreading n3 = new NodeThreading(trys);
		NodeThreading n4 = new NodeThreading(keturi);
		NodeThreading n5 = new NodeThreading(penki);
		threads.add(n1);
		threads.add(n2);
		threads.add(n3);
		threads.add(n4);
		threads.add(n5);
		n1.start();
		n2.start();
		n3.start();
		n4.start();
		n5.start();
	}
	
	private static void printMenu() {
		
		System.out.println("1 - get routing table");
		System.out.println("2 - add node");
		System.out.println("3 - add connection");
		System.out.println("4 - remove node");
		System.out.println("5 - send packet");
		System.out.println("0 - exit");
		System.out.println("");
		
	}
	
	private static void printTable(String nodeId, List<NodeThreading> threads) {
		
		for(NodeThreading thread : threads) {
			
			if(thread.getNode().getId().equals(nodeId)) {
				
				for(DVRResult res : thread.getNode().getRoutingTable()) {
					
					System.out.println("Destination: " + res.destination + " hops: " + res.hopCount +
							" next: " + res.nextHop);
				}
				System.out.println("");
			}
		}
	}
	
	private static boolean addNode(String id, List<NodeThreading> threads, List<Node> nodes) {
		
		for( NodeThreading thread : threads) {
			
			if(thread.getNode().getId().equals(id)) {
				
				return false;
			}
		}
		Node node = new Node(id);
		NodeThreading thr = new NodeThreading(node);
		thr.start();
		nodes.add(node);	
		threads.add(thr);
		return true;
	}
	
	private static boolean addConnection(String fId, String sId, List<NodeThreading> threads) {
		
		boolean found1 = false;
		boolean found2 = false;
		Node node1 = null;
		Node node2 = null;
		for(NodeThreading thread : threads) {
			
			if(thread.getNode().getId().equals(fId)) { 
				
				found1 = true;
				node1 = thread.getNode();
			}
			else if(thread.getNode().getId().equals(sId)) {
				
				found2 = true;
				node2 = thread.getNode();
			}
		}
		if(found1 && found2) {
			
			node1.addNeighbour(node2);
			return true;
		}
		
		return false;
	}
	
	private static boolean removeNode(String nodeId, List<NodeThreading> threads) {
		
		
		boolean found = false;
		NodeThreading temp = null;
		for(NodeThreading thr : threads) {
			
			if(thr.getNode().getId().equals(nodeId)) {
				
				found = true;
				thr.stop();
				temp = thr;
				thr.getNode().removeNode();
			}
		}
		if(temp != null) {
			
			threads.remove(temp);
		}
		if(found) {
			
			return true;
		}
		return false;
	}
	
	private static boolean sendPacket(String sourceId, String destinationId,
									String message, List<NodeThreading> threads) throws InterruptedException {
		
		boolean found1 = false;
		boolean found2 = false;
		Node node1 = null;
		Node node2 = null;
		for(NodeThreading thread : threads) {
			
			if(thread.getNode().getId().equals(sourceId)) { 
				
				found1 = true;
				node1 = thread.getNode();
			}
			else if(thread.getNode().getId().equals(destinationId)) {
				
				found2 = true;
				node2 = thread.getNode();
			}
		}
		if(found1 && found2 || (sourceId.equals(destinationId) && found1) 
							|| (sourceId.equals(destinationId) && found2)) {
			
			node1.sendPacket(0, true, destinationId, message);
			return true;
		}
		return false;
	}

}
