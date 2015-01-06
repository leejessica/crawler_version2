/**
 * 
 */
package mo.umac.crawler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import paint.PaintShapes;

import mo.umac.db.DBInMemory;
import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.ResultSetD2;
import mo.umac.metadata.plugins.IntersectPoint;
import mo.umac.metadata.plugins.VQP;
import mo.umac.spatial.Circle;

import com.infomatiq.jsi.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @author Jessica
 * 
 */
public class Hexagon_optimize extends Strategy {

	/**
	 * 
	 */
	// public static int recursion = 1;
	public static int NEED_POINTS_NUMBER = 200;
	public static int countPoint = 0;
	public static double sqrt3 = Math.sqrt(3);
	public static double key = 0.97;
	public static int countquery = 0;
	
	
	private static Coordinate startPoint=new Coordinate();
	
	public Hexagon_optimize() {
		//startPoint.x=-73.355835;
		//startPoint.y= 42.746632;
		startPoint.x=-73;
		startPoint.y= 42;
		logger.info("------------HexagonCrawler2_Modify------------");
	}
	
	public Hexagon_optimize(Coordinate startPoint) {
		//this.startPoint=startPoint;
		this.startPoint.x=startPoint.x;
		this.startPoint.y=startPoint.y;
		System.out.println("startPoint="+startPoint.toString());
		logger.info("------------HexagonCrawler2_Modify------------");
	}
	

	@Override
	public void crawl(String state, int category, String query,
			Envelope envelopeState) {

		if (logger.isDebugEnabled()) {
			logger.info("------------crawling-----------");
			logger.info(envelopeState.toString());
		}
		// finished crawling
		if (envelopeState == null) {
			return;
		}
		// Coordinate c = evenlopeState.centre();

		// record points for next round query
		LinkedList<VQP> visited_Queue = new LinkedList<VQP>();
		// record all visited query points
		// LinkedList<Coordinate> visited_Queue1 = new LinkedList<Coordinate>();
		LinkedList<Coordinate> unvisited_Queue = new LinkedList<Coordinate>();
		// @param visitedcircle_Queue: record the information(coordinate,
		// radius)of the visited points
		LinkedList<VQP> visitedcircle_Queue = new LinkedList<VQP>();
		ununiformlyquery(startPoint, envelopeState,visited_Queue, visitedcircle_Queue,
				unvisited_Queue, state, category, query);
		logger.info("eligiblepoint="+countPoint);
		//System.out.println("         countquery"+countquery);
	}

	/* calculate the centeral points of the hexagons */
	public void calculatePoint(Coordinate startPoint, double radius,
			LinkedList<VQP> visitedcircle_Queue,
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
			if (!myContain2(visitedcircle_Queue, d[i])
					&& !myContain1(unvisited_Queue, d[i]))
				unvisited_Queue.addLast(d[i]);
		}
	}

	private boolean myContain1(LinkedList<Coordinate> q, Coordinate c) {
		boolean flag=false;
		for (int i = 0; i < q.size()&&!flag; i++) {
			Coordinate one = q.get(i);
			if (c.equals2D(one)) {
				 flag=true;
			}
		}
		return flag;
	}

	private boolean myContain2(LinkedList<VQP> q, Coordinate c) {
		boolean flag=false;
		for (int i = 0; i < q.size()&&!flag; i++) {
			Coordinate one = q.get(i).getCoordinate();
			if (c.equals2D(one)) {
				 flag=true;
			}
		}
		return flag;
	}

	/*
	 * a round query means queries to cover a series of queries in order to
	 * cover the hexagons at the same level
	 * 
	 * @param visited_Queue: record the points used to query in next round query
	 * to serve calculate the radius of muximum inscribed circle
	 * 
	 * @param visitedcircle_Queue: record the points information(Coordinate,
	 * radius) visited so far
	 * 
	 * @param unvisited_Queue: record the points unvisited
	 */
	public void ununiformlyquery(Coordinate startPoint,Envelope envelopeState,
			LinkedList<VQP> visited_Queue, LinkedList<VQP> visitedcircle_Queue,
			LinkedList<Coordinate> unvisited_Queue, String state, int category,
			String query) {

		AQuery Firstquery = new AQuery(startPoint, state, category, query,
				MAX_TOTAL_RESULTS_RETURNED); // issue the first query

		ResultSetD2 resultSetStart = query(Firstquery);
		countquery++;
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
		// update the visitedcircle_Queue
		visitedcircle_Queue.addLast(new VQP(startPoint, distance));
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
		calculatePoint(startPoint, radius, visitedcircle_Queue, unvisited_Queue);

		int level = 1;
		while (countPoint < NEED_POINTS_NUMBER) {
			for (int i = 1; i <= level * 6; i++) {

				if (!unvisited_Queue.isEmpty()) {

					Coordinate p = unvisited_Queue.removeFirst();
					/*
					 * compute coordinates of the points which are used to next
					 * round query
					 */
					calculatePoint(p, radius, visitedcircle_Queue,
							unvisited_Queue);
					VQP c = new VQP(p, radius);
					if (needQuery(c, visitedcircle_Queue,envelopeState)) {
						AQuery Hexquery = new AQuery(p, state, category, query,
								MAX_TOTAL_RESULTS_RETURNED);
						ResultSetD2 resultSet = query(Hexquery);
						countquery++;
						// add the queried point to the set
						queryset.addAll(resultSet.getPOIs());
						int size1 = resultSet.getPOIs().size();
						APOI farthest1 = resultSet.getPOIs().get(size1 - 1);
						Coordinate farthest1Coordinate = farthest1
								.getCoordinate();
						double distance1 = p.distance(farthest1Coordinate);
						double crawl_radius = distance1;
						/*
						 * record the information of the visited point，using to
						 * record all the visited points since we want to using
						 * the information to obtain the neighbor circles and
						 * futher reduce the query cost
						 */
						visitedcircle_Queue.addLast(new VQP(p, crawl_radius));
						// query in the hexagon
						if (crawl_radius < radius * key) {
							LinkedList<VQP> temp_visited_Queue = new LinkedList<VQP>();
							LinkedList<Coordinate> temp_unvisited_Queue = new LinkedList<Coordinate>();
							crawl_radius = queryInHexagon(p, crawl_radius,envelopeState,
									radius, temp_visited_Queue,
									visitedcircle_Queue, temp_unvisited_Queue,
									state, category, query, queryset);
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
					} else {
						visitedcircle_Queue.addLast(c);
						visited_Queue.addLast(c);
					}
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
								vqp,vqp1);
						minRadius = Math.min(temp_radius, minRadius);	
					}
				}
			}
			double coverRadius = minRadius;
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
				if (startPoint.distance(pp.getCoordinate()) < coverRadius
						|| Math.abs(startPoint.distance(pp.getCoordinate())
								- coverRadius) < 1e-6)
					eligibleset.add(pp);
			}
			countPoint = eligibleset.size();
			level++;
		}
	}

	/*
	 * @crawl_radius: the radius of the circle with the center of query point
	 * 
	 * @radius: the radius of the circle with the center of startPoint
	 */
	public double queryInHexagon(Coordinate point, double crawl_radius,Envelope envelopeState,
			double radius, LinkedList<VQP> visited_Queue,
			LinkedList<VQP> visitedcircle_Queue,
			LinkedList<Coordinate> unvisited_Queue, String state, int category,
			String query, Set<APOI> set) {
		// @param coverRadius:record the maximum inscribed circle of the covered
		// region
		double coverRadius = crawl_radius;
		/* compute coordinates of the points which are used to next query */
		calculatePoint(point, crawl_radius, visitedcircle_Queue,
				unvisited_Queue);
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
					calculatePoint(q, crawl_radius, visitedcircle_Queue,
							unvisited_Queue);
					/*
					 * determine whether need issue query in position q
					 */
					VQP c = new VQP(q, crawl_radius);
					// the query is needed
					if (needQuery(c, visitedcircle_Queue,envelopeState)){
						AQuery InhexgonQuery = new AQuery(q, state, category,
								query, MAX_TOTAL_RESULTS_RETURNED);
						ResultSetD2 resultSetInhexgon = query(InhexgonQuery);
						countquery++;
						set.addAll(resultSetInhexgon.getPOIs());
						int size = resultSetInhexgon.getPOIs().size();
						APOI farthest = resultSetInhexgon.getPOIs().get(
								size - 1);
						Coordinate farthestCoordinate = farthest
								.getCoordinate();
						double distance = q.distance(farthestCoordinate);
						double inRadius = distance;
						visitedcircle_Queue.addLast(new VQP(q, inRadius));
						// recursively call the InHexagon algorithm
						if (inRadius < key * crawl_radius) {
							LinkedList<VQP> temp_visited_Queue1 = new LinkedList<VQP>();
							// LinkedList<Coordinate> temp_visited_Queue11 = new
							// LinkedList<Coordinate>();
							LinkedList<Coordinate> temp_unvisited_Queue1 = new LinkedList<Coordinate>();
							inRadius = queryInHexagon(q, inRadius,envelopeState,
									crawl_radius, temp_visited_Queue1,
									visitedcircle_Queue, temp_unvisited_Queue1,
									state, category, query, set);
						}
						Circle aaaCircle = new Circle(q, inRadius);
						if (logger.isDebugEnabled() && PaintShapes.painting) {
							PaintShapes.paint.color = PaintShapes.paint.greenTranslucence;
							PaintShapes.paint.addCircle(aaaCircle);
							PaintShapes.paint.myRepaint();
						}
						VQP qVQP = new VQP(q, inRadius);
						visited_Queue.addLast(qVQP);
					} else {
						visitedcircle_Queue.addLast(c);
						visited_Queue.addLast(c);
					}
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
					if (Math.abs(point1.distance(point2) - sqrt3 * key
							* crawl_radius) < 1e-6) {
						double tem_radius = calculateIncircle(point, avqp,avqp1);
						minRadius = Math.min(minRadius, tem_radius);
					}

				}
			}
			coverRadius = minRadius;
			temp_Level++;
		}
		return coverRadius;
	}

	public Set<VQP> obtainNeighborSet(VQP circle,
			LinkedList<VQP> visitedcircle_Queue) {
		Set Neighbor_set = new HashSet<VQP>();// record the effective neighbor
		Set tempNeighbor_set = new HashSet<VQP>();
		Iterator<VQP> it = visitedcircle_Queue.iterator();
		// initial the tempNeighbor_set
		while (it.hasNext()) {
			VQP circle1 = it.next();
			double d1 = circle.getRadius() - circle1.getRadius();
			/*
			 * 【 Definition 】temporary neighbor: the circle intersect with the
			 * given circle or contained by the given circle
			 */
			if (circles_Insecter(circle, circle1)
					|| (d1 > 0 && circle_contain(circle, circle1))) {
				tempNeighbor_set.add(circle1);
			}
		}
		/*
		 * optimal the Neighbor_set and only retain the effective neighbors 【
		 * Definition 】effective neighbors
		 */
		Iterator<VQP> it1 = tempNeighbor_set.iterator();
		while (it1.hasNext()) {
			VQP c1 = it1.next();
			boolean effective = true;
			Iterator<VQP> it2 = tempNeighbor_set.iterator();
			while (it2.hasNext() && effective) {
				VQP c2 = it2.next();
				if (!pointsequal(c1.getCoordinate(), c2.getCoordinate())) {
					if (circle_contain(c2, c1))
						effective = false;
				}
			}
			if (effective)
				Neighbor_set.add(c1);
		}
		return Neighbor_set;
	}

	/*
	 * algorithm 1 To calculate the maximum inscribed circle of a given area
	 */
	public double calculateIncircle(Coordinate startPoint, VQP circle1, VQP circle2) {
		IntersectPoint inter=calculateIntersectPoint(circle1, circle2);
		double d1=inter.getIntersectPoint_left().distance(startPoint);
		double d2=inter.getIntersectPoint_right().distance(startPoint);
		return Math.max(d1, d2);
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
		if (!(Xc!=Xd)&&!(Yc!=Yd)) {
			Coordinate intersectP1 = new Coordinate(Xc, Yc);
			intersect = new IntersectPoint(p1, r1, p2, r2, intersectP1, null);
		} else {
			Coordinate intersectP1 = new Coordinate(Xc, Yc);
			Coordinate intersectP2 = new Coordinate(Xd, Yd);
			intersect = new IntersectPoint(p1, r1, p2, r2, intersectP1,
					intersectP2);
		}
		return intersect;
	}

	/*
	 * Consider a circle Ci(coordinate qi, double ri) centered at qi and its
	 * radius is ri, to determine every point in Ci is covered by at least one
	 * query circle Consider a circle Cj(qj, rj), if 0<dist(qi, qj)<ri+rj, we
	 * say Cj is a neighbor of Ci, The set of Ci's neighbor is denoted as
	 * Ni={Cj|0<dist(qi, qj)<ri+rj}
	 * 
	 * @param cirlce : a circle which we want to determine whether it is covered
	 * by its neighbor
	 * 
	 * @param visitedcircle_Queue: record the information of all the visited
	 * points through out the whole period of running this algorithm
	 */
	public boolean needQuery(VQP circle, LinkedList<VQP> visitedcircle_Queue, Envelope envelopeState) {
		boolean needquery = false;
		if(!outspace(circle, envelopeState)){
			// record the effective neighbors of the circle need to be judged
			Set Neighbor_set = new HashSet<VQP>();
			Iterator<VQP> it = visitedcircle_Queue.iterator();
			// "beIncluded=true" means circle was included by another visited circle
			boolean beIncluded = false;
			// record all the neighbors of a query circle
			Set tempNeighbor_set = new HashSet<VQP>();
			while (it.hasNext() && !beIncluded) {
				VQP circle1 = it.next();
				// to judge which circle is the larger one
				double d1 = circle.getRadius() - circle1.getRadius();
				if (circles_Insecter(circle, circle1)
						|| (d1 > 0 && circle_contain(circle, circle1))) {
					tempNeighbor_set.add(circle1);
				}
				// if the circle under judgment is completely covered by a visited
				// circle, then there is no need to query
				else if (d1 < 0 && circle_contain(circle1, circle)) {
					beIncluded = true;// no need to query
				}
			}
			if (!beIncluded) {
				// optimal the Neighbor_set and only retain the effective neighbors
				Iterator<VQP> it1 = tempNeighbor_set.iterator();
				while(it1.hasNext()){
					VQP c1=it1.next();
					boolean effective=true;
					Iterator<VQP>it2=tempNeighbor_set.iterator();
					while(it.hasNext()&&effective){
						VQP c2=it2.next();
						if(!pointsequal(c1.getCoordinate(),c2.getCoordinate())){
							if(circle_contain(c2, c1)){
								effective=false;
							}
							else if(circles_Insecter(circle, c1)&&circles_Insecter(c1, c2)){
								if(arc_contain(c1, c2, circle)){
									IntersectPoint tp=calculateIntersectPoint(c1, c2);
									if(isinCircle(tp.getIntersectPoint_left(),circle))
										effective=false;
									else if(isinCircle(tp.getIntersectPoint_right(), circle))
										effective=false;
								}
							}
						}
					}
					if(effective)
						Neighbor_set.add(c1);
				}
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
		}
		return needquery;
	}

	/*
	 * To determine whether the circumference of a circle is covered by other
	 * circles or not
	 * 
	 * @param circle: a circle which we want to determine whether its
	 * circumference is covered or not
	 * 
	 * @param Neighbor_set: a set of circles which are the neighbor circle to
	 * the given circle
	 */
	public boolean isCircumferenceCoverage(VQP circle, Set<VQP> Neighbor_set) {
		boolean coverage = true;
		Iterator<VQP> it1 = Neighbor_set.iterator();
		// if there is a intersecting point is not in any other circles, the
		// circle is not perimeter covered
		while (it1.hasNext() && coverage) {
			VQP circle1 = it1.next();
			if (circles_Insecter(circle, circle1)) {
				IntersectPoint inter1 = calculateIntersectPoint(circle, circle1);
				if(inter1.getIntersectPoint_right()==null)
					System.out.println("right intersect in null!");
				// "stop=true" means a intersecting point is inside a circle
				boolean stop1 = false;
				boolean stop2 = false;
				Iterator<VQP> it2 = Neighbor_set.iterator();
				// if the two intersecting points are in any other neighbor
				// circles,
				// then stop
				while (it2.hasNext()) {
					VQP circle2 = it2.next();
					// System.out.println("circle2="+circle2.getCoordinate());
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
					// System.out.println("stop1="+stop1+"  stop2="+stop2);
				}
				// There is at least a point not inside any circle
				if (!stop1 || !stop2)
					coverage = false;
			}
		}
		return coverage;
	}

	/*
	 * to determine whether an arc is covered by other neighbor circles of the
	 * circle or not
	 * 
	 * @param p1, p2: the endpoints of the arc
	 * 
	 * @param circle: a circle we want to determine whether is covered by its
	 * neighbor circles
	 * 
	 * @param Neighbor_set: a set of circles which are neighbors to the circle
	 */
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
					&& circles_Insecter(circle1, circle2))
				intercircleQ.addLast(circle2);
		}
		Iterator<VQP> it2 = intercircleQ.iterator();
		while (it2.hasNext() && coverage) {
			VQP circletemp1 = it2.next();
			IntersectPoint inter1 = calculateIntersectPoint(circle1,
					circletemp1);
			boolean stopleft = false;
			boolean stopright = false;
			if (isinCircle(inter1.getIntersectPoint_left(), circle)
					|| isAtCircumference(inter1.getIntersectPoint_left(),
							circle)) {
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
						}
					}
				}
			} else {
				stopleft = true;
			}
			if (isinCircle(inter1.getIntersectPoint_right(), circle)
					|| isAtCircumference(inter1.getIntersectPoint_right(),
							circle)) {
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

	// to determine whether a circle contains another circle and assume that
	// circle1.radius>circle2.radius(i.e., judge if circle1 contains circle2)
	public boolean circle_contain(VQP circle1, VQP circle2) {
		double d1 = circle1.getCoordinate().distance(circle2.getCoordinate());
		double d2 = circle1.getRadius() - circle2.getRadius();
		if (d1 <=d2 )
			return true;
		return false;
	}

	/*
	 * Consider two circle denoted by c1 and c2. Both the
	 * two circles intersect with circle c, if the arc on the perimeter of c
	 * covered by c1 contains the arc covered by c2, we say c1 is an effective
	 * neighbor, on the contrary, if the arc covered by c2 contains the arc
	 * covered by c1, then c2 is an effective neighbor
	 * 
	 * we assume the arc covered by c1 is completely included in arc covered by
	 * c2
	 */
	public boolean arc_contain(VQP c1, VQP c2, VQP c) {
		IntersectPoint inter1 = calculateIntersectPoint(c1, c);
		
		// IntersectPoint inter2 = calculateIntersectPoint(c2, c);
		if (isinCircle(inter1.getIntersectPoint_left(), c2)
				&& isinCircle(inter1.getIntersectPoint_right(), c2)) {
			//System.out.println("call the arc_contain");
			return true;
		} else
			return false;
	}

	// To determine whether point p1 equals to point p2
	public boolean pointsequal(Coordinate p1, Coordinate p2) {
		if(p1.equals2D(p2))
			return true;
		else return false;
	}

	// determine whether a point is in a circle or not
	public boolean isinCircle(Coordinate p, VQP vqp) {
		boolean flag = false;
		if (vqp.getCoordinate().distance(p) <vqp.getRadius())
			flag = true;
		//if(vqp.getRadius()-vqp.getCoordinate().distance(p)>0.0001)
			//flag=true;
		return flag;
	}

	// determine whether a point is at the circumference of a circle
	public boolean isAtCircumference(Coordinate p, VQP circle) {
		boolean atCircumference = false;
		if (Math.abs(circle.getCoordinate().distance(p) - circle.getRadius()) < 1e-38) {
			atCircumference = true;
		}
		return atCircumference;
	}

	/*
	 * Suppose： line segment a ：P1(x1, y1)、P2(x2, y2)　　　　　　line segment b:
	 * Q1(x3, y3)、Q2(x4, y4) 　　　　d1 ====> (P2 - P1) x (Q1 - P1) (cross product)
	 * 　　　　d2 ====> (P2 - P1) x (Q2 - P1) (cross product) 　　　　d3 ====> (Q2 - Q1)
	 * x (P1 - Q1) (cross product) 　　　　d4 ====> (Q2 - Q1) x (P2 - P1) (cross
	 * product) if d1*d2<0 and d3*d4<0, then a intersects with b
	 */
	public boolean line_is_intersect(Coordinate P1, Coordinate P2,
			Coordinate Q1, Coordinate Q2) {
		boolean intersect = false;
		double d1 = (P2.x - P1.x) * (Q1.y - P1.y) - (Q1.x - P1.x)
				* (P2.y - P1.y);
		double d2 = (P2.x - P1.x) * (Q2.y - P1.y) - (Q2.x - P1.x)
				* (P2.y - P1.y);
		double d3 = (Q2.x - Q1.x) * (P1.y - Q1.y) - (P1.x - Q1.x)
				* (Q2.y - Q1.y);
		double d4 = (Q2.x - Q1.x) * (P2.y - Q1.y) - (P2.x - Q1.x)
				* (Q2.y - Q1.y);
		if (d1 * d2 < 0 && d3 * d4 < 0)
			intersect = true;
		return intersect;
	}
	
	public boolean outspace(VQP c,Envelope envelopeState){
		double minX=c.getCoordinate().x-c.getRadius();
		double maxX=c.getCoordinate().x+c.getRadius();
		double minY=c.getCoordinate().y-c.getRadius();
		double maxY=c.getCoordinate().y+c.getRadius();
		Envelope e1=new Envelope(minX,maxX,minY,maxY);
		if(e1.intersects(envelopeState))
			return false;
		return true;
	}
}
