package cci.service.owncert;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cci.model.owncert.OwnCertificate;
import cci.model.owncert.OwnCertificateExport;
import cci.model.owncert.OwnCertificateHeaders;
import cci.model.owncert.OwnCertificates;
import cci.repository.SQLBuilder;
import cci.repository.owncert.OwnCertificateDAO;
import cci.service.cert.CertService;
import cci.web.controller.owncert.OwnFilter;


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
	
	// ------------------------------------------------------------------------------------
	//       RESTFUL methods  
	// ------------------------------------------------------------------------------------
	public OwnCertificates getOwnCertificates(OwnFilter filter)  {
	   return owncertificateDAO.getOwnCertificates(filter, true);
	}
	
	public OwnCertificateHeaders getOwnCertificateHeaders(OwnFilter filter) {
		   return owncertificateDAO.getOwnCertificateHeaders(filter, true);
	}
	
	public OwnCertificate getOwnCertificateById(int id) throws Exception {
		return owncertificateDAO.findOwnCertificateByID(id);
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
