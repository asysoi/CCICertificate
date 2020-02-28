package cci.repository.owncert;

import java.util.List;

import org.springframework.security.core.Authentication;

import cci.model.cert.Report;
import cci.model.owncert.OwnCertificate;
import cci.model.owncert.OwnCertificateExport;
import cci.model.owncert.OwnCertificateHeaders;
import cci.model.owncert.OwnCertificates;
import cci.repository.SQLBuilder;
import cci.service.owncert.TNVEDRegexpTemplate;
import cci.web.controller.owncert.OwnFilter;
import cci.web.controller.owncert.ViewWasteOwnCertificate;

public interface OwnCertificateDAO {
    // ------------   RESTFUL methods -------------------------------
	OwnCertificates getOwnCertificates(OwnFilter filter, boolean b);

	OwnCertificateHeaders getOwnCertificateHeaders(OwnFilter filter, boolean b);

	OwnCertificate findOwnCertificateByID(OwnFilter filter) throws Exception ;

    OwnCertificate saveOwnCertificate(OwnCertificate certificate) throws Exception;

	OwnCertificate updateOwnCertificate(OwnCertificate certificate);
	
	OwnCertificate findOwnCertificateByNumber(String number, String blanknumber, String datecert, String otd_id) throws Exception;
	
	boolean deleteOwnCertificate(String number, String blanknumber, String datecert, String otd_id);
	
	// OwnCertificate checkOwnCertificateExist(String number, String blanknumber, String datecert) throws Exception;
	
	// ---------------- Web orientated methods ----------------------
	int getViewPageCount(SQLBuilder builder);

	List<OwnCertificate> findViewNextPage(String[] fields, int page, int pagesize, int pagecount, String orderby,
			String order, SQLBuilder builder);

	List<OwnCertificateExport> getCertificates(String[] fields, String orderby, String order, SQLBuilder builder);
	
	List<OwnCertificate> getOrshaCertificates(String reportdate, String query, String otd_id);

	boolean updateOwnCertificateFileName(String number, String blanknumber, String datecert, String filename);

	List<Report> getReport(String[] fields, SQLBuilder builder);

	List<ViewWasteOwnCertificate> getWasteOwnCertificates(String reportdate, List<TNVEDRegexpTemplate> templates);

}
