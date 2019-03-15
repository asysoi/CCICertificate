package cci.pdfbuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

@Service
public class PDFUtils {
    private final int x_pos = 468;
    private final int y_pos = 765;
    
	/* ----------------------------------
	 * 	 PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
	 *	 FileOutputStream fos = new FileOutputStream(dest);
     *   fos.write(output.toByteArray());
     *   fos.close();
	 * ---------------------------------- */
	public ByteArrayOutputStream mergePdf(String src, String back1, String back2, List<String> numbers)
			throws IOException, DocumentException {
		PdfReader s_back_1 = new PdfReader(back1);
		PdfReader s_back_2 = new PdfReader(back2);

		PdfReader reader = new PdfReader(src);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(reader, output);
		invert(stamper);

		PdfImportedPage back_1 = stamper.getImportedPage(s_back_1, 1);
		PdfImportedPage back_2 = stamper.getImportedPage(s_back_2, 1);

		int n = reader.getNumberOfPages();
		PdfContentByte canvas;

		for (int i = 1; i <= n; i++) {
			canvas = stamper.getOverContent(i);
			canvas.addTemplate(i == 1 ? back_1 : back_2, 0, 0);

			if (numbers != null && numbers.size() >= i && !numbers.get(i-1).isEmpty()) {
				Font ft = new Font() ;
        		ft.setColor(BaseColor.RED);
        		ft.setSize(14);
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(numbers.get(i-1), ft), x_pos, y_pos, 0);
			}
		}
		stamper.close();
		s_back_1.close();
		s_back_2.close();
		reader.close();
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
}
