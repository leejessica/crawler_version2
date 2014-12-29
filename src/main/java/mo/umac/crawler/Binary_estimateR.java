package mo.umac.crawler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import mo.umac.db.DBInMemory;
import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.plugins.IntersectPoint;
import mo.umac.metadata.plugins.PandC;
import mo.umac.metadata.ResultSetD2;
import mo.umac.metadata.plugins.VQP;
import mo.umac.metadata.plugins.VQP1;
import paint.PaintShapes;
import mo.umac.spatial.Circle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineSegment;

public class Binary_estimateR<PeripherQuery_Optimize> extends Strategy {

	private static int countquery = 0;
	private static int NEED_POINTS_NUM = 150;
	private static int countpoint = 0;
	private static int level = 0;
	private static Set<APOI> queryset = new HashSet<APOI>();// record all points
															// queried
	private static Set<APOI> eligibleset = new HashSet<APOI>();// record all
																// eligible
																// points
	public static Coordinate startPoint1 = new Coordinate();// record the start
															// point of every
															// level query
	private static double inRadius = 0;// using it to keep track of the radius
										// of
										// the mixmum inscribe circle

	// for test
	public static int i = 0;

	public Binary_estimateR() {
		super();
		logger.info("--------------Binary_search--------------");
	}

	@Override
	public void crawl(String state, int category, String query,
			Envelope evenlopeState) {

		if (logger.isDebugEnabled()) {
			logger.info("-----------crawling---------------");
			logger.info(evenlopeState.toString());
		}
		// finished crawling
		if (evenlopeState == null) {
			return;
		}
		Coordinate startPoint = new Coordinate();
		startPoint.x = (evenlopeState.getMinX() + evenlopeState.getMaxX()) / 2;
		startPoint.y = (evenlopeState.getMinY() + evenlopeState.getMaxY()) / 2;
		startQuery(startPoint, state, category, query, evenlopeState);
	}

	public void startQuery(Coordinate startPoint, String state, int category,
			String query, Envelope evenlopeState) {
		// issue the first query
		AQuery Firstquery = new AQuery(startPoint, state, category, query,
				MAX_TOTAL_RESULTS_RETURNED);
		ResultSetD2 resultSetStart = query(Firstquery);
		countquery++;

		queryset.addAll(resultSetStart.getPOIs());
		eligibleset.addAll(queryset);
		countpoint = eligibleset.size();
		int size = resultSetStart.getPOIs().size();
		double radius = startPoint.distance(resultSetStart.getPOIs()
				.get(size - 1).getCoordinate());
		inRadius = radius;
		System.out.println("startquery inRadius====" + inRadius + "======");
		// record all the query circle
		LinkedList<VQP> visitedInfoQ = new LinkedList<VQP>();
		LinkedList<Coordinate> visitedQ = new LinkedList<Coordinate>();
		/*
		 * record all the query circle on the
		 * linesegment(startPoint,refCoordinate) and arrange them in an
		 * ascending sort order of the distance from the startPoint
		 */
		TreeSet<VQP> visitedOnlineQ = new TreeSet<VQP>(new SortedBydistance(
				startPoint));
		/* add circle(startPoint,radius) to the visitedInfoQ */
		visitedInfoQ.addLast(new VQP(startPoint, radius));
		visitedQ.addLast(startPoint);
		Circle aCircle = new Circle(startPoint, radius);
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(aCircle);
			PaintShapes.paint.myRepaint();
		}

		// find the refCoordinate
		double estimateRadius = Math.sqrt(NEED_POINTS_NUM * inRadius * inRadius
				/ countpoint);
		// double estimateRadius=2*inRadius;
		Coordinate refCoordinate = new Coordinate(
				startPoint.x + estimateRadius, startPoint.y);
		visitedOnlineQ.add(new VQP(refCoordinate, 0));
		while (countpoint < NEED_POINTS_NUM) {
			System.out.println("estimateRadius=" + estimateRadius);
			if (inRadius < estimateRadius) {
				binaryQuery(startPoint, refCoordinate, state, category, query,
						visitedInfoQ, visitedOnlineQ, visitedQ);
			} else {
				estimateRadius = Math.sqrt(NEED_POINTS_NUM * inRadius
						* inRadius / countpoint);

				refCoordinate = new Coordinate(startPoint.x + estimateRadius,
						startPoint.y);
				visitedOnlineQ.add(new VQP(refCoordinate, 0));
			}
		}

	}

	/**
	 * binary search procedure: find the nearest ring contain new points and
	 * cover the ring
	 * 
	 * @param startPoint
	 *            : the given start point
	 * @param refCoordinate
	 *            : the position which has the longest distance to the
	 *            startPoint
	 * */
	public void binaryQuery(Coordinate startPoint, Coordinate refCoordinate,
			String state, int category, String query,
			LinkedList<VQP> visitedInfoQ, TreeSet<VQP> visitedOnlineQ,
			LinkedList<Coordinate> visitedQ) {

		// the maximum inscribed circle centered at startPoint
		VQP inscribedCircle = new VQP(startPoint, inRadius);
		/*
		 * obtain the intersection point between the linesegment(startPoint,
		 * refCoordinate) and the circle(startPoint, inRadius)
		 */
		Coordinate intsectPoint = getIntersectPoint(startPoint, refCoordinate,
				inRadius);
		// initial the startPoint1 in a fresh binary search procedure
		startPoint1 = intsectPoint;
		/*
		 * calculate the intersection point between c1 and
		 * linesegment(c1.getCoordinate(), startPoint)
		 */
		VQP firstCircle = visitedOnlineQ.first();
		Coordinate intsectPoint1 = getIntersectPoint(
				firstCircle.getCoordinate(), startPoint,
				firstCircle.getRadius());
		// keep track of the position for binary search
		Coordinate biCoordinate = new Coordinate();
		biCoordinate.x = (intsectPoint1.x + intsectPoint.x) / 2;
		biCoordinate.y = (intsectPoint1.y + intsectPoint.y) / 2;

		// exist vacancy between the biCoordinate position and the inscribed
		// circle
		while (!isinCircle1(biCoordinate, inscribedCircle)) {

			// issue a query at the biCoordinate
			AQuery query1 = new AQuery(biCoordinate, state, category, query,
					MAX_TOTAL_RESULTS_RETURNED);
			ResultSetD2 resultSet1 = query(query1);
			countquery++;
			queryset.addAll(resultSet1.getPOIs());
			int size1 = resultSet1.getPOIs().size();
			double biRadius = biCoordinate.distance(resultSet1.getPOIs()
					.get(size1 - 1).getCoordinate());
			Coordinate biCoordinateclone = (Coordinate) biCoordinate.clone();
			// record the circle(biCoordinate, biRadius)
			visitedInfoQ.addLast(new VQP(biCoordinateclone, biRadius));
			visitedOnlineQ.add(new VQP(biCoordinateclone, biRadius));
			// System.out.println("biCoordinate="+biCoordinate+"visitedOnlineQ="+visitedOnlineQ.size());
			Circle aCircle = new Circle(biCoordinate, biRadius);
			if (logger.isDebugEnabled() && PaintShapes.painting) {
				PaintShapes.paint.color = PaintShapes.paint.blueTranslucence;
				PaintShapes.paint.addCircle(aCircle);
				PaintShapes.paint.myRepaint();
			}
			intsectPoint1 = getIntersectPoint(biCoordinate, startPoint,
					biRadius);
			// calculate new biCoordinate
			biCoordinate.x = (intsectPoint1.x + intsectPoint.x) / 2;
			biCoordinate.y = (intsectPoint1.y + intsectPoint.y) / 2;

		}// END find the right position for ringCover
		/* cover the ring */
		double ringRadius = startPoint.distance(refCoordinate);// initial the
																// ringRadius
		// obtain the first element and remove it
		if (!visitedOnlineQ.isEmpty()) {
			firstCircle = visitedOnlineQ.pollFirst();
			ringRadius = startPoint.distance(firstCircle.getCoordinate())
					+ firstCircle.getRadius();
		}
		LinkedList<Coordinate[]> uncoveredArc = new LinkedList<Coordinate[]>();
		HashMap<Integer, LinkedList<VQP1>> map = new HashMap<Integer, LinkedList<VQP1>>();
		/*
		 * the condition of termination of coverRing procedure is:
		 * inRadius>=ringRadius or countpoint>=NEED_POINTS_NUM
		 */
		while (inRadius < ringRadius && countpoint < NEED_POINTS_NUM) {

			onelevelQuery(startPoint, inRadius, state, category, query,
					visitedQ, map, uncoveredArc, visitedInfoQ);

		}
	}

	public IntersectPoint calculateIntersectPoint(Coordinate p1, double r1,
			Coordinate p2, double r2) {

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
			intersect = new IntersectPoint(p1, r1, p2, r2, intersectP1, null);
		} else {
			Coordinate intersectP1 = new Coordinate(Xc, Yc);
			Coordinate intersectP2 = new Coordinate(Xd, Yd);
			intersect = new IntersectPoint(p1, r1, p2, r2, intersectP1,
					intersectP2);
		}
		return intersect;
	}

	/* one time query */
	// calculate the intersect point between the query with central circle
	public VQP1 onequery(Coordinate startPoint, double radius,
			Coordinate point, String state, int category, String query,
			LinkedList<Coordinate> visitedQ, LinkedList<VQP> visitedInfoQ) {

		AQuery query1 = new AQuery(point, state, category, query,
				MAX_TOTAL_RESULTS_RETURNED);
		ResultSetD2 resultset1 = query(query1);
		countquery++;
		queryset.addAll(resultset1.getPOIs());
		int size = resultset1.getPOIs().size();
		double radius1 = point.distance(resultset1.getPOIs().get(size - 1)
				.getCoordinate());
		visitedQ.addLast(point);
		visitedInfoQ.addLast(new VQP(point, radius1));
		Coordinate leftIntersectpoint = calculateIntersectPoint(startPoint,
				radius, point, radius1).getIntersectPoint_left();
		Coordinate rightIntersectpoint = calculateIntersectPoint(startPoint,
				radius, point, radius1).getIntersectPoint_right();
		VQP1 vpq1 = new VQP1();
		vpq1.setself(new VQP(point, radius1));
		PandC pc1 = new PandC();
		pc1.setintersection(leftIntersectpoint);
		vpq1.setleft(pc1);
		PandC pc2 = new PandC();
		pc2.setintersection(rightIntersectpoint);
		vpq1.setright(pc2);
		Circle aCircle = new Circle(point, radius1);
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(aCircle);
			PaintShapes.paint.myRepaint();
		}
		return vpq1;
	}

	//
	public void onelevelQuery(Coordinate startPoint, double radius,
			String state, int category, String query,
			LinkedList<Coordinate> visitedQ,
			HashMap<Integer, LinkedList<VQP1>> map,
			LinkedList<Coordinate[]> uncoveredArc, LinkedList<VQP> visitedInfoQ) {

		level++;
		// @param querypoint use to record all the query circle at the current
		// level
		LinkedList<VQP1> querypoint = new LinkedList<VQP1>();
		VQP1 vpq1 = onequery(startPoint, radius, startPoint1, state, category,
				query, visitedQ, visitedInfoQ);
		// calculate the second query point at the current level
		Coordinate a[] = new Coordinate[2];
		a = line_circle_intersect(startPoint, radius, startPoint1);
		Coordinate startPoint2 = new Coordinate();
		if (!myContain(visitedQ, a[0]))
			startPoint2 = a[0];
		else
			startPoint2 = a[1];
		// start the second query at current level
		VQP1 vpq2 = onequery(startPoint, radius, startPoint2, state, category,
				query, visitedQ, visitedInfoQ);
		// update the neighbor
		PandC pc1 = vpq1.getleft();
		pc1.setneighborcenter(startPoint2);
		pc1.setRadius(vpq2.getself().getRadius());
		vpq1.setleft(pc1);
		PandC pc2 = vpq1.getright();
		pc2.setneighborcenter(startPoint2);
		pc2.setRadius(vpq2.getself().getRadius());
		vpq1.setright(pc2);
		PandC pc3 = vpq2.getleft();
		pc3.setneighborcenter(startPoint1);
		pc3.setRadius(vpq1.getself().getRadius());
		vpq2.setleft(pc3);
		PandC pc4 = vpq2.getright();
		pc4.setneighborcenter(startPoint1);
		pc4.setRadius(vpq1.getself().getRadius());
		vpq2.setright(pc4);
		querypoint.addLast(vpq1);
		querypoint.addLast(vpq2);

		if (!isinCircle(vpq1.getleft().getintersection(), vpq2.getself())) {
			// use the array to record the arc which is not been covered
			Coordinate b[] = new Coordinate[2];
			Coordinate c[] = new Coordinate[2];
			b[0] = vpq1.getleft().getintersection();
			c[0] = vpq1.getright().getintersection();
			double d3 = b[0].distance(vpq2.getleft().getintersection());
			double d4 = b[0].distance(vpq2.getright().getintersection());
			if (d3 < d4) {
				b[1] = vpq2.getleft().getintersection();
				c[1] = vpq2.getright().getintersection();
			} else {
				b[1] = vpq2.getright().getintersection();
				c[1] = vpq2.getleft().getintersection();
			}
			uncoveredArc.addLast(b);
			uncoveredArc.addLast(c);
			// if the periphery has not been covered, continue query
			while (!uncoveredArc.isEmpty()) {
				Coordinate temp[] = new Coordinate[2];
				temp = uncoveredArc.removeFirst();
				continueQuery(temp, querypoint, uncoveredArc, startPoint,
						radius, state, category, query, visitedQ, visitedInfoQ);
			}
		}
		// add the querypoint to the map
		map.put(level, querypoint);

		// calculate the incircle and count the eligible points
		LinkedList<Coordinate> startPoint1Q = new LinkedList<Coordinate>();
		double incircleRadius = getIncircleRadius(map, startPoint, startPoint1Q);
		inRadius = incircleRadius;
		// get the new startPoint1
		Iterator<Coordinate> it1 = startPoint1Q.iterator();
		while (it1.hasNext()) {
			Coordinate tp1 = it1.next();
			if (Math.abs(inRadius - tp1.distance(startPoint)) < 1e-6) {
				startPoint1 = tp1;
			}
		}
		Circle aCircle = new Circle(startPoint, incircleRadius);
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.blueTranslucence;
			PaintShapes.paint.addCircle(aCircle);
			PaintShapes.paint.myRepaint();
		}
		Iterator<APOI> it = queryset.iterator();
		while (it.hasNext()) {
			int id = it.next().getId();
			APOI pp = DBInMemory.pois.get(id);
			if (startPoint.distance(pp.getCoordinate()) < inRadius
					|| startPoint.distance(pp.getCoordinate()) - inRadius < 1e-6)
				eligibleset.add(pp);
		}
		countpoint = eligibleset.size();

	}

	// calculate the radius of the incircle
	public double getIncircleRadius(HashMap<Integer, LinkedList<VQP1>> map,
			Coordinate startPoint, LinkedList<Coordinate> startPoint1Q) {
		double minRadius = 1e308;
		LinkedList<VQP1> tempquerypoint = map.get(level);
		Iterator<VQP1> it = tempquerypoint.iterator();
		while (it.hasNext()) {
			VQP1 vqp1 = it.next();
			double a = calculateMaxradius(startPoint, vqp1.getself()
					.getCoordinate(), vqp1.getself().getRadius(), vqp1
					.getleft().getneighborcenter(), vqp1.getleft().getRadius(),
					startPoint1Q);

			double b = calculateMaxradius(startPoint, vqp1.getself()
					.getCoordinate(), vqp1.getself().getRadius(), vqp1
					.getright().getneighborcenter(), vqp1.getright()
					.getRadius(), startPoint1Q);
			minRadius = Math.min(a, minRadius);
			minRadius = Math.min(b, minRadius);
		}
		double incircleRadius = minRadius;
		return incircleRadius;
	}

	// get arbitrary intersecting circle's max radius according startPoint
	public double calculateMaxradius(Coordinate startPoint, Coordinate point1,
			double radius1, Coordinate point2, double radius2,
			LinkedList<Coordinate> startPoint1Q) {
		double maxRadius = 0;
		IntersectPoint intersectPoint = new IntersectPoint();
		intersectPoint = calculateIntersectPoint(point1, radius1, point2,
				radius2);
		Coordinate p1 = intersectPoint.getIntersectPoint_left();
		double d1 = startPoint.distance(p1);
		Coordinate p2 = intersectPoint.getIntersectPoint_right();
		double d2 = startPoint.distance(p2);
		if (d1 > d2) {
			maxRadius = d1;
			startPoint1Q.addLast(p1);
		} else {
			maxRadius = d2;
			startPoint1Q.addLast(p2);
		}
		return maxRadius;
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

	/* return the the intersection point between a linesegment and a circle */
	public Coordinate getIntersectPoint(Coordinate startPoint, Coordinate p,
			double radius) {
		Coordinate[] intsectArray = line_circle_intersect(startPoint, radius, p);
		// record the intersection point
		Coordinate intsectPoint = new Coordinate();
		// obtain the point which is on the linesegment l
		LineSegment l = new LineSegment(startPoint, p);
		if (isOnLinesegment(intsectArray[0], l))
			intsectPoint = intsectArray[0];
		else
			intsectPoint = intsectArray[1];
		return intsectPoint;
	}

	// judge whether a point is in a circle or not
	public boolean isinCircle(Coordinate p, VQP vqp) {
		boolean flag = false;
		if (vqp.getCoordinate().distance(p) < vqp.getRadius())
			flag = true;
		return flag;
	}

	// judge whether a point is in a circle or on the circumference of the
	// circle
	public boolean isinCircle1(Coordinate p, VQP vqp) {
		if (vqp.getCoordinate().distance(p) < vqp.getRadius()
				|| Math.abs(vqp.getCoordinate().distance(p) - vqp.getRadius()) < 1e-6)
			return true;
		return false;
	}

	/*
	 * judge whether a point is on a line segment or not
	 */
	public boolean isOnLinesegment(Coordinate p, LineSegment l) {
		if (Math.abs(l.distance(p) - 0) < 1e-6)
			return true;
		else
			return false;
	}

	// the periphery has not been covered of the current level besides the
	// previous 2 query
	public void continueQuery(Coordinate a[], LinkedList<VQP1> querypoint,
			LinkedList<Coordinate[]> uncoveredArc, Coordinate startPoint,
			double radius, String state, int category, String query,
			LinkedList<Coordinate> visitedQ, LinkedList<VQP> visitedInfoQ) {
		Iterator<VQP1> it = querypoint.iterator();
		int index1 = -1;
		int index2 = -1;
		// get the 2 circle which are related to the arc
		while (it.hasNext()) {
			VQP1 tempvqp1 = it.next();
			if (myContain1(a[0], tempvqp1)) {
				index1 = querypoint.indexOf(tempvqp1);
				System.out.println("index1=" + index1);
			} else if (myContain1(a[1], tempvqp1)) {
				index2 = querypoint.indexOf(tempvqp1);
			}
		}
		// calculate the midpoint of the arc
		// int size=a.length;
		Coordinate midpoint = new Coordinate((a[0].x + a[1].x) / 2,
				(a[0].y + a[1].y) / 2);
		Coordinate b[] = new Coordinate[2];
		b = line_circle_intersect(startPoint, radius, midpoint);
		Coordinate SM = new Coordinate(midpoint.x - startPoint.x, midpoint.y
				- startPoint.y);
		Coordinate SQ1 = new Coordinate(b[0].x - startPoint.x, b[0].y
				- startPoint.y);
		Coordinate nextcenter = new Coordinate();
		// judge the middle point of the arc through vector:colineation
		if ((SQ1.x * SM.x + SQ1.y * SM.y) > 0) {
			nextcenter = b[0];
		} else
			nextcenter = b[1];
		// query
		VQP1 centervqp1 = onequery(startPoint, radius, nextcenter, state,
				category, query, visitedQ, visitedInfoQ);
		// update the neighbor of tvqp1 and tvqp2
		VQP1 tvqp1 = querypoint.get(index1);
		VQP1 tvqp2 = querypoint.get(index2);

		/***************************************************************/
		// update the neighbor of the centervqp1
		PandC pc1 = centervqp1.getleft();
		double d1 = tvqp1.getself().getCoordinate()
				.distance(pc1.getintersection());
		double d2 = tvqp2.getself().getCoordinate()
				.distance(pc1.getintersection());
		if (d1 < d2) {
			// centervqp1's left neighbor is tvqp1
			centervqp1.getleft().setneighborcenter(
					tvqp1.getself().getCoordinate());
			centervqp1.getleft().setRadius(tvqp1.getself().getRadius());
			centervqp1.getright().setneighborcenter(
					tvqp2.getself().getCoordinate());
			centervqp1.getright().setRadius(tvqp2.getself().getRadius());
			Coordinate t1 = tvqp1.getleft().getintersection();
			// tvqp1's left neighbor is centervqp1
			if (Math.abs(t1.x - a[0].x) < 1e-6
					&& Math.abs(t1.y - a[0].y) < 1e-6) {
				tvqp1.getleft().setneighborcenter(
						centervqp1.getself().getCoordinate());
				tvqp1.getleft().setRadius(centervqp1.getself().getRadius());
				if (!isinCircle(t1, centervqp1.getself())) {
					Coordinate A1[] = new Coordinate[2];
					A1[0] = centervqp1.getleft().getintersection();
					A1[1] = tvqp1.getleft().getintersection();
					uncoveredArc.add(A1);
				}
			}
			// tvqp1's right neighbor is centervqp1
			else {
				tvqp1.getright().setneighborcenter(
						centervqp1.getself().getCoordinate());
				tvqp1.getright().setRadius(centervqp1.getself().getRadius());
				if (!isinCircle(tvqp1.getright().getintersection(),
						centervqp1.getself())) {
					Coordinate A2[] = new Coordinate[2];
					A2[0] = centervqp1.getleft().getintersection();
					A2[1] = tvqp1.getright().getintersection();
					uncoveredArc.add(A2);
				}
			}
			// tvqp2's left neighbor is centervqp1
			Coordinate t2 = tvqp2.getleft().getintersection();
			if (Math.abs(t2.x - a[1].x) < 1e-6
					&& Math.abs(t2.y - a[1].y) < 1e-6) {
				tvqp2.getleft().setneighborcenter(
						centervqp1.getself().getCoordinate());
				tvqp2.getleft().setRadius(centervqp1.getself().getRadius());
				if (!isinCircle(t2, centervqp1.getself())) {
					Coordinate A3[] = new Coordinate[2];
					A3[0] = centervqp1.getright().getintersection();
					A3[1] = tvqp2.getleft().getintersection();
					uncoveredArc.add(A3);
				}
			}
			// tvqp2's right neighbor is centervqp1
			else {
				tvqp2.getright().setneighborcenter(
						centervqp1.getself().getCoordinate());
				tvqp2.getright().setRadius(centervqp1.getself().getRadius());
				if (!isinCircle(tvqp2.getright().getintersection(),
						centervqp1.getself())) {
					Coordinate A4[] = new Coordinate[2];
					A4[0] = centervqp1.getright().getintersection();
					A4[1] = tvqp2.getright().getintersection();
					uncoveredArc.add(A4);
				}
			}
		} else {
			// centervqp1's left neighbor is tvqp2
			centervqp1.getright().setneighborcenter(
					tvqp1.getself().getCoordinate());
			centervqp1.getright().setRadius(tvqp1.getself().getRadius());
			centervqp1.getleft().setneighborcenter(
					tvqp2.getself().getCoordinate());
			centervqp1.getleft().setRadius(tvqp2.getself().getRadius());
			Coordinate t3 = tvqp1.getleft().getintersection();
			// tvqp1's left neighbor is centervqp1
			if (Math.abs(t3.x - a[0].x) < 1e-6
					&& Math.abs(t3.y - a[0].y) < 1e-6) {
				tvqp1.getleft().setneighborcenter(
						centervqp1.getself().getCoordinate());
				tvqp1.getleft().setRadius(centervqp1.getself().getRadius());
				if (!isinCircle(t3, centervqp1.getself())) {
					Coordinate A5[] = new Coordinate[2];
					A5[0] = centervqp1.getright().getintersection();
					A5[1] = tvqp1.getleft().getintersection();
					uncoveredArc.add(A5);
				}
			}
			// tvqp1's right neighbor is centervqp1
			else {
				tvqp1.getright().setneighborcenter(
						centervqp1.getself().getCoordinate());
				tvqp1.getright().setRadius(centervqp1.getself().getRadius());
				if (!isinCircle(tvqp1.getright().getintersection(),
						centervqp1.getself())) {
					Coordinate A6[] = new Coordinate[2];
					A6[0] = centervqp1.getright().getintersection();
					A6[1] = tvqp1.getright().getintersection();
					uncoveredArc.add(A6);
				}
			}
			// tvqp2's left neighbor is centervqp1
			Coordinate t4 = tvqp2.getleft().getintersection();
			if (Math.abs(t4.x - a[1].x) < 1e-6
					&& Math.abs(t4.y - a[1].y) < 1e-6) {
				tvqp2.getleft().setneighborcenter(
						centervqp1.getself().getCoordinate());
				tvqp2.getleft().setRadius(centervqp1.getself().getRadius());
				if (!isinCircle(t4, centervqp1.getself())) {
					Coordinate A7[] = new Coordinate[2];
					A7[0] = centervqp1.getleft().getintersection();
					A7[1] = tvqp2.getleft().getintersection();
					uncoveredArc.add(A7);
				}
			}
			// tvqp2's right neighbor is centervqp1
			else {
				tvqp2.getright().setneighborcenter(
						centervqp1.getself().getCoordinate());
				tvqp2.getright().setRadius(centervqp1.getself().getRadius());
				if (!isinCircle(tvqp2.getright().getintersection(),
						centervqp1.getself())) {
					Coordinate A8[] = new Coordinate[2];
					A8[0] = centervqp1.getleft().getintersection();
					A8[1] = tvqp2.getright().getintersection();
					uncoveredArc.add(A8);
				}
			}
		}
		// update the querypoint linkedList
		querypoint.addLast(centervqp1);
		querypoint.set(index1, tvqp1);
		querypoint.set(index2, tvqp2);
	}

	// judge which circle is related with the arc
	public boolean myContain1(Coordinate a, VQP1 vqp1) {
		boolean flag = false;
		if ((Math.abs(a.x - vqp1.getleft().getintersection().x) < 1e-6 && Math
				.abs(a.y - vqp1.getleft().getintersection().y) < 1e-6)
				|| (Math.abs(a.x - vqp1.getright().getintersection().x) < 1e-6 && Math
						.abs(a.y - vqp1.getright().getintersection().y) < 1e-6))
			flag = true;
		return flag;

	}

	/*
	 * circle: the circle centered at startPoint with radiusline: startPoint and
	 * p are on the line
	 */
	public Coordinate[] line_circle_intersect(Coordinate startPoint,
			double radius, Coordinate p) {
		Coordinate[] a = new Coordinate[2];
		a[0] = new Coordinate();
		a[1] = new Coordinate();
		// the slope of the line:k=infinite
		if (p.x == startPoint.x) {
			a[0].x = startPoint.x;
			a[0].y = startPoint.y + radius;
			a[1].x = startPoint.x;
			a[1].y = startPoint.y - radius;
		}
		// k=0
		else if (p.y == startPoint.y) {
			a[0].x = startPoint.x + radius;
			a[0].y = startPoint.y;
			a[1].x = startPoint.x - radius;
			a[1].y = startPoint.y;
		} else {
			double k = (p.y - startPoint.y) / (p.x - startPoint.x);
			double A = Math.sqrt((radius * radius) / (1 + k * k));
			a[0].x = startPoint.x + A;
			a[0].y = startPoint.y + k * A;
			a[1].x = startPoint.x - A;
			a[1].y = startPoint.y - k * A;
		}
		return a;
	}
}
