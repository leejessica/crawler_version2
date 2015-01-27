/**
 * 
 */
package mo.umac.crawler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import mo.umac.db.DBInMemory;
import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.ResultSetD2;
import mo.umac.metadata.plugins.IntersectPoint;
import mo.umac.metadata.plugins.VQP;
import paint.PaintShapes;
import mo.umac.spatial.Circle;

import com.infomatiq.jsi.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @author Jessica
 * 
 */
public class Hexagon extends Strategy {

	/**
	 * 
	 */
	// public static int recursion = 1;
	public static int NEED_POINTS_NUMBER=100;
	public static int countPoint = 0;
	public static double sqrt3 = Math.sqrt(3);
	public static double key = 0.97;
	public static int countquery = 0;
	public static Coordinate startPoint = new Coordinate();
	
	private static Set<APOI> queryset = new HashSet<APOI>(); 
	private static Set<APOI> eligibleset = new HashSet<APOI>();
	// record all visited query points
	private static Set<Coordinate> visited_Queue1 = new HashSet<Coordinate>();

	public Hexagon() {
		super();
		startPoint.x = 500;
		startPoint.y=500;
//		startPoint.x=-73.3566809;
//		startPoint.y= 42.372965;
		logger.info("------------HexagonCrawler2------------");
	}

	@Override
	public void crawl(String state, int category, String query,
			Envelope evenlopeState) {

		if (logger.isDebugEnabled()) {
			logger.info("------------crawling-----------");
			logger.info(evenlopeState.toString());
		}
		// finished crawling
		if (evenlopeState == null) {
			return;
		}
		
		
		// Coordinate c = evenlopeState.centre();		
		ununiformlyquery(startPoint, state, category, query);
		
	}

	public void calculatePoint(Coordinate startPoint, double radius,
			Set<Coordinate> visited_Queue1,
			LinkedList<Coordinate> unvisited_Queue) {
		Coordinate[] d = new Coordinate[6];
		for (int i = 0; i < d.length; i++) {
			d[i] = new Coordinate();
		}
		d[0].x = startPoint.x;
		d[0].y = startPoint.y + sqrt3 * radius * key;
		d[1].x = startPoint.x + 3 * radius * key / 2;
		d[1].y = startPoint.y + sqrt3 * radius * key / 2;
		d[2].x = startPoint.x + 3 * radius * key / 2;
		d[2].y = startPoint.y - sqrt3 * radius * key / 2;
		d[3].x = startPoint.x;
		d[3].y = startPoint.y - sqrt3 * radius * key;
		d[4].x = startPoint.x - 3 * radius * key / 2;
		d[4].y = startPoint.y - sqrt3 * radius * key / 2;
		d[5].x = startPoint.x - 3 * radius * key / 2;
		d[5].y = startPoint.y + sqrt3 * radius * key / 2;
		for (int i = 0; i < 6; i++) {
			if (!myContain2(visited_Queue1, d[i])
					&& !myContain1(unvisited_Queue, d[i]))
				unvisited_Queue.addLast(d[i]);
		}
	}

	private boolean myContain1(LinkedList<Coordinate> q, Coordinate c) {
	       boolean flag=false;
			for (int i = 0; i < q.size()&&!flag; i++) {
				Coordinate one = q.get(i);
				if (Math.abs(one.x-c.x)<1e-6&&Math.abs(one.y-c.y)<1e-6) {
					 flag=true;
				}
			}
			return flag;
		}

	private boolean myContain2(Set<Coordinate> q, Coordinate c) {
        boolean flag=false;
		Iterator<Coordinate>it=q.iterator();
		while(it.hasNext()){
			Coordinate one=it.next();
			if (Math.abs(one.x-c.x)<1e-6&&Math.abs(one.y-c.y)<1e-6) {
				 flag=true;
			}
		}
		return flag;
	}
	public void ununiformlyquery(Coordinate startPoint, String state, int category,String query) {

		// record points for next round query
		LinkedList<VQP> visited_Queue = new LinkedList<VQP>();		
		LinkedList<Coordinate> unvisited_Queue = new LinkedList<Coordinate>();
		AQuery Firstquery = new AQuery(startPoint, state, category, query,
				MAX_TOTAL_RESULTS_RETURNED); // issue the first query

		ResultSetD2 resultSetStart = query(Firstquery);
		countquery++;
		visited_Queue1.add(startPoint);
		
		queryset.addAll(resultSetStart.getPOIs()); // put all points gotten from
													// querying into a set
		countPoint = queryset.size(); // count the returned points
		/* calculate the crawl radius */
		int size = resultSetStart.getPOIs().size();
		APOI farthest = resultSetStart.getPOIs().get(size - 1);
		Coordinate farthestCoordinate = farthest.getCoordinate();
		double distance = startPoint.distance(farthestCoordinate);
		//
		Circle aCircle = new Circle(startPoint, distance);
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(aCircle);
			PaintShapes.paint.myRepaint();
		}

		//
		double radius = distance; 
		/* compute coordinates of the points which are used to next round query */
		calculatePoint(startPoint, radius, visited_Queue1, unvisited_Queue);
		int level = 1;
		while (countPoint < NEED_POINTS_NUMBER) {
			for (int i = 1; i <= level * 6; i++) {

				if (!unvisited_Queue.isEmpty()) {

					Coordinate p = unvisited_Queue.removeFirst();
					/*
					 * compute coordinates of the points which are used to next
					 * round query
					 */
					calculatePoint(p, radius, visited_Queue1, unvisited_Queue);
					AQuery Hexquery = new AQuery(p, state, category, query,
							MAX_TOTAL_RESULTS_RETURNED);
					ResultSetD2 resultSet = query(Hexquery);
					countquery++;
					// add the queried point to the set
					queryset.addAll(resultSet.getPOIs());
					int size1 = resultSet.getPOIs().size();
					APOI farthest1 = resultSet.getPOIs().get(size1 - 1);
					Coordinate farthest1Coordinate = farthest1.getCoordinate();
					double distance1 = p.distance(farthest1Coordinate);
					double crawl_radius = distance1;
					visited_Queue1.add(p);
					if (crawl_radius < radius * key) {
						crawl_radius = queryInHexgon(p, crawl_radius, radius, state, category, query);								
					}
					Circle aaCircle = new Circle(p, crawl_radius);
					if (logger.isDebugEnabled() && PaintShapes.painting) {
						PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
						PaintShapes.paint.addCircle(aaCircle);
						PaintShapes.paint.myRepaint();
					}
					VQP visitedPoint = new VQP(p, crawl_radius);
					visited_Queue.addLast(visitedPoint);
					
				}
			}
			double coverRadius = calculateIncircle(startPoint, radius, visited_Queue);
			visited_Queue.clear();
			//
			Circle circle = new Circle(startPoint, coverRadius);
			if (logger.isDebugEnabled() && PaintShapes.painting) {
				PaintShapes.paint.color = PaintShapes.paint.blueTranslucence;
				PaintShapes.paint.addCircle(circle);
				PaintShapes.paint.myRepaint();
			}
			
			// compute the number of eligible point
			Iterator<APOI> it = queryset.iterator();
			while (it.hasNext()) {
				int id = it.next().getId();
				APOI pp = DBInMemory.pois.get(id);
				if (startPoint.distance(pp.getCoordinate()) <= coverRadius)
					eligibleset.add(pp);
			}
			countPoint = eligibleset.size();
			logger.info("eliglible point during the query="+countPoint+"  level="+level);
			if(countPoint==Strategy.TOTAL_POINTS){
				logger.info("We can only find "+TOTAL_POINTS+"points!");
				break;
			}
			level++;
		}
	}

	/*
	 * @crawl_radius: the radius of the circle with the center of query point
	 * 
	 * @radius: the radius of the circle with the center of startPoint
	 */
	public double queryInHexgon(Coordinate point, double crawl_radius,
			double radius, String state, int category,
			String query) {
		LinkedList<VQP>visited_Queue=new LinkedList<VQP>();
        LinkedList<Coordinate>unvisited_Queue=new LinkedList<Coordinate>();
		double coverRadius = crawl_radius;
		/* compute coordinates of the points which are used to next query */
		calculatePoint(point, crawl_radius, visited_Queue1, unvisited_Queue);
		Circle circle = new Circle(point, coverRadius);
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.greenTranslucence;
			PaintShapes.paint.addCircle(circle);
			PaintShapes.paint.myRepaint();
		}

		int temp_Level = 1;
		while (coverRadius < key * radius) {
			for (int i = 1; i <= 6 * temp_Level; i++) {
				if (!unvisited_Queue.isEmpty()) {
					Coordinate q = unvisited_Queue.removeFirst();
					/*
					 * compute coordinates of the points which are used to next
					 * query
					 */
					calculatePoint(q, crawl_radius, visited_Queue1,
							unvisited_Queue);
					AQuery InhexgonQuery = new AQuery(q, state, category,
							query, MAX_TOTAL_RESULTS_RETURNED);
					ResultSetD2 resultSetInhexgon = query(InhexgonQuery);
					countquery++;
					queryset.addAll(resultSetInhexgon.getPOIs());
					int size = resultSetInhexgon.getPOIs().size();
					APOI farthest = resultSetInhexgon.getPOIs().get(size - 1);
					Coordinate farthestCoordinate = farthest.getCoordinate();
					double distance = q.distance(farthestCoordinate);
					double inRadius = distance;
					visited_Queue1.add(q);
					if (inRadius < key * crawl_radius) {
						inRadius = queryInHexgon(q, inRadius, crawl_radius,
								 state, category, query);
					}
					Circle aaaCircle = new Circle(q, inRadius);
					if (logger.isDebugEnabled() && PaintShapes.painting) {
						PaintShapes.paint.color = PaintShapes.paint.greenTranslucence;
						PaintShapes.paint.addCircle(aaaCircle);
						PaintShapes.paint.myRepaint();
					}
					VQP qVQP = new VQP(q, inRadius);
					visited_Queue.addLast(qVQP);
				}
			}

			coverRadius = calculateIncircle(point, radius, visited_Queue);
			visited_Queue.clear();
			temp_Level++;
		}

		return coverRadius;
	}

	/* calculate the intersecting points of two circle */
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
			intersect = new IntersectPoint(p1, r1, p2, r2, intersectP1, intersectP1);
		} else {
			Coordinate intersectP1 = new Coordinate(Xc, Yc);
			Coordinate intersectP2 = new Coordinate(Xd, Yd);
			intersect = new IntersectPoint(p1, r1, p2, r2, intersectP1,
					intersectP2);
		}
		return intersect;
	}

	// to determine whether a circle contains another circle and assume that
		// circle1.radius>circle2.radius(i.e., judge if circle1 contains circle2)
	public boolean circle_contain(VQP circle1, VQP circle2) {
			double d1 = circle1.getCoordinate().distance(circle2.getCoordinate());
			double d2 = circle1.getRadius() - circle2.getRadius();
			if (d1 <=d2 )
				return true;
			return false;
		}

		// to determine whether 2 circles intersect or not
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
	
	// determine whether a point is in a circle or not
		public boolean isinCircle(Coordinate p, VQP vqp) {
			boolean flag = false;
			if(vqp.getRadius()>vqp.getCoordinate().distance(p))
				flag=true;
			return flag;
		}
	/* algorithm 1 */
	public double calculateIncircle(Coordinate startPoint,double radius, LinkedList<VQP>visitedcircle_Queue) {
		double minRadius=1e308;
		for(int i=0;i<visitedcircle_Queue.size()-1;i++){
			VQP circle1=visitedcircle_Queue.get(i);
			for(int j=i+1;j<visitedcircle_Queue.size();j++){
				VQP circle2=visitedcircle_Queue.get(j);
				
				double dr=circle1.getRadius()-circle2.getRadius();
				//circle1 contain circle2, no need processing circle2
				if(dr>0&&circle_contain(circle1, circle2)){					
					continue;				
				}
				//circle2 contain circle1, no need processing circle1
				else if(dr<0&&circle_contain(circle2, circle1)){
					break;
					}
				else if(circles_Insecter(circle1, circle2)){
					IntersectPoint inter=calculateIntersectPoint(circle1, circle2);
					double d1=inter.getIntersectPoint_left().distance(startPoint);
					double d2=inter.getIntersectPoint_right().distance(startPoint);
					Coordinate temP=new Coordinate();
					if(d1>d2)
						temP=inter.getIntersectPoint_left();
					else temP=inter.getIntersectPoint_right();
					//test if the temP is inside another circle
					boolean in=false;
					Iterator<VQP>it=visitedcircle_Queue.iterator();
					while(it.hasNext()&&!in){
						VQP circle3=it.next();						
						if(!circle1.getCoordinate().equals2D(circle3.getCoordinate())
								&&!circle2.getCoordinate().equals2D(circle3.getCoordinate())){
							if(isinCircle(temP, circle3)){
								in=true;
							}
						}
					}
					if(!in){
						minRadius=Math.min(minRadius, temP.distance(startPoint));
					}
				}
			}
		}
		return minRadius;
	}
}

