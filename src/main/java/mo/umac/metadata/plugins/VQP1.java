/*This class used to record the information used in PeripheyQuery
 *@param left, right: record the intersection points and corresponding information of the neighbor circle
 *@param self: record the center and radius of the query circle*/
package mo.umac.metadata.plugins;

import com.vividsolutions.jts.geom.Coordinate;
/*
 * @left */
public class VQP1 {
	private PandC left=new PandC();
	private PandC right=new PandC();
	private VQP self=new VQP();
	
	public VQP1(){
		
	}
	
	public VQP1(PandC l, PandC r, VQP s){
		this.left=l;
		this.right=r;
		this.self=s;
	}
	
	
	public void setleft(PandC l){
		this.left=l;
	}
	
	public PandC getleft(){
		return this.left;
	}
	
	public void setright(PandC r){
		this.right=r;
	}
	
	public PandC getright(){
		return this.right;
	}
	
	public void setself(VQP q3){
		this.self=q3;
	}
	
	public VQP getself(){
		return this.self;
	}
	

}
