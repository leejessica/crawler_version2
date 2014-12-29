package mo.umac.metadata.plugins;

import com.vividsolutions.jts.geom.Coordinate;

/*
 * this class is used in perpheryquery and record one intersection point between one circle 
 * and the central circle and some other information, such as the neighbor circle correspoding 
 * to the intersection point, which is the nearest circle to the intersection point on the same 
 * level circumference
 * */

public class PandC {
	private Coordinate intersection=new Coordinate();
	private Coordinate neighborP=new Coordinate();
	private double radius;
	
	public PandC(){
	}
	
	public PandC(Coordinate a, Coordinate b, double r){
		this.intersection=a;
		this.neighborP=b;
		this.radius=r;
	}
	
	public void setintersection(Coordinate a){
		this.intersection=a;
	}
	
	public Coordinate getintersection(){
		return this.intersection;
	}
	
	public void setneighborcenter(Coordinate b){
		this.neighborP=b;
	}
	
	public Coordinate getneighborcenter(){
		return this.neighborP;
	}
	
	public void setRadius(double r){
		this.radius=r;
	}
	 
	public double getRadius(){
		return this.radius;
	}

}
