package mo.umac.metadata.plugins;

import com.vividsolutions.jts.geom.Coordinate;

public class NeighborPoint {
	private Coordinate centerPoint = new Coordinate();
	private VQP[] neighborQ = new VQP[6];

	public NeighborPoint() {

	}

	public NeighborPoint(Coordinate centerPoint, VQP[] neighbor) {
		super();
		this.centerPoint = centerPoint;
		this.neighborQ = neighbor;
	}

	public void setCenterPoint(Coordinate centerPoint) {
		this.centerPoint = centerPoint;
	}

	public Coordinate getCenterPoint() {
		return centerPoint;
	}

	public void setNeighborQ(VQP[] neighbor) {
		System.arraycopy(neighborQ, 0, neighbor, 0, neighbor.length);
	}

	public VQP[] getNeighborQ() {
		return neighborQ;
	}
}
