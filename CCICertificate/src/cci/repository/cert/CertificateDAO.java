package cci.repository.cert;

import java.util.List;
import java.util.Map;

import cci.model.cert.Certificate;
import cci.model.cert.CertificateList;
import cci.model.cert.Report;
import cci.model.fscert.FSCertificate;
import cci.repository.SQLBuilder;
import cci.web.controller.cert.CertFilter;
import cci.web.controller.fscert.FSFilter;

public interface CertificateDAO {

	public Certificate findByID(Long id);
	
	public List<Certificate> findByNBlanka(String number);
	
	public List<Certificate> findByNumberCert(String number);
	
	public Certificate check(Certificate cert); 
	
	public List<Certificate> findAll();
		
	public List<Certificate> findByCertificate(Certificate cert);
	
	public List<Certificate> findViewNextPage(String[] dbfields, int page, int pagesize, int pagecount, String orderby, String order, SQLBuilder builder);

	public int getViewPageCount(SQLBuilder builder);

	public Map<String, String> getBranchesList();
	
	public Map<String,String> getACL();

	public Map<String, Map<String,String>> getCountriesList();

	public List<String> getFormsList();

	public List<Certificate> getCertificates(String[] dbfields, String orderby, String order,
			SQLBuilder builder);

	public List<Report> getReport(String[] fields, SQLBuilder builder, Boolean onfilter);

	public List<Certificate> findViewNextReportPage(String[] dbfields, int page, int pagesize,
			String orderby, String order, SQLBuilder builder);

	public int getViewPageReportCount(SQLBuilder builder);

	public List<Certificate> getReportCertificates(String[] dbfields, String orderby,
			String order, SQLBuilder builder);

	
	// --------------  Certificate methods for REST ------------------------
	public long save(Certificate cert) throws Exception;

	public Certificate update(Certificate cert, String otd_id) throws Exception;

	public String getCertificates(CertFilter filter, boolean b) throws Exception;

	public Certificate getCertificateByNumber(String number, String blanknumber, String date) throws Exception;

	public void deleteCertificate(String number, String blanknumber, String date, String otd_id) throws Exception;

	public long getNextValuePool(String seq_name, int poolsize) throws Exception;

	public List<Certificate> getCertificatesPage(int page, int blocksize) throws Exception;
	
}
