package cci.service.owncert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cci.model.cert.Report;
import cci.model.owncert.OwnCertificate;
import cci.model.owncert.OwnCertificateExport;
import cci.model.owncert.OwnCertificateHeaders;
import cci.model.owncert.OwnCertificates;
import cci.repository.SQLBuilder;
import cci.repository.owncert.OwnCertificateDAO;
import cci.service.cert.CertService;
import cci.web.controller.owncert.OwnFilter;
import cci.web.controller.owncert.ViewWasteOwnCertificate;

@Service
public class OwnCertificateService {
	private static final Logger LOG=Logger.getLogger(OwnCertificateService.class);
	
	@Autowired
	private OwnCertificateDAO owncertificateDAO;
	@Autowired
	private CertService certService;

	// ------------------------------------------------------------------------------
	//  This method returns count of pages in certificate list 
	// ------------------------------------------------------------------------------
	public int getViewPageCount(SQLBuilder builder) {
		Locale.setDefault(new Locale("en", "en"));
		
		int counter = 0;
		try {
			counter = owncertificateDAO.getViewPageCount(builder);
		} catch (Exception ex) {
			LOG.info(ex.getMessage());
		}
		return counter;
	}

	// ------------------------------------------------------------------------------
	//  This method returns a current page of the certificate's list
	// ------------------------------------------------------------------------------
	public List<OwnCertificate> readCertificatesPage(String[] fields, int page, int pagesize, int pagecount,
			String orderby, String order, SQLBuilder builder) {
		Locale.setDefault(new Locale("en", "en"));

		List<OwnCertificate> certs = null;

		try {
			certs = owncertificateDAO.findViewNextPage(fields, page, pagesize, pagecount, orderby,
					order, builder);
		} catch (Exception ex) {
			LOG.info(ex.getMessage());
		}
		return certs;
	}
	
	// ------------------------------------------------------------------------------
	//  Method for Own Certificate Export
	// ------------------------------------------------------------------------------
	public List<OwnCertificateExport> readCertificates(String[] fields, String orderby, String order, SQLBuilder builder) {
		List<OwnCertificateExport> certs = null;

		try {
			certs = owncertificateDAO.getCertificates(fields, orderby, order, builder);
		} catch (Exception ex) {
			LOG.info(ex.getMessage());
		}
		return certs;
	}
	
	/* -----------------------------
	 * Find OTD_ID by Role - METHOD HAVE TO MOVED TO PARENT SERVICE CLASS - DUBLICATE CODE
	 * ----------------------------- */
	public String getOtd_idByRole(Authentication aut) {
		String ret = null;
		
		if (aut != null) {
			Iterator iterator = aut.getAuthorities().iterator();
			while (iterator.hasNext()) {
				String roleName = ((GrantedAuthority) iterator.next()).getAuthority();
								
				if  (certService.getACL().containsKey(roleName)) {
			      ret = certService.getACL().get(roleName);
				}
			}
		} else {
			LOG.info("Authentification object is not presented.");			
		}
		return ret;
	}
	
	/* ----------------------------------------------- 
	 * Convert certificate's
	 * numbers splited by delimeter into List 
	 * to write into certificate blanks
	 * ----------------------------------------------
	 */
	public List<String> splitOwnCertNumbers(String number, String addblanks) {
		List<String> ret = new ArrayList<String>();
		ret.add(number);
		if (addblanks != null && !addblanks.trim().isEmpty()) {

			addblanks = addblanks.trim().replaceAll("\\s*-\\s*", "-");
			addblanks = addblanks.replaceAll(",", ";");
			addblanks = addblanks.replaceAll("\\s+", ";");
			addblanks = addblanks.replaceAll(";+", ";");
			addblanks = addblanks.replaceAll(";\\D+;", ";");

			String[] lst = addblanks.split(";");

			for (String str : lst) {
				ret.addAll(getSequenceNumbers(str));
			}
		}
		return ret;
	}

	
	/* ----------------------------------------------- 
	 * Convert certificate's
	 * numbers range into List of separated numbers to write into certificate
	 * blanks ----------------------------------------------
	 */
	private Collection<String> getSequenceNumbers(String addblanks) {
		List<String> numbers = new ArrayList<String>();
		int pos = addblanks.indexOf("-");
		
		if (pos > 0) {
			String strFirstNumber = addblanks.substring(0, pos);
			String strLastNumber = addblanks.substring(pos + 1);
			
			if (strLastNumber.length() < strFirstNumber.length()) {
				strLastNumber = strFirstNumber.substring(0, strFirstNumber.length()-strLastNumber.length()) + strLastNumber; 
			}
			
			int firstnumber = Integer.parseInt(strFirstNumber);
			int lastnumber = Integer.parseInt(strLastNumber);
			
			for (int i = firstnumber; i <= lastnumber; i++) {
				numbers.add(addnull(i + ""));
			}
		} else if (!addblanks.trim().isEmpty())
			numbers.add(addblanks);
		return numbers;
	}
	
	private String addnull(String number) {
		if (number.length() < 7) {
			number = addnull("0"+number);
		}
		return number;
	}
	
	/* --------------------------------------------------------------------------------------
	 * Create pivot report grouped by selected parameter 
	 * ------------------------------------------------------------------------------------- */
	public List<Report> makeReports(String[] fields, SQLBuilder builder) {

		Locale.setDefault(new Locale("en", "en"));
		List<Report> reports = null;

		try {
			reports = owncertificateDAO.getReport(fields, builder);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return reports;
	}

	/* --------------------------------------------------------------------------------------
	 * Create Orsha report  
	 * ------------------------------------------------------------------------------------- */
	public List<OwnCertificate> getOrshaCertificates(String reportdate, String[] localities, String otd_id) {
		List<OwnCertificate> certs = null;

		try {
			certs = owncertificateDAO.getOrshaCertificates(reportdate, localities, otd_id);
		} catch (Exception ex) {
			LOG.info(ex.getMessage());
		}
		return certs;
	}
	
	/* --------------------------------------------------------------------------------------
	 * Create Waste Report report  
	 * ------------------------------------------------------------------------------------- */
	public List<ViewWasteOwnCertificate> getWasteOwnReport(String certdate, List<TNVEDRegexpTemplate> templates) {
		   List<ViewWasteOwnCertificate> certs = null;

		try {
			certs = owncertificateDAO.getWasteOwnCertificates(certdate, templates);
		} catch (Exception ex) {
			LOG.info(ex.getMessage());
		}
		return certs;
	}

	
	// ------------------------------------------------------------------------------------
	//       RESTFUL methods  
	// ------------------------------------------------------------------------------------
	public OwnCertificates getOwnCertificates(OwnFilter filter)  {
	   return owncertificateDAO.getOwnCertificates(filter, true);
	}
	
	public OwnCertificateHeaders getOwnCertificateHeaders(OwnFilter filter) {
		   return owncertificateDAO.getOwnCertificateHeaders(filter, true);
	}
	
	public OwnCertificate getOwnCertificateById(OwnFilter filter) throws Exception {
		return owncertificateDAO.findOwnCertificateByID(filter);
	}
	
	// propagation=Propagation.REQUIRED, rollbackFor=Exception.class
	@Transactional("own")
	public void addOwnSertificate(OwnCertificate certificate) throws Exception {
		owncertificateDAO.saveOwnCertificate(certificate);
	}

	@Transactional("own")
	public OwnCertificate updateOwnCertificate(OwnCertificate certificate) throws Exception {
		return owncertificateDAO.updateOwnCertificate(certificate);
	} 

	@Transactional("own")
	public boolean deleteOwnCertificate(String number, String blanknumber, String datecert, String otd_id) throws Exception {
		return owncertificateDAO.deleteOwnCertificate(number, blanknumber, datecert, otd_id);
	}
	
	public OwnCertificate getOwnCertificateByNumber(String number, String blanknumber, String datecert, String otd_id) throws Exception {
		return owncertificateDAO.findOwnCertificateByNumber(number, blanknumber, datecert, otd_id);
	}

	@Transactional("own")
	public boolean updateOwnCertificateFileName(String number, String blanknumber, String datecert, String filename) throws Exception {
		return owncertificateDAO.updateOwnCertificateFileName(number, blanknumber, datecert, filename);
	}

	
}
