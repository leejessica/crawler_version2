import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.xml.DOMConfigurator;

import paint.PaintShapes;
import paint.WindowUtilities;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import mo.umac.crawler.Hexagon_optimize;
import mo.umac.crawler.MainYahoo;
import mo.umac.crawler.Strategy;
import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.ResultSetD2;
import mo.umac.metadata.plugins.IntersectPoint;
import mo.umac.metadata.plugins.VQP;
import mo.umac.spatial.Circle;

public class FunctionTest extends Strategy {

	public static double key = 0.97;
	public static double sqrt3 = Math.sqrt(3);

	@Override
	public void crawl(String state, int category, String query,
			Envelope envelopeState) {

	}

	public static void main(String arg[]) {
		DOMConfigurator.configure(MainYahoo.LOG_PROPERTY_PATH);
		FunctionTest test = new FunctionTest();
		PaintShapes.painting = true;
	    WindowUtilities.openInJFrame(PaintShapes.paint, 1000, 1000);

		test.calling();
	}

	public void calling() {
		/** inorder to correct the paint function */
		Circle tc = new Circle(new Coordinate(-73, 42), 0);
		if (PaintShapes.painting && logger.isDebugEnabled()) {
			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(tc);
			PaintShapes.paint.myRepaint();
		}
		Set<VQP> visitedInfo = new HashSet<VQP>();
		visitedInfo.add(new VQP(new Coordinate(500.0, 606.9270349867776),50.45995809029888));
		visitedInfo.add(new VQP(new Coordinate(622.9633844698596,677.9199781109185), 0));
		visitedInfo.add(new VQP(new Coordinate(568.1209434219035,709.5832762129721), 33.77047622597312));
		visitedInfo.add(new VQP(new Coordinate(568.1209434219035,762.2262387702363), 48.87814278279693));
		visitedInfo.add(new VQP(new Coordinate(568.1209434219035,582.9300838047575), 70.35027225504778));
		visitedInfo.add(new VQP(new Coordinate(613.7110863269672,735.9047574916042), 50.27771531851156));
		visitedInfo.add(new VQP(new Coordinate(500.0, 685.5863250309521),54.27155890487294));
		visitedInfo.add(new VQP(new Coordinate(500.0, 500.0), 68.5937249258495));
		visitedInfo.add(new VQP(new Coordinate(522.5308005168397,735.9047574916042), 52.73226003822247));
		visitedInfo.add(new VQP(new Coordinate(568.1209434219035,646.2566800088648), 40.62403040589344));
		visitedInfo.add(new VQP(new Coordinate(613.7110863269672,683.2617949343401), 52.56267841697679));
		visitedInfo.add(new VQP(new Coordinate(522.5308005168397,683.2617949343401), 0));
		visitedInfo.add(new VQP(new Coordinate(622.9633844698596,614.5933819068111), 68.5801280129872));
		visitedInfo.add(new VQP(new Coordinate(568.1209434219035,656.940313655708), 0));
		visitedInfo.add(new VQP(new Coordinate(513.2785023739473,614.5933819068111), 0));
		visitedInfo.add(new VQP(new Coordinate(431.8790565780965, 619.0014933099038), 45.125938915443506));
		visitedInfo.add(new VQP(new Coordinate(500.0, 528.267744942603), 53.23507394129593));
		visitedInfo.add(new VQP(new Coordinate(431.8790565780965, 567.5973899646904), 32.97574767090039));
		visitedInfo.add(new VQP(new Coordinate(513.2785023739473, 677.9199781109185), 0));
		visitedInfo.add(new VQP(new Coordinate(568.1209434219035, 567.5973899646904), 0));
		VQP circle = new VQP(new Coordinate(476.396315933812, 593.2994416372972), 32.97574767090039);
		Iterator<VQP>itt=visitedInfo.iterator();
		while(itt.hasNext()){
			VQP ttv=itt.next();
			Circle tcc1 = new Circle(ttv.getCoordinate(), ttv.getRadius());
			if (PaintShapes.painting && logger.isDebugEnabled()) {
				PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
				PaintShapes.paint.addCircle(tcc1);
				PaintShapes.paint.myRepaint();
			}
		}
		Circle tcc = new Circle(circle.getCoordinate(), circle.getRadius());
		if (PaintShapes.painting && logger.isDebugEnabled()) {
			PaintShapes.paint.color = PaintShapes.paint.blueTranslucence;
			PaintShapes.paint.addCircle(tcc);
			PaintShapes.paint.myRepaint();
		}
		
		
		
//		if (needQuery(circle, visitedInfo))
//			System.out.println("need query");
		System.out.println("end calling!");
	}

	
	public boolean needQuery(VQP circle, Set<VQP> visitedcircle_Queue
	/* Envelope envelopeState */) {
		boolean needquery = false;

		// record the effective neighbors of the circle need to be judged
		Set Neighbor_set = new HashSet<VQP>();
		Iterator<VQP> it = visitedcircle_Queue.iterator();
		// "beIncluded=true" means circle was included by another visited
		// circle
		boolean beIncluded = false;
		// record all the neighbors of a query circle
		Set tempNeighbor_set = new HashSet<VQP>();
		while (it.hasNext() && !beIncluded) {
			VQP circle1 = it.next();
			// to judge which circle is the larger one
			if (circle1.getRadius() > 0) {
				double d1 = circle.getRadius() - circle1.getRadius();
				if (d1 > 0 && circle_contain(circle, circle1)) {
					tempNeighbor_set.add(circle1);
				} else if (d1 < 0 && circle_contain(circle1, circle)) {
					beIncluded = true;
				} else if (circles_Insecter(circle, circle1)) {
					IntersectPoint inter = calculateIntersectPoint(circle,
							circle1);
					if (!inter.getIntersectPoint_left().equals2D(
							inter.getIntersectPoint_right()))
						tempNeighbor_set.add(circle1);
				}
			}
		}
		//
		Iterator<VQP> itt1 = tempNeighbor_set.iterator();
		while (itt1.hasNext()) {
			VQP vtt = itt1.next();
			logger.info(vtt.getCoordinate().toString() + "  " + vtt.getRadius());
			Circle cc = new Circle(vtt.getCoordinate(), vtt.getRadius());
			if (PaintShapes.painting && logger.isDebugEnabled()) {
				PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
				PaintShapes.paint.addCircle(cc);
				PaintShapes.paint.myRepaint();
			}
		}
		logger.info("-------------------------------------------");
		//
		if (!beIncluded) {
			// optimal the Neighbor_set and only retain the effective
			// neighbors
			Iterator<VQP> it1 = tempNeighbor_set.iterator();
			while (it1.hasNext()) {
				VQP c1 = it1.next();
				boolean effective = true;
				Iterator<VQP> it2 = tempNeighbor_set.iterator();
				while (it2.hasNext() && effective) {
					VQP c2 = it2.next();
					if (!pointsequal(c1.getCoordinate(), c2.getCoordinate())) {
						if (circle_contain(c2, c1)) {
							effective = false;
						} else if (circles_Insecter(circle, c1)
								&& circles_Insecter(c1, c2)) {
							if (arc_contain(c1, c2, circle)) {
								IntersectPoint tp = calculateIntersectPoint(c1,
										c2);
								if (!isinCircle(tp.getIntersectPoint_left(),
										circle)
										&& !isinCircle(
												tp.getIntersectPoint_right(),
												circle))
									effective = false;
							}
						}
					}
				}
				if (effective)
					Neighbor_set.add(c1);
			}
			Iterator<VQP> itt2 = Neighbor_set.iterator();
			while (itt2.hasNext()) {
				VQP vtt1 = itt2.next();
				logger.info(vtt1.getCoordinate().toString() + "  "
						+ vtt1.getRadius());
//				Circle cc = new Circle(vtt1.getCoordinate(), vtt1.getRadius());
//				if (PaintShapes.painting && logger.isDebugEnabled()) {
//					PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
//					PaintShapes.paint.addCircle(cc);
//					PaintShapes.paint.myRepaint();
//				}
			}
			logger.info("==================================================");
			// determine whether the circle need to be queried or not
			if (!isCircumferenceCoverage(circle, Neighbor_set))
				needquery = true;
			else {
				Iterator<VQP> it3 = Neighbor_set.iterator();
				while (it3.hasNext() && !needquery) {
					VQP circle1 = it3.next();
					if (!isPerimeterCoverage(circle, circle1, Neighbor_set))
						needquery = true;
				}
			}
		}

		return needquery;
	}

	public boolean arc_contain(VQP c1, VQP c2, VQP c) {
		IntersectPoint inter1 = calculateIntersectPoint(c1, c);
		Coordinate mid=new Coordinate();
		mid.x=(inter1.getIntersectPoint_left().x+inter1.getIntersectPoint_right().x)/2;
		mid.y=(inter1.getIntersectPoint_left().y+inter1.getIntersectPoint_right().y)/2;
		Coordinate A[]=line_circle_intersect(c, mid);
		Coordinate arcmidpoint=new Coordinate();
		if(isinCircle(A[0], c1))
			arcmidpoint=A[0];
		else arcmidpoint=A[1];
		if (isinCircle(inter1.getIntersectPoint_left(), c2)
				&& isinCircle(inter1.getIntersectPoint_right(), c2)
				&&isinCircle(arcmidpoint, c2)) {
			return true;
		} else
			return false;
	}
	
	public Coordinate[] line_circle_intersect(VQP circle,Coordinate p){
		Coordinate startPoint=circle.getCoordinate();
		double radius=circle.getRadius();
		 Coordinate[] a =new Coordinate[2];
		 a[0]=new Coordinate();
		 a[1]=new Coordinate();
		 //the slope of the line:k=infinite
		 if(p.x==startPoint.x){
			a[0].x=startPoint.x;
			a[0].y=startPoint.y+radius;
			a[1].x=startPoint.x;
			a[1].y=startPoint.y-radius;
		 }
		 //k=0
		 else if(p.y==startPoint.y){
			 a[0].x=startPoint.x+radius;
			 a[0].y=startPoint.y;
			 a[1].x=startPoint.x-radius;
			 a[1].y=startPoint.y;
		 }
		 else{
			 double k=(p.y-startPoint.y)/(p.x-startPoint.x);
			 double A=Math.sqrt((radius*radius)/(1+k*k));
			 a[0].x=startPoint.x+A;
			 a[0].y=startPoint.y+k*A;
			 a[1].x=startPoint.x-A;
			 a[1].y=startPoint.y-k*A;
		 }
		 return a;
	}

	public boolean isCircumferenceCoverage(VQP circle, Set<VQP> Neighbor_set) {
		boolean coverage = true;
		Iterator<VQP> it1 = Neighbor_set.iterator();
		// if there is a intersecting point is not in any other circles, the
		// circle is not perimeter covered
		while (it1.hasNext() && coverage) {
			VQP circle1 = it1.next();
			if (circles_Insecter(circle, circle1)) {
				IntersectPoint inter1 = calculateIntersectPoint(circle, circle1);
				// "stop=true" means a intersecting point is inside a circle
				boolean stop1 = false;
				boolean stop2 = false;
				Iterator<VQP> it2 = Neighbor_set.iterator();
				// if the two intersecting points are in any other neighbor
				// circles,
				// then stop
				while (it2.hasNext()) {
					VQP circle2 = it2.next();
					if (!pointsequal(circle1.getCoordinate(),
							circle2.getCoordinate())) {

						if (!stop1
								&& (isinCircle(inter1.getIntersectPoint_left(),
										circle2) || isAtCircumference(
										inter1.getIntersectPoint_left(),
										circle2))) {
							stop1 = true;
						}
						if (!stop2
								&& (isinCircle(
										inter1.getIntersectPoint_right(),
										circle2) || isAtCircumference(
										inter1.getIntersectPoint_right(),
										circle2))) {
							stop2 = true;
						}
					}
				}
				// There is at least a point not inside any circle
				if (!stop1 || !stop2)
					coverage = false;
			}
		}
		return coverage;
	}

	private boolean myContain2(LinkedList<VQP> q, Coordinate c) {
		boolean flag = false;
		System.out.println(q.size());
		for (int i = 0; i < q.size() && !flag; i++) {
			Coordinate one = q.get(i).getCoordinate();
			if (c.equals2D(one)) {
				flag = true;
			}
		}
		return flag;
	}

	public boolean pointsequal(Coordinate p1, Coordinate p2) {
		if (Math.abs(p1.x - p2.x) < 1e-6 && Math.abs(p1.y - p2.y) < 1e-6)
			return true;
		else
			return false;
	}

	// determine whether a point is at the circumference of a circle
	public boolean isAtCircumference(Coordinate p, VQP circle) {
		boolean atCircumference = false;
		if (Math.abs(circle.getCoordinate().distance(p) - circle.getRadius()) < 1e-6) {
			atCircumference = true;
		}
		return atCircumference;
	}

	public boolean isPerimeterCoverage(VQP circle, VQP circle1,
			Set<VQP> Neighbor_set) {
		boolean coverage = true;
		Iterator<VQP> it1 = Neighbor_set.iterator();
		// @param: record all the neighbors to circle1 except circle
		LinkedList<VQP> intercircleQ = new LinkedList<VQP>();
		// update the intercircleQ
		while (it1.hasNext()) {
			VQP circle2 = it1.next();
			if (!pointsequal(circle1.getCoordinate(), circle2.getCoordinate())
					&& circles_Insecter(circle1, circle2)) {
				IntersectPoint inter = calculateIntersectPoint(circle1, circle2);
				if (!inter.getIntersectPoint_left().equals2D(
						inter.getIntersectPoint_right()))
					intercircleQ.addLast(circle2);
			}
		}
		Iterator<VQP> it2 = intercircleQ.iterator();
		while (it2.hasNext() && coverage) {
			VQP circletemp1 = it2.next();
			System.out.println("circletemp1="
					+ circletemp1.getCoordinate().toString());
			IntersectPoint inter1 = calculateIntersectPoint(circle1,
					circletemp1);
			boolean stopleft = false;
			boolean stopright = false;
			if (isinCircle(inter1.getIntersectPoint_left(), circle)) {
				Iterator<VQP> it3 = intercircleQ.iterator();
				while (it3.hasNext() && !stopleft) {
					VQP circletemp2 = it3.next();
					if (!pointsequal(circletemp1.getCoordinate(),
							circletemp2.getCoordinate())) {
						if (isinCircle(inter1.getIntersectPoint_left(),
								circletemp2)
								|| isAtCircumference(
										inter1.getIntersectPoint_left(),
										circletemp2)) {
							stopleft = true;
							System.out.println("circletemp2="
									+ circletemp2.getCoordinate().toString());
						}
					}
				}
			} else {
				stopleft = true;
			}
			if (isinCircle(inter1.getIntersectPoint_right(), circle)) {
				Iterator<VQP> it4 = intercircleQ.iterator();
				while (it4.hasNext() && !stopright) {
					VQP circletemp3 = it4.next();
					if (!pointsequal(circletemp1.getCoordinate(),
							circletemp3.getCoordinate())) {
						if (isinCircle(inter1.getIntersectPoint_right(),
								circletemp3)
								|| isAtCircumference(
										inter1.getIntersectPoint_right(),
										circletemp3)) {
							stopright = true;
						}
					}
				}
			} else {
				stopright = true;
			}
			if (!stopleft || !stopright)
				coverage = false;
		}
		return coverage;
	}

	public IntersectPoint calculateIntersectPoint(VQP circle1, VQP circle2) {

		Coordinate p1 = circle1.getCoordinate();
		double r1 = circle1.getRadius();
		Coordinate p2 = circle2.getCoordinate();
		double r2 = circle2.getRadius();
		double L = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
				* (p1.y - p2.y));
		double AE = (r1 * r1 - r2 * r2 + L * L) / (2 * L);
		double CE = Math.sqrt(r1 * r1 - AE * AE);
		double Xc = 0;
		double Yc = 0;
		double Xd = 0;
		double Yd = 0;
		if (p1.y == p2.y) {
			double x0 = p1.x + ((p2.x - p1.x) * AE) / L;
			double y0 = p1.y;
			Xc = x0;
			Xd = x0;
			Yc = y0 + CE;
			Yd = y0 - CE;
		} else if (p1.x == p2.x) {
			double x0 = p1.x;
			double y0 = p1.y + ((p2.y - p1.y) * AE) / L;
			Yc = y0;
			Yd = y0;
			Xc = x0 + CE;
			Xd = x0 - CE;
		} else {
			double k1 = (p1.y - p2.y) / (p1.x - p2.x);
			double k2 = -1 / k1;
			double x0 = p1.x + ((p2.x - p1.x) * AE) / L;
			double y0 = p1.y + k1 * (x0 - p1.x);
			double R2 = r1 * r1 - (x0 - p1.x) * (x0 - p1.x) - (y0 - p1.y)
					* (y0 - p1.y);
			double EF = Math.sqrt(R2 / (1 + k2 * k2));
			Xc = x0 - EF;
			Yc = y0 + k2 * (Xc - x0);
			Xd = x0 + EF;
			Yd = y0 + k2 * (Xd - x0);
		}
		IntersectPoint intersect = new IntersectPoint();
		if (Math.abs(Xc - Xd) < 1e-6 && Math.abs(Yc - Yd) < 1e-6) {
			Coordinate intersectP1 = new Coordinate(Xc, Yc);
			intersect = new IntersectPoint(p1, r1, p2, r2, intersectP1,
					intersectP1);
		} else {
			Coordinate intersectP1 = new Coordinate(Xc, Yc);
			Coordinate intersectP2 = new Coordinate(Xd, Yd);
			intersect = new IntersectPoint(p1, r1, p2, r2, intersectP1,
					intersectP2);
		}
		return intersect;
	}

	public boolean circles_Insecter(VQP circle1, VQP circle2) {
		boolean intersect = false;
		double l1 = circle1.getCoordinate().distance(circle2.getCoordinate());
		double l2 = Math.abs(circle1.getRadius() - circle2.getRadius());
		double l3 = circle1.getRadius() + circle2.getRadius();
		if (l2 < l1 && l1 < l3) {
			intersect = true;
		}
		return intersect;
	}

	public double calculateIncircle(Coordinate startPoint, double radius,
			LinkedList<VQP> visitedcircle_Queue) {
		double minRadius = 1e308;
		for (int i = 0; i < visitedcircle_Queue.size() - 1; i++) {
			VQP circle1 = visitedcircle_Queue.get(i);
			for (int j = i + 1; j < visitedcircle_Queue.size(); j++) {
				VQP circle2 = visitedcircle_Queue.get(j);

				double dr = circle1.getRadius() - circle2.getRadius();
				// circle1 contain circle2, no need processing circle2
				if (dr > 0 && circle_contain(circle1, circle2)) {
					continue;
				}
				// circle2 contain circle1, no need processing circle1
				else if (dr < 0 && circle_contain(circle2, circle1)) {
					break;
				} else if (circles_Insecter(circle1, circle2)) {
					IntersectPoint inter = calculateIntersectPoint(circle1,
							circle2);
					double d1 = inter.getIntersectPoint_left().distance(
							startPoint);
					double d2 = inter.getIntersectPoint_right().distance(
							startPoint);
					Coordinate temP = new Coordinate();
					if (d1 > d2)
						temP = inter.getIntersectPoint_left();
					else
						temP = inter.getIntersectPoint_right();
					// test if the temP is inside another circle
					boolean in = false;
					VQP firstcircle = new VQP(startPoint, radius);
					if (isinCircle(temP, firstcircle))
						in = true;
					Iterator<VQP> it = visitedcircle_Queue.iterator();
					while (it.hasNext()) {
						if (!in) {
							VQP circle3 = it.next();
							if (!circle1.getCoordinate().equals2D(
									circle3.getCoordinate())
									&& !circle2.getCoordinate().equals2D(
											circle3.getCoordinate())) {
								if (isinCircle(temP, circle3)) {
									in = true;
								}
							}
						}
					}
					if (!in) {
						minRadius = Math.min(minRadius,
								temP.distance(startPoint));
					}
				}
			}
		}
		return minRadius;
	}

	public boolean circle_contain(VQP circle1, VQP circle2) {
		double d1 = circle1.getCoordinate().distance(circle2.getCoordinate());
		double d2 = circle1.getRadius() - circle2.getRadius();
		if (d1 <= d2)
			return true;
		return false;
	}

	// determine whether a point is in a circle or not
	public boolean isinCircle(Coordinate p, VQP vqp) {
		boolean flag = false;
		if (vqp.getRadius() > vqp.getCoordinate().distance(p))
			flag = true;
		return flag;
	}
	
	public LinkedList<Coordinate []> findUncoverarc(VQP circle, LinkedList<VQP>neighborList){
		LinkedList<Coordinate[]>uncoverArc=new LinkedList<Coordinate[]>();
		LinkedList<double[]>coverangle=new LinkedList<double[]>();
		HashMap<Double, Coordinate> angle_coordinate=new HashMap<Double, Coordinate>();
		//record all the covered arc 
		for(int i=0;i<neighborList.size();i++){
			VQP c=neighborList.get(i);
			IntersectPoint inter=calculateIntersectPoint(circle, c);
		    Coordinate mid=new Coordinate();
		    mid.x=(inter.getIntersectPoint_left().x+inter.getIntersectPoint_right().x)/2;
		    mid.y=(inter.getIntersectPoint_left().y+inter.getIntersectPoint_right().y)/2;
		    Coordinate a[]=line_circle_intersect(circle, mid);
		    Coordinate arcmid=new Coordinate();
		    if(isinCircle(a[0], c))
		    	arcmid=a[0];
		    else arcmid=a[1];
		    double angle0=getSlantangle(circle.getCoordinate(), inter.getIntersectPoint_left());
		    double angle1=getSlantangle(circle.getCoordinate(), inter.getIntersectPoint_right());
		    double angle2=getSlantangle(circle.getCoordinate(), arcmid);
		    if(angle2<Math.min(angle0, angle1)||angle2>Math.max(angle0, angle1)){
		        double b1[]=new double[2];
		        b1[0]=0;
		        b1[1]=Math.min(angle0, angle1);
		        coverangle.add(b1);
		        double b2[]=new double[2];
		        b2[0]=Math.max(angle0, angle1);
		        b2[1]=360;
		        coverangle.add(b2);
		    }
		    else{
		    	double b3[]=new double[2];
		    	b3[0]=Math.min(angle0, angle1);
		    	b3[1]=Math.max(angle0, angle1);
		    	coverangle.add(b3);
		    }
		    angle_coordinate.put(angle0, inter.getIntersectPoint_left());
		    angle_coordinate.put(angle1, inter.getIntersectPoint_right());
		}
		//merge the cover arc
		LinkedList<double[]>mergecoverangle=new LinkedList<double[]>();
		for(int j=0;j<coverangle.size()-1;j++){
			double c1[]=coverangle.get(j);
			for(int k=j+1;k<coverangle.size();k++){
				double c2[]=coverangle.get(k);
			}
		}
		return uncoverArc;
	}
	
	public double getSlantangle(Coordinate centerP, Coordinate p2){
		double slantangle=0;
		if(p2.x==centerP.x){
			if(p2.y>centerP.y){
				slantangle=Math.PI/2;
				slantangle=Math.toDegrees(slantangle);
			}
			else {
				slantangle=Math.PI*3/2;
				slantangle=Math.toDegrees(slantangle);
			}
		}
		else{
			double k=(p2.y-centerP.y)/(p2.x-centerP.x);
			slantangle=Math.atan(k);
			slantangle=Math.toDegrees(slantangle);
		}
		return slantangle;
	}

}
