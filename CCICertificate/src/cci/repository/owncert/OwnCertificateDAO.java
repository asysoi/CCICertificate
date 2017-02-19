package cci.repository.owncert;

import java.util.List;

import cci.model.owncert.OwnCertificate;
import cci.model.owncert.OwnCertificateExport;
import cci.model.owncert.OwnCertificateHeaders;
import cci.model.owncert.OwnCertificates;
import cci.repository.SQLBuilder;
import cci.web.controller.owncert.OwnFilter;

public interface OwnCertificateDAO {
    // ------------   RESTFUL methods -------------------------------
	OwnCertificates getOwnCertificates(OwnFilter filter, boolean b);

	OwnCertificateHeaders getOwnCertificateHeaders(OwnFilter filter, boolean b);

	OwnCertificate findOwnCertificateByID(int id) throws Exception ;

    OwnCertificate saveOwnCertificate(OwnCertificate certificate) throws Exception;

	OwnCertificate updateOwnCertificate(OwnCertificate certificate);
	
	OwnCertificate findOwnCertificateByNumber(String number) throws Exception;

	// ---------------- Web orientated methods ----------------------
	int getViewPageCount(SQLBuilder builder);

	List<OwnCertificate> findViewNextPage(String[] fields, int page, int pagesize, int pagecount, String orderby,
			String order, SQLBuilder builder);

	List<OwnCertificateExport> getCertificates(String[] fields, String orderby, String order, SQLBuilder builder);



}
