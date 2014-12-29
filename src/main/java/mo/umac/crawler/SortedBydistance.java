package mo.umac.crawler;

import java.util.Comparator;

import com.vividsolutions.jts.geom.Coordinate;

import mo.umac.metadata.plugins.VQP;

/*sort the elements in an ascending sort order of the distance from the startPoint*/

public class SortedBydistance implements Comparator<VQP> {
	private Coordinate startPoint = new Coordinate();

	public SortedBydistance(Coordinate startPoint) {
		super();
		this.startPoint = startPoint;
	}

	@Override
	public int compare(VQP p1, VQP p2) {
		double d1 = startPoint.distance(p1.getCoordinate());
		double d2 = startPoint.distance(p2.getCoordinate());
		if (d1 > d2)
			return 1;
		if (d1 < d2)
			return -1;
		return 0;
	}

}

