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
		WindowUtilities.openInJFrame(PaintShapes.paint, 1000, 1000);
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
		System.out.println("Coordinate="+tc.getCenter()+"   radius="+tc.getRadius());
		if(PaintShapes.painting&&logger.isDebugEnabled()){
			PaintShapes.paint.color=PaintShapes.paint.blueTranslucence;
			PaintShapes.paint.addCircle(tc);	
			PaintShapes.paint.myRepaint();
		}		
		
		visitedInfo.add(new VQP(new Coordinate(590, 500), 40));
		visitedInfo.add(new VQP(new Coordinate(500, 590), 50));
		visitedInfo.add(new VQP(new Coordinate(410, 500), 60));
		visitedInfo.add(new VQP(new Coordinate(400, 500), 60));
		visitedInfo.add(new VQP(new Coordinate(510, 520), 30));
		Iterator<VQP>it=visitedInfo.iterator();
		while(it.hasNext()){
			VQP tc1=it.next();
			System.out.println("Coordinate="+tc1.getCoordinate()+"   radius="+tc1.getRadius());
			Circle tc11=new Circle(tc1.getCoordinate(),tc1.getRadius());
			if(PaintShapes.painting&&logger.isDebugEnabled()){
				PaintShapes.paint.color=PaintShapes.paint.redTranslucence;
				PaintShapes.paint.addCircle(tc11);	
				PaintShapes.paint.myRepaint();
			}		
		}
		
		
	}

}
