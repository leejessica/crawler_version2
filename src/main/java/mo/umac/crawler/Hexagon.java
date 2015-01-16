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
	public static int NEED_POINTS_NUMBER=200;
	public static int countPoint = 0;
	public static double sqrt3 = Math.sqrt(3);
	public static double key = 0.97;
	public static int countquery = 0;

	public Hexagon() {
		super();
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
		Coordinate startPoint = new Coordinate();
		startPoint.x = (evenlopeState.getMinX() + evenlopeState.getMaxX()) / 2;
		startPoint.y = (evenlopeState.getMinY() + evenlopeState.getMaxY()) / 2;
//		startPoint.x=-73.3566809;
//		startPoint.y= 42.372965;
		// Coordinate c = evenlopeState.centre();

		// record points for next round query
		LinkedList<VQP> visited_Queue = new LinkedList<VQP>();
		// record all visited query points
		LinkedList<Coordinate> visited_Queue1 = new LinkedList<Coordinate>();
		LinkedList<Coordinate> unvisited_Queue = new LinkedList<Coordinate>();
		ununiformlyquery(startPoint, visited_Queue, visited_Queue1,
				unvisited_Queue, state, category, query);
		System.out.println("eligiblepoint="+countPoint);
		System.out.println("         countquery"+countquery);
	}

	public void calculatePoint(Coordinate startPoint, double radius,
			LinkedList<Coordinate> visited_Queue1,
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
			if (!myContain(visited_Queue1, d[i])
					&& !myContain(unvisited_Queue, d[i]))
				unvisited_Queue.addLast(d[i]);
		}
	}

	private boolean myContain(LinkedList<Coordinate> q, Coordinate c) {
		for (int i = 0; i < q.size(); i++) {
			Coordinate one = q.get(i);
			if (Math.abs(one.x - c.x) < 0.000001
					&& Math.abs(one.y - c.y) < 0.000001) {
				return true;
			}
		}

		return false;
	}

	public void ununiformlyquery(Coordinate startPoint,
			LinkedList<VQP> visited_Queue,
			LinkedList<Coordinate> visited_Queue1,
			LinkedList<Coordinate> unvisited_Queue, String state, int category,
			String query) {

		AQuery Firstquery = new AQuery(startPoint, state, category, query,
				MAX_TOTAL_RESULTS_RETURNED); // issue the first query

		ResultSetD2 resultSetStart = query(Firstquery);
		countquery++;
		visited_Queue1.addLast(startPoint);
		Set<APOI> queryset = new HashSet<APOI>(); // record all point queried
		Set<APOI> eligibleset = new HashSet<APOI>(); // record all eligible
														// point
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
		double radius = distance; // record the first crawl radius
		// add startPoint to visited_Queue1
		// VQP startPoint1=new VQP(startPoint,radius);
		// visited_Queue1.addLast(startPoint1);

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
					if (crawl_radius < radius * key) {
						LinkedList<VQP> temp_visited_Queue = new LinkedList<VQP>();
						// LinkedList<Coordinate> temp_visited_Queue1 = new
						// LinkedList<Coordinate>();
						LinkedList<Coordinate> temp_unvisited_Queue = new LinkedList<Coordinate>();
						crawl_radius = queryInHexgon(p, crawl_radius, radius,
								temp_visited_Queue, visited_Queue1,
								temp_unvisited_Queue, state, category, query,
								queryset);
					}
					Circle aaCircle = new Circle(p, crawl_radius);
					if (logger.isDebugEnabled() && PaintShapes.painting) {
						PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
						PaintShapes.paint.addCircle(aaCircle);
						PaintShapes.paint.myRepaint();
					}
					VQP visitedPoint = new VQP(p, crawl_radius);
					// denote the point has been visited
					visited_Queue.addLast(visitedPoint);
					visited_Queue1.addLast(p);
				}
			}
			/* calculate the cover radius */
			double minRadius = 1e308;
			int flag = 0;
			while (!visited_Queue.isEmpty()) {
				VQP vqp = visited_Queue.removeFirst();
				Coordinate point1 = vqp.getCoordinate();
				double radius1 = vqp.getRadius();
				Iterator<VQP> iterator = visited_Queue.iterator();
				while (iterator.hasNext()) {
					VQP vqp1 = iterator.next();
					Coordinate point2 = vqp1.getCoordinate();
					double radius2 = vqp1.getRadius();
					if (Math.abs(point1.distance(point2) - sqrt3 * key * radius) < 1e-6) {
						flag++;
						double temp_radius = calculateIncircle(startPoint,
								point1, radius1, point2, radius2);
						minRadius = Math.min(temp_radius, minRadius);
					}
				}
			}
			double coverRadius = minRadius;
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
			System.out.println("eligible point=" + countPoint + "   countquery="
					+ countquery);
			level++;
		}
	}

	/*
	 * @crawl_radius: the radius of the circle with the center of query point
	 * 
	 * @radius: the radius of the circle with the center of startPoint
	 */
	public double queryInHexgon(Coordinate point, double crawl_radius,
			double radius, LinkedList<VQP> visited_Queue,
			LinkedList<Coordinate> visited_Queue1,
			LinkedList<Coordinate> unvisited_Queue, String state, int category,
			String query, Set<APOI> set) {

		double coverRadius = crawl_radius;
		visited_Queue1.addLast(point);
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
					set.addAll(resultSetInhexgon.getPOIs());
					int size = resultSetInhexgon.getPOIs().size();
					APOI farthest = resultSetInhexgon.getPOIs().get(size - 1);
					Coordinate farthestCoordinate = farthest.getCoordinate();
					double distance = q.distance(farthestCoordinate);
					double inRadius = distance;
					if (inRadius < key * crawl_radius) {
						LinkedList<VQP> temp_visited_Queue1 = new LinkedList<VQP>();
						// LinkedList<Coordinate> temp_visited_Queue11 = new
						// LinkedList<Coordinate>();
						LinkedList<Coordinate> temp_unvisited_Queue1 = new LinkedList<Coordinate>();
						inRadius = queryInHexgon(q, inRadius, crawl_radius,
								temp_visited_Queue1, visited_Queue1,
								temp_unvisited_Queue1, state, category, query,
								set);
					}
					Circle aaaCircle = new Circle(q, inRadius);
					if (logger.isDebugEnabled() && PaintShapes.painting) {
						PaintShapes.paint.color = PaintShapes.paint.greenTranslucence;
						PaintShapes.paint.addCircle(aaaCircle);
						PaintShapes.paint.myRepaint();
					}
					VQP qVQP = new VQP(q, inRadius);
					visited_Queue.addLast(qVQP);
					visited_Queue1.addLast(q);
				}
			}

			/* calculate the incircle */
			double minRadius = 1e308;
			while (!visited_Queue.isEmpty()) {
				VQP avqp = visited_Queue.removeFirst();
				Coordinate point1 = avqp.getCoordinate();
				double radius1 = avqp.getRadius();
				Iterator<VQP> it = visited_Queue.iterator();
				while (it.hasNext()) {
					VQP avqp1 = it.next();
					Coordinate point2 = avqp1.getCoordinate();
					double radius2 = avqp1.getRadius();
					if (Math.abs(point1.distance(point2) - sqrt3 * key * crawl_radius) < 1e-6) {
						double tem_radius = calculateIncircle(point, point1,
								radius1, point2, radius2);
						minRadius = Math.min(minRadius, tem_radius);
					}

				}
			}
			coverRadius = minRadius;
			temp_Level++;
		}

		return coverRadius;
	}

	/* algorithm 1 */
	public double calculateIncircle(Coordinate startPoint, Coordinate point1,
			double radius1, Coordinate point2, double radius2) {
		double AB = startPoint.distance(point1);
		double AD = startPoint.distance(point2);
		double BD = point1.distance(point2);
		double cosBCD = (Math.pow(radius1, 2) + Math.pow(radius2, 2) - Math
				.pow(BD, 2)) / (2 * radius1 * radius2);
		double angleBCD = Math.acos(cosBCD);
		double cosBAD = (Math.pow(AB, 2) + Math.pow(AD, 2) - Math.pow(BD, 2))
				/ (2 * AB * AD);
		double angleBAD = Math.acos(cosBAD);
		/*
		 * using cosine rule to calculate AC
		 */
		double a1 = Math.pow(AB, 2) + Math.pow(radius1, 2) - Math.pow(AD, 2)
				- Math.pow(radius2, 2);
		double a2 = 2 * AB * radius1;
		double a3 = 2 * AD * radius2
				* Math.cos(2 * Math.PI - angleBCD - angleBAD);
		double a4 = 2 * AD * radius2
				* Math.sin(2 * Math.PI - angleBCD - angleBAD);
		double b1 = Math.pow(a4, 2) - Math.pow(a1, 2);
		double b2 = Math.pow((a2 - a3), 2) + Math.pow(a4, 2);
		double b3 = 2 * a1 * (a2 - a3);

		double X1 = (b3 + Math.sqrt(Math.pow(b3, 2) + 4 * b2 * b1)) / (2 * b2);
		double X2 = (b3 - Math.sqrt(Math.pow(b3, 2) + 4 * b2 * b1)) / (2 * b2);
		double AC1 = Math.sqrt(Math.pow(AB, 2) + Math.pow(radius1, 2) - 2 * AB
				* radius1 * X1);
		double AC2 = Math.sqrt(Math.pow(AB, 2) + Math.pow(radius1, 2) - 2 * AB
				* radius1 * X2);
		double AC = Math.max(AC1, AC2);
		return AC;
	}
}

