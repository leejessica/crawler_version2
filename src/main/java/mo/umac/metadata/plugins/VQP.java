package mo.umac.metadata.plugins;

/******************************************************
 * The data structure is used to record the center of a circle and its radius

 * */
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import mo.umac.db.DBInMemory;
import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class VQP {
	private Coordinate point = new Coordinate();
	private double radius;
	//private int level;

	public VQP() {

	}

	public VQP(Coordinate point, double radius/*, int level*/) {
		super();
		this.point = point;
		this.radius = radius;
		//this.level = level;
	}

	/*public void setlevel(int level) {
		this.level = level;
	}

	public int getlevel() {
		return this.level;
	}*/

	public void setLongitude(double longitude) {
		this.point.y = longitude;
	}

	public double getLongtitude() {
		return point.y;
	}

	public void setLatitude(double latitude) {
		this.point.x = latitude;
	}

	public double getLatitude() {
		return point.x;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.point = coordinate;
	}

	public Coordinate getCoordinate() {
		return point;
	}

	public void setRadius(double farthestdistance) {
		this.radius = farthestdistance;

	}

	public double getRadius() {
		return this.radius;
	}
}
