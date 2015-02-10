/**
 * 
 */
package mo.umac.crawler;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import paint.PaintShapes;

import mo.umac.db.DBInMemory;
import mo.umac.excel.WriteExcel;
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
 * This algorithm call the periphery to finish the query in circle
 * 
 */
public class Hexagon_Optimize6 extends Strategy {

	/**
	 * 
	 */
	// public static int recursion = 1;
	public static int NEED_POINTS_NUMBER =5500 ;
	public static int countPoint = 0;
	public static double sqrt3 = Math.sqrt(3);
	public static double key = 1;
	public static double key2=3*sqrt3/(2*Math.PI);
	public static int countquery = 0;

	private static Coordinate startPoint = new Coordinate();
	private static Set<APOI> queryset = new HashSet<APOI>();
	private static Set<APOI> eligibleset = new HashSet<APOI>();
	// @param visitedcircle_Queue: record the information(coordinate,radius)of
	// the visited points
	private static LinkedList<VQP> visitedcircle_Queue = new LinkedList<VQP>();
	private static LinkedList<VQP>visitedcircle_Queueclone=new LinkedList<VQP>();
	private static Coordinate levelstartPinHex=new Coordinate();
	private static String filepath1= "../experiment_result/result1.xls";
	public Hexagon_Optimize6() {
//		startPoint =new Coordinate (-73.355835, 42.746632);
		startPoint=new Coordinate(-74.124299, 41.322171);
//		 startPoint.x=500;
//		 startPoint.y= 500;

		logger.info("------------Hexagon combine with periphery------------");
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
		
		ununiformlyquery(startPoint, envelopeState, state, category, query);
		queryset.clear();
		eligibleset.clear();
		visitedcircle_Queueclone.clear();
		visitedcircle_Queue.clear();
		logger.info("eligiblepoint=" + countPoint);
	}
	
	public void setkey(double a){
		key=a;
	}
	
	public void setNEED_POINTS_NUM(int a){
		NEED_POINTS_NUMBER=a;
	}
	public void setfilepath(String filepath){
		filepath1=filepath;
	}
	/* calculate the centeral points of the hexagons */
	public void calculatePoint(Coordinate startPoint, double radius,
			LinkedList<VQP> visitedcircle_Queue, LinkedList<Coordinate> unvisited_Queue) {
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
		boolean flag = false;
		for (int i = 0; i < q.size() && !flag; i++) {
			Coordinate one = q.get(i);
			if (Math.abs(one.x - c.x) < 1e-6 && Math.abs(one.y - c.y) < 1e-6) {
				flag = true;
			}
		}
		return flag;
	}

	private boolean myContain2(LinkedList<VQP> q, Coordinate c) {
		boolean flag = false;
		Iterator<VQP> it = q.iterator();
		while (it.hasNext()) {
			Coordinate one = it.next().getCoordinate();
			if (Math.abs(one.x - c.x) < 1e-6 && Math.abs(one.y - c.y) < 1e-6) {
				flag = true;
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
	public void ununiformlyquery(Coordinate startPoint, Envelope envelopeState,
			String state, int category, String query) {
		countPoint=0;
		countquery=0;
		// record points for next round query
		LinkedList<Coordinate> unvisited_Queue = new LinkedList<Coordinate>();
		LinkedList<VQP>lowCirclelist=new LinkedList<VQP>();
		/* issue the first query */
		AQuery Firstquery = new AQuery(startPoint, state, category, query,
				MAX_TOTAL_RESULTS_RETURNED);
		ResultSetD2 resultSetStart = query(Firstquery);
		countquery++;
		queryset.addAll(resultSetStart.getPOIs());
		countPoint = queryset.size();
		int size = resultSetStart.getPOIs().size();
		APOI farthest = resultSetStart.getPOIs().get(size - 1);
		Coordinate farthestCoordinate = farthest.getCoordinate();
		double distance = startPoint.distance(farthestCoordinate);
		//
		double area=Math.PI*distance*distance;
		double density=100/area;
		double pk=density/1588.9580739153143;
		logger.info("density= "+density+"  pk= "+pk);
		logger.info("------------------------");
		
		//
		visitedcircle_Queue.add(new VQP(startPoint, distance));
		visitedcircle_Queueclone.add(new VQP(startPoint, distance));
		//
		Circle aCircle = new Circle(startPoint, distance);
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(aCircle);
			PaintShapes.paint.myRepaint();
		}

		double radius = distance; // record the first crawl radius
		/* compute coordinates of the points which are used to next round query */
		calculatePoint(startPoint, radius, visitedcircle_Queue, unvisited_Queue);
		int level = 1;
		MainYahoo mainyahoo=new MainYahoo();	
		//
		WriteExcel writer=new WriteExcel();
	    writer.settabletitle(filepath1, 1588.95807391531, density, NEED_POINTS_NUMBER, mainyahoo.gettopK());
		//
		while (countPoint < NEED_POINTS_NUMBER) {
			for (int i = 1; i <= level * 6; i++) {
				if (!unvisited_Queue.isEmpty()) {

					Coordinate p = unvisited_Queue.removeFirst();
					calculatePoint(p, radius, visitedcircle_Queue,
							unvisited_Queue);
					VQP c = new VQP(p, radius);
					if (needQuery(c, visitedcircle_Queue, envelopeState)) {
						AQuery Hexquery = new AQuery(p, state, category, query,
								MAX_TOTAL_RESULTS_RETURNED);
						ResultSetD2 resultSet = query(Hexquery);
						countquery++;
						queryset.addAll(resultSet.getPOIs());
						int size1 = resultSet.getPOIs().size();
						APOI farthest1 = resultSet.getPOIs().get(size1 - 1);
						Coordinate farthest1Coordinate = farthest1
								.getCoordinate();
						double distance1 = p.distance(farthest1Coordinate);
						double crawl_radius = distance1;
						visitedcircle_Queue.add(new VQP(p, crawl_radius));
						visitedcircle_Queueclone.add(new VQP(p, crawl_radius));
						//record all the need position where need continue issuing query						
						if (crawl_radius < radius * key) {
							lowCirclelist.add(new VQP(p, crawl_radius));						
						}
						Circle aaCircle = new Circle(p, crawl_radius);
						if (logger.isDebugEnabled() && PaintShapes.painting) {
							PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
							PaintShapes.paint.addCircle(aaCircle);
							PaintShapes.paint.myRepaint();
						}
					}
					/* no need to query! */
					else {
						visitedcircle_Queue.add(new VQP(p, 0));
						visitedcircle_Queueclone.add(c);
					}
				}
			}
			//cover the hexagon
			for(int index=0;index<lowCirclelist.size();index++){
				VQP lowcircle=lowCirclelist.get(index);
				queryInhexagon(lowcircle,radius, state, category, query);
			}
		   lowCirclelist.clear();
			/* calculate the cover radius */
			double coverRadius = calculateIncircle(startPoint,  visitedcircle_Queueclone);
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
				if (startPoint.distance(pp.getCoordinate()) < coverRadius)
					eligibleset.add(pp);
			}
			countPoint = eligibleset.size();
			logger.info("eliglible point during the query=" + countPoint
					+ "  level=" + level);
			logger.info("countquery="+countquery);
			if (countPoint == Strategy.TOTAL_POINTS) {
				logger.info("We can only find " + TOTAL_POINTS + "points!");
				break;
			}
			level++;
//			if(NEED_POINTS_NUMBER-countPoint<level*6*mainyahoo.gettopK()*key2*key*key){
//				while(countPoint<NEED_POINTS_NUMBER){
//					AQuery aquery2=new AQuery(levelstartPinHex, state, category, query, MAX_TOTAL_RESULTS_RETURNED);
//					ResultSetD2 result2=query(aquery2);
//					queryset.addAll(result2.getPOIs());
//					countquery++;
//					double radius2=levelstartPinHex.distance(result2.getPOIs().get(result2.getPOIs().size()-1).getCoordinate());
//					visitedcircle_Queue.add(new VQP(levelstartPinHex, radius2));
//					visitedcircle_Queueclone.add(new VQP(levelstartPinHex, radius2));
//					coverRadius=calculateIncircle(startPoint, visitedcircle_Queueclone);
//					Iterator<APOI> it1 = queryset.iterator();
//					while (it1.hasNext()) {
//						int id1 = it1.next().getId();
//						APOI pp1 = DBInMemory.pois.get(id1);
//						if (startPoint.distance(pp1.getCoordinate()) < coverRadius)
//							eligibleset.add(pp1);
//					}
//					countPoint=eligibleset.size();
//				}			
//			}
			logger.info("=======================================");
		}
		writer.setResult(filepath1, countPoint, queryset.size(), countquery, key);
	}

	public double queryInhexagon(VQP circle, double radius, String state, int category, String query){
		double  coverradius=circle.getRadius();
		LinkedList<VQP>neighborList=new LinkedList<VQP>();
		for(int i=0;i<visitedcircle_Queue.size();i++){
			VQP neighbor=visitedcircle_Queue.get(i);
			if(neighbor.getRadius()>0&&circles_Insecter(circle, neighbor)){
				neighborList.add(neighbor);
			}			
		}
		//cover the periphery of the circle
		Map<Double[], Coordinate[]>uncoverArc=findUncoverarc(circle, neighborList);
		int uncoversize=uncoverArc.size();
		while(uncoversize>0){
			Iterator<Map.Entry<Double[], Coordinate[]>>it=uncoverArc.entrySet().iterator();
			Map.Entry<Double[], Coordinate[]>entry=it.next();
			Coordinate mid=new Coordinate();
		    mid.x=(entry.getValue()[0].x+entry.getValue()[1].x)/2;
		    mid.y=(entry.getValue()[0].y+entry.getValue()[1].y)/2;
		    Coordinate a[]=line_circle_intersect(circle, mid);
		    double angle1=getSlantangle(circle.getCoordinate(), a[0]);
		    double angle2=getSlantangle(circle.getCoordinate(), a[1]);
		    Coordinate midarc=new Coordinate();
		    if(angle1>entry.getKey()[0]&&angle1<entry.getKey()[1]){
		    	midarc=a[0];
		    }
		    else midarc=a[1];
		    uncoverArc.remove(entry.getKey());
		    //issue the query 
		    AQuery aquery=new AQuery(midarc, state, category, query, MAX_TOTAL_RESULTS_RETURNED);
		    ResultSetD2 aresult=query(aquery);
		    countquery++;
		    queryset.addAll(aresult.getPOIs());
		    double midradius=midarc.distance(aresult.getPOIs().get(aresult.getPOIs().size()-1).getCoordinate());
		    VQP newneighbor=new VQP(midarc, midradius);
		    neighborList.add(newneighbor);
		    visitedcircle_Queue.add(newneighbor);
		    visitedcircle_Queueclone.add(newneighbor);
		    uncoverArc=findUncoverarc(circle, neighborList);
		    uncoversize=uncoverArc.entrySet().size();
		    
		    Circle aCircle = new Circle(midarc, midradius);
			if (logger.isDebugEnabled() && PaintShapes.painting) {
				PaintShapes.paint.color = PaintShapes.paint.greenTranslucence;
				PaintShapes.paint.addCircle(aCircle);
				PaintShapes.paint.myRepaint();
			}
		}
		//calculate the cover radius
		coverradius=calculateIncircle(circle.getCoordinate(), visitedcircle_Queueclone);
		while(coverradius<radius*key){
			AQuery aquery1=new AQuery(levelstartPinHex, state, category, query, MAX_TOTAL_RESULTS_RETURNED);
			ResultSetD2 aresult1=query(aquery1);
			countquery++;
			queryset.addAll(aresult1.getPOIs());
			double aradius=levelstartPinHex.distance(aresult1.getPOIs().get(aresult1.getPOIs().size()-1).getCoordinate());
			visitedcircle_Queue.add(new VQP(levelstartPinHex, aradius));
			visitedcircle_Queueclone.add(new VQP(levelstartPinHex, aradius));
			 Circle aCircle1 = new Circle(levelstartPinHex, aradius);
				if (logger.isDebugEnabled() && PaintShapes.painting) {
					PaintShapes.paint.color = PaintShapes.paint.greenTranslucence;
					PaintShapes.paint.addCircle(aCircle1);
					PaintShapes.paint.myRepaint();
				}
			coverradius=calculateIncircle(circle.getCoordinate(), visitedcircle_Queueclone);
			 
		}
		Circle aCircle2 = new Circle(circle.getCoordinate(), coverradius);
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.blueTranslucence;
			PaintShapes.paint.addCircle(aCircle2);
			PaintShapes.paint.myRepaint();
		}
		return coverradius;
	}

	public void coverRectangle(Envelope envelope, Envelope envelopeState,
			String state, int category, String query,
			LinkedList<VQP> visited_Queue) {

		Envelope e[] = partitionEnvelope(envelope);
		for (int i = 0; i < e.length; i++) {
			// rectangle is intersect with envelopeState
			Coordinate p1 = new Coordinate();
			p1.x = (e[i].getMinX() + e[i].getMaxX()) / 2;
			p1.y = (e[i].getMinY() + e[i].getMaxY()) / 2;
			Coordinate p2 = new Coordinate(e[i].getMinX(), e[i].getMinY());
			double needradius = p1.distance(p2);
			VQP vqp=new VQP(p1, needradius);
			if (needQuery(vqp, visitedcircle_Queue, envelopeState)) {
				AQuery Firstquery = new AQuery(p1, state, category, query,
						MAX_TOTAL_RESULTS_RETURNED);
				ResultSetD2 resultSetStart = query(Firstquery);
				countquery++;
				queryset.addAll(resultSetStart.getPOIs());
				int size = resultSetStart.getPOIs().size();
				APOI farthest = resultSetStart.getPOIs().get(size - 1);
				double radius = p1.distance(farthest.getCoordinate());
				visitedcircle_Queue.add(new VQP(p1, radius));
				visited_Queue.add(new VQP(p1, radius));
				Circle circle = new Circle(p1, radius);
				if (logger.isDebugEnabled() && PaintShapes.painting) {
					PaintShapes.paint.color = PaintShapes.paint.blueTranslucence;
					PaintShapes.paint.addCircle(circle);
					PaintShapes.paint.myRepaint();
				}
				if (needradius > radius) {
					coverRectangle(e[i], envelopeState, state, category, query,
							visited_Queue);
				} 					
			} else {
				visitedcircle_Queue.add(new VQP(p1, 0));
				visited_Queue.add(new VQP(p1, needradius));
			}
		}
	}

	public Envelope[] partitionEnvelope(Envelope envelop) {
		Envelope[] e = new Envelope[4];
		double width = envelop.getWidth();// maxX-minX
		double height = envelop.getHeight();
		e[0]=new Envelope(envelop.getMinX(), envelop.getMinX()+width/2, envelop.getMinY(), envelop.getMinY()+height/2);
		e[1]=new Envelope(envelop.getMinX(), envelop.getMinX()+width/2, envelop.getMinY()+height/2, envelop.getMaxY());
		e[2]=new Envelope(envelop.getMinX()+width/2, envelop.getMaxX(), envelop.getMinY(), envelop.getMinY()+height/2);
		e[3]=new Envelope(envelop.getMinX()+width/2, envelop.getMaxX(), envelop.getMinY()+height/2, envelop.getMaxY());
		return e;
	}

	/*
	 * algorithm 1 To calculate the maximum inscribed circle of a given area
	 */
	public double calculateIncircle(Coordinate startPoint,
			LinkedList<VQP> visitedcircle_Queue) {
		Coordinate s=new Coordinate();
		double minRadius = 1e308;
		for (int i = 0; i < visitedcircle_Queue.size() - 1; i++) {
			VQP circle1 = visitedcircle_Queue.get(i);
			for (int j = i + 1; j < visitedcircle_Queue.size(); j++) {
				VQP circle2 = visitedcircle_Queue.get(j);
				double dr = circle1.getRadius() - circle2.getRadius();
				// circle1 contain circle2, no need processing circle
				if (dr > 0 && circle_contain(circle1, circle2)) {
					continue;
				}
				// circle2 contain circle1, no need processing circle1
				 if (dr < 0 && circle_contain(circle2, circle1)) {
					break;
				} 
				 if (circles_Insecter(circle1, circle2)) {
					IntersectPoint inter = calculateIntersectPoint(circle1,
							circle2);
				    boolean leftin=false;				
					boolean rightin = false;
					Iterator<VQP> it = visitedcircle_Queue.iterator();
					while (it.hasNext()&&!(leftin&&rightin)) {
						VQP circle3 = it.next();
						if (!circle1.getCoordinate().equals2D(
								circle3.getCoordinate())
								&& !circle2.getCoordinate().equals2D(
										circle3.getCoordinate())) {
							if (!leftin&&isinCircle(inter.getIntersectPoint_left(), circle3)) {
								leftin = true;																														
							}
							if(!rightin&&isinCircle(inter.getIntersectPoint_right(), circle3)){
								rightin=true;
							}
						}
					}
					
					if(!leftin&&rightin){
						double d1=startPoint.distance(inter.getIntersectPoint_left());
						if(d1<minRadius){
							s=inter.getIntersectPoint_left();
							minRadius=d1;
						}
					}
					if(leftin&&!rightin){
						double d2=startPoint.distance(inter.getIntersectPoint_right());
						if(d2<minRadius){
							s=inter.getIntersectPoint_right();
							minRadius=d2;
						}
					}
					if(!leftin&&!rightin){
						double d3=startPoint.distance(inter.getIntersectPoint_left());
						double d4=startPoint.distance(inter.getIntersectPoint_right());
						if(d3<d4&&d3<minRadius){
							s=inter.getIntersectPoint_left();
							minRadius=d3;
						}
						if(d4<d3&&d4<minRadius){
							s=inter.getIntersectPoint_right();
							minRadius=d4;
						}
					}
				  }
				
			}
		}
		levelstartPinHex=s;
		return minRadius;
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
	public boolean needQuery(VQP circle, LinkedList<VQP> visitedcircle_Queue,
			Envelope envelopeState) {
		boolean needquery = false;
		if (!outspace(circle, envelopeState)) {
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
									// IntersectPoint
									// tp=calculateIntersectPoint(c1, c2);
									// if(isinCircle(tp.getIntersectPoint_left(),circle))
									// effective=false;
									// else
									// if(isinCircle(tp.getIntersectPoint_right(),
									// circle))
									// effective=false;
									IntersectPoint tp = calculateIntersectPoint(
											c1, c2);
									if (!isinCircle(
											tp.getIntersectPoint_left(), circle)
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
					&& circles_Insecter(circle1, circle2)) {
				IntersectPoint inter = calculateIntersectPoint(circle1, circle2);
				if (!inter.getIntersectPoint_left().equals2D(
						inter.getIntersectPoint_right()))
					intercircleQ.addLast(circle2);
			}
		}
		
		for(int i=0;i<intercircleQ.size();i++) {
			VQP circletemp1=intercircleQ.get(i);
			IntersectPoint inter1 = calculateIntersectPoint(circle1,circletemp1);
			boolean stopleft = false;
			boolean stopright = false;
			if (isinCircle(inter1.getIntersectPoint_left(), circle)) {
				for(int j=0;j<intercircleQ.size()&&!stopleft&&j!=i;j++){
					VQP circletemp2 = intercircleQ.get(j);					
					if (isinCircle(inter1.getIntersectPoint_left(),circletemp2)
						|| isAtCircumference(inter1.getIntersectPoint_left(),circletemp2)) {
							stopleft = true;
						}
					
				}
			} else {
				stopleft = true;
			}
			if (isinCircle(inter1.getIntersectPoint_right(), circle)) {
				for(int k=0;k<intercircleQ.size()&&!stopright&&k!=i;k++) {
					VQP circletemp3 = intercircleQ.get(k);
					if (isinCircle(inter1.getIntersectPoint_right(),circletemp3)
						|| isAtCircumference(inter1.getIntersectPoint_right(),circletemp3)) {
							stopright = true;
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
		if (d1 <= d2)
			return true;
		return false;
	}

	/*
	 * Consider two circle denoted by c1 and c2. Both the two circles intersect
	 * with circle c, if the arc on the perimeter of c covered by c1 contains
	 * the arc covered by c2, we say c1 is an effective neighbor, on the
	 * contrary, if the arc covered by c2 contains the arc covered by c1, then
	 * c2 is an effective neighbor
	 * 
	 * we assume the arc covered by c1 is completely included in arc covered by
	 * c2
	 */
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

	// To test whether point p1 equals to point p2
	public boolean pointsequal(Coordinate p1, Coordinate p2) {
		if (Math.abs(p1.x - p2.x) < 1e-6 && Math.abs(p1.y - p2.y) < 1e-6)
			return true;
		else
			return false;
	}

	// determine whether a point is in a circle or not
	public boolean isinCircle(Coordinate p, VQP vqp) {
		boolean flag = false;
		if (vqp.getRadius() > vqp.getCoordinate().distance(p))
			flag = true;
		return flag;
	}

	// determine whether a point is at the circumference of a circle
	public boolean isAtCircumference(Coordinate p, VQP circle) {
		boolean atCircumference = false;
		if (Math.abs(circle.getCoordinate().distance(p) - circle.getRadius()) < 1e-6) {
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

	public boolean outspace(VQP c, Envelope envelopeState) {
		double minX = c.getCoordinate().x - c.getRadius();
		double maxX = c.getCoordinate().x + c.getRadius();
		double minY = c.getCoordinate().y - c.getRadius();
		double maxY = c.getCoordinate().y + c.getRadius();
		Envelope e1 = new Envelope(minX, maxX, minY, maxY);
		// System.out.println("e1="+e1.toString());
		if (e1.intersects(envelopeState))
			return false;
		return true;
	}
	
	public Map<Double[],Coordinate[] > findUncoverarc(VQP circle,
			LinkedList<VQP> neighborList) {
		Map<Double[], Coordinate[]>uncoverArc=new HashMap<Double[], Coordinate[]>();
		LinkedList<double[]> coverangle = new LinkedList<double[]>();
		HashMap<Double, Coordinate> angle_coordinate = new HashMap<Double, Coordinate>();
		// record all the covered arc
		for (int i = 0; i < neighborList.size(); i++) {
			VQP c = neighborList.get(i);
			IntersectPoint inter = calculateIntersectPoint(circle, c);
			Coordinate mid = new Coordinate();
			mid.x = (inter.getIntersectPoint_left().x + inter
					.getIntersectPoint_right().x) / 2;
			mid.y = (inter.getIntersectPoint_left().y + inter
					.getIntersectPoint_right().y) / 2;
			Coordinate a[] = line_circle_intersect(circle, mid);
			Coordinate arcmid = new Coordinate();
			if (isinCircle(a[0], c))
				arcmid = a[0];
			else
				arcmid = a[1];
			double angle0 = getSlantangle(circle.getCoordinate(),
					inter.getIntersectPoint_left());
			double angle1 = getSlantangle(circle.getCoordinate(),
					inter.getIntersectPoint_right());
			double angle2 = getSlantangle(circle.getCoordinate(), arcmid);
			angle_coordinate.put(angle0, inter.getIntersectPoint_left());
			angle_coordinate.put(angle1, inter.getIntersectPoint_right());
			if (angle2 < Math.min(angle0, angle1)
					|| angle2 > Math.max(angle0, angle1)) {
				double b1[] = new double[2];
				b1[0] = 0;
				b1[1] = Math.min(angle0, angle1);
				coverangle.add(b1);
				double b2[] = new double[2];
				b2[0] = Math.max(angle0, angle1);
				b2[1] = 360;
				coverangle.add(b2);
			} else {
				double b3[] = new double[2];
				b3[0] = Math.min(angle0, angle1);
				b3[1] = Math.max(angle0, angle1);
				coverangle.add(b3);
			}
			
		}
		// add a virtual angle
		double b4[] = new double[2];
		b4[0] = b4[1] = 360;
		coverangle.add(b4);
		Coordinate end = new Coordinate(circle.getCoordinate().x
				+ circle.getRadius(), circle.getCoordinate().y);
		angle_coordinate.put(b4[0], end);
		// sort the cover arc
		int minindex = 0;
		for (int j = 0; j < coverangle.size() - 1; j++) {
			minindex = j;
			for (int k = j + 1; k < coverangle.size(); k++) {
				if (coverangle.get(minindex)[0] > coverangle.get(k)[0]) {
					minindex = k;
				}
			}
			double tem[] = new double[2];
			tem = coverangle.get(j);
			coverangle.set(j, coverangle.get(minindex));
			coverangle.set(minindex, tem);
		}
		// find the uncover arc
		int startposition = 0;
		while (startposition < coverangle.size() - 1) {
			double coverArc[] = coverangle.get(startposition);
			boolean stop = false;
			int m = 0;
			for (m = startposition + 1; m < coverangle.size() && !stop; m++) {
				double bb[] = coverangle.get(m);
				if (bb[0] <= coverArc[1]) {
					coverArc[0] = Math.min(coverArc[0], bb[0]);
					coverArc[1] = Math.max(coverArc[1], bb[1]);
				} else {
					Coordinate uncover[]=new Coordinate[2];
					//record the consistant uncover angle
					Double[] uncoverA=new Double[2];
					uncoverA[0]=coverArc[1];
					uncoverA[1]=bb[0];
					Iterator<Map.Entry<Double, Coordinate>> entries = angle_coordinate
							.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<Double, Coordinate> entry = entries.next();
						if (entry.getKey() == coverArc[1])
							uncover[0] = entry.getValue();
						else if (entry.getKey() == bb[0])
							uncover[1] = entry.getValue();
					}
					uncoverArc.put(uncoverA, uncover);
					startposition = m;
					stop = true;
				}
			}
			if(m==coverangle.size()){
				startposition=m;
			}
		}
		return uncoverArc;
	}

	public double getSlantangle(Coordinate centerP, Coordinate p2) {
		double slantangle = 0;
		if (p2.x == centerP.x) {
			if (p2.y >= centerP.y) {
				slantangle=90;
			} else {
				slantangle=270;
			}
		} 
		else if(p2.y==centerP.y){
			if(p2.x>=centerP.x){
				slantangle=0;
			}
			else slantangle=180;
			
		}else {
			double k = (p2.y - centerP.y) / (p2.x - centerP.x);
			slantangle = Math.atan(k);
			if(p2.x>centerP.x){
				//1st and 4th quadrant
				slantangle=(Math.toDegrees(slantangle)+360)%360;
			}
			if(p2.x<centerP.x){
				//2nd and 3rd quadrant
				slantangle=Math.toDegrees(slantangle)+180;
			}
		}
		
		
		return slantangle;
	}
	
	public Coordinate[] line_circle_intersect(VQP circle, Coordinate p) {
		Coordinate startPoint = circle.getCoordinate();
		double radius = circle.getRadius();
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
