package mo.umac.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.vividsolutions.jts.geom.Coordinate;

//读取Excel文件
public class ReadExcel {

	public static ArrayList<Double> getkey(String filepath){
		ArrayList<Double>keyList=new ArrayList<Double>();
		FileInputStream is=null;
		try{
		 is=new FileInputStream(filepath);}
		catch (FileNotFoundException e){
			e.printStackTrace();
		}
		HSSFWorkbook wb=null;
		try{
	      wb=new HSSFWorkbook(is);
	      }catch (IOException e){
	    	  e.printStackTrace();
	      }
		
		HSSFSheet sheet=wb.getSheetAt(0);
		int totalRows=sheet.getPhysicalNumberOfRows();
		for(int i=1;i<totalRows;i++){
			HSSFRow row=sheet.getRow(i);
			double key=new BigDecimal(row.getCell(0).getNumericCellValue()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
          //  System.out.println(key);
			keyList.add(key);
		}
		
        return keyList;		
	}
	
	public ArrayList<Coordinate>getStartPoint(String filepath){
	   ArrayList<Coordinate>startpointList=new ArrayList<Coordinate>(); 
	   FileInputStream is=null;
		try{
		 is=new FileInputStream(filepath);}
		catch (FileNotFoundException e){
			e.printStackTrace();
		}
		HSSFWorkbook wb=null;
		try{
	      wb=new HSSFWorkbook(is);
	      }catch (IOException e){
	    	  e.printStackTrace();
	      }
		
		HSSFSheet sheet=wb.getSheetAt(0);
		int totalRows=sheet.getPhysicalNumberOfRows();
		for(int i=1;i<totalRows;i++){
			HSSFRow row=sheet.getRow(i);
			Coordinate startpoint=new Coordinate();
			startpoint.x=row.getCell(0).getNumericCellValue();
			startpoint.y=row.getCell(1).getNumericCellValue();
         //  System.out.println(key);
			startpointList.add(startpoint);
		}
		return startpointList;
	}
	
	public static ArrayList<Integer> getNeedPoint(String filepath){
		ArrayList<Integer>needpointList=new ArrayList<Integer>();
		FileInputStream is=null;
		try{
		 is=new FileInputStream(filepath);}
		catch (FileNotFoundException e){
			e.printStackTrace();
		}
		HSSFWorkbook wb=null;
		try{
	      wb=new HSSFWorkbook(is);
	      }catch (IOException e){
	    	  e.printStackTrace();
	      }
		
		HSSFSheet sheet=wb.getSheetAt(0);
		int totalRows=sheet.getPhysicalNumberOfRows();
		for(int i=1;i<totalRows;i++){
			HSSFRow row=sheet.getRow(i);
			Integer neednum=(int) row.getCell(0).getNumericCellValue();
          //  System.out.println(key);
			needpointList.add(neednum);
		}
		
        return needpointList;		
	}
	
	public static void main(String arg[]) throws Exception{
		String filepath = "../experiment_result/needpointList.xls";
		String filepath3 = "../experiment_result/startpointList.xls";
//		List<Double>keyList=getkey(filepath);
//		for(int i=0;i<keyList.size();i++){
//			System.out.println(keyList.get(i));
//		}
//		List<Integer>needpointlist=getNeedPoint(filepath);
//		for(int j=0;j<needpointlist.size();j++){
//			System.out.println(needpointlist.get(j));
//		}
		ReadExcel re=new ReadExcel();
		List<Coordinate>startpointList=re.getStartPoint(filepath3);
		for(int i=0;i<startpointList.size();i++){
			System.out.println(startpointList.get(i).toString());
		}
	}
				
}

