package mo.umac.spatial;

import com.vividsolutions.jts.geom.Coordinate;

public class ECEFLLA {
	/*
	 * ECEF - Earth Centered Earth Fixed
	 * LLA - Lat Lon Alt
	 * ported from matlab code at https://gist.github.com/1536054 and
	 * https://gist.github.com/1536056
	 */

	// WGS84 ellipsoid constants
	private final static double a = 6378137; // radius
	private final static double e = 8.1819190842622e-2; // eccentricity

	private final static double asq = Math.pow(a, 2);
	private final static double esq = Math.pow(e, 2);

	public static double[] ecef2lla(double[] ecef) {
		double x = ecef[0];
		double y = ecef[1];
		double z = ecef[2];

		double b = Math.sqrt(asq * (1 - esq));
		double bsq = Math.pow(b, 2);
		double ep = Math.sqrt((asq - bsq) / bsq);
		double p = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		double th = Math.atan2(a * z, b * p);

		double lon = Math.atan2(y, x);
		double lat = Math.atan2((z + Math.pow(ep, 2) * b * Math.pow(Math.sin(th), 3)), (p - esq * a * Math.pow(Math.cos(th), 3)));
		double N = a / (Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2)));
		double alt = p / Math.cos(lat) - N;

		// mod lat to 0-2pi
		lon = lon % (2 * Math.PI);

		// correction for altitude near poles left out.

		double[] ret = { radians2Degree(lat), radians2Degree(lon), radians2Degree(alt) };

		return ret;
	}

	/**
	 * LLA2ECEF - convert latitude, longitude, and altitude to earth-centered,
	 * earth-fixed (ECEF) cartesian
	 * <p>
	 * USAGE: [x,y,z] = lla2ecef(lat,lon,alt)
	 * <p>
	 * x = ECEF X-coordinate (m)
	 * <p>
	 * y = ECEF Y-coordinate (m)
	 * <p>
	 * z = ECEF Z-coordinate (m)
	 * <p>
	 * lat = geodetic latitude (degree)
	 * <p>
	 * lon = longitude (degree)
	 * <p>
	 * alt = height above WGS84 ellipsoid (m)
	 * <p>
	 * Notes: This function assumes the WGS84 model.
	 * <p>
	 * Latitude is customary geodetic (not geocentric).
	 * 
	 * @param lla
	 * @return
	 */
	public static double[] lla2ecef(double[] lla) {
		double lat = degrees2Radians(lla[0]);
		double lon = degrees2Radians(lla[1]);
		double alt = degrees2Radians(lla[2]);

		double N = a / Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2));

		double x = (N + alt) * Math.cos(lat) * Math.cos(lon);
		double y = (N + alt) * Math.cos(lat) * Math.sin(lon);
		double z = ((1 - esq) * N + alt) * Math.sin(lat);

		double[] ret = { x, y, z };
		return ret;
	}

	public static Coordinate ecef2lla(Coordinate ecef) {
		double longitude = ecef.x;
		double latitude = ecef.y;
		double[] ecef2 = { latitude, longitude, 0 };
		double[] lla2 = ecef2lla(ecef2);
		Coordinate lla = new Coordinate(lla2[1], lla2[0]);
		return lla;
	}

	public static Coordinate lla2ecef(Coordinate lla) {
		double longitude = lla.x;
		double latitude = lla.y;
		double[] lla2 = { latitude, longitude, 0 };
		double[] ecef2 = lla2ecef(lla2);
		Coordinate ecef = new Coordinate(ecef2[1], ecef2[0]);
		return ecef;
	}

	/**
	 * {@link http://www.mathworks.com/help/aeroblks/llatoecefposition.html}
	 * 
	 * Another implementation
	 * 
	 * @author kate
	 * 
	 * @param lla
	 * @return
	 */
	public static double[] myLla2ecef(double[] lla) {
		double lat = degrees2Radians(lla[0]);
		double lon = degrees2Radians(lla[1]);
		double alt = degrees2Radians(lla[2]);

		double f = 1 / 298.257223563;
		double r = 6378137;

		double lamdaSeaLevel = Math.atan(Math.pow((1 - f), 2) * Math.tan(lat));
		double radiusSurfacePoint = Math.sqrt(Math.pow(r, 2) / (1 + (1 / (Math.pow((1 - f), 2)) - 1) * Math.pow(Math.sin(lamdaSeaLevel), 2)));
		double x = radiusSurfacePoint * Math.cos(lamdaSeaLevel) * Math.cos(lon) + alt * Math.cos(lat) * Math.cos(lon);
		double y = radiusSurfacePoint * Math.cos(lamdaSeaLevel) * Math.sin(lon) + alt * Math.cos(lat) * Math.sin(lon);
		double z = radiusSurfacePoint * Math.sin(lamdaSeaLevel) + alt * Math.sin(lat);
		double[] ret = { x, y, z };
		return ret;
	}

	public static double degrees2Radians(double degree) {
		return degree * Math.PI / 180;
	}

	public static double radians2Degree(double radian) {
		return radian * 180 / Math.PI;
	}

	public static void main(String[] args) {
		double[] lla = { 40.477399, -79.76259, 0 };

		double[] ecef = { -4535481.430374093, 1137889.5962155731, 0 };

		double[] ret2 = ECEFLLA.lla2ecef(lla);
		System.out.println(ret2[0] + ", " + ret2[1] + ", " + ret2[2]);

		ret2[2] = 0;

		double[] ret3 = ECEFLLA.ecef2lla(ret2);
		System.out.println(ret3[0] + ", " + ret3[1] + ", " + ret3[2]);

	}

}
