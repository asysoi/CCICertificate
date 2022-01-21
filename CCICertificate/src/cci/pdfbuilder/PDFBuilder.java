package cci.pdfbuilder;
 
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cci.config.cert.BoxConfig;
import cci.config.cert.CTCell;
import cci.config.cert.ImageBox;
import cci.config.cert.PDFPageConfig;
import cci.config.cert.Stamp;
import cci.config.cert.TableConfig;
import cci.model.cert.Certificate;
import cci.model.cert.Product;
import cci.model.cert.ProductIterator;
import cci.service.CountryConverter;
import cci.service.utils.CCIUtil;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.VerticalText;

public abstract class PDFBuilder {
	private static final Logger LOG=Logger.getLogger(PDFBuilder.class);
	private static final String CHAR_PARAGRAPH = "\n";
	private static final String EMPTY_STRING = "";
	
// =============================================================================================
// Page builder controller 
// =============================================================================================

	public void createPDFPage(PdfWriter writer, Object certificate,
			PDFPageConfig pconfig) throws DocumentException, IOException {
		
		List<BoxConfig> textboxes = pconfig.getTextboxes();
		List<TableConfig> tables = pconfig.getTables();
		List<Stamp> stamps = pconfig.getStamps();
		List<BoxConfig> boxes = pconfig.getBoxes();
		List<BoxConfig> outputs = pconfig.getOutputs();
		List<ImageBox> images = pconfig.getImages();
		
		if (boxes != null) {
			for (int i = 0; i < boxes.size(); i++) {
				BoxConfig box = boxes.get(i);
				if (box.getColorFill() == 0f) {
					makeBorderedBoxtInAbsolutePosition(writer, box);
				} else {
					makeFilledBorderedBoxtInAbsolutePosition(writer, box);
				}
			}
		}

		if (textboxes != null) {
			for (int i = 0; i < textboxes.size(); i++) {
				BoxConfig box = textboxes.get(i);

				if (box.isVertical()) {
					makeVerticalTextBoxInAbsolutePosition(writer,
							box.getText(), box);
				} else if (box.getRotation() > 0f) {
					makeRotatedTextBoxtInAbsolutePosition(writer,
							box.getText(), box);
				} else {
					makeTexBoxtInAbsolutePosition(writer, box.getText(), box);
				}
			}
		}

		if (outputs != null) {
			for (int i = 0; i < outputs.size(); i++) {
				BoxConfig box = outputs.get(i);

				if (box.isVertical()) {
					makeVerticalTextBoxInAbsolutePosition(writer,
							getCertificateTextByMap(certificate, box.getMap()),
							box);
				} else if (box.getRotation() > 0f) {
					makeRotatedTextBoxtInAbsolutePosition(writer,
							getCertificateTextByMap(certificate, box.getMap()),
							box);
				} else {
					makeTexBoxtInAbsolutePosition(writer,
							getCertificateTextByMap(certificate, box.getMap()),
							box);
				}
			}
		}

		if (tables != null) {
			for (int i = 0; i < tables.size(); i++) {
				TableConfig tablecon = tables.get(i);
				makeTableInAbsolutePosition(writer, certificate, tablecon);
			}
		}

		if (images != null) {
			for (ImageBox image : images) {
				makeImageBoxtInAbsolutePosition(writer, image);
			}
		}

		drawStamp(writer, stamps);
        
	}
	
// =============================================================================================
// Components rendering functions 
// =============================================================================================
	public void makeBorderedTexBoxtInAbsolutePosition(PdfWriter writer,
			String text, BoxConfig config) throws IOException,
			DocumentException {

		PdfContentByte canvas = writer.getDirectContent();
		canvas.saveState();
		canvas.beginText();
		canvas.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE);
		canvas.setLineWidth(config.getWidthTextLine());
		Rectangle rect = new Rectangle(Utilities.millimetersToPoints(config
				.getXl()), Utilities.millimetersToPoints(config.getYl()),
				Utilities.millimetersToPoints(config.getXr()),
				Utilities.millimetersToPoints(config.getYr()));
		Phrase ptext = new Phrase(text, new Font(config.getBf(),
				config.getFontSize()));

		ColumnText column = new ColumnText(canvas);
		column.setSimpleColumn(ptext, rect.getLeft(), rect.getBottom(),
				rect.getRight(), rect.getTop(), config.getLeading(),
				config.getgAlign());
		column.go();
		canvas.endText();
		canvas.restoreState();

		PdfContentByte img = writer.getDirectContentUnder();
		img.saveState();
		rect.setBorderWidth(config.getWidthBorderLine());
		rect.setBorder(config.getBorder());
		img.rectangle(rect);
		img.stroke();
		img.restoreState();
	}

	public void makeTexBoxtInAbsolutePosition(PdfWriter writer, String text,
			BoxConfig config) throws IOException, DocumentException {
		PdfContentByte canvas = writer.getDirectContent();
		if (text != null) { 
		   text = text.replaceAll("\\s+", " ");
		}
		canvas.saveState();
		canvas.beginText();
		canvas.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE);
		canvas.setLineWidth(config.getWidthTextLine());
		Rectangle rect = new Rectangle(Utilities.millimetersToPoints(config
				.getXl()), Utilities.millimetersToPoints(config.getYl()),
				Utilities.millimetersToPoints(config.getXr()),
				Utilities.millimetersToPoints(config.getYr()));
		Font fnt = new Font(config.getBf(),	config.getFontSize());
		ColumnText column = new ColumnText(canvas);
		
		float fntSize = column.fitText(fnt, text, rect, config.getFontSize(), column.getRunDirection());
		float leading = config.getLeading();
		
		if (fntSize < config.getFontSize()) {
			System.out.println("Set font size: " + fntSize);
			fnt.setSize(fntSize - 0.1f);
		    leading = fntSize - 0.1f;	
		}
		Phrase ptext = new Phrase(text, fnt);
		column.setSimpleColumn(ptext, rect.getLeft(), rect.getBottom(),
				rect.getRight(), rect.getTop(), leading,
				config.getgAlign());
		column.go();
		canvas.endText();
		canvas.restoreState();
	}

	public void makeBorderedBoxtInAbsolutePosition(PdfWriter writer,
			BoxConfig config) throws IOException, DocumentException {
		//System.out.println("makeBorderedBoxtInAbsolutePosition");
		Rectangle rect = new Rectangle(Utilities.millimetersToPoints(config
				.getXl()), Utilities.millimetersToPoints(config.getYl()),
				Utilities.millimetersToPoints(config.getXr()),
				Utilities.millimetersToPoints(config.getYr()));
		PdfContentByte img = writer.getDirectContentUnder();
		img.saveState();
		rect.setBorderWidth(config.getWidthBorderLine());
		rect.setBorder(config.getBorder());
		img.rectangle(rect);
		img.stroke();
		img.restoreState();
	}

	public void makeFilledBorderedBoxtInAbsolutePosition(PdfWriter writer,
			BoxConfig config) throws IOException, DocumentException {
		//System.out.println("makeFilledBorderedBoxtInAbsolutePosition");
		PdfContentByte canvas = writer.getDirectContentUnder();
		canvas.saveState();
		Rectangle rect = new Rectangle(Utilities.millimetersToPoints(config
				.getXl()), Utilities.millimetersToPoints(config.getYl()),
				Utilities.millimetersToPoints(config.getXr()),
				Utilities.millimetersToPoints(config.getYr()));
		rect.setBorderWidth(config.getWidthBorderLine());
		rect.setBorder(config.getBorder());
		rect.setGrayFill(config.getColorFill());
		canvas.rectangle(rect);
		canvas.fillStroke();
		canvas.restoreState();
	}

	public void makeImageBoxtInAbsolutePosition(PdfWriter writer,
			ImageBox imagebx) throws IOException, DocumentException {

		PdfContentByte canvas = writer.getDirectContentUnder();
		canvas.saveState();
		try {
			Image img = Image.getInstance(imagebx.getFilename());
			img.scaleAbsolute(
					Utilities.millimetersToPoints(imagebx.getXr()
							- imagebx.getXl()),
					Utilities.millimetersToPoints(imagebx.getYr()
							- imagebx.getYl()));
			img.setAbsolutePosition(
					Utilities.millimetersToPoints(imagebx.getXl()),
					Utilities.millimetersToPoints(imagebx.getYl()));
			writer.getDirectContent().addImage(img);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		canvas.restoreState();

	}

	public void makeRotatedTextBoxtInAbsolutePosition(PdfWriter writer,
			String text, BoxConfig config) throws IOException,
			DocumentException {
		// LOG.info("makeRotatedTextBoxtInAbsolutePosition");
		PdfContentByte canvas = writer.getDirectContent();
		canvas.saveState();
		canvas.beginText();
		canvas.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE);
		canvas.setLineWidth(config.getWidthTextLine());

		Phrase ptext = new Phrase(text, new Font(config.getBf(),
				config.getFontSize()));
		ColumnText column = new ColumnText(canvas);
		ColumnText.showTextAligned(canvas, config.getgAlign(), ptext,
				Utilities.millimetersToPoints(config.getXl()),
				Utilities.millimetersToPoints(config.getYl()),
				config.getRotation());
		column.go();
		canvas.endText();
		canvas.restoreState();
	}

	public void makeVerticalTextBoxInAbsolutePosition(PdfWriter writer,
			String text, BoxConfig config) throws IOException,
			DocumentException {

		//System.out.println("makeVerticalTextBoxInAbsolutePosition");
		PdfContentByte canvas = writer.getDirectContent();
		canvas.setLineWidth(config.getWidthTextLine());
		canvas.saveState(); // ?????
		VerticalText vbox = new VerticalText(canvas);
		vbox.setVerticalLayout(
				Utilities.millimetersToPoints(config.getXl()),
				Utilities.millimetersToPoints(config.getYr()),
				Utilities.millimetersToPoints(config.getYr())
						- Utilities.millimetersToPoints(config.getYl()), 1, 0);

		Phrase ptext = new Phrase(text, new Font(config.getBf(),
				config.getFontSize()));
		vbox.setAlignment(config.getgAlign());
		vbox.addText(ptext);
		vbox.go();
		canvas.restoreState(); // ?????

	}

	public void drawStamp(PdfWriter writer, List<Stamp> stamps) {
		if (stamps != null) {
			PdfContentByte canvas = writer.getDirectContentUnder();
			canvas.saveState();

			for (Stamp stamp : stamps) {

				canvas.setLineWidth(2);
				canvas.setLineDash(1, 1, 0);
				canvas.circle(Utilities.millimetersToPoints(stamp.getX()),
						Utilities.millimetersToPoints(stamp.getY()),
						Utilities.millimetersToPoints(stamp.getR()));
				canvas.stroke();

			}
			canvas.restoreState();
		}
	}

	protected PdfPTable makeTableInAbsolutePosition(PdfWriter writer,
			Object cert, TableConfig tablecon) throws DocumentException,
			IOException {
		
		// LOG.info("Make table"); 
		PdfPTable table = new PdfPTable(tablecon.getColnumber());
		
		makeTableHeader(writer, table, tablecon, cert);
		makeTableBody(table, tablecon, cert);
		makeTableFooter(writer, table, tablecon, cert);
		table.completeRow();

		table.writeSelectedRows(0, -1,
				Utilities.millimetersToPoints(tablecon.getXl()),
				Utilities.millimetersToPoints(tablecon.getYr()),
				writer.getDirectContent());
		
		//LOG.info("Table created ");

	    //LOG.info("Table height : " + table.getTotalHeight()
		// + "  Calculate height : " + getTableHeight(table));
		 
		//LOG.info("Table height : "
		// + Utilities.pointsToMillimeters(table.getTotalHeight())
		// + "  Calculate height : "
		// + Utilities.pointsToMillimeters(getTableHeight(table)));
				 
		return table;
	}

	private void makeTableFooter(PdfWriter writer, PdfPTable table,
			TableConfig tablecon, Object cert) throws DocumentException,
			IOException {
		List<CTCell> row = tablecon.getFooterRow();
		
		if (row != null) {
			row.get(1).setText("" + "[cert.text]");
			row.get(2).setText("" + "[cert.text]");
			row.get(3).setText("[cert.text]");

			writeRow(table, row);
			table.getRow(table.getLastCompletedRowIndex()).setMaxHeights(
					Utilities.millimetersToPoints(tablecon.getHeightrows().get(
							tablecon.getHeightrows().size() - 1)));
		}
	}

	private void makeTableHeader(PdfWriter writer, PdfPTable table,
			TableConfig tablecon, Object cert) throws DocumentException,
			IOException {

		float[] widths = new float[tablecon.getColnumber()];
		for (int i = 0; i < tablecon.getColnumber(); i++) {
			widths[i] = Utilities.millimetersToPoints(tablecon.getColwidths()
					.get(i));
		}
		table.setTotalWidth(Utilities.millimetersToPoints(tablecon.getWidth()));

		try {
			table.setWidths(widths);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		table.setLockedWidth(true);
       
		for (int i = 0; i < tablecon.getHeader().size(); i++) {
			
			List<CTCell> row = tablecon.getHeader().get(i);
                
			writeRow(table, row);
			table.getRow(table.getLastCompletedRowIndex()).setMaxHeights(
					Utilities.millimetersToPoints(tablecon.getHeightrows().get(
							i)));
		}

	}
		
	public void drawWatermark(PdfWriter writer, String text, BoxConfig config) throws IOException, DocumentException {
		PdfContentByte canvas = writer.getDirectContentUnder();
		
		Phrase ptext = new Phrase(text, new Font(config.getBf(),
				config.getFontSize()));
		
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, ptext,
				Utilities.millimetersToPoints(config.getXl()),
				Utilities.millimetersToPoints(config.getYl()),
				config.getRotation());
	}

// =============================================================================================
// Table body filling functions 
// =============================================================================================
	private void makeTableBody(PdfPTable table, TableConfig tablecon,
			Object cert) throws IOException, DocumentException {
        // LOG.info("Make Table Body");
        
		List<CTCell> row = tablecon.getBodyRow();
		table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
		float ht = 0f;
        boolean nextpage = false;
		ProductIterator cursor = ((Certificate) cert).getIterator();
         
		try {
			while (!nextpage && cursor.hasNext() ) {
				Product product = cursor.next();
				fillInRow(row, product);
				writeRow(table, row);
				ht += table.getRows().get(table.getLastCompletedRowIndex())
							.getMaxHeights();
                    
				if (ht > Utilities.millimetersToPoints(tablecon
							.getWorkHeight())) {
					    Product bufProduct = new Product();
					    float tableLimit = Utilities.millimetersToPoints(tablecon.getWorkHeight());
					    
					    while (ht > tableLimit) {
					    	PdfPCell[] cells = table.getRows().get(table.getLastCompletedRowIndex()).getCells();
					    	int ind = 0, i = 0;
					    	float cellMaxHeight = 0.0f;
					    	for (PdfPCell cell : cells) {
					    		if (cell.getHeight() > cellMaxHeight) {
					    			cellMaxHeight = cell.getHeight();
					        	    ind = i;				        	
					    		}
					    		i++;
					    	}
					    	
					    	ht -= table.getRows().get(table.getLastCompletedRowIndex())
				        			.getMaxHeights();
					    	table.deleteLastRow();
				    	
					        if (truncateProductField(product, bufProduct, ind)) {
					        	fillInRow(row, product);						
					        	writeRow(table, row);
					        	ht += table.getRows().get(table.getLastCompletedRowIndex())
									.getMaxHeights();
					        } else {
					        	if (ht + cellMaxHeight > tableLimit) {
					        		mergeProducts(product, bufProduct);
					        	}
					        }
					    }
					    cursor.insertProductInNextPosition(bufProduct);
					    nextpage = true;
					    break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		float delta = Utilities.millimetersToPoints(tablecon.getWorkHeight())
				- ht;
		PdfPRow tabrow = table.getRows().get(table.getLastCompletedRowIndex());
		tabrow.setMaxHeights(tabrow.getMaxHeights() + delta);
	}
	
	private void mergeProducts(Product product, Product bufProduct) {
	    bufProduct.setNumerator(ifNULL(product.getNumerator()) + ifNULL(bufProduct.getNumerator()));
	    bufProduct.setTovar(ifNULL(product.getTovar()) + ifNULL(bufProduct.getTovar()));
	    bufProduct.setFobvalue(ifNULL(product.getFobvalue()) + ifNULL(bufProduct.getFobvalue()));
	    bufProduct.setKriter(ifNULL(product.getKriter()) + ifNULL(bufProduct.getKriter()));
	    bufProduct.setSchet(ifNULL(product.getSchet()) + ifNULL(bufProduct.getSchet()));
	    bufProduct.setVes(ifNULL(product.getVes()) + ifNULL(bufProduct.getVes()));
	    bufProduct.setVidup(ifNULL(product.getVidup()) + ifNULL(bufProduct.getVidup()));
    }

	private String ifNULL(String str) {
		return str == null ? "" : str;
	}

	protected void writeRow(PdfPTable table, List<CTCell> row)
			throws DocumentException, IOException {
		
		for (int i = 0; i < row.size(); i++) {
			table.addCell(makeCell(row.get(i)));
		}
	}

	private PdfPCell makeCell(CTCell ctCell) throws DocumentException,
			IOException {
		
		//!-- moved to page config 5.5.2016 
		//  BaseFont bf = BaseFont.createFont(
		//		fontpath + ctCell.getFont() + ".ttf", 
		//		BaseFont.IDENTITY_H,
		//		BaseFont.EMBEDDED);
		
		PdfPCell cell = new PdfPCell(new Phrase(ctCell.getText(), new Font(ctCell.getBf(),
				ctCell.getFontsize())));
		
		cell.setBorder(ctCell.getBorder());
		cell.setBorderWidth(ctCell.getBorderWidth());
		if (ctCell.isFill()) {
			cell.setGrayFill(0.9f);
		}
		cell.setHorizontalAlignment(ctCell.getAlign());
		cell.setVerticalAlignment(ctCell.getVerticalAlign());
		cell.setColspan(ctCell.getColspan());
		return cell;
	}
	

	protected void fillInRow(List<CTCell> row, Product product) {
		// overwrite it in subclass
	}
	
	protected boolean truncateProductField(Product product, Product bproduct, int ind) {
		String bstr = null;
		boolean ret = false;
		
		switch(ind) {
		   case 0:
			  bstr = product.getNumerator();  break;
		   case 1:
			  bstr = product.getVidup();  break; 
		   case 2:
			  bstr = product.getTovar();  break;
		   case 3:
			  bstr = product.getKriter();  break;
		   case 4:
			  bstr = product.getVes();  break;
		   case 5:
			  bstr = product.getSchet();  break;
		}
		
		int pos = bstr.lastIndexOf(" ");
		
		if (pos > 0) {
			ret = true;
			switch(ind) {
			   case 0:
				  bproduct.setNumerator(bstr.substring(pos) + bproduct.getNumerator());
				  product.setNumerator(bstr.substring(0, pos));
				  break;
			   case 1:
				  bproduct.setVidup(bstr.substring(pos) + bproduct.getVidup());
				  product.setVidup(bstr.substring(0, pos));
				  break; 
			   case 2:
				  bproduct.setTovar(bstr.substring(pos) + bproduct.getTovar());
				  product.setTovar(bstr.substring(0, pos));
				  bstr = product.getTovar();  break;
			   case 3:
				  bproduct.setKriter(bstr.substring(pos) + bproduct.getKriter());
				  product.setKriter(bstr.substring(0, pos));
				  bstr = product.getKriter();  break;
			   case 4:
 				  bproduct.setVes(bstr.substring(pos) + bproduct.getVes());
				  product.setVes(bstr.substring(0, pos));
				  bstr = product.getVes();  break;
			   case 5:
				  bproduct.setSchet(bstr.substring(pos) + bproduct.getSchet());
				  product.setSchet(bstr.substring(0, pos));
				  bstr = product.getSchet();  break;
			}
		}
		return ret;
	}

	private float getTableHeight(PdfPTable table) {
		Float height = 0f;
		for (PdfPRow row : table.getRows()) {
			height += row.getMaxHeights();
		}
		return height;
	}
	
// =============================================================================================
// Get content functions
// =============================================================================================
	protected String getCertificateTextByMap(Object object, String map) {

		Certificate certificate = (Certificate) object;
		String str = "";

		if ("exporter".equals(map)) {
			str = 
					renderString(getExporterName(certificate), checkfield(certificate.getAdress()) ? ", " : " ") + 
					renderString(certificate.getAdress(), " ") +
					((certificate.getExpp() != null && certificate.getExpp().trim() != "" ) || 
					(certificate.getExpadress() != null && certificate.getExpadress().trim() != "") ? "  по поручению  " : "") +
					renderString(certificate.getExpp(), checkfield(certificate.getExpadress()) ? ", " : " ") + 
					renderString(certificate.getExpadress(),  ""); 
					
		} else if  ("exporterenglish".equals(map)) {
			str = 
					renderString(getExporterName(certificate), checkfield(certificate.getAdress()) ? ", " : " ") + 
					renderString(certificate.getAdress(), " ") +
					((certificate.getExpp() != null && certificate.getExpp().trim() != "" ) || 
					(certificate.getExpadress() != null && certificate.getExpadress().trim() != "") ? "  by order  " : "") +
					renderString(certificate.getExpp(), checkfield(certificate.getExpadress()) ? ", " : "") + 
					renderString(certificate.getExpadress(),  "");

		} else if ("importer".equals(map)) {
			str =
					renderString(certificate.getPoluchat(), checkfield(certificate.getAdresspol()) ? ", " : " ") + 
					renderString(certificate.getAdresspol(), " ") +
					((certificate.getImporter() != null && certificate.getImporter().trim() != "" ) || 
					(certificate.getAdressimp() != null && certificate.getAdressimp().trim() != "") ? "  по поручению  " : "") +
					renderString(certificate.getImporter(), checkfield(certificate.getAdressimp()) ? ", " : " ")   + 
					renderString(certificate.getAdressimp(), "");
			
		} else if ("importerenglish".equals(map)) {
			str = 
					renderString(certificate.getPoluchat(), checkfield(certificate.getAdresspol()) ? ", " : " ") + 
					renderString(certificate.getAdresspol(), " ") +
					((certificate.getImporter() != null && certificate.getImporter().trim() != "" ) || 
					(certificate.getAdressimp() != null && certificate.getAdressimp().trim() != "") ? "  by order  " : "") +
					renderString(certificate.getImporter(), checkfield(certificate.getAdressimp()) ? ", " : " ")   + 
					renderString(certificate.getAdressimp(), "");
					
		}else if ("drive".equals(map)) {
			str = renderString(certificate.getTransport(), "\n") + 
				  renderString(certificate.getMarshrut(), "");
		} else if ("certnumber".equals(map)) {
			str = certificate.getNomercert();
		} else if ("blanknumber".equals(map)) {
			str = getBlankNumberByPageNumber(certificate.getNblanka(), certificate.getCurrentlist());
		} else if ("subcountry".equals(map)) {
			str = CountryConverter.getCountryNameByCode(certificate.getStranapr());
		} else if ("sourcecountry".equals(map)) {
			str = CountryConverter.getCountryNameByCode(certificate.getStranav());
		} else if ("note".equals(map)) {
			str = certificate.getOtmetka() == null ? "" : certificate.getOtmetka();
		} else if ("status".equals(map)) {
			str = certificate.getStatus() == null ? "ДЕЙСТВИТЕЛЬНЫЙ" : certificate.getStatus();	
		} else if ("department".equals(map)) {	
			str = "Унитарное предприятие по оказанию услуг \""
					+ certificate.getOtd_name() + "\", "
					+ certificate.getOtd_addr_index() + ", "
					+ certificate.getOtd_addr_city() + ", "
					+ certificate.getOtd_addr_line() + ", "
					+ certificate.getOtd_addr_building();
		} else if ("departmentenglish".equals(map)) {
			str = 	certificate.getEotd_name() + ", "
					+ certificate.getOtd_addr_building() + ", "
					+ certificate.getEotd_addr_line() + ", "
					+ certificate.getOtd_addr_index() + ", "
					+ certificate.getEotd_addr_city();
		}else if ("expert".equals(map)) {
			str = renderString(certificate.getExpert(), "");;
		} else if ("customer".equals(map)) {
			str = renderString(certificate.getRukovod(), "");
		} else if ("dateexpert".equals(map)) {
			str = certificate.getDatacert();
		} else if ("datecustomer".equals(map)) {
			str = certificate.getDatacert();
		} else if ("issuecountry".equals(map)) {
			str = (certificate.getStranap() == null ? "Республике Беларусь" : CountryConverter.getCountryNameByCode(certificate.getStranap()));
		} else if ("listnumber".equals(map)) {
			str = "" + certificate.getCurrentlist();  
		} else if ("place".equals(map)) {
			str = "" + certificate.getEotd_addr_city();  
		} else if ("exporterplace".equals(map)) {
			str = "" + certificate.getEotd_addr_city();  
		} else if ("category".equals(map)) {
			str = "" + certificate.getCategory();  
		}

		return str;
	}

	private String getBlankNumberByPageNumber(String blanks, int currentlist) {
		String blank = "";

		if (blanks.indexOf(",") > -1 || blanks.indexOf("-") > -1) {
			List<String> numbers = new ArrayList<String>();
			
			if (blanks != null && !blanks.trim().isEmpty()) {
				blanks = blanks.trim().replaceAll("\\s*-\\s*", "-");
				String[] lst = blanks.split(",");
				
				for (String str : lst) {
					numbers.addAll(CCIUtil.getSequenceNumbers(str));
				}
			}

			try {
				blank = numbers.get(currentlist);
			} catch (Exception ex) {
				LOG.info(ex.getMessage());
			}
		} else {
			blank = blanks;
		}
		return blank;	
	}

	private String getExporterName(Certificate cert) {
		String exporter = cert.getKontrp();
		
        if ((exporter == null)  || (EMPTY_STRING.equals(exporter))) {
        	exporter = cert.getKontrp() != null ? cert.getKontrp() : EMPTY_STRING;
        }
		return exporter;
	}

	protected boolean checkfield(String field) {
		return (trim(field).length() > 0);
	}

	private String trim(String str) {
        if (str != null) {
          return str.trim();  	
        } else {
          return ""; 	
        }
	}

	protected String renderString(String field, String strtermination) {
		return (field != null && field.trim() != "") ? field.trim() + strtermination : "";
	}
}
