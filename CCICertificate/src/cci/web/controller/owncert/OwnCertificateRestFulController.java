package cci.web.controller.owncert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import cci.model.owncert.OwnCertificate;
import cci.model.owncert.OwnCertificateHeaders;
import cci.model.owncert.OwnCertificates;
import cci.pdfbuilder.PDFUtils;
import cci.service.utils.XMLService;
import cci.web.controller.cert.exception.AddCertificateException;
import cci.web.controller.cert.exception.CertificateUpdateException;
import cci.web.controller.cert.exception.NotFoundCertificateException;
import cci.web.controller.owncert.exception.CheckOwnCertificateException;
import cci.web.controller.owncert.exception.NotUpdatedOwnCertificateFileNameException;

@RestController
public class OwnCertificateRestFulController {
	private static final Logger LOG=Logger.getLogger(OwnCertificateRestFulController.class);
	private String relativeWebPath = "/resources/in";
	
	@Autowired
	private PDFUtils pdfutils;
	@Autowired
	private cci.service.owncert.OwnCertificateService service;
	@Autowired
	private XMLService xmlService;

	/* -----------------------------------------
	 * Fill in ownproductdenorm table from ownproduct
	 * ----------------------------------------- */
	@RequestMapping(value="rownfillindenorm.do",  method = RequestMethod.GET, headers = "Accept=application/xml,application/json")
	@ResponseStatus (HttpStatus.OK)
	public String fillInOwnProductDenorm() {
		return service.fillInOwnProductDenorm();
	}
	
	/* -----------------------------------------
	 * Get list of all certificates by filter
	 * ----------------------------------------- */
	@RequestMapping(value="rowncerts.do",  method = RequestMethod.GET, headers = "Accept=application/xml,application/json")
	@ResponseStatus (HttpStatus.OK)
	public cci.model.owncert.OwnCertificates getCertificates(
			@RequestParam(value = "number", required = false) String number,
			@RequestParam(value = "blanknumber", required = false) String blanknumber,
			@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			Authentication aut) {
		OwnCertificates certificates;
					
		OwnFilter filter = new OwnFilter(number, blanknumber, 
		           from == null ? from : convertDateToMySQLFormat(from), 
		           to == null ? to : convertDateToMySQLFormat(to),
		           service.getOtd_idByRole(aut));
		
		certificates = service.getOwnCertificates(filter);
		
		if (certificates.getOwncertificates().size() == 0 ) {
			throw (new NotFoundCertificateException("Не найдено сертификатов, удовлетворяющих условиям поиска: " + filter.toString()));
		}
		return certificates;
	}
	
	/* -----------------------------------------
	 * Get list of all headr's certificates by filter 
	 * ----------------------------------------- */
	@RequestMapping(value = "rowncertheaders.do", method = RequestMethod.GET, headers = "Accept=application/xml,application/json")
	@ResponseStatus (HttpStatus.OK)
	public OwnCertificateHeaders getCertificateHeaders(
			@RequestParam(value = "number", required = false) String number,
			@RequestParam(value = "blanknumber", required = false) String blanknumber,
			@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			Authentication aut) {
		
		OwnCertificateHeaders certificates;
		
		OwnFilter filter = new OwnFilter(number, blanknumber, 
				           from == null ? from : convertDateToMySQLFormat(from), 
				           to == null ? to : convertDateToMySQLFormat(to),
				           service.getOtd_idByRole(aut));
		
		certificates = service.getOwnCertificateHeaders(filter);
		
		if (certificates.getOwncertificateheaders().size() == 0 ) {
			throw (new NotFoundCertificateException("Не найдено сертификатов, удовлетворяющих условиям поиска: " + filter.toString()));
		}
		return certificates;
	}
	
	/* -------------------------------------------------
	 * Add new certificate
	 * Certificate should be stored into database. After 
	 * certificate stored it should be parsed into XML and 
	 * return to client    
	 * ------------------------------------------------- */
	@RequestMapping(value = "rowncert.do", method = RequestMethod.POST, headers = "Accept=application/xml,application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public OwnCertificate addCertificate(@RequestBody OwnCertificate certificate,
			                             Authentication aut)  {
		try {
			verify(certificate);
			 
			certificate.setXml(xmlService.parceObjectToXMLString(certificate));
		
			// get ACL information and store otd_id
			String otd_id = service.getOtd_idByRole(aut);
			//certificate.setOtd_id(Integer.parseInt(otd_id));
			certificate.setOtd_id(otd_id);
						
			// date can't be empty, only null or real date			
			if (certificate.getDatechange() != null &&  certificate.getDatechange().isEmpty()) {
				certificate.setDatechange(null);}
			if (certificate.getDatestop() != null &&  certificate.getDatestop().isEmpty()) {
				certificate.setDatestop(null);}
			 
			service.addOwnSertificate(certificate);
			try {
			   String datecert = convertDateToMySQLFormat(certificate.getDatecert());
			   certificate = service.getOwnCertificateByNumber(certificate.getNumber(), 
					certificate.getBlanknumber(), datecert, otd_id);
			} catch (Exception ex) {
				LOG.error("Ошибка чтения добавленного сертификата: " + ex.getMessage());
				throw(new AddCertificateException("Ошибка чтения добавленного сертификата: " + ex.getMessage()));	  
			}
		} catch (Exception ex) {
			LOG.error(ex.toString());
			throw(new AddCertificateException(ex.toString()));
		}
		return certificate;
	}
	
	/* -------------------------------------------------
	 * Upload certificate's file 
	 * ------------------------------------------------- */
	@RequestMapping(value = "rowncertupfile.do", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public String addCertificateAndFileUpload(
			            @RequestParam("number") String number,
			            @RequestParam("blanknumber") String blanknumber,
			            @RequestParam("datecert") String datecert,
						@RequestParam("file") MultipartFile multipartFile,
						HttpServletRequest request,
			            Authentication aut) {
		
		try {
		     String otd_id = service.getOtd_idByRole(aut);
			 datecert = convertDateToMySQLFormat(datecert);
			 
			 OwnCertificate owncert = service.getOwnCertificateByNumber(number, blanknumber, datecert, otd_id);
		     
			 if  (owncert != null) {
				 LOG.info("Сертификат найден");
				 
				 String fileName = multipartFile.getOriginalFilename();
				 
				 String absoluteDiskPath = request.getSession().getServletContext().getInitParameter("upload.location");
			     fileName = datecert.replaceAll("..\\...\\.", "").trim() + System.getProperty("file.separator") 
			     			+ owncert.getOtd_id().trim() + System.getProperty("file.separator") 
			    		    + (number+"_" + blanknumber).replaceAll("[\\.\\/\\\\]", "_") 
			    		    + "." + FilenameUtils.getExtension(fileName);
			     
				 File certFile = new File(absoluteDiskPath, fileName);
				 LOG.info("File path: " + certFile.getAbsolutePath());
				 multipartFile.transferTo(certFile);

				 if (! service.updateOwnCertificateFileName(number, blanknumber, datecert, fileName)) {
					 throw(new NotUpdatedOwnCertificateFileNameException("Сcылка на загруженный файл не сохранена для сертификата. Ошибка обновление имени файла")); 
				 };
			 } else {
				 LOG.info("Не найдено сертификата для загрузки файла");
				 throw(new NotFoundCertificateException("Не найдено сертификата для загрузки файла"));
			 }
		} catch (EmptyResultDataAccessException ex) {
			LOG.info("Не найдено сертификата для загрузки файла");
			throw(new CertificateUpdateException(" Не найден сертификат с номером: " + number + " на бланке: " + blanknumber + ", выданный  "+ datecert));
		} catch (Exception ex) {
			LOG.info(ex.getLocalizedMessage());
			throw(new CertificateUpdateException(ex.getLocalizedMessage()));
		}
		return "File uploaded and joint to certificate";
    }
	
	/* ---------------------------------------------------
	 * Convert date from format dd.mm.yyyy to yyyy-mm-dd  
	 * It return original date now. Modification is ignored.
	 * --------------------------------------------------- */
	private String convertDateToMySQLFormat(String datecert) {
		String retdate = datecert;
		try {
			 retdate = new SimpleDateFormat("yyyy-mm-dd").format(
					   new SimpleDateFormat("dd.mm.yyyy").parse(datecert));
			 LOG.debug("Coverted date: " + retdate);
		} catch (Exception ex) {
			LOG.error("DateStringConverter: " + ex.getMessage());
		}
		return datecert;
	}

	/* ---------------------------------------------------
	 * Get certificate by identification
	 * ------------------------------------------------- */
	@RequestMapping(value = "rowncert.do", method = RequestMethod.GET, 
			headers = "Accept=application/xml,application/json")
	@ResponseStatus(HttpStatus.OK)
	public OwnCertificate getOwnCertificateByNumber(
			@RequestParam(value = "number", required = true) String number,
			@RequestParam(value = "blanknumber", required = true) String blanknumber,
			@RequestParam(value = "datecert", required = true) String datecert,
			Authentication aut)  {
		try {
			String otd_id = service.getOtd_idByRole(aut);
			datecert = convertDateToMySQLFormat(datecert);
			 
		    return service.getOwnCertificateByNumber(number, blanknumber, datecert, otd_id);
		} catch (Exception ex) {
			throw(new NotFoundCertificateException("Сертификат " + datecert + " с номером " + number 
									+ " на бланке " + blanknumber +  " не найден или отсутствуют права доступа к нему"));			
		}
	}
	

	/* ---------------------------------------------------------------------------------------
	*  Download OwnCertificate file in PDF 
	* ---------------------------------------------------------------------------------------*/
	@RequestMapping(value = "rowncertfile.do", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public void ownCertificateFileDownload (
			@RequestParam(value = "number", required = true) String number,
			@RequestParam(value = "blanknumber", required = true) String blanknumber,
			@RequestParam(value = "datecert", required = true) String datecert,
			Authentication aut,
			HttpServletRequest request,
			HttpServletResponse response, 
			ModelMap model) {
		
		try {
			OwnCertificate owncert = getOwnCertificateByNumber(number, blanknumber, datecert, aut);

			if (owncert != null && !owncert.getFilename().trim().isEmpty()) {
				
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=" + owncert.getFilename());
				
				String templateDiskPath = request.getSession().getServletContext().getRealPath(relativeWebPath);
				String pdfFilePath = request.getSession().getServletContext().getInitParameter("upload.location");
				String checksize = request.getSession().getServletContext().getInitParameter("pdfimage.checksize");
				
				String pagefirst = null;
				String pagenext = null;

				if ("с/п".equals(owncert.getType()) ) {
					pagefirst = templateDiskPath + System.getProperty("file.separator") + "ownproductfirst.pdf";
					pagenext = templateDiskPath + System.getProperty("file.separator") + "ownproductnext.pdf";
				} else if ("р/у".equals(owncert.getType()) ) {
					pagefirst = templateDiskPath + System.getProperty("file.separator") + "ownservicefirst.pdf";
					pagenext = templateDiskPath + System.getProperty("file.separator") + "ownservicenext.pdf";
				} else if ("б/у".equals(owncert.getType()) ) {
					pagefirst = templateDiskPath + System.getProperty("file.separator") + "ownbankfirst.pdf";
					pagenext = templateDiskPath + System.getProperty("file.separator") + "ownbanknext.pdf";
				} else {
					throw new NotFoundCertificateException("Для данного типа сертификата не определены формы бланков.");
				}
								
				String pdffile = pdfFilePath + System.getProperty("file.separator") + owncert.getFilename();
				List<String> numbers = service.splitOwnCertNumbers(owncert.getBlanknumber(), owncert.getAdditionalblanks()); 
				ByteArrayOutputStream  output = pdfutils.mergePdf(pdffile, pagefirst, pagenext, numbers, Integer.parseInt(checksize));
				
				if (output != null) { 
				   response.getOutputStream().write(output.toByteArray());
				   output.close();
				} else {
				   throw new NotFoundCertificateException("Ошибка обработки файла печатной формы сертификата.");	
				}
			} else {
				throw new NotFoundCertificateException("Сертификат не найден или у сертификата нет печатной формы.");
			}
		} catch (Exception ex) {
			throw new NotFoundCertificateException(ex.getMessage());
		}
	}
		
	/* -----------------------------
	 *  Update certificate
	 *  Certificate must be exist
	 * ----------------------------- */
	@RequestMapping(value = "rowncert.do", method = RequestMethod.PUT, headers = "Accept=application/xml,application/json")
	@ResponseStatus(HttpStatus.OK)
	public OwnCertificate updateC(@RequestBody OwnCertificate certificate,
								  Authentication aut) throws Exception {
        
			try {
				verify(certificate);
  			    certificate.setXml(xmlService.parceObjectToXMLString(certificate));

				// get ACL information and store otd_id
				String otd_id = service.getOtd_idByRole(aut);
				//certificate.setOtd_id(Integer.parseInt(otd_id));
				certificate.setOtd_id(otd_id);

				// date can't be empty, only null or real date			
				if (certificate.getDatechange() != null && certificate.getDatechange().isEmpty()) {
					certificate.setDatechange(null);}
				if (certificate.getDatestop() != null && certificate.getDatestop().isEmpty()) {
					certificate.setDatestop(null);}
				
				service.updateOwnCertificate(certificate);
				try {
					String datecert = convertDateToMySQLFormat(certificate.getDatecert());
					certificate = service.getOwnCertificateByNumber(certificate.getNumber(), 
							certificate.getBlanknumber(), datecert, otd_id);
				} catch (Exception ex) {
					throw(new CertificateUpdateException("Ошибка чтения обновленного сертификата: " + ex.getLocalizedMessage()));	  
				}
			} catch (Exception ex) {
				throw(new CertificateUpdateException("Ошибка обновления сертификата в базе: " + ex.getLocalizedMessage()));
			}
			return certificate;
	}
	
	/* -----------------------------
	 *  Verify certificate meets existing filling rules
	 *  All required values should be present
	 * ----------------------------- */
	private void verify(OwnCertificate cert) {
		StringBuffer response = new StringBuffer();
		
		if (cert.getNumber() == null || cert.getNumber().trim().isEmpty()) response.append("номер сертификата обязателен; ");
	    if (cert.getBlanknumber() == null || cert.getBlanknumber().trim().isEmpty())    
	    	response.append("номер бланка сертификата обязателен и не может быть пустым значением; ");
        if (! isDate(cert.getDatecert()) ) 
        	response.append("корректная дата сертификата обязательна; ");
        if (! isDate(cert.getDatestart()) ) 
        	response.append("корректная дата начала действия сертификата обязательна; ");
        if (! isDate(cert.getDateexpire()) ) 
        	response.append("корректная дата окончания действия сертификата обязательна; ");
        if (cert.getBeltpp().getAddress() == null || cert.getBeltpp().getAddress().trim().isEmpty()) 
        	response.append("отсутствует адрес отделения БелТПП; ");
	    if (cert.getBeltpp().getName() == null || cert.getBeltpp().getName().trim().isEmpty()) 
	    	response.append("отсутствует наименование отделения БелТПП; ");
	    if (cert.getType() == null || ! (cert.getType().equals("с/п") || cert.getType().equals("р/у") || cert.getType().equals("б/у"))) 
	    	response.append("тип сертификата отсутствует или не соответствует предопределенным значениям (с/п, р/у, б/у) " + cert.getType() + "; ");
	    if ((cert.getFactories() == null || cert.getFactorylist().trim().isEmpty()) && cert.getType().equals("с/п") ) 
	    	response.append("место нахождения производства обязательно для сертификата продукции собственного производства; ");
	    if (cert.getCustomername() == null || cert.getCustomername().trim().isEmpty()) 
        	response.append("отсутствует наименование производителя; ");
	    if (cert.getCustomeraddress() == null || cert.getCustomeraddress().trim().isEmpty()) 
        	response.append("отсутствует адрес производителя; ");
	    if (cert.getSigner() == null || cert.getSigner().trim().isEmpty()) 
        	response.append("не определено лицо, подписавшее сертификат; ");
	    if (cert.getExpert() == null || cert.getExpert().trim().isEmpty()) 
        	response.append("не определен эксперт, подготовивший сертификат; ");
	    if (cert.getSignerjob() == null || cert.getSignerjob().trim().isEmpty()) 
        	response.append("не определена должность подписавшего сертификат; ");
	    if (cert.getStatus() == null || cert.getStatus().trim().isEmpty()) 
        	response.append("статус сертификата должен быть явно указан valid/invalid; ");
	    if (cert.getProducts() == null || cert.getProductlist().trim().isEmpty() ) 
	    	response.append("должна быть информация о продукции или услуге; ");
	    	    
	    if (! response.toString().trim().isEmpty()) {
	    	throw new CheckOwnCertificateException("Некоторые сведения сертификата не отвечают предъявляемым требованиям: " + response.toString());	
	    }
	}
	
	/* -----------------------------
	 * Delete certificate
	 * ----------------------------- */
	private boolean isDate(String datecert) {   
	    boolean ret = false;
	    
	    if (datecert != null && ! datecert.trim().isEmpty()) {
			try {
				 //LocalDate date = LocalDate.parse(datecert);
				 //date.
				 ret = true;
			} catch (Exception ex) {
				System.out.println("Строка не интерпретируется как дата");
				ret = false; 
			}
	    }
	    return ret;
	}

	/* -----------------------------
	 * Delete certificate
	 * ----------------------------- */
	@RequestMapping(value = "rowncert.do", method = RequestMethod.DELETE, headers = "Accept=application/xml,application/json")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ResponseEntity<String> deleteCountry(
			@RequestParam(value = "number", required = true) String number,
			@RequestParam(value = "blanknumber", required = true) String blanknumber,
			@RequestParam(value = "datecert", required = true) String datecert,
			Authentication aut ) throws Exception {
		
	    String otd_id = service.getOtd_idByRole(aut);
		datecert = convertDateToMySQLFormat(datecert);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("DeleteResponseHeader", "Own Certificate deletion");
		if (service.deleteOwnCertificate(number, blanknumber, datecert, otd_id)) {
			return new ResponseEntity<String>("Certificate haas been deleted", responseHeaders, HttpStatus.OK); 	
		} else {
			return new ResponseEntity<String>("Certificate isn't deleted", responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}
	
	/* -----------------------------
	 * Exception handling 
	 * ----------------------------- */
	@ExceptionHandler(Exception.class)
	@ResponseBody
    public ResponseEntity<String> handleIOException(Exception ex) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Error-Code", "12345");
		responseHeaders.set("Content-Type", "application/json;charset=utf-8");
		return new ResponseEntity<String>(ex.toString(), responseHeaders,  HttpStatus.BAD_REQUEST);
    }

}
