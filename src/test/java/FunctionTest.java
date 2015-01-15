import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.xml.DOMConfigurator;

import paint.PaintShapes;
import paint.WindowUtilities;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import mo.umac.crawler.Hexagon_optimize;
import mo.umac.crawler.MainYahoo;
import mo.umac.crawler.Strategy;
import mo.umac.metadata.plugins.IntersectPoint;
import mo.umac.metadata.plugins.VQP;
import mo.umac.spatial.Circle;


public class FunctionTest extends Strategy{
	
	@Override
	public void crawl(String state, int category, String query, Envelope envelopeState){
		
	}
	
	public static void main(String arg[]){
		DOMConfigurator.configure(MainYahoo.LOG_PROPERTY_PATH);
		FunctionTest test=new FunctionTest();
		PaintShapes.painting=true;
		//WindowUtilities.openInJFrame(PaintShapes.paint, 1000, 1000);
		test.calling();
	}
	
	public void calling(){
		/**inorder to correct the paint function*/
		Circle tcc=new Circle(new Coordinate(0,0), 0);
		System.out.println("Coordinate="+tcc.getCenter()+"   radius="+tcc.getRadius());
		if(PaintShapes.painting&&logger.isDebugEnabled()){
			PaintShapes.paint.color=PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(tcc);	
			PaintShapes.paint.myRepaint();
		}	
		
		//Hexagon_optimize crawler=new Hexagon_optimize();
		LinkedList<VQP> visitedInfo=new LinkedList<VQP>();
		VQP c=new VQP(new Coordinate(500, 500), 50);
		Circle tc=new Circle(c.getCoordinate(),c.getRadius());
		//System.out.println("Coordinate="+tc.getCenter()+"   radius="+tc.getRadius());
		//if(PaintShapes.painting&&logger.isDebugEnabled()){
		//	PaintShapes.paint.color=PaintShapes.paint.blueTranslucence;
		//	PaintShapes.paint.addCircle(tc);	
		//	PaintShapes.paint.myRepaint();
		//}		
		
		visitedInfo.add(new VQP(new Coordinate(590, 500), 40));
		visitedInfo.add(new VQP(new Coordinate(500, 590), 50));
		visitedInfo.add(new VQP(new Coordinate(410, 500), 60));
		visitedInfo.add(new VQP(new Coordinate(400, 500), 60));
		visitedInfo.add(new VQP(new Coordinate(510, 520), 30));
		
	    VQP t1=new VQP(new Coordinate(0, 0), 13.578584);
        VQP t2=new VQP(new Coordinate(0, 18.3987540000621), 4.82018);
        VQP t3=new VQP(new Coordinate(0,0), 13.578584000006);
        visitedInfo.add(t2);
        System.out.println("visitedInfo="+visitedInfo.size());
        visitedInfo.clear();
        System.out.println("visitedInfo="+visitedInfo.size());
       
        Coordinate p=new Coordinate(0, 13.578583);
        Coordinate p1=new Coordinate(0, 18.3987540000621);
        Hexagon_optimize crawler=new Hexagon_optimize();
       
 //       Coordinate a=new Coordinate(572.258759, 656.962191);
 //       Coordinate b=new Coordinate(572.258758, 656.962191);
 //       System.out.println("distance="+a.distance(b));
        
/*        VQP circle1=new VQP(new Coordinate(-73.68502914116391, 42.63456365243072),0.06170537896056009);
        VQP circletemp1=new VQP(new Coordinate(-73.7474520582137, 42.634563652430714),0.02145117424391179);
         double dist1=circletemp1.getCoordinate().distance(circle1.getCoordinate());
         double dist2=circle1.getRadius()-circletemp1.getRadius();
         double dist3=circle1.getRadius()+circletemp1.getRadius();
         System.out.println("d1="+dist1+"   d2="+dist2+"   d3="+dist3);
         System.out.println("d3-d1="+(dist3-dist1));
         BigDecimal bb=new BigDecimal(dist1);
         dist1=new BigDecimal(dist1).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
         System.out.println("dist1="+dist1);
         System.out.println("bb="+bb.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue());
         
         if(crawler.circles_Insecter(circle1, circletemp1)){
         	System.out.println("intersect!");
         	IntersectPoint inter11=crawler.calculateIntersectPoint(circle1, circletemp1);
         	System.out.println("left="+inter11.getIntersectPoint_left().toString());
        	System.out.println("right="+inter11.getIntersectPoint_right().toString());
         }
*/
        
/*        VQP circle1=new VQP(new Coordinate(1033.639158, 1028.660796), 170.98036880333964);
        VQP	circle2=new VQP(new Coordinate(1102.651834, 988.816309),  47.43139255776436);
        Envelope envelopeState=new Envelope(0, 1000, 0, 1000);
        if(crawler.outspace(circle2, envelopeState))
        	System.out.println("out of space!");
        if(crawler.circles_Insecter(circle1, circle2))
        	System.out.println("intersect!");
        IntersectPoint inter=crawler.calculateIntersectPoint(circle1, circle2);
        System.out.println("left="+inter.getIntersectPoint_left().toString()+
        		"  right="+inter.getIntersectPoint_right().toString());
*/
        LinkedList<VQP> visited_queue=new LinkedList<VQP>();
//        radius=68.5937249258495
//        		level=1
//        		(500.0, 615.2435821524158, NaN)radius=94.86928489032088
//        		(599.803869767111, 557.621791076208, NaN)radius=68.5937249258495
//        		(599.803869767111, 442.3782089237921, NaN)radius=80.95326480130609
//        		(500.0, 384.7564178475842, NaN)radius=81.49246811113481
//        		(400.196130232889, 442.3782089237921, NaN)radius=68.5937249258495
//        		(400.196130232889, 557.621791076208, NaN)radius=82.77367398625933
//        double rr=36.53639487072362;
//       double dd=visited_queue.get(0).getCoordinate().distance(visited_queue.get(1).getCoordinate());
//        System.out.println("dd="+dd);
//        double dr=calculateIncircle(pp, rr, visited_queue);
//        System.out.println("dr="+dr);
        System.out.println("end calling!");
	}
	private boolean myContain2(LinkedList<VQP> q, Coordinate c) {
		boolean flag=false;
		System.out.println(q.size());
		for (int i = 0; i < q.size()&&!flag; i++) {
			Coordinate one = q.get(i).getCoordinate();
			if (c.equals2D(one)) {
				 flag=true;
			}
		}
		return flag;
	}
	
	public IntersectPoint calculateIntersectPoint(VQP circle1, VQP circle2){
		double a=circle1.getCoordinate().x;
		double b=circle1.getCoordinate().y;
		double c=circle2.getCoordinate().x;
		double d=circle2.getCoordinate().y;
		double r0=circle1.getRadius();
		double r1=circle2.getRadius();
		double D=circle1.getCoordinate().distance(circle2.getCoordinate());
		double e=0.25*Math.sqrt((D+r0+r1)*(D+r0-r1)*(D-r0+r1)*(-D+r0+r1));
		double Xc=(a+c)/2+(c-a)*(r0*r0-r1*r1)/(2*D*D)+2*(b-d)*e/D*D;
		double Xd=(a+c)/2+(c-a)*(r0*r0-r1*r1)/(2*D*D)-2*(b-d)*e/D*D;
		double Yc=(b+d)/2+(d-b)*(r0*r0-r1*r1)/2*D*D-2*(a-c)*e/D*D;
		double Yd=(b+d)/2+(d-b)*(r0*r0-r1*r1)/2*D*D+2*(a-c)*e/D*D;
		IntersectPoint intersect = new IntersectPoint();
		if (Math.abs(Xc - Xd) < 1e-6 && Math.abs(Yc - Yd) < 1e-6) {
			Coordinate intersectP1 = new Coordinate(Xc, Yc);
			intersect = new IntersectPoint(circle1.getCoordinate(), r0, circle2.getCoordinate(), r1, intersectP1, intersectP1);
		} else {
			Coordinate intersectP1 = new Coordinate(Xc, Yc);
			Coordinate intersectP2 = new Coordinate(Xd, Yd);
			intersect = new IntersectPoint(circle1.getCoordinate(), r0, circle2.getCoordinate(), r1, intersectP1,
					intersectP2);
		}
		return intersect;
	}
	
	public double calculateIncircle(Coordinate startPoint,double radius, LinkedList<VQP>visitedcircle_Queue) {
		double minRadius=1e308;
		for(int i=0;i<visitedcircle_Queue.size()-1;i++){
			VQP circle1=visitedcircle_Queue.get(i);
			for(int j=i+1;j<visitedcircle_Queue.size();j++){
				VQP circle2=visitedcircle_Queue.get(j);
			    if(Math.abs(circle1.getCoordinate().distance(circle2.getCoordinate())-0.97*radius*Math.sqrt(3))<1e-6){
			    		IntersectPoint inter=calculateIntersectPoint(circle1, circle2);
			    		double d1=startPoint.distance(inter.getIntersectPoint_left());
			    		double d2=startPoint.distance(inter.getIntersectPoint_right());
			    		double d=Math.max(d1, d2);
			    		System.out.println("d="+d);
			    		minRadius=Math.min(minRadius, d);
			    }
			}
		}
		return minRadius;
	}

}
