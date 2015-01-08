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
		//Iterator<VQP>it=visitedInfo.iterator();
		//while(it.hasNext()){
		//	VQP tc1=it.next();
		//	System.out.println("Coordinate="+tc1.getCoordinate()+"   radius="+tc1.getRadius());
		//	Circle tc11=new Circle(tc1.getCoordinate(),tc1.getRadius());
		//	if(PaintShapes.painting&&logger.isDebugEnabled()){
		//		PaintShapes.paint.addCircle(tc11);	
		//		PaintShapes.paint.myRepaint();
		//	}		
		//}
	    VQP t1=new VQP(new Coordinate(0, 0), 13.578584);
        VQP t2=new VQP(new Coordinate(0, 18.3987540000621), 4.82018);
        VQP t3=new VQP(new Coordinate(0,0), 13.578584000006);
        visitedInfo.add(t2);
       
        Coordinate p=new Coordinate(0, 13.578583);
        Coordinate p1=new Coordinate(0, 18.3987540000621);
        Hexagon_optimize crawler=new Hexagon_optimize();
        //if(crawler.circles_Insecter(t1, t2)){
        //	System.out.println("intersect!");
        //	IntersectPoint q1=crawler.calculateIntersectPoint(t1, t2);
        //	System.out.println("left="+q1.getIntersectPoint_left().toString()
       // 			+"   right="+q1.getIntersectPoint_right().toString());
       // 	if(crawler.isinCircle(q1.getIntersectPoint_left(),t3))
        //		System.out.println("is in circle!");
       // }
       // if(crawler.isinCircle(p, t1))
        //	System.out.println("is in circle!");
        //double x=0.0000000000003;
       // double y=0.00000000000002;
       // if(x!=y)
        //	System.out.println("x!=y");
       // Coordinate p2=t2.getCoordinate();
       // if(p1.equals2D(p2))
       // 	System.out.println("equal");
       // if(myContain2(visitedInfo, p1))
       // 	System.out.println("contain!");		
       
      //  VQP c1=new VQP(new Coordinate(-73.355835, 42.746632),147.36656275248086);
       // VQP c2=new VQP(new Coordinate(-73.355835, 290.3356148033616),147.36656275248086);
       // IntersectPoint inter1=crawler.calculateIntersectPoint(c1, c2);
       // if(crawler.circles_Insecter(c1, c2))
     //   	System.out.println("true!");
      //  System.out.println(inter1.getIntersectPoint_left().toString()+"; "+inter1.getIntersectPoint_right().toString());
        
      //  VQP c1=new VQP(new Coordinate(0, 0), 13.4678251);
      //  VQP c2=new VQP(new Coordinate(0, 5.8257795), 7.6420456);
      //  if(crawler.circle_contain(c1, c2))
      //  	System.out.println("circle_contain!");
        
       VQP circle1=new VQP(new Coordinate(-73.68502914116391, 42.63456365243072),0.06170537896056009);
       VQP circletemp1=new VQP(new Coordinate(-73.7474520582137, 42.634563652430714),0.02145117424391179);
        double dist1=circletemp1.getCoordinate().distance(circle1.getCoordinate());
        double dist2=circle1.getRadius()-circletemp1.getRadius();
        double dist3=circle1.getRadius()+circletemp1.getRadius();
        System.out.println("d1="+dist1+"   d2="+dist2+"   d3="+dist3);
        if(crawler.circles_Insecter(circle1, circletemp1)){
        	System.out.println("intersect!");
        	IntersectPoint inter11=crawler.calculateIntersectPoint(circle1, circletemp1);
        	System.out.println("left="+inter11.getIntersectPoint_left().toString());
       	System.out.println("right="+inter11.getIntersectPoint_right().toString());
        }
        
        //Coordinate tt1=new Coordinate(-73.74305287532752000000200000000001,  42.6345636524307140057);
       // Coordinate tt2=new Coordinate(-73.74305287532752000000200000000001,  42.6345636524307140057);
       // if(tt1.equals(tt2))
       // 	System.out.println("equal!");
        
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

}
