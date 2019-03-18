package cci.service.cert;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class XSLWriter {
	public static Logger LOG=Logger.getLogger(XSLWriter.class);
	
	public Workbook makeWorkbook(List<Object> items, String[] headers, String[] dbfields, String title) {
		long start = System.currentTimeMillis();
		
		Workbook workbook = new SXSSFWorkbook();
		// Workbook workbook = new HSSFWorkbook();

		Sheet sheet = workbook.createSheet(title);
		createRow(sheet, 0, headers);
		
		int i = 1;
		for (Object item : items) {
			createRow(sheet, i++, getData(item, dbfields));
		}
		long end = System.currentTimeMillis();
		LOG.info("File creation duration: " + (end - start));

		return workbook;
	}
	
	private void createRow(Sheet sheet, int rownum, Object[] data) {
		Row row = sheet.createRow(rownum);
		Cell cell;
		int cellnum = 0;

		for (Object obj : data) {
			cell = row.createCell(cellnum++);
			// System.out.println(obj);
			if (obj instanceof String)
				cell.setCellValue((String) obj);
			else if (obj instanceof Integer)
				cell.setCellValue((Integer) obj);
			else if (obj instanceof Long)
				cell.setCellValue((Long) obj);
			else if (obj instanceof List) 
                cell.setCellValue(obj.toString());      
			else if (obj instanceof Date)
				cell.setCellValue( 			
					new SimpleDateFormat("dd/MM/yyyy").format((Date) obj));
		}
	}
	
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
}
