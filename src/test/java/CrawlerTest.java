
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import mo.umac.crawler.AlgoSlice;
import mo.umac.crawler.Hexagon;
import mo.umac.crawler.Hexagon_Optimize6;
import mo.umac.crawler.Hexagon_optimize;
import mo.umac.crawler.Hexagon_optimize2;
import mo.umac.crawler.Hexagon_optimize2_2;
import mo.umac.crawler.Hexagon_optimize3;
import mo.umac.crawler.Hexagon_optimize3_2;
import mo.umac.crawler.Hexagon_optimize4;
import mo.umac.crawler.Hexagon_optimize4_2;
import mo.umac.crawler.Hexagon_optimize5;
import mo.umac.crawler.Hexagon_optimize5_2;
import mo.umac.crawler.Hexagon_optimize_2;
import mo.umac.crawler.MainYahoo;
import mo.umac.crawler.Periphery_Optimize;
import mo.umac.crawler.Periphery_Optimize2;
import mo.umac.crawler.Strategy;
import mo.umac.db.DBInMemory;
import mo.umac.db.H2DB;
import mo.umac.metadata.APOI;
import mo.umac.metadata.Rating;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.xml.DOMConfigurator;

import paint.PaintShapes;
import paint.WindowUtilities;
import utils.FileOperator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class CrawlerTest extends Strategy/* extends SliceCrawler */{

	@Override
	protected void crawl(String state, int category, String query, Envelope envelopeState) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DOMConfigurator.configure(MainYahoo.LOG_PROPERTY_PATH);
		CrawlerTest test = new CrawlerTest();
		PaintShapes.painting = true;
		if(PaintShapes.painting){
		WindowUtilities.openInJFrame(PaintShapes.paint, 1000, 1000);
		}
		test.calling();

	}

	public void calling() {
		/************************* Change these lines *************************/
		Strategy.CATEGORY_ID_PATH = "./src/main/resources/cat_id.txt";
		// YahooLocalCrawlerStrategy crawlerStrategy = new QuadTreeCrawler();
		// SliceCrawler crawler = new SliceCrawler();
		// HexagonCrawler crawler = new HexagonCrawler();
		//AlgoSlice crawler = new AlgoSlice();
		
//		Hexagon crawler=new Hexagon();
		//Binary_estimateR crawler=new Binary_estimateR();
		//Binary_wholeSpace crawler=new Binary_wholeSpace();
		//PeripheryQuery crawler=new PeripheryQuery();
//		Periphery_Optimize crawler=new Periphery_Optimize();
//	    Periphery_Optimize2 crawler=new Periphery_Optimize2();	
//		Hexagon_optimize crawler=new Hexagon_optimize();
//		Hexagon_optimize_2 crawler=new Hexagon_optimize_2();
//		Hexagon_optimize2 crawler=new Hexagon_optimize2();
//		Hexagon_optimize2_2 crawler=new Hexagon_optimize2_2();
//		Hexagon_optimize4 crawler=new Hexagon_optimize4();
//		Hexagon_optimize4_2 crawler=new Hexagon_optimize4_2();
//		Hexagon_optimize3_2 crawler=new Hexagon_optimize3_2();
//		Hexagon_optimize3 crawler=new Hexagon_optimize3();
//		Hexagon_optimize5_2 crawler=new Hexagon_optimize5_2();
//		Hexagon_optimize5 crawler=new Hexagon_optimize5();
		Hexagon_Optimize6 crawler=new Hexagon_Optimize6();
		
		String state = "NY";
		int categoryID = 96926236;
		String category = "Restaurants";
		Envelope envelopeECEF = new Envelope(0, 1000, 0, 1000);	
		String testSource = "../crawler-data/yahoolocal-h2/source";
		String testTarget = "../crawler-data/yahoolocal-h2/target";
		//
		int numItems = 2000;
		int topK = 10;
		Strategy.MAX_TOTAL_RESULTS_RETURNED = topK;
		//
		Strategy.categoryIDMap = FileOperator.readCategoryID(CATEGORY_ID_PATH);
		Strategy.dbInMemory.poisCrawledTimes = new HashMap<Integer, Integer>();
		// source database
		Strategy.dbExternal = new H2DB(testSource, testTarget);
		// generate dataset
		//List<Coordinate> points = generateSimpleCase(testSource, category, state, numItems);
//		List<Coordinate>points=skewDataset(numItems);
//		List<Coordinate>points=Exponentialdistribution(numItems);
//		exportToH2(points, testSource, category, state);
		//=============================================		
		Strategy.dbInMemory = new DBInMemory();
		DBInMemory.pois = readFromGeneratedDB(testSource);
		//
		Iterator it2 = DBInMemory.pois.entrySet().iterator();
		while (it2.hasNext()) {
			Entry entry = (Entry) it2.next();
			APOI aPoint = (APOI) entry.getValue();
			Coordinate coordinate = aPoint.getCoordinate();
			PaintShapes.paint.addPoint(coordinate);
		}
		PaintShapes.paint.myRepaint();
		//
		Strategy.dbInMemory.index();
		// target database
		Strategy.dbExternal.createTables(testTarget);

		crawler.crawl(state, categoryID, category, envelopeECEF);

		logger.info("before updating");
		printExternalDB();
		Strategy.dbInMemory.updataExternalDB();
		// testing updataExternalDB
		logger.info("after updating");
		printExternalDB();

		// close the connections
		Strategy.endData();

		logger.info("Finished ! Oh ! Yeah! ");
		logger.info("number of queries issued = " + Strategy.countNumQueries);
		logger.info("number of points crawled = " + Strategy.dbInMemory.poisIDs.size());
		logger.info("number of eligble points="+crawler.countPoint);
		Set set = Strategy.dbInMemory.poisIDs;
		Iterator<Integer> it = set.iterator();
		while (it.hasNext()) {
			int id = it.next();
			logger.debug(id);
		}

	}
	
	public List<Coordinate>Exponentialdistribution(int n){
		List<Coordinate>list=new ArrayList<Coordinate>();
		ExponentialDistribution stat=new ExponentialDistribution(0.4);
		Random random = new Random(System.currentTimeMillis());
		double x=1.0;
		double y=1.0;
		for(int i=0;i<n;i++){
			x=random.nextDouble();
			y=stat.cumulativeProbability(x);
			System.out.println("x="+x+"  y="+y);
			x=Math.log(x*1000)+200;
			y=Math.log(y+500)+500;
			
			Coordinate p=new Coordinate(x, y);
			list.add(p);
		}
		return list;
	}

	/**
	 * Generate simple case, write them to the testSource database
	 */
	private List<Coordinate> generateSimpleCase(String testSource, String category, String state, int numItems) {
		double x = 1.0;
		double y = 1.0;
		List list = new ArrayList<Coordinate>();
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < numItems; i++) {
			x = random.nextDouble() * 1000;
			y = random.nextDouble() * 1000;
			Coordinate coordinate = new Coordinate(x, y);
			list.add(coordinate);
		}
		return list;
	}

	private HashMap<Integer, APOI> readFromGeneratedDB(String dbNameSource) {
		HashMap<Integer, APOI> map = new HashMap<Integer, APOI>();
		H2DB h2 = (H2DB) Strategy.dbExternal;
		String dbName = Strategy.dbExternal.dbNameSource;
		// TODO check sql
		try {
			Connection conn = h2.getConnection(dbName);
			Statement stat = conn.createStatement();

			String sql = "SELECT * FROM item";
			try {
				java.sql.ResultSet rs = stat.executeQuery(sql);
				while (rs.next()) {

					int itemID = rs.getInt(1);
					String title = rs.getString(2);
					String city = rs.getString(3);
					String state = rs.getString(4);

					double latitude = rs.getDouble(5);
					double longitude = rs.getDouble(6);
					double distance = rs.getDouble(7);

					double averageRating = rs.getDouble(8);
					double totalRating = rs.getDouble(9);
					double totalReviews = rs.getDouble(10);

					Rating rating = new Rating();
					rating.setAverageRating(averageRating);
					rating.setTotalRatings((int) totalRating);
					rating.setTotalReviews((int) totalReviews);

					int numCrawled = rs.getInt(11);
					//
					// print query result to console
					//logger.debug("itemID: " + itemID);
					//logger.debug("latitude: " + latitude);
					//logger.debug("longitude: " + longitude);
					//logger.debug("--------------------------");
					APOI poi = new APOI(itemID, title, city, state, longitude, latitude, rating, distance, null, numCrawled);
					map.put(itemID, poi);
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	public void printExternalDB() {
		H2DB h2 = (H2DB) Strategy.dbExternal;
		String dbName = Strategy.dbExternal.dbNameTarget;
		// TODO check sql
		try {
			Connection conn = h2.getConnection(dbName);
			Statement stat = conn.createStatement();

			String sql = "SELECT * FROM item";
			try {
				java.sql.ResultSet rs = stat.executeQuery(sql);
				while (rs.next()) {

					int itemID = rs.getInt(1);
					String title = rs.getString(2);
					String city = rs.getString(3);
					String state = rs.getString(4);

					double latitude = rs.getDouble(5);
					double longitude = rs.getDouble(6);
					double distance = rs.getDouble(7);

					double averageRating = rs.getDouble(8);
					double totalRating = rs.getDouble(9);
					double totalReviews = rs.getDouble(10);

					Rating rating = new Rating();
					rating.setAverageRating(averageRating);
					rating.setTotalRatings((int) totalRating);
					rating.setTotalReviews((int) totalReviews);

					int numCrawled = rs.getInt(11);
					//
					// print query result to console
					logger.debug("itemID: " + itemID);
					logger.debug("latitude: " + latitude);
					logger.debug("longitude: " + longitude);
					logger.debug("--------------------------");
					APOI poi = new APOI(itemID, title, city, state, longitude, latitude, rating, distance, null, numCrawled);
					logger.info(poi.toString());
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Coordinate> skewDataset(int n) {
		double x = 1.0;
		double y = 1.0;
		double u1, u2;
		double mean = 0.4;
		double lamda = 1 / mean;
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		List<Coordinate> list = new ArrayList<Coordinate>();
		Random random = new Random(System.currentTimeMillis());
		Random r2 = new Random(System.currentTimeMillis());
		// center
		Coordinate center = new Coordinate(500, 500);
		for (int i = 0; i < n; i++) {
			u1 = random.nextDouble();
			u2 = random.nextDouble();
			boolean sign1 = r2.nextBoolean();
			boolean sign2 = r2.nextBoolean();

			x = (Math.log(1 - u1) / (-lamda));
			y = (Math.log(1 - u2) / (-lamda));

			if (x < minX) {
				minX = x;
			}
			if (x > maxX) {
				maxX = x;
			}
			if (y > maxY) {
				maxY = y;
			}
			if (y < minY) {
				minY = y;
			}

			if (!sign1) {
				x *= -1;
			}
			if (!sign2) {
				y *= -1;
			}

			Coordinate coordinate = new Coordinate(x, y);
			list.add(coordinate);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("boundary = " + minX + "," + maxX + ";" + minY + ","
					+ maxY);
		}
		List<Coordinate> list2 = new ArrayList<Coordinate>();
		for (int i = 0; i < n; i++) {
			Coordinate coordinate = list.get(i);
			x = (500 + coordinate.x / (maxX - minX) * 500) % 1000;
			y = (500 + coordinate.y / (maxY - minY) * 500) % 1000;
			Coordinate coordinate2 = new Coordinate(x, y);
			list2.add(coordinate2);
		}

		return list2;
	}


	private void exportToH2(List<Coordinate> points, String testSource, String category, String state) {
		HashMap<Integer, APOI> map = new HashMap<Integer, APOI>();

		H2DB h2 = (H2DB) Strategy.dbExternal;
		String dbName = Strategy.dbExternal.dbNameSource;
		try {
			Connection conn = h2.getConnection(dbName);
			Statement stat = conn.createStatement();
			// create table
			String sqlCreate = "CREATE TABLE IF NOT EXISTS ITEM " + "(ITEMID INT PRIMARY KEY, TITLE VARCHAR(200), CITY VARCHAR(200), STATE VARCHAR(10), "
					+ "LATITUDE DOUBLE, LONGITUDE DOUBLE, DISTANCE DOUBLE, AVERAGERATING DOUBLE, TOTALRATINGS DOUBLE, TOTALREVIEWS DOUBLE, NUMCRAWLED INT)";
			;
			stat.execute(sqlCreate);
			stat.close();
			// import data
			String sqlInsert = "INSERT INTO ITEM (ITEMID, TITLE, CITY, STATE, "
					+ "LATITUDE, LONGITUDE, DISTANCE, AVERAGERATING, TOTALRATINGS, TOTALREVIEWS, NUMCRAWLED) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement prepItem = conn.prepareStatement(sqlInsert);
			for (int i = 0; i < points.size(); i++) {
				Coordinate coordinate = points.get(i);

				double longitude = coordinate.x;
				double latitude = coordinate.y;
				// table 1

				prepItem.setInt(1, i);
				prepItem.setString(2, "title");
				prepItem.setString(3, "city");
				prepItem.setString(4, state);
				prepItem.setDouble(5, latitude);
				prepItem.setDouble(6, longitude);
				prepItem.setDouble(7, 0);
				prepItem.setDouble(8, Rating.noAverageRatingValue);
				prepItem.setDouble(9, Rating.noAverageRatingValue);
				prepItem.setDouble(10, Rating.noAverageRatingValue);
				prepItem.setInt(11, 0);

				prepItem.addBatch();

			}
			prepItem.executeBatch();
			conn.commit();
			prepItem.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
