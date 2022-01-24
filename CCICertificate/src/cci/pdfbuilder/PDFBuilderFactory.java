package cci.pdfbuilder;

import cci.pdfbuilder.cert.APDFBuilder;
import cci.pdfbuilder.cert.CT1PDFBuilder;
import cci.pdfbuilder.cert.CT2PDFBuilder;
import cci.pdfbuilder.cert.CT3PDFBuilder;
import cci.pdfbuilder.cert.CTUZBuilder;
import cci.pdfbuilder.cert.EAVPDFBuilder;
import cci.pdfbuilder.cert.GeneralPDFBuilder;
import cci.pdfbuilder.cert.TextilePDFBuilder;

public class PDFBuilderFactory {
	  public static final String PAGE_CT1 = "СТ-1";
	  public static final String PAGE_CT1_b = "СТ-1b";
	  public static final String PAGE_GENERAL = "Общая";
	  public static final String PAGE_GENERAL_b = "Общаяb";
	  public static final String PAGE_GENERAL_ENG = "Общая англ.";
	  public static final String PAGE_GENERAL_ENG_b = "Общая англ.b";
	  public static final String PAGE_CT2 = "СТ-2";
	  public static final String PAGE_CT2_b = "СТ-2b";
	  public static final String PAGE_CT2_ENG = "СТ-2 англ.";
	  public static final String PAGE_CT2_ENG_b = "СТ-2 англ.b";
	  public static final String PAGE_A = "А";
	  public static final String PAGE_TEXTILE = "Текстиль";
	  public static final String PAGE_EAV = "EAV";
	  public static final String PAGE_EAV_b = "EAV.b";
	  public static final String PAGE_FS = "FS";
	  public static final String PAGE_FS_b = "FS_b";
	  public static final String PAGE_CTUZ = "СТ-1 Узбекистан";
	  public static final String PAGE_CT3 = "СТ-3";
	  public static final String PAGE_CT3_b = "СТ-3b";
 
	  	  
      public static PDFBuilder getPADFBuilder(String pagename) {
    	  PDFBuilder builder = null; 
    	  
    	  if (pagename.equals(PAGE_CT1)) {
    		  builder = new CT1PDFBuilder();
    	  } else if (pagename.equals(PAGE_CT1_b)) {
    		  builder = new CT1PDFBuilder();    		  
    	  } else if (pagename.equals(PAGE_GENERAL)) {
    		  builder = new GeneralPDFBuilder();    		  
    	  } else if (pagename.equals(PAGE_GENERAL_b)) {
    		  builder = new GeneralPDFBuilder();    		  
    	  } else if (pagename.equals(PAGE_GENERAL_ENG)) {
    		  builder = new GeneralPDFBuilder();    		  
    	  } else if (pagename.equals(PAGE_GENERAL_ENG_b)) {
    		  builder = new GeneralPDFBuilder();    		  
    	  } else if (pagename.equals(PAGE_CT2)) {
    		  builder = new CT2PDFBuilder();    		  
    	  } else if (pagename.equals(PAGE_CT2_b)) {
    		  builder = new CT2PDFBuilder();    		  
    	  } else if (pagename.equals(PAGE_CT2_ENG)) {
    		  builder = new CT2PDFBuilder();    		  
    	  } else if (pagename.equals(PAGE_CT2_ENG_b)) {
    		  builder = new CT2PDFBuilder();    		  
    	  } else if (pagename.equals(PAGE_A)) {
    		  builder = new APDFBuilder();    		  
    	  } else if (pagename.equals(PAGE_TEXTILE)) {
    		  builder = new TextilePDFBuilder();    		  
    	  } else if (pagename.equals(PAGE_EAV)) {
    		  builder = new EAVPDFBuilder();    		  
    	  }  else if (pagename.equals(PAGE_EAV_b)) {
    		  builder = new EAVPDFBuilder();    		  
    	  } else if (pagename.equals(PAGE_CT3)) {
    		  builder = new CT3PDFBuilder();    		  
    	  }  else if (pagename.equals(PAGE_CT3_b)) {
    		  builder = new CT3PDFBuilder();    		  
    	  }  else if (pagename.equals(PAGE_CTUZ)) {
    		  builder = new CTUZBuilder();
    	  }
    	  return builder;
      }
}
