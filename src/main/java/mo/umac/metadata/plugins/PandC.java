package mo.umac.metadata.plugins;

import com.vividsolutions.jts.geom.Coordinate;

public class PandC {
   private Coordinate intersectPoint=new Coordinate();
   private VQP neighbor=new VQP();
   
   public PandC(){
	   
   }
   
   public PandC(Coordinate i, VQP n){
	   this.intersectPoint=i;
	   this.neighbor=n;
   }
   
   public void setintersectpoint(Coordinate i){
	   this.intersectPoint=i;
   }
   
   public Coordinate getintersectpoint(){
	   return this.intersectPoint;
   }
   
   public void setneighbor(VQP n){
	   this.neighbor=n;
   }
   
   public VQP getneighbor(){
	   return this.neighbor;
   }

}
