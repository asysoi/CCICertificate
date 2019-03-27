package cci.web.controller.owncert;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import cci.config.own.ExportOwnCertConfig;
import cci.config.own.ReportOwnCertConfig;
import cci.model.owncert.OwnCertificate;
import cci.pdfbuilder.PDFUtils;
import cci.repository.SQLBuilder;
import cci.repository.owncert.SQLBuilderOwnCertificate;
import cci.service.FieldType;
import cci.service.Filter;
import cci.service.XSLService;
import cci.service.cert.CertService;
import cci.service.owncert.OwnCertificateService;
import cci.service.owncert.OwnFilter;
import cci.web.controller.ViewManager;
import cci.web.controller.cert.CertificateController;
import cci.web.controller.cert.exception.NotFoundCertificateException;

@Controller
@SessionAttributes({ "owncertfilter", "ownmanager"})
public class OwnCertificateController {
	
	private static final Logger LOG=Logger.getLogger(CertificateController.class);
	private final String relativeWebPath = "/resources/in";
	
	@Autowired
	private PDFUtils pdfutils;
	@Autowired
	private OwnCertificateService ownCertService;
	@Autowired
	private CertService certService;

	/* ---------------------------------------------------------------------------------------
	*  Main Request - Get List of Own Certificates
	* --------------------------------------------------------------------------------------- */
	@RequestMapping(value = "owncerts.do", method = RequestMethod.GET)
	public String listcerts(
			// HttpServletRequest request,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "pagesize", required = false) Integer pagesize,
			@RequestParam(value = "orderby", required = false) String orderby,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "filter", required = false) Boolean onfilter,
			Authentication aut,
			ModelMap model) {

		long start = System.currentTimeMillis();
		LOG.debug("< == GET OWN CERT LIST == >");

		ViewManager vmanager = (ViewManager) model.get("ownmanager");

		if (vmanager == null) {
			vmanager = initViewManager(model);
		}

		if (orderby == null || orderby.isEmpty())
			orderby = "datecert";
		if (order == null || order.isEmpty())
			order = ViewManager.ORDASC;
		if (onfilter == null)
			onfilter = false;

		vmanager.setPage(page == null ? 1 : page);
		vmanager.setPagesize(pagesize == null ? 10 : pagesize);
		vmanager.setOrderby(orderby);
		vmanager.setOrder(order);
		vmanager.setOnfilter(onfilter);
		vmanager.setUrl("owncerts.do");
		long step1 = System.currentTimeMillis();

		Filter filter = null;
		
		if (onfilter) {
			filter = vmanager.getFilter();

			if (filter == null) {
				if (model.get("owncertfilter") != null) {
					filter = (Filter) model.get("owncertfilter");
				} else {
					filter = new OwnFilter();
					model.addAttribute("owncertfilter", filter);
				}
				vmanager.setFilter(filter);
			}
		} 
		
		// ACL needs: use filter to restrict access to documents  
		if (filter == null) {
			filter = new OwnFilter();
		}
				
		Iterator iterator = aut.getAuthorities().iterator(); 
		while (iterator.hasNext()) {
		      String roleName = ((GrantedAuthority) iterator.next()).getAuthority();
		      if  (certService.getACL().containsKey(roleName)) {      
			      filter.setConditionValue("OTD_ID", "OTD_ID", "=", 
			    		  certService.getACL().get(roleName), FieldType.NUMBER);
		      }
		}
		
		SQLBuilder builder = new SQLBuilderOwnCertificate();
		builder.setFilter(filter);
		
		long step2 = System.currentTimeMillis();
		vmanager.setPagecount(ownCertService.getViewPageCount(builder));
		
		long step3 = System.currentTimeMillis();
		
		List<OwnCertificate> certs = ownCertService.readCertificatesPage(
				vmanager.getOrdnames(),
				vmanager.getPage(), vmanager.getPagesize(), vmanager.getPagecount(),  
				vmanager.getOrderby(), vmanager.getOrder(), builder);
		
		long step4 = System.currentTimeMillis();
		vmanager.setElements(certs);
        
		
		model.addAttribute("ownmanager", vmanager);
		model.addAttribute("certs", certs);
		model.addAttribute("next_page", vmanager.getNextPageLink());
		model.addAttribute("prev_page", vmanager.getPrevPageLink());
		model.addAttribute("last_page", vmanager.getLastPageLink());
		model.addAttribute("first_page", vmanager.getFirstPageLink());
		model.addAttribute("pages", vmanager.getPagesList());
		model.addAttribute("sizes", vmanager.getSizesList());
		model.addAttribute("timeduration", 
				" " + (step3 - step2) + " + " + (step4 - step3) + " = " + (System.currentTimeMillis() - start));

		return "listowncertificates";
	}
	
	/* -----------------------------------------------------------------------------
	*  Init viewmanager 
	* ------------------------------------------------------------------------------ */
	private ViewManager initViewManager(ModelMap model) {
		ViewManager vmanager = new ViewManager();
		vmanager.setHnames(new String[] {"Номер Сертификата",  "Отделение", 
				"Предприятие", "Номер бланка", "Дата"});
		vmanager.setOrdnames(new String[] { "number", "beltppname", "customername",
				"blanknumber", "datecert"});
		vmanager.setWidths(new int[] { 15, 20, 40, 10, 15});
		model.addAttribute("ownmanager", vmanager);
		return vmanager;
	}
	
	/* -----------------------------------------------------------------------------
	*    Own Certificate filter handling Method GET
	* -----------------------------------------------------------------------------*/
	@RequestMapping(value = "owncertfilter.do", method=RequestMethod.GET)
	public String openFilter(
				@ModelAttribute("owncertfilter") OwnFilter fc, ModelMap model) {

			if (fc == null) {
				fc = new OwnFilter();
				LOG.debug("New filterOwnCertificate created in GET method");
				model.addAttribute("owncertfilter", fc);
			} else {
				LOG.debug("Found FilterOwnCertificate in GET : ");
			}

			ViewOwnCertificateFilter vf = new ViewOwnCertificateFilter(
					((OwnFilter) fc).getViewcertificate(),
					((OwnFilter) fc).getCondition());
			
			model.addAttribute("viewownfilter", vf);
			return "own/ownfilter";
	}
		
	/* -----------------------------------------------------------------------------
	*   Own Certificate filter handling Method POST
	* ----------------------------------------------------------------------------- */
	@RequestMapping(value = "owncertfilter.do", method=RequestMethod.POST)
	public String submitFilter(
				@ModelAttribute("viewownfilter") ViewOwnCertificateFilter viewfilter,
				@ModelAttribute("owncertfilter") OwnFilter fc,
				BindingResult result, SessionStatus status, ModelMap model) {

		    LOG.debug (viewfilter);
		    
			if (fc == null) {
				fc = new OwnFilter();
				LOG.debug("New filterOwnCertificate created in the POST method");
			} else {
				LOG.debug("Found FilterOwnCertificate in POST");
			}
			// remove space
			viewfilter.getViewcertificate().setProductcode( 
					viewfilter.getViewcertificate().getViewcode().replaceAll("\\s", ""));
			fc.loadViewcertificate(viewfilter.getViewcertificate());
			fc.loadCondition(viewfilter.getCondition());

			model.addAttribute("owncertfilter", fc);
			return "own/ownfilter";
	}
	
	/* -----------------------------------------------------------------------------
	*  View own certs table config  for Export certificates to Excel format
	* ----------------------------------------------------------------------------- */
	@RequestMapping(value = "owncertconfig.do", method = RequestMethod.GET)
	public String openConfig(ModelMap model) {
		ViewManager vmanager = (ViewManager) model.get("ownmanager");

		if (vmanager == null) {
			vmanager = initViewManager(model);
		}
       
		if (vmanager.getDownloadconfig() == null) {
			vmanager.setDownloadconfig(new ExportOwnCertConfig());
		}
		
		model.addAttribute("downloadconfig", vmanager.getDownloadconfig());
		model.addAttribute("headermap", vmanager.getDownloadconfig().getHeadermap());
		return "own/config";
	}

	// ---------------------------------------------------------------------------------------
	//   Post own certs table config  for Export certificates to Excel format
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "owncertconfig.do", method = RequestMethod.POST)
	public String submitConfig(@ModelAttribute("downloadconfig") ExportOwnCertConfig config,
			BindingResult result, SessionStatus status, ModelMap model) {
		
		ViewManager vmanager = (ViewManager) model.get("ownmanager");
		vmanager.setDownloadconfig(config);
                
		model.addAttribute("downloadconfig", vmanager.getDownloadconfig());
		model.addAttribute("headermap", config.getHeadermap());
		return "own/config";
	}
	
	// ---------------------------------------------------------------------------------------
	//  Download list of Own Certificate as Excel file
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "owncertdownload.do", method = RequestMethod.GET)
	public void XSLFileDownload(HttpSession session,
				HttpServletResponse response,
				Authentication aut,
				ModelMap model) {
			try {
				
	            LOG.debug("Download started...");   
				ViewManager vmanager = (ViewManager) model.get("ownmanager");
				
				SQLBuilder builder = getBuilder(vmanager, model, aut);
				
				if (vmanager.getDownloadconfig() == null) {
					vmanager.setDownloadconfig(new ExportOwnCertConfig());
				}
				
				List certs = ownCertService.readCertificates(vmanager.getDownloadconfig().getFields(),
						vmanager.getOrderby(), vmanager.getOrder(), builder);
				
				LOG.debug("Download. Certificates loaded from database..."); 
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

				response.setHeader("Content-Disposition",
						"attachment; filename=certificates.xlsx");
				(new XSLService()).makeWorkbook(certs,
						vmanager.getDownloadconfig().getHeaders(),
						vmanager.getDownloadconfig().getFields(), "Лист Сертификатов").write(
						response.getOutputStream());
				response.flushBuffer();
				LOG.debug("Download finished...");

			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/*
	 * -------------------------------------------------------------------------
	 * ---- View Own Certificate as HTML or PDF page
	 * -------------------------------------------------------------------------
	 */
	@RequestMapping(value = "owncert.do", method = RequestMethod.GET)
	public void gocert(@RequestParam(value = "certid", required = true) Integer certid, Authentication aut,
			HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException, ServletException {
        
		String pathToTempFile = "/resources/out/own";
		String pathToJSP = "/WEB-INF/jsp/";
		
		OwnCertificate cert = null;
		
		try {
			cci.web.controller.owncert.OwnFilter filter = new cci.web.controller.owncert.OwnFilter();
			filter.setId(certid);
			filter.setOtd_id(ownCertService.getOtd_idByRole(aut));
			cert = ownCertService.getOwnCertificateById(filter);
		} catch (EmptyResultDataAccessException emex) {
			model.addAttribute("error", "Сертификат не найден или отсутстуют права доступа к нему.");
			request.setAttribute("error", "Сертификат не найден или отсутстуют права доступа к нему.");
			request.getRequestDispatcher(pathToJSP+"400.jsp").forward(request, response);
		} catch (Exception ex) {
			model.addAttribute("error", ex.getClass().getName() + " - " + ex.getLocalizedMessage());
			request.setAttribute("error", ex.getClass().getName() + " - " + ex.getLocalizedMessage());
			request.getRequestDispatcher(pathToJSP+"400.jsp").forward(request, response);
		} 
		
		if (cert != null && cert.getFilename() != null && !cert.getFilename().isEmpty()) {
			try {
				
				String templateDiskPath = request.getSession().getServletContext().getRealPath(relativeWebPath);
				String pdfFilePath = request.getSession().getServletContext().getInitParameter("upload.location");
				String tempname = cert.getBlanknumber() + ".pdf";
				//String tempfile = request.getSession().getServletContext().getRealPath(pathToTempFile) 
				//		          + System.getProperty("file.separator") + tempname;

				String pagefirst = null;
				String pagenext = null;

				if ("с/п".equals(cert.getType())) {
					pagefirst = templateDiskPath + System.getProperty("file.separator") + "ownproductfirst.pdf";
					pagenext = templateDiskPath + System.getProperty("file.separator") + "ownproductnext.pdf";
				} else if ("р/у".equals(cert.getType())) {
					pagefirst = templateDiskPath + System.getProperty("file.separator") + "ownservicefirst.pdf";
					pagenext = templateDiskPath + System.getProperty("file.separator") + "ownservicenext.pdf";
				} else if ("б/у".equals(cert.getType())) {
					pagefirst = templateDiskPath + System.getProperty("file.separator") + "ownbankfirst.pdf";
					pagenext = templateDiskPath + System.getProperty("file.separator") + "ownbanknext.pdf";
				} else {
					throw new NotFoundCertificateException("Для данного типа сертификата не определены формы бланков.");
				}

				String pdffile = pdfFilePath + System.getProperty("file.separator") + cert.getFilename();
				List<String> numbers = ownCertService.splitOwnCertNumbers(cert.getBlanknumber(),
						cert.getAdditionalblanks());
				ByteArrayOutputStream output = pdfutils.mergePdf(pdffile, pagefirst, pagenext, numbers);
				
				if (output != null) {
					// Firefox Browser doesn't display the file correctly
					response.setContentType("application/pdf");
					response.setHeader("Accept-Ranges", "bytes");
					response.setHeader("Content-Disposition", "inline; filename=" + tempname);
					// response.setHeader("Content-Disposition", "attachment; filename=" + tempname);
					response.setContentLength(output.size());
					response.getOutputStream().write(output.toByteArray());
					response.getOutputStream().flush();
					response.getOutputStream().close();
					response.flushBuffer();
									
					// It's need to make redirect
					// This is unsafe process because file can be corrucpted in case of concurrent identical request
					/*
					FileOutputStream fos = new FileOutputStream(tempfile);
				    fos.write(output.toByteArray());
				    fos.close();
				    return "redirect:" + pathToTempFile + System.getProperty("file.separator") + tempname;
				    */
				    output.close();
				    
				} else {
					model.addAttribute("error", "Ошибка формирования файла с изображением выданного сертификата.");
					request.setAttribute("error", "Ошибка формирования файла с изображением выданного сертификата.");
					request.getRequestDispatcher(pathToJSP+"400.jsp").forward(request, response);
					// return "error";
				}
			} catch (Exception ex) {
				model.addAttribute("error", ex.getMessage());
				model.addAttribute("owncert", cert);
				request.setAttribute("error", ex.getMessage());
				ViewOwnCertificateJSPHelper viewcert = new ViewOwnCertificateJSPHelper(cert); 
				request.setAttribute("viewcert", viewcert);
				request.getRequestDispatcher(pathToJSP+"own/ownview.jsp").forward(request, response);
				//request.getRequestDispatcher(pathToJSP+"400.jsp").forward(request, response);
				// return "400";
			}
		} else if (cert != null) {
			model.addAttribute("owncert", cert);
			ViewOwnCertificateJSPHelper viewcert = new ViewOwnCertificateJSPHelper(cert); 
			request.setAttribute("viewcert", viewcert);
			request.getRequestDispatcher(pathToJSP+"own/ownview.jsp").forward(request, response);
		}
	}
	
	/* ---------------------------------------------------------------------------------------
	* Report Page making
	* --------------------------------------------------------------------------------------- */
	@RequestMapping(value = "ownconfigreport.do", method = RequestMethod.GET)
	public String openPreprt(ModelMap model) {

		ReportOwnCertConfig reportconfig = new ReportOwnCertConfig();

		model.addAttribute("ownreportconfig", reportconfig);
		model.addAttribute("ownheadermap", reportconfig.getHeadermap());
		return "own/ownconfigreport";
	}

	// ---------------------------------------------------------------------------------------
	// Generate report
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "owncertmakereport.do", method = RequestMethod.POST)
	public String submitReport(@ModelAttribute("ownreportconfig") ReportOwnCertConfig ownreportconfig,
			BindingResult result, SessionStatus status, Authentication aut, ModelMap model) {

		List reports = null;
		LOG.debug("Own Report generation started...");
		ViewManager vmanager = (ViewManager) model.get("ownmanager");

		try {
		    SQLBuilder builder = getBuilder(vmanager, model, aut);
			reports = ownCertService.makeReports(ownreportconfig.getFields(), builder);

			LOG.debug("Own Reporting finished...");

		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addAttribute("ownreports", reports);
		model.addAttribute("ownheaders", ownreportconfig.getHeaders());
		return "own/ownviewreport";
	}
	
	// ---------------------------------------------------------------------------------------
	// Create builder and set filter depending onFilter
	// ---------------------------------------------------------------------------------------
	private SQLBuilder getBuilder(ViewManager vmanager, ModelMap model, Authentication aut) {
		SQLBuilder builder = new SQLBuilderOwnCertificate();
		Filter filter;

		if (vmanager.getOnfilter()) {
			filter = vmanager.getFilter();

			if (filter == null) {
				if (model.get("owncertfilter") != null) {
					filter = (Filter) model.get("owncertfilter");
				} else {
					filter = new OwnFilter();
					model.addAttribute("owncertfilter", filter);
				}
			}
			vmanager.setFilter(filter);
		} else {
			filter = new OwnFilter();
		}

		String otd_id = ownCertService.getOtd_idByRole(aut);
		if (otd_id != null)
			filter.setConditionValue("OTD_ID", "OTD_ID", "=", otd_id, FieldType.NUMBER);

		builder.setFilter(filter);
		return builder;
	}

	
	/* ---------------------------------------------------------------------------------------
	* Download Orsha Report as Excel file
	* --------------------------------------------------------------------------------------- */
	@RequestMapping(value = "ownorshareport.do", method = RequestMethod.GET)
	public void OrshaReportDownload(
			@RequestParam(value = "reportdate", required = false) String reportdate,
			HttpSession session, 
			HttpServletRequest request, HttpServletResponse response, 
			Authentication aut, ModelMap model) {
		try {
			if (reportdate == null)
				reportdate = (new SimpleDateFormat("dd.MM.yyyy")).format(new Date());
            
			String otd_id = ownCertService.getOtd_idByRole(aut);
			
			if (otd_id == null) {
				otd_id = certService.getACL().get("ROLE_VITEBSK"); 
			}
			
			List<OwnCertificate> certs = ownCertService.getOrshaCertificates(reportdate, "ОРША", otd_id);

			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=certificates.xlsx");
			
			String filetemplate = request.getSession().getServletContext().getRealPath(relativeWebPath) 
									+ System.getProperty("file.separator") 
									+ "ReportOrsha.xlsx";
			
			(new XSLService()).makeWorkbookReportOrsha(certs, filetemplate, reportdate).write(response.getOutputStream());
			response.flushBuffer();
			LOG.debug("Download report finished...");

		} catch (Exception ex) {
			model.addAttribute("error", ex.getMessage());
			LOG.error("OrshaReportDownload: " + ex.getLocalizedMessage());
			ex.printStackTrace();
		}
	}
	
	
	/*-------------------------------------------------------------------------
	 * Download Orsha Report as Excel file
	 * -------------------------------------------------------------------------	 */
	@RequestMapping(value = "ownwastereport.do", method = RequestMethod.GET)
	public void wasteReportDownload(@RequestParam(value = "reportdate", required = false) String reportdate,
			HttpSession session, HttpServletRequest request, HttpServletResponse response, Authentication aut,
			ModelMap model) {
		
		try {
			if (reportdate == null)
				reportdate = (new SimpleDateFormat("dd.MM.yyyy")).format(new Date());

			String otd_id = ownCertService.getOtd_idByRole(aut);

			if (otd_id == null) {
				String filetwastenumbers = request.getSession().getServletContext().getRealPath(relativeWebPath)
						+ System.getProperty("file.separator") + "WasteNumbers.xlsx";

				List<String> numbers = loadProductCodeNumbers(filetwastenumbers);

				if (numbers != null) {
					List<ViewWasteOwnCertificate> certs = ownCertService.getWasteOwnReport(reportdate, numbers);

					response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
					response.setHeader("Content-Disposition", "attachment; filename=certificates.xlsx");

					String filetemplate = request.getSession().getServletContext().getRealPath(relativeWebPath)
							+ System.getProperty("file.separator") + "WasteReport.xlsx";

					(new XSLService()).makeWorkbookWasteReport(certs, filetemplate, reportdate)
							.write(response.getOutputStream());
					response.flushBuffer();
				} else {
					LOG.error("WasteReportDownload: list of product codes is not defined. It's empty");
				}
				LOG.debug("Download waste report finished...");
			}

		} catch (Exception ex) {
			model.addAttribute("error", ex.getMessage());
			LOG.error("WasteReportDownload: " + ex.getLocalizedMessage());
			ex.printStackTrace();
		}
	}
			
	private List<String> loadProductCodeNumbers(String filename) {
		List<String> numbers = 
			(new XSLService()).getWasteNumbers(filename);
		
		return numbers;
	}

	// ---------------------------------------------------------------
	// Get LIst od Own Certificate Types
	// ---------------------------------------------------------------
	@ModelAttribute("types")
	public Map<String, String> populateOwnCertificateTypesList() {
		Map<String, String> types = new HashMap<String, String>();
		types.put("с/п", "Продукция");
		types.put("р/у", "Работы/Услуги");
		types.put("б/у", "Банковские услуги");
		return types;
	}
		
	@ModelAttribute("departments")
	public Map<String, String> populateDepartmentssList() {
		return certService.getBranchesList();
	}

	
}
