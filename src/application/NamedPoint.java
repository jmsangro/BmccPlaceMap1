package application;

public class NamedPoint implements Comparable<NamedPoint> {
	private String name;
	private double x;
	private double y;
	
	public NamedPoint() {
	
	}
	
	public NamedPoint( String name, double x, double y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	@Override
	public int compareTo(NamedPoint o) {
		// TODO Auto-generated method stub
		return this.name.compareTo(o.getName());
	}
	
	public boolean equals(NamedPoint o) {
		return this.name.equals(o.getName());
	}
	
	public String toString() {
		return new String(this.name+'_'+Double.toString(x)+'_'+Double.toString(y));
	}

}
