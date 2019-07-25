package cci.service.cert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cci.model.cert.Certificate;
import cci.model.cert.CertificateList;
import cci.repository.cert.CertificateDAO;
import cci.web.controller.cert.CertFilter;

@Service
public class CertificateRestFulService {

	@Autowired
	private CertificateDAO certificateDAO;

	public String getCertificates(CertFilter filter) throws Exception {
	   return certificateDAO.getCertificates(filter, true);
	}

	public void addCertificate(Certificate certificate) throws Exception {
		certificateDAO.save(certificate);
	}

	public Certificate getCertificateByNumber(String number, String blanknumber) throws Exception {
		return certificateDAO.getCertificateByNumber(number, blanknumber);
	}

	public Certificate updateCertificate(Certificate certificate, String otd_id) throws Exception {
		return certificateDAO.update(certificate, otd_id);
	}

	public void deleteCertificate(String number, String blanknumber, String date, String otd_id) throws Exception {
	  certificateDAO.deleteCertificate(number, blanknumber, date, otd_id);
	}
}
