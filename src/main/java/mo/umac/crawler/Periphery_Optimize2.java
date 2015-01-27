package mo.umac.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import mo.umac.db.DBInMemory;
import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.plugins.IntersectPoint;
import mo.umac.metadata.plugins.Level_info;
import mo.umac.metadata.plugins.PandC;
import mo.umac.metadata.ResultSetD2;
import mo.umac.metadata.plugins.VQP;
import mo.umac.metadata.plugins.VQP1;
import paint.PaintShapes;
import mo.umac.spatial.Circle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class Periphery_Optimize2 extends Strategy{
	
	//private Coordinate startPoint=new Coordinate();
	
	public static int countquery=0;
	public static int NEED_POINTS_NUM=57584;
	public static int countPoint=0;
	public static int level=0;
	public static Coordinate startPoint=new Coordinate();
	public static double inRadius=0;//record the incircle radius after the level query
	public static int onequerycount=0;//record the number of call the onequery
	
	private static Set<APOI> queryset = new HashSet<APOI>();// record all points queried
	private static Set<APOI> eligibleset= new HashSet<APOI>(); //record all eligible points
	private static LinkedHashSet<VQP> visitedcircle_Queue=new LinkedHashSet<VQP>();//record all the query circle
	private static Coordinate levelstartPoint=new Coordinate();//record the start point of every level 
	
	private static double firstradius=0; 
	
	
	public Periphery_Optimize2() {
		//super();
//		startPoint.x = -73.355835;
//		startPoint.y = 42.746632;
		 startPoint.x=500;
		 startPoint.y= 500;
		logger.info("------------PeripheryQuery------------");
	}
	// public PeripheryQuery(Coordinate a){
		// this.startPoint=a;
	 //}
	
	
	
	@Override
	public void crawl(String state, int category, String query,
			Envelope evenlopeState){
		
		if (logger.isDebugEnabled()) {
			logger.info("------------crawling-----------");
			logger.info(evenlopeState.toString());
		}
		// finished crawling
		if (evenlopeState == null) {
			return;
		}		
		startQuery(state, category, query);
	    logger.info("eligiblepoint="+countPoint);		
	}
	
	public void startQuery(String state, int category, String query){
		//issue the first query
		AQuery Firstquery = new AQuery(startPoint, state, category, query,
				MAX_TOTAL_RESULTS_RETURNED);
		ResultSetD2 resultSetStart = query(Firstquery);
		countquery++;
		queryset.addAll(resultSetStart.getPOIs());
		countPoint = queryset.size();
		int size = resultSetStart.getPOIs().size();
		APOI farthest = resultSetStart.getPOIs().get(size - 1);
		Coordinate farthestCoordinate = farthest.getCoordinate();
		firstradius = startPoint.distance(farthestCoordinate);
		inRadius=firstradius;
		visitedcircle_Queue.add(new VQP(startPoint, inRadius));
		//
		Circle aCircle = new Circle(startPoint, inRadius);
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(aCircle);
			PaintShapes.paint.myRepaint();
		}
        levelstartPoint.x=startPoint.x;
        levelstartPoint.y=startPoint.y+inRadius;
        //record all the issued query except the first one
        LinkedList<VQP> visited_Queue=new LinkedList<VQP>();
        if(countPoint<NEED_POINTS_NUM){
        	onelevelQuery(state, category, query, visited_Queue);
            logger.info("countPoint="+countPoint+"  countquery="+countquery);
        }
        int k=1;
        while(countPoint<NEED_POINTS_NUM){
        	AQuery continuequery=new AQuery(levelstartPoint, state, category, query, MAX_TOTAL_RESULTS_RETURNED);
        	ResultSetD2 continueresult=query(continuequery);
        	queryset.addAll(continueresult.getPOIs());
        	countquery++;
        	int continuesize=continueresult.getPOIs().size();
        	double continueradius=levelstartPoint.distance(continueresult.getPOIs().get(continuesize-1).getCoordinate());
        	visitedcircle_Queue.add(new VQP(levelstartPoint, continueradius));
        	visited_Queue.addLast(new VQP(levelstartPoint, continueradius));
        	Circle aaaCircle = new Circle(levelstartPoint, continueradius);
    		if (logger.isDebugEnabled() && PaintShapes.painting) {
    			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
    			PaintShapes.paint.addCircle(aaaCircle);
    			PaintShapes.paint.myRepaint();
    		}
        	inRadius=calculateIncircle(startPoint,firstradius, visited_Queue);
        	Iterator<APOI>iterator=queryset.iterator();
        	while(iterator.hasNext()){
        		APOI continueAPOI=iterator.next();
        	    if(startPoint.distance(continueAPOI.getCoordinate())<=inRadius)
        	    	eligibleset.add(continueAPOI);
        	}
        	countPoint=eligibleset.size();
        	Circle aaCircle = new Circle(startPoint, inRadius);
    		if (logger.isDebugEnabled() && PaintShapes.painting) {
    			PaintShapes.paint.color = PaintShapes.paint.blueTranslucence;
    			PaintShapes.paint.addCircle(aaCircle);
    			PaintShapes.paint.myRepaint();
    		}
    		System.out.println("k="+k);
    		k++;
        }
        
	}
	
	public void onelevelQuery(String state, int category, String query, LinkedList<VQP>visited_Queue){
		LinkedList<Coordinate[]>uncoverArc=new LinkedList<Coordinate[]>();
		//issue a query at the levelstartPoint
		AQuery Firstquery = new AQuery(levelstartPoint, state, category, query,
				MAX_TOTAL_RESULTS_RETURNED);
		ResultSetD2 resultSetStart = query(Firstquery);
		countquery++;
		queryset.addAll(resultSetStart.getPOIs());
		int size = resultSetStart.getPOIs().size();
		APOI farthest = resultSetStart.getPOIs().get(size - 1);
		Coordinate farthestCoordinate = farthest.getCoordinate();
	    double radius = levelstartPoint.distance(farthestCoordinate);
	    VQP inCircle=new VQP(startPoint, inRadius);
	    VQP circle1=new VQP(levelstartPoint, radius);
		visitedcircle_Queue.add(circle1);
		visited_Queue.addLast(circle1);
		
		Circle aCircle = new Circle(levelstartPoint, radius);
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(aCircle);
			PaintShapes.paint.myRepaint();
		}
		//the periphery hasn't been covred
		if(circles_Insecter(circle1, inCircle)){
			Coordinate nextPosition1=new Coordinate(startPoint.x, startPoint.y-inRadius);
			AQuery aquery2=new AQuery(nextPosition1, state, category, query, MAX_TOTAL_RESULTS_RETURNED);
			ResultSetD2 resultSetStart2=query(aquery2);
			countquery++;
			queryset.addAll(resultSetStart2.getPOIs());
			int size2=resultSetStart2.getPOIs().size();
			double radius2=nextPosition1.distance(resultSetStart2.getPOIs().get(size2-1).getCoordinate());
			VQP circle3=new VQP(nextPosition1, radius2);
			visitedcircle_Queue.add(circle3);
			visited_Queue.addLast(circle3);	
			Circle aCircle1 = new Circle(nextPosition1, radius2);
			if (logger.isDebugEnabled() && PaintShapes.painting) {
				PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
				PaintShapes.paint.addCircle(aCircle1);
				PaintShapes.paint.myRepaint();
			}
			if(circles_Insecter(circle3, inCircle)){
				IntersectPoint inter2=calculateIntersectPoint(circle3, inCircle);
				//the periphery hasn't been covered
				if(!isinCircle(inter2.getIntersectPoint_left(), circle1)){
					IntersectPoint inter=calculateIntersectPoint(inCircle, circle1);
					double dd1=inter.getIntersectPoint_left().distance(inter2.getIntersectPoint_left());
					double dd2=inter.getIntersectPoint_left().distance(inter2.getIntersectPoint_right());
					
					if(dd1<dd2){
						Coordinate aa[]=new Coordinate[2];
						aa[0]=inter.getIntersectPoint_left();
						aa[1]=inter2.getIntersectPoint_left();
						uncoverArc.addLast(aa);
						Coordinate aa1[]=new Coordinate[2];
						aa1[0]=inter.getIntersectPoint_right();
						aa1[1]=inter2.getIntersectPoint_right();
						uncoverArc.addLast(aa1);
					}
					else{
						Coordinate aa2[]=new Coordinate[2];
						aa2[0]=inter.getIntersectPoint_left();
						aa2[1]=inter2.getIntersectPoint_right();
						uncoverArc.addLast(aa2);
						Coordinate aa3[]=new Coordinate[2];
						aa3[0]=inter.getIntersectPoint_right();
						aa3[1]=inter2.getIntersectPoint_left();
						uncoverArc.addLast(aa3);						
					}
					for(int i=0;i<uncoverArc.size();i++){
						Coordinate t[]=uncoverArc.get(i);
//						logger.info("uncoverArc "+i+"th element is ="+t[0].toString()+"  "+t[1].toString());
					}
				}
			}		  			
		}
		//periphery has been covered, no need to update the neighborlist
		else{
			Coordinate nextPosition =new Coordinate(startPoint.x, startPoint.y-inRadius);
			AQuery aquery= new AQuery(nextPosition, state, category, query,
					MAX_TOTAL_RESULTS_RETURNED);
			ResultSetD2 result = query(Firstquery);
			countquery++;
			queryset.addAll(result.getPOIs());
			int size1 = resultSetStart.getPOIs().size();
			APOI farthest1 = resultSetStart.getPOIs().get(size - 1);
		    double radius1 = levelstartPoint.distance(farthest1.getCoordinate());
		    VQP circle2=new VQP(levelstartPoint, radius);
			visitedcircle_Queue.add(circle2);
			visited_Queue.addLast(circle2);			
		}	
		while(!uncoverArc.isEmpty()){
			//issue a query at the middle point of the uncovered arc
			Coordinate a[]=uncoverArc.removeFirst();
			Coordinate mid=new Coordinate((a[0].x+a[1].x)/2, (a[0].y+a[1].y)/2);
			Coordinate b[]=line_circle_intersect(startPoint, inRadius, mid);
			Coordinate nextP=new Coordinate();
			Coordinate vector1=new Coordinate(mid.x-startPoint.x, mid.y-startPoint.y);
			Coordinate vector2=new Coordinate(mid.x-b[0].x, mid.y-b[0].y);
			double vp=vector1.x*vector2.x+vector1.y*vector2.y;
			if(vp<0)
				nextP=b[0];
			else nextP=b[1];
//			logger.info("==============================");
//			logger.info("a="+a[0].toString()+"  "+a[1].toString());
//			logger.info("mid="+mid.toString());
//			logger.info("b="+b[0].toString()+"  "+b[1].toString());
//			logger.info("nextP="+nextP.toString());
			AQuery nextAquery=new AQuery(nextP, state, category, query, MAX_TOTAL_RESULTS_RETURNED);
			ResultSetD2 nextresult=query(nextAquery);
			countquery++;
			queryset.addAll(nextresult.getPOIs());
			int nextsize=nextresult.getPOIs().size();
			double nextradius=nextP.distance(nextresult.getPOIs().get(nextsize-1).getCoordinate());
			VQP nextcircle=new VQP(nextP, nextradius);
			visitedcircle_Queue.add(nextcircle);
			visited_Queue.addLast(nextcircle);
			Circle Circle = new Circle(nextP, nextradius);
			if (logger.isDebugEnabled() && PaintShapes.painting) {
				PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
				PaintShapes.paint.addCircle(Circle);
				PaintShapes.paint.myRepaint();
			}
			//the arc hasn't been covered
			if(!isinCircle(a[0], nextcircle)){
				if(circles_Insecter(inCircle, nextcircle)){
					IntersectPoint inter1=calculateIntersectPoint(inCircle, nextcircle);
					double d1=a[0].distance(inter1.getIntersectPoint_left());
					double d2=a[0].distance(inter1.getIntersectPoint_right());
					if(d1<d2){
						Coordinate arc1[]=new Coordinate[2];
						arc1[0]=a[0];
						arc1[1]=inter1.getIntersectPoint_left();
						Coordinate arc2[]=new Coordinate[2];
						arc2[0]=a[1];
						arc2[1]=inter1.getIntersectPoint_right();
						uncoverArc.addLast(arc1);
						uncoverArc.addLast(arc2);
					}
					else{
						Coordinate arc3[]=new Coordinate[2];
						arc3[0]=a[1];
						arc3[1]=inter1.getIntersectPoint_left();
						Coordinate arc4[]=new Coordinate[2];
						arc4[0]=a[0];
						arc4[1]=inter1.getIntersectPoint_right();
						uncoverArc.addLast(arc3);
						uncoverArc.addLast(arc4);
					}
				}
				for(int i=0;i<uncoverArc.size();i++){
					Coordinate tt[]=uncoverArc.get(i);
//					logger.info("uncoverArc "+i+"th element is ="+tt[0].toString()+"  "+tt[1].toString());
				}
			}
		}
		inRadius=calculateIncircle(startPoint, firstradius, visited_Queue);
		Circle Circle11 = new Circle(startPoint, inRadius);
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.blueTranslucence;
			PaintShapes.paint.addCircle(Circle11);
			PaintShapes.paint.myRepaint();
		}
		Iterator<APOI>iterator=queryset.iterator();
		while(iterator.hasNext()){
			APOI apoi=iterator.next();
			if(startPoint.distance(apoi.getCoordinate())<=inRadius){
				eligibleset.add(apoi);
			}
		}
		countPoint=eligibleset.size();
	}
	
	private boolean myContain2(Set<VQP> q, Coordinate c) {
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
	
	public Coordinate[] line_circle_intersect(Coordinate startPoint, double radius,Coordinate p){
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
	// to determine whether a circle contains another circle and assume that
		// circle1.radius>circle2.radius(i.e., judge if circle1 contains circle2)
	public boolean circle_contain(VQP circle1, VQP circle2) {
		double d1 = circle1.getCoordinate().distance(circle2.getCoordinate());
		double d2 = circle1.getRadius() - circle2.getRadius();
		if (d1 <= d2)
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

	// determine whether a point is in a circle or not
	public boolean isinCircle(Coordinate p, VQP vqp) {
		boolean flag = false;
		if (vqp.getRadius() > vqp.getCoordinate().distance(p))
			flag = true;
		return flag;
	}
	
	public double calculateIncircle(Coordinate startPoint,double radius,
			LinkedList<VQP> visitedcircle_Queue) {
		Coordinate s=new Coordinate();
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
					VQP firstcircle=new VQP(startPoint, radius);
					if(isinCircle(temP, firstcircle))
						in=true;
					Iterator<VQP> it = visitedcircle_Queue.iterator();
					while (it.hasNext() && !in) {
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
					if (!in) {
						if(minRadius>temP.distance(startPoint)){
						   s=temP;
						}
						minRadius = Math.min(minRadius,	temP.distance(startPoint));
					}
				}
			}
		}
		levelstartPoint=s;
		return minRadius;
	}

}