/*This class used to record the information of every level: include all the points' information at the same level*/
package mo.umac.metadata.plugins;

import java.util.LinkedList;

public class Level_info {
	private int level=0;
	private LinkedList<VQP1> queryPoint=new LinkedList<VQP1>();
	
	public Level_info(){
		
	}
	
	public Level_info(int a, LinkedList<VQP1> L){
		this.level=a;
		this.queryPoint=L;
	}
	
	public void setlevel(int a){
		this.level=a;
	}
	
	public int getlevel(){
		return this.level;
	}
	
	public void setqueryPoint(LinkedList<VQP1>L){
		this.queryPoint=L;
	}
	
	public LinkedList<VQP1> getqueryPoint(){
		return this.queryPoint;
	}

}
