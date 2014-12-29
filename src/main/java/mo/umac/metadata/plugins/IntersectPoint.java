package mo.umac.metadata.plugins;

/*This structure is to record the information of two intersecting circles*/
import com.vividsolutions.jts.geom.Coordinate;

public class IntersectPoint {

	private Coordinate circlePoint1 = new Coordinate();
	private Coordinate circlePoint2 = new Coordinate();
	double radius1;
	double radius2;
	Coordinate intersectPoint_left = new Coordinate();
	Coordinate intersectPoint_right = new Coordinate();

	public IntersectPoint() {

	}

	public IntersectPoint(Coordinate circleP1, double r1, Coordinate circleP2,
			double r2, Coordinate intersectP1, Coordinate intersectP2) {
		this.circlePoint1 = circleP1;
		this.radius1 = r1;
		this.circlePoint2 = circleP2;
		this.radius2 = r2;
		this.intersectPoint_left = intersectP1;
		this.intersectPoint_right = intersectP2;
	}

	public void setCiclePoint1(Coordinate circleP1) {
		this.circlePoint1 = circleP1;
	}

	public Coordinate getCirclePoint1() {
		return this.circlePoint1;
	}

	public void setCiclePoint2(Coordinate circleP2) {
		this.circlePoint2 = circleP2;
	}

	public Coordinate getCirclePoint2() {
		return this.circlePoint2;
	}

	public void setIntersectPoint_left(Coordinate intersectP1) {
		this.intersectPoint_left = intersectP1;
	}

	public Coordinate getIntersectPoint_left() {
		return this.intersectPoint_left;
	}

	public void setIntersectPoint_right(Coordinate intersectP2) {
		this.intersectPoint_right = intersectP2;
	}

	public Coordinate getIntersectPoint_right() {
		return this.intersectPoint_right;
	}

	public void setRadius1(double r1) {
		this.radius1 = r1;
	}

	public double getRadius1() {
		return this.radius1;
	}

	public void setRadius2(double r2) {
		this.radius1 = r2;
	}

	public double getRadius2() {
		return this.radius2;
	}
}
