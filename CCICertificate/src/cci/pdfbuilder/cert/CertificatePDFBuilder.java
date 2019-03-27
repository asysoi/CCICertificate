package cci.pdfbuilder.cert;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cci.config.cert.PDFConfigReader;
import cci.config.cert.PDFPageConfig;
import cci.config.cert.XMLConfigReader;
import cci.model.cert.Certificate;
import cci.model.cert.Product;
import cci.pdfbuilder.PDFBuilder;
import cci.pdfbuilder.PDFBuilderFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

public class CertificatePDFBuilder {
	public static final String FONT_DIR = "resources/fonts/";
	private Document document;
	private PdfWriter writer;
	private PDFConfigReader xreader;
	private PDFPageConfig pconfig;
	private String fontpath;
	
	public static void main(String[] arg) {
		CertificatePDFBuilder builder = new CertificatePDFBuilder();
		Certificate cert = new Certificate();
		cert.setKontrp("Exporter Address Exporter");
		cert.setKontrp("Получатель");
		cert.setImporter("Importer Importer Importer Importer Importer Importer Importer Importer Importer Importer Importer Importer");
		cert.setMarshrut("Маршрут движения транспорта");
		cert.setNomercert("1234567890");
		cert.setNblanka("000678");
		cert.setDatacert("23.12.2016");
		cert.setStranap("Страна Получатель");
		cert.setStranapr("Страна ПРоизводитель");
		cert.setStranav("Страна Ввоза");
		cert.setEotd_name("Minsk branch of BelCCI");
		cert.setExpert("Minsk");
		cert.setRukovod("Minsk");
		cert.setOtmetka("Note about textil certificate");
		cert.setOtmetka("Это отметка о сертификате для Вьетнама. Получение сертификата во вьетнам ничем не отличается от ...");
		cert.setEotd_addr_city("Minsk");
		cert.setCategory("Category");
		
		cert.setForms(PDFBuilderFactory.PAGE_TEXTILE);
		
		List<Product> products = new ArrayList<Product>();
		for (int i = 1; i < 30; i++) {
				Product product = new Product();
				product.setNumerator(i + ".");
				product.setTovar("Tovar " + i);
				product.setVidup("vidup " + i);
				product.setVes("ves " + i);
				product.setSchet("schet");
				products.add(product);
		}
		cert.setProducts(products);
		
		
		try {
		   builder.createPdf("c:\\tmp\\cert\\certificate.pdf", cert, "c:\\Java\\git\\CCICertificate\\CCICertificate\\WebContent\\resources\\config\\pages.xml", "c:\\Windows\\Fonts\\");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void createPdf(String outfilename, Certificate cert,
			String configFileName, String fpath) throws IOException, DocumentException {
		
		fontpath = fpath;
		
		// step 1
		document = new Document(PageSize.A4, 0, 0, 0, 0);

		// step 2
		writer = PdfWriter.getInstance(document,
				new FileOutputStream(outfilename));
		// step 3
		document.open();

		// step 4
		xreader = XMLConfigReader.getInstance(configFileName, fontpath);
		createMain(cert);

		// step 5
		document.close();
	}
	
	

	private void createMain(Certificate cert) throws DocumentException, IOException {
		String pagename; 
		
		if (cert.getForms() != null) {
			pagename = cert.getForms().trim();
		} else {
			pagename = PDFBuilderFactory.PAGE_CT1;
		}
		
		cert.setCurrentlist(0);  // start from main certification list
		
		while (cert.getIterator().hasNext()) {
		    pconfig = xreader.getPDFPageConfig(pagename);
		    PDFBuilder pmaker = PDFBuilderFactory.getPADFBuilder(pagename);
		    pmaker.createPDFPage(writer, cert, pconfig);
		    pagename = pconfig.getNextPage();
		    cert.setCurrentlist(cert.getCurrentlist() + 1);
;		    document.newPage();
		}
	}
	
}
