package cci.pdfbuilder.cert;

public class PDFBuilderFactory {
	  public static final String PAGE_CT1 = "��-1";
	  public static final String PAGE_CT1_b = "��-1b";
	  public static final String PAGE_GENERAL = "�����";
	  public static final String PAGE_GENERAL_b = "�����b";
	  public static final String PAGE_GENERAL_ENG = "����� ����.";
	  public static final String PAGE_GENERAL_ENG_b = "����� ����.b";
	  public static final String PAGE_CT2 = "��-2";
	  public static final String PAGE_CT2_b = "��-2b";
	  public static final String PAGE_CT2_ENG = "��-2 ����.";
	  public static final String PAGE_CT2_ENG_b = "��-2 ����.b";
	  public static final String PAGE_A = "�";
	  public static final String PAGE_TEXTILE = "��������";
	  
	  
	  
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
    		  builder = new ADFBuilder();    		  
    	  } else if (pagename.equals(PAGE_TEXTILE)) {
    		  builder = new TextilePDFBuilder();    		  
    	  }  
    	  return builder;
      }
}
