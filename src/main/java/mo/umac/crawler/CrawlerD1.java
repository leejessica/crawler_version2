/**
 * 
 */
package mo.umac.crawler;

import java.util.List;

import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.ResultSetD1;
import mo.umac.metadata.ResultSetD2;
import mo.umac.spatial.Circle;

import org.apache.log4j.Logger;

import paint.PaintShapes;
import utils.GeoOperator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geomgraph.Position;

/**
 * The one dimensional crawler.
 * 
 * @author Kate
 */
public class CrawlerD1 extends Strategy {

	// public static Logger logger = Logger.getLogger(CrawlerD1.class.getName());

	public static ResultSetD1 oneDimCrawl(String state, int category, String query, LineSegment middleLine) {
		ResultSetD1 finalResultSet = new ResultSetD1();
		Coordinate up = middleLine.p0;
		Coordinate down = middleLine.p1;
		if (logger.isDebugEnabled()) {
			logger.debug("up = " + up.toString());
			logger.debug("down = " + down.toString());
		}
		// query the one end point
		// TODO only return
		AQuery aQuery = new AQuery(up, state, category, query, MAX_TOTAL_RESULTS_RETURNED);
		ResultSetD2 resultSet = query(aQuery);
		if (logger.isDebugEnabled()) {
			logger.debug("resultSet.getPOIs().size() = " + resultSet.getPOIs().size());
		}
		List<APOI> resultPoints = resultSet.getPOIs();

		// if (logger.isDebugEnabled()) {
		// for (int i = 0; i < resultPoints.size(); i++) {
		// APOI aPoint = resultPoints.get(i);
		// logger.debug("APoint: " + aPoint.getId() + ", ["
		// + aPoint.getCoordinate().toString() + "]");
		// }
		// }

		// farthest point lower than the up point
		Coordinate farthestCoordinate = farthest(resultSet, up, true);
		if (farthestCoordinate == null) {
			logger.error("farestest point is null");
			// farthestCoordinate = farthest(resultSet, up, true);
		}
		double radius = up.distance(farthestCoordinate);
		if (logger.isDebugEnabled()) {
			logger.debug("farthestCoordinate = " + farthestCoordinate.toString());
			logger.debug("radius = " + radius);
		}
		Circle aCircle = new Circle(up, radius);
		resultSet.addACircle(aCircle);
		double newUp = up.y + radius;
		if (logger.isDebugEnabled()) {
			logger.debug("new up = " + newUp);
		}
		//
		if (logger.isDebugEnabled() && PaintShapes.painting) {

			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(aCircle);
			PaintShapes.paint.myRepaint();
		}
		addResults(up, middleLine, finalResultSet, resultSet);
		if (logger.isDebugEnabled()) {
			logger.debug("middleLine.getLength() = " + middleLine.getLength());
		}
		if (radius >= middleLine.getLength()) {
			// finished crawling
			if (logger.isDebugEnabled()) {
				logger.debug("finished crawling");
			}
			return finalResultSet;
		}

		// query another end point
		aQuery = new AQuery(down, state, category, query, MAX_TOTAL_RESULTS_RETURNED);
		resultSet = query(aQuery);
		if (logger.isDebugEnabled()) {
			logger.debug("resultSet.getPOIs().size() = " + resultSet.getPOIs().size());
		}
		resultPoints = resultSet.getPOIs();

		// if (logger.isDebugEnabled()) {
		// for (int i = 0; i < resultPoints.size(); i++) {
		// APOI aPoint = resultPoints.get(i);
		// logger.debug("APoint: " + aPoint.getId() + ", ["
		// + aPoint.getCoordinate().toString() + "]");
		// }
		// }

		// farthest point lower than the up point
		farthestCoordinate = farthest(resultSet, down, false);
		if (farthestCoordinate == null) {
			logger.error("farestest point is null");
			// farthestCoordinate = farthest(resultSet, up, true);
		}
		radius = down.distance(farthestCoordinate);
		if (logger.isDebugEnabled()) {
			logger.debug("farthestCoordinate = " + farthestCoordinate.toString());
			logger.debug("radius = " + radius);
		}
		aCircle = new Circle(down, radius);
		resultSet.addACircle(aCircle);
		double newDown = down.y - radius;
		if (logger.isDebugEnabled()) {
			logger.debug("new down = " + newDown);
		}
		//
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(aCircle);
			PaintShapes.paint.myRepaint();
		}
		addResults(down, middleLine, finalResultSet, resultSet);
		if (logger.isDebugEnabled()) {
			logger.debug("middleLine.getLength() = " + middleLine.getLength());
		}
		if (radius >= down.y - newUp) {
			// finished crawling
			if (logger.isDebugEnabled()) {
				logger.debug("finished crawling");
			}
			return finalResultSet;
		}

		LineSegment newMiddleLine = new LineSegment(up.x, newUp, up.x, newDown);
		ResultSetD1 middleResultSet = oneDimCrawlFromMiddle(state, category, query, newMiddleLine);
		addResults(finalResultSet, middleResultSet);
		return finalResultSet;
	}

	private void queryOnePointOneD1() {

	}

	/**
	 * Begin at the center of the line
	 * 
	 * @param state
	 * @param category
	 * @param query
	 * @param envelopeState
	 * @param middleLine
	 * @return
	 */
	public static ResultSetD1 oneDimCrawlFromMiddle(String state, int category, String query, LineSegment middleLine) {
		ResultSetD1 finalResultSet = new ResultSetD1();
		Coordinate up = middleLine.p0;
		Coordinate down = middleLine.p1;
		Coordinate center = middleLine.midPoint();
		// add at 2013-08-21

		if (logger.isDebugEnabled()) {
			logger.debug("up = " + up.toString());
			logger.debug("down = " + down.toString());
			logger.debug("center = " + center.toString());
		}
		AQuery aQuery = new AQuery(center, state, category, query, MAX_TOTAL_RESULTS_RETURNED);
		ResultSetD2 resultSet = query(aQuery);
		if (logger.isDebugEnabled()) {
			logger.debug("resultSet.getPOIs().size() = " + resultSet.getPOIs().size());
		}
		List<APOI> resultPoints = resultSet.getPOIs();

		// if (logger.isDebugEnabled()) {
		// for (int i = 0; i < resultPoints.size(); i++) {
		// APOI aPoint = resultPoints.get(i);
		// logger.debug("APoint: " + aPoint.getId() + ", ["
		// + aPoint.getCoordinate().toString() + "]");
		// }
		// }

		Coordinate farthestCoordinate = farthest(resultSet);
		double radius = center.distance(farthestCoordinate);
		if (logger.isDebugEnabled()) {
			logger.debug("farthestCoordinate = " + farthestCoordinate.toString());
			logger.debug("radius = " + radius);
		}
		Circle aCircle = new Circle(center, radius);
		resultSet.addACircle(aCircle);
		//
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(aCircle);
			PaintShapes.paint.myRepaint();
		}
		addResults(center, middleLine, finalResultSet, resultSet);
		if (logger.isDebugEnabled()) {
			logger.debug("middleLine.getLength() / 2 = " + middleLine.getLength() / 2);
		}
		if (radius >= middleLine.getLength() / 2) {
			// finished crawling
			if (logger.isDebugEnabled()) {
				logger.debug("finished crawling");
			}
			return finalResultSet;
		}

		// recursively crawl
		// upper
		// Coordinate newRight = middleLine.pointAlongOffset(0.5, -radius);
		Coordinate newDown = newDown(center, radius);
		LineSegment upperLine = new LineSegment(up, newDown);
		if (logger.isDebugEnabled()) {
			logger.debug("newDown: " + newDown.toString());
			logger.debug("upperLine: " + upperLine.toString());
		}
		ResultSetD1 newLeftResultSet = oneDimCrawlFromMiddle(state, category, query, upperLine);
		addResults(finalResultSet, newLeftResultSet);
		// lower
		// Coordinate newLeft = middleLine.pointAlongOffset(0.5, radius);
		Coordinate newUp = newUp(center, radius);
		LineSegment lowerLine = new LineSegment(newUp, down);
		if (logger.isDebugEnabled()) {
			logger.debug("newUp: " + newUp.toString());
			logger.debug("lowerLine: " + lowerLine.toString());
		}
		ResultSetD1 newRightResultSet = oneDimCrawlFromMiddle(state, category, query, lowerLine);
		addResults(finalResultSet, newRightResultSet);

		return finalResultSet;
	}

	/**
	 * This line is perpendicular, so it has the same x as center.x
	 * 
	 * @param center
	 * @param radius
	 * @return
	 */
	private static Coordinate newDown(Coordinate center, double radius) {
		Coordinate newDown = new Coordinate(center.x, center.y - radius);
		return newDown;
	}

	private static Coordinate newUp(Coordinate center, double radius) {
		Coordinate newDown = new Coordinate(center.x, center.y + radius);
		return newDown;
	}

	public static Coordinate farthest(ResultSetD2 resultSet) {
		Coordinate farthestCoordinate;
		int size = resultSet.getPOIs().size();
		if (size == 0) {
			return null;
		} else {
			APOI farthestPOI = resultSet.getPOIs().get(size - 1);
			farthestCoordinate = farthestPOI.getCoordinate();
		}
		return farthestCoordinate;
	}

	/**
	 * Find the farthest point which is lower/higher than the point p
	 * 
	 * @param resultSet
	 *            , already ranged by the distance
	 * @param p
	 * @param lower
	 * @return
	 */
	private static Coordinate farthest(ResultSetD2 resultSet, Coordinate p, boolean lower) {
		int size = resultSet.getPOIs().size();
		if (size == 0) {
			return null;
		} else {
			// for (int i = size - 1; i >= 0; i--) {
			// APOI farthestPOI = resultSet.getPOIs().get(i);
			// Coordinate c = farthestPOI.getCoordinate();
			// if (lower) {
			// if (c.y > p.y) {
			// return c;
			// }
			// } else {
			// if (c.y < p.y) {
			// return c;
			// }
			// }
			// }
			// add at 2013-10-01
			// FIXME revised farthest!!!
			return resultSet.getPOIs().get(size - 1).getCoordinate();
		}
	}

	private static void addResults(Coordinate center, LineSegment line, ResultSetD1 finalResultSet, ResultSetD2 resultSet) {
		List<APOI> pois = resultSet.getPOIs();
		for (int i = 0; i < pois.size(); i++) {
			APOI poi = pois.get(i);
			int position = GeoOperator.findPosition(line, poi.getCoordinate());
			switch (position) {
			case Position.LEFT:
				finalResultSet.getLeftPOIs().add(poi);
				break;
			case Position.RIGHT:
				finalResultSet.getRightPOIs().add(poi);
				break;
			case Position.ON:
				finalResultSet.getOnPOIs().add(poi);
				break;
			}
		}
		finalResultSet.addAll(finalResultSet.getCircles(), resultSet.getCircles());
	}

	private static void addResults(ResultSetD1 finalResultSet, ResultSetD1 newResultSet) {
		finalResultSet.addAll(finalResultSet.getLeftPOIs(), newResultSet.getLeftPOIs());
		finalResultSet.addAll(finalResultSet.getRightPOIs(), newResultSet.getRightPOIs());
		finalResultSet.addAll(finalResultSet.getCircles(), newResultSet.getCircles());
	}

	@Override
	public void crawl(String state, int category, String query, Envelope envelopeState) {
		// TODO Auto-generated method stub

	}

	public static ResultSetD1 oneDimCrawlProject(String state, int category, String query, LineSegment middleLine) {
		ResultSetD1 finalResultSet = new ResultSetD1();
		Coordinate up = middleLine.p0;
		Coordinate down = middleLine.p1;
		Coordinate center = middleLine.midPoint();
		AQuery aQuery = new AQuery(center, state, category, query, MAX_TOTAL_RESULTS_RETURNED);
		ResultSetD2 resultSet = query(aQuery);
		//
		Coordinate farthestCoordinate = farthest(resultSet);
		double radius = center.distance(farthestCoordinate);
		//
		double radiusProject = farthestProject(resultSet, center);
		Circle aCircle = new Circle(center, radius);
		resultSet.addACircle(aCircle);
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(aCircle);
			PaintShapes.paint.myRepaint();
		}
		//
		addResults(center, middleLine, finalResultSet, resultSet);
		if (radiusProject >= middleLine.getLength() / 2) {
			// finished crawling
			return finalResultSet;
		}

		// recursively crawl
		// upper
		// Coordinate newRight = middleLine.pointAlongOffset(0.5, -radius);
		Coordinate newDown = newDown(center, radiusProject);
		LineSegment upperLine = new LineSegment(up, newDown);
		ResultSetD1 newLeftResultSet = oneDimCrawlProject(state, category, query, upperLine);
		addResults(finalResultSet, newLeftResultSet);
		// lower
		// Coordinate newLeft = middleLine.pointAlongOffset(0.5, radius);
		Coordinate newUp = newUp(center, radiusProject);
		LineSegment lowerLine = new LineSegment(newUp, down);
		ResultSetD1 newRightResultSet = oneDimCrawlProject(state, category, query, lowerLine);
		addResults(finalResultSet, newRightResultSet);

		return finalResultSet;
	}

	private static double farthestProject(ResultSetD2 resultSet, Coordinate center) {
		double maxRadius = 0;
		int size = resultSet.getPOIs().size();
		if (size == 0) {
			return 0;
		}
		for (int i = 0; i < size; i++) {
			APOI poi = resultSet.getPOIs().get(i);
			double tempRadius = Math.abs(poi.getCoordinate().y - center.y);
			if(tempRadius > maxRadius){
				maxRadius = tempRadius;
			}
			
		}
		return maxRadius;
	}

}
