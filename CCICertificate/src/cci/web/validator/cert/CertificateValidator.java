package cci.web.validator.cert;

import java.util.ArrayList;
import java.util.List;

import cci.model.cert.Certificate;
import cci.pdfbuilder.PDFBuilderFactory;

public class CertificateValidator {
	 
	 public static List<String> validate(Certificate cert) {
		 List<String> errors=new ArrayList<String>();
		 
		 if (nullOrEmpty(cert.getNomercert())) {
			 errors.add("Certificate number isn't defined.");
		 } 
		 
		 if (nullOrEmpty(cert.getNblanka())) {
			 errors.add("Blank number isn't defined.");
		 }	 
		 
		 if (nullOrEmpty(cert.getDatacert())) {
			 errors.add("Date isn't defined.");
		 }	 
		 
		 if (nullOrEmpty(cert.getUnn())) {
			 errors.add("UNN of exporter isn't defined.");
		 }	 

		 if (nullOrEmpty(cert.getExpp()) && nullOrEmpty(cert.getKontrp()) && nullOrEmpty(cert.getExps()) && nullOrEmpty(cert.getKontrs())) {
			 errors.add("Exporter/consigner aren't defined.");
		 }	 
		 		 
		 if (nullOrEmpty(cert.getImporter()) && nullOrEmpty(cert.getPoluchat())) {
			 errors.add("Importer/consignee aren't defined.");
 		 }	 
		 
		 if (!PDFBuilderFactory.checkValidFormName(cert.getForms()) )  {
			 errors.add("Form name isn't correct.");
 		 }	 
		 
		 if ("Y".equals(cert.getAgree()) && nullOrEmpty(cert.getDataexpire())  )  {
			 errors.add("Expiration date isn't defined.");
 		 }	 

		 return errors;			 
	 }
	 
	 private static boolean nullOrEmpty(String str) {
		return (str == null || str.trim().length() == 0);
	 }
	 
}
