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
		visitedInfo.add(new VQP(new Coordinate(-73.72273322396315, 43.79429422140348),0.3253699639590399));
		visitedInfo.add(new VQP(new Coordinate(-73.56926284169472, 43.70568805488818),0.10547792595767536));
		visitedInfo.add(new VQP(new Coordinate(-73.87620360623157, 43.70568805488818),0.10547792595767536));
		visitedInfo.add(new VQP(new Coordinate(-73.4157924594263, 43.61708188837287), 0.10547792595767536));
		visitedInfo.add(new VQP(new Coordinate(-73.4157924594263, 43.43986955534226), 0.10547792595767536));
		visitedInfo.add(new VQP(new Coordinate(-73.4157924594263, 43.26265722231165), 0.10547792595767536));
		visitedInfo.add(new VQP(new Coordinate(-73.56926284169472, 43.17405105579635), 0.10547792595767536));
		visitedInfo.add(new VQP(new Coordinate(-73.72273322396315, 43.08544488928104), 0.10547792595767536));
		visitedInfo.add(new VQP(new Coordinate(-73.87620360623157, 43.17405105579635), 0.10547792595767536));
		visitedInfo.add(new VQP(new Coordinate(-74.0296739885, 43.26265722231165), 0.28718249115063094));
		visitedInfo.add(new VQP(new Coordinate(-74.0296739885, 43.43986955534226), 0.10547792595767536));
		visitedInfo.add(new VQP(new Coordinate(-74.0296739885, 43.61708188837287), 0.3679837037074432));
		Coordinate point=new Coordinate(-73.72273322396315, 43.43986955534226);
		double crawl_radius=0.10547792595767536;
		System.out.println("visitedInfo.size="+visitedInfo.size());
        Hexagon_optimize crawler=new Hexagon_optimize();
        double coverradius=calculateIncircle(point, crawl_radius, visitedInfo);
        System.out.println("coverradius="+coverradius);
 

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
	
	public double calculateIncircle(Coordinate startPoint,double radius, LinkedList<VQP>visitedcircle_Queue) {
		double minRadius=1e308;
		for(int i=0;i<visitedcircle_Queue.size()-1;i++){
			VQP circle1=visitedcircle_Queue.get(i);
			
			for(int j=i+1;j<visitedcircle_Queue.size();j++){
				VQP circle2=visitedcircle_Queue.get(j);
				if(circles_Insecter(circle1, circle2)){
					System.out.println("intersect!");
					System.out.println("circle1="+circle1.getCoordinate().toString()+"  radius="+circle1.getRadius());
					System.out.println("circle2="+circle2.getCoordinate().toString()+"  radius="+circle2.getRadius());
					double difference=Math.abs(circle1.getCoordinate().distance(circle2.getCoordinate())-0.97*radius*Math.sqrt(3));
					System.out.println("difference="+difference);	
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
		}
		return minRadius;
	}

}
