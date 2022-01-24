package cci.pdfbuilder.cert;

import java.util.List;

import cci.config.cert.CTCell;
import cci.model.cert.Product;
import cci.pdfbuilder.PDFBuilder;

public class CTUZBuilder extends PDFBuilder {
	
	protected void fillInRow(List<CTCell> row, Product product) {
		row.get(0).setText(product.getNumerator() == null ? "" : product.getNumerator());
		row.get(1).setText(product.getVidup() == null ? "" : product.getVidup());
		row.get(2).setText(product.getTovar() == null ? "" : product.getTovar());
		row.get(3).setText(product.getVes() == null ? "" : product.getVes());
		row.get(4).setText(product.getSchet() == null ? "" : product.getSchet());
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
			  bstr = product.getVes();  break;
		   case 4:
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
 				  bproduct.setVes(bstr.substring(pos) + bproduct.getVes());
				  product.setVes(bstr.substring(0, pos));
				  bstr = product.getVes();  break;
			   case 4:
				  bproduct.setSchet(bstr.substring(pos) + bproduct.getSchet());
				  product.setSchet(bstr.substring(0, pos));
				  bstr = product.getSchet();  break;
			}
		}
		
		return ret;
	}
}
