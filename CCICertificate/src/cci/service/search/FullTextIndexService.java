package cci.service.search;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cci.model.cert.Certificate;
import cci.repository.SQLBuilder;
import cci.repository.cert.CertificateDAO;
import cci.repository.cert.SQLBuilderCertificate;

@Service
public class FullTextIndexService {
	private static final Logger LOG = Logger.getLogger(FullTextIndexService.class);
	
	private final String ID = "id";
	private final String CONTENT = "content";
	private final String DATE = "date";
	
	@Autowired
	IndexManager indexManager;

	@Autowired
	private CertificateDAO certificateDAO;
		
	/* ------------------------------------------------------
	 * Create FulTextIndex of CT Certificates
	 * 
	 * ------------------------------------------------------ */
	public void createFullTextIndexCertificates(String indexPath, int blockSize) throws SQLException {
		 indexManager.clearIndexDatabase(indexPath);
		 addCertificatesToIndex(indexPath, blockSize, false);
	}
		
   /* ***************************
	*  Filling  fulltext index
	* ****************************/ 
	private void addCertificatesToIndex(String indexPath, int blocksize, boolean create) throws SQLException {
		SQLBuilder builder = new SQLBuilderCertificate();
		int documentsAmount = certificateDAO.getViewPageCount(builder);
		
		try {
			for (int page = 1; page <= documentsAmount/blocksize; page++) {
				    List<Certificate> certs = certificateDAO.getCertificatesPage(page, blocksize);
					indexManager.addUpdateIndex(indexPath, certs, ID, CONTENT, DATE, create);
					certs.clear();
				} 
		} catch (Exception e) {
			LOG.error(e.getMessage());;
		} 
	}

	
   /* --------------------------------------
	*  Filling  fulltext index
	* ------------------------------------ */ 
	public List<Certificate> search(String indexPath, String query, int page, int pagesize, boolean sortbydate) {
		// TODO Auto-generated method stub
		return null;
	} 	

}
