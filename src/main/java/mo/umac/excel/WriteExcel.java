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
		// we.setKeylist(filepath);
//		for(int i=0;i<2;i++){
//		we.settabletitle(filepath1, 10, 5, 100, 10);
//		we.setResult(filepath1, 8, 15, 5, 0.97);
//		}
		// we.deleteRow(filepath1, 4);
		we.setNeedpointNum(filepath2);
		System.out.println("end ");
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
				cell2.setCellValue("cost");
				cell2.setCellStyle(style);
				HSSFCell cell3 = row.createCell(3);
				cell3.setCellValue("key");
				cell3.setCellStyle(style);

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
			cell2.setCellValue(cost);
			cell2.setCellStyle(style);
			HSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(key);
			cell3.setCellStyle(style);
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