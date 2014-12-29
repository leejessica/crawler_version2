/**
 * 
 */
package mo.umac.crawler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import mo.umac.uscensus.USDensity;

import org.apache.log4j.Logger;

import paint.PaintShapes;

import com.vividsolutions.jts.geom.Envelope;

/**
 * split the region by the external knowledge
 * 
 * @author kate
 */
public class AlgoPartition extends Strategy {
	public static Logger logger = Logger.getLogger(AlgoPartition.class.getName());

	public static String clusterRegionFile = null;
	public static ArrayList<Envelope> mbrList = null;

	public AlgoPartition() {
		super();
//		logger.info("---------AlgoPartition------------");
	}

	@Override
	public void crawl(String state, int category, String query, Envelope envelopeState) {
		// ArrayList<Envelope> list = AlgoPartition.getOKEnvelopes();
		ArrayList<Envelope> list;
		if(clusterRegionFile != null && mbrList == null){
			logger.info("reading mbrList from a file");
			list = readPartitionedEnvelopes(clusterRegionFile);
			
		} else {
//			logger.info("reading mbrList from a list");
			list = mbrList; 
		}
		// painting
		if (PaintShapes.painting) {
			PaintShapes.paint.color = Color.RED;
			for (int i = 0; i < list.size(); i++) {
				Envelope envelope = list.get(i);
				PaintShapes.addRectangleLine(envelope);
			}
			PaintShapes.paint.myRepaint();
		}
		for (int i = 0; i < list.size(); i++) {
			Envelope envelope = list.get(i);
//			AlgoSlice sc = new AlgoSlice();
			AlgoProjection sc = new AlgoProjection();
			sc.crawl(state, category, query, envelope);
//			logger.info("crawling " + envelope + ": " + Strategy.countNumQueries);
		}

	}

	public static ArrayList<Envelope> readPartitionedEnvelopes(String fileName) {
		ArrayList<Envelope> list = new ArrayList<Envelope>();
		list = USDensity.readPartition(fileName);
		return list;
	}

	public static List getNYEnList() {
		// NY: Env[-79.76259 : -71.777491, 40.477399 : 45.015865]
		return null;
	}

	public static ArrayList<Envelope> getOKEnvelopes() {
		// OK: Env[-103.002455 : -94.430662, 33.615787 : 37.002311999999996]
		/**
		 * 1) partition by the region:
		 * 1: [-103.002455:-100, 36.48:37.002311999999996],
		 * 2: [-100:-94.430662, 33.615787:37.002311999999996]
		 */
		//
		/**
		 * 2) partition by density
		 * [-97.6619662:-97.3635542, 35.3743439:35.6130527]
		 * // [-96.1299444:-95.7177651,35.9353284:36.2231295]
		 */

		Envelope e1 = new Envelope(-103.002455, -100, 36.48, 37.002311999999996);
		// sparse:
		Envelope e21 = new Envelope(-100, -97.6619662, 35.6130527, 37.002311999999996);
		Envelope e22 = new Envelope(-97.3635542, -94.430662, 33.615787, 35.6130527);
		Envelope e23 = new Envelope(-100, -97.3635542, 33.615787, 35.3743439);
		Envelope e24 = new Envelope(-95.7177651, -94.430662, 35.6130527, 37.002311999999996);
		Envelope e25 = new Envelope(-96.1299444, -95.7177651, 36.2231295, 37.002311999999996);
		Envelope e26 = new Envelope(-96.1299444, -95.7177651, 35.6130527, 35.9353284);
		Envelope e27 = new Envelope(-100, -97.6619662, 35.3743439, 35.6130527);
		// dense:
		Envelope e28 = new Envelope(-97.6619662, -97.3635542, 35.3743439, 35.6130527);
		Envelope e29 = new Envelope(-96.1299444, -95.7177651, 35.9353284, 36.2231295);
		ArrayList<Envelope> list = new ArrayList<Envelope>();
		list.add(e1);
		list.add(e21);
		list.add(e22);
		list.add(e23);
		list.add(e24);
		list.add(e25);
		list.add(e26);
		list.add(e27);
		list.add(e28);
		list.add(e29);
		return list;

	}
}
