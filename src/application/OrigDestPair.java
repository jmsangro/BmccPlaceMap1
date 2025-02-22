package application;

public class OrigDestPair {
	private String pairName;
	private NamedPoint origin;
	private NamedPoint destination;
	
	public OrigDestPair(String pairName, NamedPoint origin, NamedPoint destination) {
		this.pairName=pairName;
		this.origin=origin;
		this.destination=destination;
	}
	public String getPairName() {
		return pairName;
	}
	public void setPairName(String pairName) {
		this.pairName = pairName;
	}
	public NamedPoint getOrigin() {
		return origin;
	}
	public void setOrigin(NamedPoint origin) {
		this.origin = origin;
	}
	public NamedPoint getDestination() {
		return destination;
	}
	public void setDestination(NamedPoint destination) {
		this.destination = destination;
	}

}
