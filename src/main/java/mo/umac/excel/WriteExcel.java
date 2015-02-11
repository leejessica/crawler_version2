package mo.umac.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/*
 * @author jessica.lee
 */

public class WriteExcel {

	public static void main(String args[]) throws Exception {
		WriteExcel we = new WriteExcel();
		String filepath = "../experiment_result/keylist.xls";
		String filepath1 = "../experiment_result/result1.xls";
		String filepath2 = "../experiment_result/needpointList.xls";
		String filepath3 = "../experiment_result/startpointList.xls";
		String filepath4="../experiment_result/recursive_periphery/-74.124299.xls";
		ArrayList<String>filelist=new ArrayList<String>();
		for(int i=0;i<13;i++){
			String file="../experiment_result/recursive_periphery/-74.124299_"+(i+1)*500+".xls";
			filelist.add(file);
		}
		we.combineResult(filepath3, filelist);
		// we.setKeylist(filepath);
//		for(int i=0;i<2;i++){
//		we.settabletitle1(filepath1, 10, 5 );
//		we.setResult1(filepath1, 8, 15, 5);
//		}
		// we.deleteRow(filepath1, 4);
		//we.setNeedpointNum(filepath2);
//		we.setStartPoint(filepath3);
		System.out.println("end ");
	}

	public void setStartPoint(String filepath){
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet ws = wb.createSheet();
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		ws.setColumnWidth((short) 0, (short) 6500);
		
		HSSFRow row = ws.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("Coordinate.x");
		cell.setCellStyle(style);
		HSSFCell cell_1 = row.createCell(1);
		cell_1.setCellValue("Coordinate.y");
		cell_1.setCellStyle(style);
		
		HSSFRow row1 = ws.createRow(1);
		HSSFCell cell1 = row1.createCell(0);
		cell1.setCellValue(-73.355835);
		cell.setCellStyle(style);
		HSSFCell cell1_1 = row1.createCell(1);
		cell1_1.setCellValue(42.746632);
		cell1_1.setCellStyle(style);
		
		HSSFRow row2 = ws.createRow(2);
		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(-73.974213);
		cell2.setCellStyle(style);
		HSSFCell cell2_1 = row2.createCell(1);
		cell2_1.setCellValue(40.75768);
		cell2_1.setCellStyle(style);
		
		HSSFRow row3 = ws.createRow(3);
		HSSFCell cell3 = row3.createCell(0);
		cell3.setCellValue(-74.124299);
		cell3.setCellStyle(style);
		HSSFCell cell3_1 = row3.createCell(1);
		cell3_1.setCellValue(41.3221718);
		cell3_1.setCellStyle(style);
		
		
		try {
			FileOutputStream fout = new FileOutputStream(filepath);
			wb.write(fout);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setKeylist(String filepath) {

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet ws = wb.createSheet("keyvalue");
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		ws.setColumnWidth((short) 0, (short) 6500);
		List<Double> keyList = new ArrayList<Double>();
		for (int i = 0; i <= 42; i++) {
			double key = 1 - i * 0.01;
			keyList.add(key);
		}
		System.out.println(keyList.size() + "===============");
		HSSFRow row = ws.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("key");
		cell.setCellStyle(style);
		for (int j = 0; j < keyList.size(); j++) {
			HSSFRow row1 = ws.createRow(j + 1);
			HSSFCell cell1 = row1.createCell(0);
			cell1.setCellValue(keyList.get(j));
			cell1.setCellStyle(style);
		}
		try {
			FileOutputStream fout = new FileOutputStream(filepath);
			wb.write(fout);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setNeedpointNum(String filepath){
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet ws = wb.createSheet("needpointNum");
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		ws.setColumnWidth((short) 0, (short) 6500);
		List<Integer> needpointList = new ArrayList<Integer>();
		for (int i = 0; i <13; i++) {
			int neednum =(i+1)*500;
			needpointList.add(neednum);
		}
	
		HSSFRow row = ws.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("NEED_POINTS_NUM");
		cell.setCellStyle(style);
		for (int j = 0; j < needpointList.size(); j++) {
			HSSFRow row1 = ws.createRow(j + 1);
			HSSFCell cell1 = row1.createCell(0);
			cell1.setCellValue(needpointList.get(j));
			cell1.setCellStyle(style);
		}
		try {
			FileOutputStream fout = new FileOutputStream(filepath);
			wb.write(fout);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void settabletitle(String filepath, double averageD, double firstD,
			int need, int topk) {
		File file = new File(filepath);
		if (file.length() == 0) {
			try {
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet ws = wb.createSheet();
				HSSFCellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				for (int i = 0; i < 4; i++) {
					ws.setColumnWidth((short) i, (short) 6500);
				}
				HSSFRow row1 = ws.createRow(0);
				HSSFCell cell4 = row1.createCell(0);
				cell4.setCellValue("average density");
				cell4.setCellStyle(style);
				HSSFCell cell5 = row1.createCell(1);
				cell5.setCellValue(averageD);
				cell5.setCellStyle(style);
				HSSFCell cell6 = row1.createCell(2);
				cell6.setCellValue("Need Points");
				cell6.setCellStyle(style);
				HSSFCell cell7 = row1.createCell(3);
				cell7.setCellValue(need);
				cell7.setCellStyle(style);

				HSSFRow row2 = ws.createRow(1);
				HSSFCell cell8 = row2.createCell(0);
				cell8.setCellValue("first density");
				cell8.setCellStyle(style);
				HSSFCell cell9 = row2.createCell(1);
				cell9.setCellValue(firstD);
				cell9.setCellStyle(style);
				HSSFCell cell10 = row2.createCell(2);
				cell10.setCellValue("top-K");
				cell10.setCellStyle(style);
				HSSFCell cell11 = row2.createCell(3);
				cell11.setCellValue(topk);
				cell11.setCellStyle(style);

				HSSFRow row3 = ws.createRow(2);
				HSSFCell cell12 = row3.createCell(0);
				cell12.setCellValue("pk");
				cell12.setCellStyle(style);
				HSSFCell cell13 = row3.createCell(1);
				cell13.setCellValue(firstD / averageD);
				cell13.setCellStyle(style);

				HSSFRow row = ws.createRow(3);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue("eligible points");
				cell.setCellStyle(style);
				HSSFCell cell1 = row.createCell(1);
				cell1.setCellValue("total crawled points");
				cell1.setCellStyle(style);
				HSSFCell cell2 = row.createCell(2);
				cell2.setCellValue("key");
				cell2.setCellStyle(style);
				HSSFCell cell3 = row.createCell(3);
				cell3.setCellValue("cost");
				cell3.setCellStyle(style);

				FileOutputStream fout = new FileOutputStream(filepath);
				wb.write(fout);
				fout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void settabletitle1(String filepath) {
		File file = new File(filepath);
		if (file.length() == 0) {
			try {
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet ws = wb.createSheet();
				HSSFCellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				for (int i = 0; i < 4; i++) {
					ws.setColumnWidth((short) i, (short) 6500);
				}
				HSSFRow row1 = ws.createRow(0);
				HSSFCell cell4 = row1.createCell(0);
				cell4.setCellValue("Need Points");
				cell4.setCellStyle(style);
				HSSFCell cell5 = row1.createCell(1);
				cell5.setCellValue("top-K");
				cell5.setCellStyle(style);
				HSSFCell cell6 = row1.createCell(2);
				cell6.setCellValue("total crawled points");
				cell6.setCellStyle(style);
				HSSFCell cell7 = row1.createCell(3);
				cell7.setCellValue("eligible points");
				cell7.setCellStyle(style);
				HSSFCell cell8 = row1.createCell(4);
				cell8.setCellValue("cost");
				cell8.setCellStyle(style);						
				FileOutputStream fout = new FileOutputStream(filepath);
				wb.write(fout);
				fout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

  
	
	public void setResult(String filepath, double eligible, double queryset,
			int cost, double key) {
		try {
			FileInputStream fs = new FileInputStream(filepath);
			POIFSFileSystem ps = new POIFSFileSystem(fs);
			HSSFWorkbook wb = new HSSFWorkbook(ps);
			HSSFSheet ws = wb.getSheetAt(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			for (int i = 0; i < 4; i++) {
				ws.setColumnWidth((short) i, (short) 6500);
			}
			int rowNum = ws.getLastRowNum() + 1;
			HSSFRow row = ws.createRow(rowNum);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue(eligible);
			cell.setCellStyle(style);
			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(queryset);
			cell1.setCellStyle(style);
			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(key);
			cell2.setCellStyle(style);
			HSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(cost);
			cell3.setCellStyle(style);
			FileOutputStream fout = new FileOutputStream(filepath);
			wb.write(fout);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	 public void combineResult(String filepath, ArrayList<String>filelist){
		   try{		       	   
			   FileInputStream fs = new FileInputStream(filepath);
				POIFSFileSystem ps = new POIFSFileSystem(fs);
				HSSFWorkbook wb = new HSSFWorkbook(ps);
				HSSFSheet ws = wb.getSheetAt(0);
				HSSFCellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				for (int j = 0; j < 5; j++) {
					ws.setColumnWidth((short) j, (short) 6500);
				}
		        for(int i=0;i<filelist.size();i++){
			   String filepath1=filelist.get(i);
					FileInputStream fs1=new FileInputStream(filepath1);
					POIFSFileSystem ps1=new POIFSFileSystem(fs1);
					HSSFWorkbook wb1=new HSSFWorkbook(ps1);
					HSSFSheet ws1=wb1.getSheetAt(0);
					
					HSSFRow row_1=ws1.getRow(0);
					double needpoint=row_1.getCell(1).getNumericCellValue();
					HSSFRow row_2=ws1.getRow(1);
					double topk=row_2.getCell(1).getNumericCellValue();
					HSSFRow row_3=ws1.getRow(3);
					double eligible=row_3.getCell(0).getNumericCellValue();
					double queryset=row_3.getCell(1).getNumericCellValue();
					double cost=row_3.getCell(2).getNumericCellValue();
					
					int rowNum = ws.getLastRowNum();
					HSSFRow row = ws.createRow(rowNum+1);
					HSSFCell cell = row.createCell(0);
					cell.setCellValue(needpoint);
					cell.setCellStyle(style);
					HSSFCell cell1 = row.createCell(1);
					cell1.setCellValue(topk);
					cell1.setCellStyle(style);
					HSSFCell cell2 = row.createCell(2);
					cell2.setCellValue(eligible);
					cell2.setCellStyle(style);
					HSSFCell cell3 = row.createCell(3);
					cell3.setCellValue(queryset);
					cell3.setCellStyle(style);
					HSSFCell cell4 = row.createCell(4);
					cell4.setCellValue(cost);
					cell4.setCellStyle(style);
					
					FileOutputStream fout = new FileOutputStream(filepath);
					wb.write(fout);
					fout.close();
		   }} catch (Exception e) {
					e.printStackTrace();
				}
		   
	   }
	
	public void setResult1(String filepath, int needNum, int topk,  double queryset,double eligible,
			int cost) {
		try {
			FileInputStream fs = new FileInputStream(filepath);
			POIFSFileSystem ps = new POIFSFileSystem(fs);
			HSSFWorkbook wb = new HSSFWorkbook(ps);
			HSSFSheet ws = wb.getSheetAt(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			for (int i = 0; i < 3; i++) {
				ws.setColumnWidth((short) i, (short) 6500);
			}
			int rowNum = ws.getLastRowNum() + 1;
			HSSFRow row = ws.createRow(rowNum);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue(needNum);
			cell.setCellStyle(style);
			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(topk);
			cell1.setCellStyle(style);
			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(queryset);
			cell2.setCellStyle(style);
			HSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(eligible);
			cell3.setCellStyle(style);
			HSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(cost);
			cell4.setCellStyle(style);
			FileOutputStream fout = new FileOutputStream(filepath);
			wb.write(fout);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void deleteRow(String filepath, int rowNum) throws Exception {
		FileInputStream fs = new FileInputStream(filepath);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow row = sheet.getRow(rowNum);
		sheet.removeRow(row);
		FileOutputStream fo = new FileOutputStream(filepath);
		wb.write(fo);
		fo.close();
	}

}