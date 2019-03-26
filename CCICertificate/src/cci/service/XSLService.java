﻿package cci.service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import cci.model.owncert.OwnCertificate;
import cci.web.controller.owncert.ViewWasteOwnCertificate;

public class XSLService {
	public static Logger LOG=Logger.getLogger(XSLService.class);
	
	/* --------------------------------------------------------------
	* Create Excel workbook 
	* ------------------------------------------------------------- */
	public Workbook makeWorkbook(List<Object> items, String[] headers, String[] dbfields, String title) {
		long start = System.currentTimeMillis();
		
		Workbook workbook = new SXSSFWorkbook();
		// Workbook workbook = new HSSFWorkbook();

		Sheet sheet = workbook.createSheet(title);
		createRow(sheet, 0, headers);

		if (items != null) {
			int i = 1;
			for (Object item : items) {
				createRow(sheet, i++, getData(item, dbfields));
			}
		} else {
			LOG.info("Массив информации для отображения пуст. Ничего не передано для вывода в Excel файл.");
		}
		long end = System.currentTimeMillis();
		LOG.info("File creation duration: " + (end - start));

		return workbook;
	}
	
	/* --------------------------------------------------------------
	 *  Fill in row
	 * ------------------------------------------------------------- */
	private void createRow(Sheet sheet, int rownum, Object[] data) {
		Row row = sheet.createRow(rownum);
		Cell cell;
		int cellnum = 0;

		for (Object obj : data) {
			if (obj instanceof String) {
				cell = row.createCell(cellnum++);
				cell.setCellValue((String) obj);
			} else if (obj instanceof Integer) {
				cell = row.createCell(cellnum++);
				cell.setCellValue((Integer) obj);
			} else if (obj instanceof Long) {
				cell = row.createCell(cellnum++);
				cell.setCellValue((Long) obj);
			} else if (obj instanceof List) {
				String[] strs;
				if (obj.toString().length() > 32000) { 
				   strs = split( obj.toString(), 32000);
				} else {
				   strs = new String[]{obj.toString()};	
				}
				
			    for (String str : strs) {
			    	cell = row.createCell(cellnum++);
			    	cell.setCellValue(str);    	
			    }
			} else if (obj instanceof Date) {
			   	cell = row.createCell(cellnum++);
				cell.setCellValue(	
					new SimpleDateFormat("dd/MM/yyyy").format((Date) obj));
			}
		}
	}
	
	private  String[] split(String str, int len) {
		List<String> strs = new ArrayList<String>();
		do {
			strs.add(str.substring(0, len));
			str = str.substring(len);
		} while (str.length() > len);
		if (str.length() > 0 ) strs.add(str);
		return strs.toArray(new String[strs.size()]);
	}
	
	/* -----------------------------------------------------------
	 *  Extract data from object
	 * --------------------------------------------------------- */
	private Object[] getData(Object cert, String[] dbfields) {
		List<Object> data = new ArrayList<Object>();

		for (String field : dbfields) {
			String getter = convertFieldNameToGetter(field);

			try {
				Method m = getMethod(cert, getter, new Class[] {});
				if (m != null) {
					data.add(m.invoke(cert, new Object[] {}));
				}
			} catch (Exception ex) {
				LOG.info("Error getData: " + ex.getMessage());
			}
		}

		return data.toArray();
	}

	/* -----------------------------------------------------------------
	 *  Reflection mrthods
	 * ---------------------------------------------------------------- */
	private Method getMethod(Object obj, String name, Class[] params) {
		Method m = null;
		try {
			try {
				m = obj.getClass().getMethod(name, params);
			} catch (Exception ex) {
				m = obj.getClass().getSuperclass().getMethod(name, params);
			}

		} catch (Exception ex) {
			LOG.info("Error get method: " + ex.getMessage());
		}

		return m;
	}

	private String convertFieldNameToGetter(String field) {
		return "get" + field.substring(0, 1).toUpperCase()
				+ field.substring(1).toLowerCase();
	}

	private String convertFieldNameToSetter(String field) {
		return "set" + field.substring(0, 1).toUpperCase()
				+ field.substring(1).toLowerCase();
	}

	/* --------------------------------------------------------------
	* Create Excel workbook Orsha Report
	* ------------------------------------------------------------- */
	public Workbook makeWorkbookReportOrsha(List<OwnCertificate> certs, String filenameTemplate, String reportdate) {
		Workbook wb = null;
		InputStream inp = null;
		
		try {
			inp = new FileInputStream(filenameTemplate);
			wb = WorkbookFactory.create(inp);
			Sheet sheet = wb.getSheet("Report");
			
			Row row = sheet.getRow(sheet.getFirstRowNum());
			row.getCell(row.getFirstCellNum()).setCellValue(
			         row.getCell(row.getFirstCellNum()).getStringCellValue() + " (" + reportdate + ")");
			
			int lnum = sheet.getLastRowNum() + 1;
			int nrow = 1;
			int iprev = -1;
			int i;

			for (i = 0; i < certs.size(); i++) {
				row = sheet.createRow(lnum + i);
				Cell cell;

				if (i == 0 || !certs.get(i).getCustomername().equals(certs.get(i - 1).getCustomername())) {
					cell = row.createCell(0);
					setStyleCenter(cell, wb, true);
					cell.setCellValue(nrow++ + ".");
                    
					String address = certs.get(i).getCustomeraddress().trim();
					cell = row.createCell(1);
					setStyleLeft(cell, wb, true);
					cell.setCellValue(certs.get(i).getCustomername().trim() + 
							(address == null || address.isEmpty() ? "" :  ", " + address));

					if (iprev != i - 1) {
						CellRangeAddress range;
						range = new CellRangeAddress(lnum + iprev, lnum + i - 1, 0, 0);
						sheet.addMergedRegion(range);

						range = new CellRangeAddress(lnum + iprev, lnum + i - 1, 1, 1);
						sheet.addMergedRegion(range);
					}
					iprev = i;
				}

				cell = row.createCell(2);
				setStyleCenter(cell, wb, true);
				cell.setCellValue(certs.get(i).getDatecert().trim());
				cell = row.createCell(3);
				setStyleCenter(cell, wb, true);
				cell.setCellValue(certs.get(i).getNumber().trim());
				cell = row.createCell(4);
				setStyleCenter(cell, wb, true);
				cell.setCellValue(certs.get(i).getDatestart().trim());
				cell = row.createCell(5);
				setStyleCenter(cell, wb, true);
				cell.setCellValue(certs.get(i).getDateexpire().trim());
				cell = row.createCell(6);
				setStyleCenter(cell, wb, true);
				cell.setCellValue(certs.get(i).getType().trim());
				cell = row.createCell(7);
				setStyleCenter(cell, wb, true);
				cell.setCellValue(certs.get(i).getProductdescription().trim());
				cell = row.createCell(8);
				setStyleCenter(cell, wb, true);
				cell.setCellValue(certs.get(i).getFactorylist().trim());
			}
			if (iprev != i - 1) {
				CellRangeAddress range;
				range = new CellRangeAddress(lnum + iprev, lnum + i - 1, 0, 0);
				sheet.addMergedRegion(range);

				range = new CellRangeAddress(lnum + iprev, lnum + i - 1, 1, 1);
				sheet.addMergedRegion(range);
			}
			sheet.autoSizeColumn(2);
		} catch (Exception ex) {
			LOG.error("Ошибка формирования оршанского отчета: " + ex.getLocalizedMessage());
		} finally {
		  if (inp != null) 
			  try { inp.close();} catch(Exception ex) {};
		}
		
		return wb;
	}
	
	/* ------------------------------------------------------------
	 *  Cell style methods 
	 * ---------------------------------------------------------- */
	private static void setStyleLeft(Cell cell, Workbook wb, boolean wrap) {
		CellStyle cs = wb.createCellStyle();
		cs.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		cs.setAlignment(CellStyle.ALIGN_LEFT);
		cs.setWrapText(wrap);
		cell.setCellStyle(cs);
		cell.setCellType(Cell.CELL_TYPE_STRING);
	}

	private static void setStyleCenter(Cell cell, Workbook wb, boolean wrap) {
		CellStyle cs = wb.createCellStyle();
		cs.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		cs.setAlignment(CellStyle.ALIGN_CENTER);
		cs.setWrapText(wrap);
		cell.setCellStyle(cs);
		cell.setCellType(Cell.CELL_TYPE_STRING);
	}

	/* --------------------------------------------------------------
	* Create Excel workbook Waste Report
	* ------------------------------------------------------------- */
	public Workbook makeWorkbookWasteReport(List<ViewWasteOwnCertificate> certs, String filenametemplate,
			String reportdate) {
		Workbook wb = null;
		InputStream inp = null;
		
		try {
			inp = new FileInputStream(filenametemplate);
			wb = WorkbookFactory.create(inp);
			Sheet sheet = wb.getSheet("Report");
			
			Row row = sheet.getRow(sheet.getFirstRowNum());
			row.getCell(row.getFirstCellNum()).setCellValue(
			         row.getCell(row.getFirstCellNum()).getStringCellValue() + " (" + reportdate + ")");
			
			int lnum = sheet.getLastRowNum() + 1;
			int nrow = 1;
			int iprev = -1;
			int i;
			
			for (i = 0; i < certs.size(); i++) {
				row = sheet.createRow(lnum + i);
				Cell cell;

				if (i == 0 || !certs.get(i).getProductcode().equals(certs.get(i - 1).getProductcode())) {
					cell = row.createCell(0);
					setStyleCenter(cell, wb, true);
					cell.setCellValue(nrow++ + ".");

					cell = row.createCell(1);
					setStyleLeft(cell, wb, true);
					cell.setCellValue(certs.get(i).getProductcode().trim());

					if (iprev != i - 1) {
						CellRangeAddress range;
						range = new CellRangeAddress(lnum + iprev, lnum + i - 1, 0, 0);
						sheet.addMergedRegion(range);

						range = new CellRangeAddress(lnum + iprev, lnum + i - 1, 1, 1);
						sheet.addMergedRegion(range);
					}
					iprev = i;
				}

				cell = row.createCell(2);
				setStyleLeft(cell, wb, true);
				cell.setCellValue(certs.get(i).getCustomername().trim());
				cell = row.createCell(3);
				setStyleLeft(cell, wb, true);
				cell.setCellValue(certs.get(i).getCustomerunp().trim());
				cell = row.createCell(4);
				setStyleLeft(cell, wb, true);
				cell.setCellValue(certs.get(i).getCustomeraddress().trim());
				cell = row.createCell(5);
				setStyleCenter(cell, wb, true);
				cell.setCellValue(certs.get(i).getDatecert().trim());
				cell = row.createCell(6);
				setStyleCenter(cell, wb, true);
				cell.setCellValue(certs.get(i).getNumber().trim());
				cell = row.createCell(7);
				setStyleCenter(cell, wb, true);
				cell.setCellValue(certs.get(i).getDatestart().trim());
				cell = row.createCell(8);
				setStyleCenter(cell, wb, true);
				cell.setCellValue(certs.get(i).getDateexpire().trim());
				
			    // split producs to n strings
				String[] strs;
				String products = certs.get(i).getProducts().trim().toString();
				if (products.length() > 32000) { 
				   strs = split( products, 32000);
				} else {
				   strs = new String[]{products};	
				}
				int cellnum = 9;
			    for (String str : strs) {
			    	cell = row.createCell(cellnum++);
			    	setStyleLeft(cell, wb, false);
			    	cell.setCellValue(str);    	
			    }
			}
			

			if (iprev != i - 1) {
					CellRangeAddress range;
					range = new CellRangeAddress(lnum + iprev, lnum + i - 1, 0, 0);
					sheet.addMergedRegion(range);

					range = new CellRangeAddress(lnum + iprev, lnum + i - 1, 1, 1);
					sheet.addMergedRegion(range);
			}

			
			sheet.autoSizeColumn(2);
		} catch (Exception ex) {
			LOG.error("Ошибка формирования оршанского отчета: " + ex.getMessage());
		} finally {
		  if (inp != null) 
			  try { inp.close();} catch(Exception ex) {};
		}
		
		return wb;
	}

	
	/* --------------------------------------------------------------
	* Get list product code numbers for Waste Report
	* ------------------------------------------------------------- */
	public List<String> getWasteNumbers(String filename) {
		List<String> numbers = null;
		InputStream inp = null;

		try {
			inp = new FileInputStream(filename);
			Workbook wb = WorkbookFactory.create(inp);
			Sheet sheet = wb.getSheetAt(0);

			for (int nrow = sheet.getFirstRowNum() + 1; nrow <= sheet.getLastRowNum(); nrow++) {
				if (numbers == null)
					numbers = new ArrayList<String>();
				Cell cell = sheet.getRow(nrow).getCell(0);

				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_NUMERIC:
					numbers.add((int) cell.getNumericCellValue() + ""); 		
					break;
				case Cell.CELL_TYPE_STRING:
					String code = cell.getStringCellValue().trim();
					code = code.replaceAll("\\D", " ");
					LOG.info("String code: " + code);
					numbers.add(code);
					break;
				}
			}
			inp.close();
		} catch (Exception ex) {
			LOG.error("Ошибка формирования списка кодов ТНВЭД: " + ex.getLocalizedMessage());
		} finally {
			if (inp != null)
				try {inp.close();} catch (Exception ex) {}
		}

		return numbers;
	}
}
