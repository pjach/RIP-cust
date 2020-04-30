
public class DVRResult {
	
	public String nextHop;
	public String destination;
	public int hopCount;
	
	public DVRResult(String hop, int count, String dest) {
	
		nextHop = hop;
		hopCount = count;
		destination = dest;
		
	}

}
