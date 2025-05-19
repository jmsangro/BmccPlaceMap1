package application;

public class Mapper {
	private NamedPoint gPSorigin;
	private NamedPoint gPSextent;
	private double fudgeX;
	private double fudgeY;
	
	public Mapper(NamedPoint gPSorigin, NamedPoint gPSextent, double fudgeX, double fudgeY) {
		this.gPSorigin = gPSorigin;
		this.gPSextent = gPSextent;
		this.fudgeX=fudgeX;
		this.fudgeY=fudgeY;
	}
	
	public NamedPoint map(NamedPoint inPoint, NamedPoint canavasOrigin, NamedPoint canvasExtent) {
		NamedPoint returnVal = new NamedPoint();
		//translate Y
		double origin = canavasOrigin.getY();
		double extent = canvasExtent.getY();
		double gpsOrigin = gPSorigin.getY();
		double gpsExtent = gPSextent.getY()-gpsOrigin;
		double fractionOfGPSExtent = (inPoint.getY() - gpsOrigin) /gpsExtent;
		returnVal.setY( origin + fractionOfGPSExtent*extent + fudgeY) ;	
		//translate X
		origin = canavasOrigin.getX();
		extent = canvasExtent.getX();
		gpsOrigin = gPSorigin.getX();
		gpsExtent = gPSextent.getX()-gpsOrigin;
		fractionOfGPSExtent = (inPoint.getX() - gpsOrigin) /gpsExtent;
		returnVal.setX( origin + fractionOfGPSExtent*extent + fudgeX) ;	
		returnVal.setName(inPoint.getName());
		
		return returnVal;
		
	}
	



}
