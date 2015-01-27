package mo.umac.crawler;

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

import mo.umac.db.DBInMemory;
import mo.umac.db.H2DB;
import mo.umac.metadata.APOI;
import mo.umac.metadata.Rating;
import mo.umac.uscensus.Cluster;
import mo.umac.uscensus.USDensity;

import org.apache.log4j.xml.DOMConfigurator;

import paint.PaintShapes;
import paint.WindowUtilities;
import utils.FileOperator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class MainSynthetic extends Strategy {

	private static String source = "../data-experiment/synthetic/2d-uniform/10000";
	private static String target = "../data-experiment/synthetic/target";
	private static int n = 1000;
	private int topK = 15;
	private static Envelope envelope = new Envelope(0, 1000, 0, 1000);
	private static String state = "NY";
	private static int categoryID = 96926236;
	private static String category = "Restaurants";
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean debug = false;
		PaintShapes.painting = false;
		MainYahoo.shutdownLogs(debug);
		DOMConfigurator.configure(MainYahoo.LOG_PROPERTY_PATH);
		MainSynthetic test = new MainSynthetic();
		if (PaintShapes.painting) {
			WindowUtilities.openInJFrame(PaintShapes.paint, 1000, 1000);
		}
		Strategy.dbExternal = new H2DB(source, target);
//		test.generateData();
		if (PaintShapes.painting) {
			test.drawDataPoints();
		}
		test.crawling();
		Strategy.endData();

	}

	public void generateData() {
		List<Coordinate> points = uniformDataset(n);
		// List<Coordinate> points = skewDataset(n);
		exportToH2(points, source, category, state);
	}

	public void drawDataPoints() {
		Strategy.dbInMemory = new DBInMemory();
		Strategy.dbInMemory.pois = readFromGeneratedDB(source);
		//
		Iterator it2 = Strategy.dbInMemory.pois.entrySet().iterator();
		while (it2.hasNext()) {
			Entry entry = (Entry) it2.next();
			APOI aPoint = (APOI) entry.getValue();
			Coordinate coordinate = aPoint.getCoordinate();
			PaintShapes.paint.addPoint(coordinate);
		}
		PaintShapes.paint.myRepaint();
	}



	public void crawling() {
		/************************* Change these lines *************************/
		Strategy.CATEGORY_ID_PATH = "./src/main/resources/cat_id.txt";
		/** switch algorithms */
		
		//Hexagon crawler=new Hexagon();
//		Hexagon_optimize crawler=new Hexagon_optimize();
//		Periphery_Optimize2 crawler=new Periphery_Optimize2();
		//Periphery_Optimize crawler=new Periphery_Optimize();
		Hexagon_optimize4 crawler=new Hexagon_optimize4();
		//
		/** end switching algorithms */
		Strategy.MAX_TOTAL_RESULTS_RETURNED = topK;
		Strategy.categoryIDMap = FileOperator.readCategoryID(CATEGORY_ID_PATH);
		Strategy.dbInMemory.poisCrawledTimes = new HashMap<Integer, Integer>();
		//
		Strategy.dbInMemory = new DBInMemory();
		Strategy.dbInMemory.pois = readFromGeneratedDB(source);
		Strategy.dbInMemory.index();
		// Strategy.dbExternal.createTables(target);
		crawler.crawl(state, categoryID, category, envelope);
		//
		logger.info("Finished ! Oh ! Yeah! ");
		logger.info("number of queries issued = " + Strategy.countNumQueries);
		logger.info("number of points crawled = "
				+ Strategy.dbInMemory.poisIDs.size());
		//logger.info("eligible points="+crawler.countPoint);
	}

	/**
	 * Generate simple case, write them to the testSource database
	 */
	private List<Coordinate> uniformDataset(int n) {
		double x = 1.0;
		double y = 1.0;
		List list = new ArrayList<Coordinate>();
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < n; i++) {
			x = random.nextDouble() * 1000;
			y = random.nextDouble() * 1000;
			Coordinate coordinate = new Coordinate(x, y);
			list.add(coordinate);
		}
		return list;
	}

	/**
	 * ref <{@link http
	 * ://stackoverflow.com/questions/2106503/pseudorandom-number
	 * -generator-exponential-distribution}
	 * 
	 * @param n
	 * @return
	 */
	public static List<Coordinate> skewDataset(int n) {
		double x = 1.0;
		double y = 1.0;
		double u1, u2;
		double mean = 0.1;
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

	private HashMap<Integer, APOI> readFromGeneratedDB(String dbNameSource) {
		HashMap<Integer, APOI> map = new HashMap<Integer, APOI>();
		H2DB h2 = (H2DB) Strategy.dbExternal;
		String dbName = Strategy.dbExternal.dbNameSource;
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
					// if (logger.isDebugEnabled()) {
					// logger.debug("itemID: " + itemID);
					// logger.debug("latitude: " + latitude);
					// logger.debug("longitude: " + longitude);
					// logger.debug("--------------------------");
					// }
					APOI poi = new APOI(itemID, title, city, state, longitude,
							latitude, rating, distance, null, numCrawled);
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
					APOI poi = new APOI(itemID, title, city, state, longitude,
							latitude, rating, distance, null, numCrawled);
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

	public static void exportToH2(List<Coordinate> points, String testSource,
			String category, String state) {
		H2DB h2 = (H2DB) Strategy.dbExternal;
		String dbName = Strategy.dbExternal.dbNameSource;
		try {
			Connection conn = h2.getConnection(dbName);
			Statement stat = conn.createStatement();
			// create table
			String sqlCreate = "CREATE TABLE IF NOT EXISTS ITEM "
					+ "(ITEMID INT PRIMARY KEY, TITLE VARCHAR(200), CITY VARCHAR(200), STATE VARCHAR(10), "
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

	@Override
	protected void crawlByCategoriesStates(
			LinkedList<Envelope> listEnvelopeStates,
			List<String> listCategoryNames, LinkedList<String> listNameStates,
			HashMap<Integer, String> categoryIDMap) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void crawl(String state, int category, String query,
			Envelope envelopeState) {
		// TODO Auto-generated method stub

	}

}
