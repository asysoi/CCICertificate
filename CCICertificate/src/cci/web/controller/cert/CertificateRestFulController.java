package cci.web.controller.cert;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cci.model.cert.Certificate;
import cci.service.FieldType;
import cci.service.cert.CertService;
import cci.service.cert.CertificateRestFulService;
import cci.service.search.FullTextIndexService;
import cci.service.search.SearchResult;
import cci.web.controller.cert.exception.AddCertificateException;
import cci.web.controller.cert.exception.CertificateDeleteException;
import cci.web.controller.cert.exception.CertificateUpdateException;
import cci.web.controller.cert.exception.NotFoundCertificateException;

@Controller
@RestController
public class CertificateRestFulController {

	private static final Logger LOG=Logger.getLogger(CertificateRestFulController.class);
	
	@Autowired
	private CertificateRestFulService restservice;
	@Autowired
    private CertService certService;
	@Autowired
    private FullTextIndexService indexService;

	/* -----------------------------------------
	 * Get list of numbers's certificates by filter
	 * ----------------------------------------- */
	@RequestMapping(value = "rcerts.do", method = RequestMethod.GET, headers = "Accept=application/csv")
	@ResponseStatus (HttpStatus.OK)
	public ResponseEntity<String> getCertificates(
			@RequestParam(value = "number", required = false) String number,
			@RequestParam(value = "nblank", required = false) String blanknumber,
			@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			Authentication aut) {
		
		String certificates = null;
		
		CertFilter filter = new CertFilter(number, blanknumber, from, to);
	    filter.setOtd_id(getOtd_idByRole(aut));
	
		try {
		   certificates = restservice.getCertificates(filter);
		
		   if (certificates == null ) {
			  throw (new NotFoundCertificateException("Не найдено сертификатов, удовлетворяющих условиям поиска: " + filter.toString()));
		   }
		} catch (Exception ex) {
			throw (new NotFoundCertificateException("Не найдено сертификатов, удовлетворяющих условиям поиска: " + filter.toString()));
		}
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Operation", "Get List Certificate Numbers");
		
		return new ResponseEntity<String>(certificates, responseHeaders, HttpStatus.OK);
	}

	/* -----------------------------
	 * Find OTD_ID by Role
	 * ----------------------------- */
	private String getOtd_idByRole(Authentication aut) {
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

	/* -----------------------------
	 * Add new certificate from XML body
	 * ----------------------------- */
	@RequestMapping(value = "rcert.do", method = RequestMethod.POST, headers = "Accept=application/xml")
	@ResponseStatus(HttpStatus.CREATED)
	public Certificate addXMLCertificate(@RequestBody Certificate certificate,
			Authentication aut) {
		
		String otd_id = getOtd_idByRole(aut);
		
		if (otd_id != null) {
			certificate.setOtd_id(Integer.parseInt(otd_id));
			try {
				restservice.addCertificate(certificate);
			} catch (Exception ex) {
				throw(new AddCertificateException("Ошибка добавления сертификата: " + ex.toString()));
			}
	   	} else {
	   		throw(new AddCertificateException("Добавлять сертификат может только авторизированный представитель отделения ."));
	   	}
		return certificate;
	}
	
	/* -----------------------------
	 * Get certificate by number & blanknumber
	 * ----------------------------- */
	@RequestMapping(value = "rcert.do", method = RequestMethod.GET, headers = "Accept=application/xml")
	@ResponseStatus(HttpStatus.OK)
	public Certificate getCertificateByNumber(
			@RequestParam(value = "number", required = true) String number,
			@RequestParam(value = "nblank", required = true) String blanknumber,
			@RequestParam(value = "date", required = true) String date,
			Authentication aut)  {
		
		String otd_id = getOtd_idByRole(aut);
		try {
			    
			    Certificate rcert = restservice.getCertificateByNumber(number, blanknumber, date);
			    
			    if (otd_id != null && rcert.getOtd_id() != Integer.parseInt(otd_id)) {
			    	throw(new NotFoundCertificateException("Нет доступа к серитификату номер " + number + ", выданному на бланке " +  blanknumber));
			    }
				return rcert; 
			} catch (Exception ex) {
				throw(new NotFoundCertificateException("Серитификат номер " + number + ", выданный на бланке " +  blanknumber + " не найден :  " + ex.toString()));			
		}
		
	}

	/* -----------------------------
	 * Update certificate
	 * ----------------------------- */
	@RequestMapping(value = "rcert.do", method = RequestMethod.PUT, headers = "Accept=application/xml")
	@ResponseStatus(HttpStatus.OK)
	public Certificate updateCertificate(@RequestBody Certificate cert, Authentication aut) {
		 Certificate rcert = null;
		 try {
			 String otd_id = getOtd_idByRole(aut);
			 if (otd_id == null) {
				 throw(new CertificateUpdateException("Изменить сертификат может только авторизированный представитель отделения."));
			 }
   		     rcert = restservice.updateCertificate(cert, otd_id);
		 } catch (NotFoundCertificateException ex) {
			 throw(new CertificateUpdateException("Cертификат номер " + cert.getNomercert() + ", выданный на бланке " +  cert.getNblanka() + " не найден в базе. Обновление невозможно. Добавьте сертификат в базу."));
		 } catch (Exception ex) {
			 throw(new CertificateUpdateException("Ошибка обновления сертификата номер " + cert.getNomercert() + ", выданного на бланке " +  cert.getNblanka() + "  :  " + ex.toString()));
		 }
		 
		 if (rcert == null) {
			 throw(new NotFoundCertificateException("Сертификат номер " + cert.getNomercert() + ", выданный на бланке " +  cert.getNblanka() + "  не найден и не может быть изменен."));		    	 
		 }
		 return rcert;
	}
	
	/* -----------------------------
	 * Delete certificate
	 * ----------------------------- */
	@RequestMapping(value = "rcert.do", method = RequestMethod.DELETE, headers = "Accept=application/txt")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ResponseEntity<String> deleteCertificate(
			@RequestParam(value = "number", required = false) String number,
			@RequestParam(value = "nblank", required = false) String blanknumber,
			@RequestParam(value = "date", required = false) String date,
			Authentication aut) {
		try {
			String otd_id = getOtd_idByRole(aut);
			 if (otd_id == null) {
				 throw(new CertificateDeleteException("Удалить сертификат может только авторизированный представитель отделения."));
			}
			restservice.deleteCertificate(number, blanknumber, date, otd_id);
		} catch(Exception ex) {
			throw(new CertificateDeleteException("Серитификат номер " + number + ", выданный " + date + " на бланке " +  blanknumber + "  не может быть удален: " + ex.toString()));	
		}
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("CertificateResponseHeader", "Message");
		return new ResponseEntity<String>("Certificate " + number + " deleted.", responseHeaders, HttpStatus.OK);
	}
	
	
	/* -----------------------------------------------------------
	 * Create full text certificate index certificates of origin
	 * -------------------------------------------------------- */
	@RequestMapping(value = "rindex.do", method = RequestMethod.GET, headers = "Accept=application/txt")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> createFullTextIndexCertificates(Authentication aut, HttpServletRequest request)  {
		try {
			String indexPath = request.getSession().getServletContext().getInitParameter("index.path");
			String pageSize = request.getSession().getServletContext().getInitParameter("index.pagesize");
			
		    indexService.createFullTextIndexCertificates(indexPath, Integer.parseInt(pageSize));  
		    HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("Operation", "Full Text Index creation");
			return new ResponseEntity<String>("Full Text Index created", responseHeaders, HttpStatus.OK);
		} catch (Exception ex) {
			throw(new NotFoundCertificateException("Ошибка создания полнотестового индекса: " + ex.getMessage()));			
		}
	}

	/* -----------------------------------------------------------
	 * Search certificates by full text index of certificates of origin
	 * -------------------------------------------------------- */
	@RequestMapping(value = "rsearch.do", method = RequestMethod.GET, headers = "Accept=application/txt")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> searchFullTextIndexCertificates(
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "pagesize", required = false) int pagesize,
			@RequestParam(value = "sortbydate", required = false) boolean sortbydate,
			Authentication aut, HttpServletRequest request)  {
		try {
			String indexPath = request.getSession().getServletContext().getInitParameter("index.path");
			
		    List<Certificate> list = indexService.search(indexPath, query, page, pagesize, sortbydate);  
		    HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("Operation", "Full Text Index creation");
			return new ResponseEntity<String>("Full Text Index created", responseHeaders, HttpStatus.OK);
		} catch (Exception ex) {
			throw(new NotFoundCertificateException("Ошибка создания полнотестового индекса: " + ex.getMessage()));			
		}
	}

	
	
	/* -----------------------------
	 * Exception handling 
	 * ----------------------------- */
	@ExceptionHandler(Exception.class)
	@ResponseBody
    public ResponseEntity<String> handleRESTException(Exception ex) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Error Name", ex.getClass().getName());
		responseHeaders.set("Content-Type", "application/json;charset=utf-8");
		return new ResponseEntity<String>(ex.toString(), responseHeaders,  HttpStatus.BAD_REQUEST);
    }
}
