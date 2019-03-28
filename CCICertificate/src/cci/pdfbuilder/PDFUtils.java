package cci.pdfbuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import cci.web.controller.cert.CertificateController;

import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PRStream;

@Service
public class PDFUtils {
	private static final Logger LOG=Logger.getLogger(PDFUtils.class);
    private final int x_pos = 468;
    private final int y_pos = 765;
    
	/* ----------------------------------
	 * 	 PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
	 *	 FileOutputStream fos = new FileOutputStream(dest);
     *   fos.write(output.toByteArray());
     *   fos.close();
	 * ---------------------------------- */
	public ByteArrayOutputStream mergePdf(String src, String back1, String back2, List<String> numbers, int checksize)
			throws IOException, DocumentException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		boolean isImage = checkImagesInFile(src, checksize);
		
		if (isImage) {
			PdfReader reader = new PdfReader(src);
			PdfStamper stamper = new PdfStamper(reader, output);
			stamper.close();
			reader.close();
		} else {
			PdfReader reader = new PdfReader(src);
			PdfStamper stamper = new PdfStamper(reader, output);
			invert(stamper);
			PdfReader s_back_1 = new PdfReader(back1);
			PdfReader s_back_2 = new PdfReader(back2);

			PdfImportedPage back_1 = stamper.getImportedPage(s_back_1, 1);
			PdfImportedPage back_2 = stamper.getImportedPage(s_back_2, 1);

			int n = reader.getNumberOfPages();
			PdfContentByte canvas;

			for (int i = 1; i <= n; i++) {
				canvas = stamper.getOverContent(i);
				canvas.addTemplate(i == 1 ? back_1 : back_2, 0, 0);

				if (numbers != null && numbers.size() >= i && !numbers.get(i - 1).isEmpty()) {
					Font ft = new Font();
					ft.setColor(BaseColor.RED);
					ft.setSize(14);
					ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(numbers.get(i - 1), ft), x_pos,
							y_pos, 0);
				}
			}
			stamper.close();
			s_back_1.close();
			s_back_2.close();
			reader.close();
		}
		return output;
	}
    /* -----------------------------------------
     * 
     * 
     *-------------------------------------------*/
	void invert(PdfStamper stamper) {
		for (int i = stamper.getReader().getNumberOfPages(); i > 0; i--) {
			reinvertPage(stamper, i);
		}
	}
	/* -----------------------------------------
     * 
     * 
     *-------------------------------------------*/
	void reinvertPage(PdfStamper stamper, int page) {
		Rectangle rect = stamper.getReader().getPageSize(page);
		PdfContentByte cb = stamper.getOverContent(page);
		PdfGState gs = new PdfGState();
		gs.setFillOpacity(1f);
		gs.setBlendMode(PdfGState.BM_DARKEN);
		cb.setGState(gs);
		cb.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
	}
	
	private boolean checkImagesInFile(String filename, int checksize)  {
		PdfReader preader = null;
				
		try {
			preader = new PdfReader(filename); 
			PdfObject po; 
			PRStream pst;
			PdfObject type;
			int n = preader.getXrefSize(); //number of objects in pdf document
			
			for(int i=0;i<n;i++) {
			   po = preader.getPdfObject(i); 
			   if( po==null || !po.isStream()) continue;
			   pst = (PRStream) po;
			   type = pst.get(PdfName.SUBTYPE); 
			   // LOG.info(po.toString() + " | " + type.toString() + " | " + pst.getLength());
			   
			   if (type!=null && type.toString().equals(PdfName.IMAGE.toString()) && pst.getLength() > checksize ) {
				 LOG.info("Image in pdf file is found ! " + pst.getLength());
	             return true;  		      	   
			   } else {
				 LOG.info("Image in pdf file is NOT found ! " + pst.getLength());  
			   }
			}
		} catch (Exception ex) {
		  LOG.error(ex.getLocalizedMessage());	
		} finally {
		  if (preader != null) preader.close();
		}
		
		if (preader != null) preader.close();
		return false;
	}
}
