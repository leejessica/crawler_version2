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
		PaintShapes.painting=false;
	//	WindowUtilities.openInJFrame(PaintShapes.paint, 1000, 1000);
		test.calling();
	}
	
	public void calling(){
		/**inorder to correct the paint function*/
		Circle tcc=new Circle(new Coordinate(-73,42), 0);
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
		double coverradius=0.21069397984662597;						
		double hex_radius=0.10547792595767536;
        IntersectPoint inter=calculateIntersectPoint(visitedInfo.get(1), visitedInfo.get(3));
        if(isinCircle(inter.getIntersectPoint_right(),visitedInfo.get(0))){
        	System.out.println("in circle");
        }
   //     double dd1=inter.getIntersectPoint_left().distance(point);
   //     double dd2=inter.getIntersectPoint_right().distance(point);
  //      System.out.println("dd1="+dd1+"  dd2="+dd2);
	
		//test the visitedInfo
/*		for(int i=0;i<visitedInfo.size()-1;i++){
			VQP circle1=visitedInfo.get(i);			
			for(int j=i+1;j<visitedInfo.size();j++){
				VQP circle2=visitedInfo.get(j);
				if(Math.abs(circle1.getCoordinate().distance(circle2.getCoordinate())-0.97*hex_radius*Math.sqrt(3))<1e-6){
					double dd1=circle1.getCoordinate().distance(circle2.getCoordinate());
					double dd2=circle1.getRadius()+circle2.getRadius();
					double dd3=Math.abs(circle1.getRadius()-circle2.getRadius());
					System.out.println("circle1="+circle1.getCoordinate().toString()+"  radius="+circle1.getRadius());
					System.out.println("circle2="+circle2.getCoordinate().toString()+"  radius="+circle2.getRadius());
					System.out.println("dd1="+dd1+"   dd2="+dd2+"   dd3="+dd3);
					System.out.println("================================");
					
			}
			   
			}
		}
*/
		
 //       Hexagon_optimize crawler=new Hexagon_optimize();
//          coverradius=calculateIncircle(point, hex_radius, visitedInfo);
 //        System.out.println("coverradius="+coverradius);
 
        System.out.println("size="+Strategy.TOTAL_POINTS);
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
			System.out.println("circle1="+circle1.getCoordinate().toString()+"  radius="+circle1.getRadius());
			for(int j=i+1;j<visitedcircle_Queue.size();j++){
				VQP circle2=visitedcircle_Queue.get(j);
				
				double dr=circle1.getRadius()-circle2.getRadius();
				//circle1 contain circle2, no need processing circle2
				if(dr>0&&circle_contain(circle1, circle2)){
					System.out.println("circle2="+circle2.getCoordinate().toString()+
							"  radius="+circle2.getRadius());
					System.out.println("circle1 contains circle2");
					continue;				
				}
				//circle2 contain circle1, no need processing circle1
				else if(dr<0&&circle_contain(circle2, circle1)){
					System.out.println("circle2="+circle2.getCoordinate().toString()+
							"  radius="+circle2.getRadius());
					System.out.println("circle2 contains circle1");
					break;
					}
				else if(circles_Insecter(circle1, circle2)){
					System.out.println("circle2="+circle2.getCoordinate().toString()+
							"  radius="+circle2.getRadius());
					System.out.println("circle1 intersect with circle2");
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
								//for test
								if(in)
									System.out.println("circle3="+circle3.getCoordinate().toString()
											+"  radius="+circle3.getRadius());	
							}
						}
					}
					if(!in){
						minRadius=Math.min(minRadius, temP.distance(startPoint));
						System.out.println("minRadius="+minRadius);
					}
				}
			}
			System.out.println("==========================");
		}
		return minRadius;
	}
	
	public boolean circle_contain(VQP circle1, VQP circle2) {
		double d1 = circle1.getCoordinate().distance(circle2.getCoordinate());
		double d2 = circle1.getRadius() - circle2.getRadius();
		if (d1 <=d2 )
			return true;
		return false;
	}
	
	// determine whether a point is in a circle or not
		public boolean isinCircle(Coordinate p, VQP vqp) {
			boolean flag = false;
			if(vqp.getRadius()>vqp.getCoordinate().distance(p))
				flag=true;
			return flag;
		}

}
