package cci.web.controller.cert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import cci.config.cert.ExportCertConfig;
import cci.config.cert.ReportCertConfig;
import cci.model.cert.Certificate;
import cci.pdfbuilder.cert.CertificatePDFBuilder;
import cci.repository.SQLBuilder;
import cci.repository.cert.SQLBuilderCertificate;
import cci.service.CountryConverter;
import cci.service.FieldType;
import cci.service.Filter;
import cci.service.XSLService;
import cci.service.cert.CertService;
import cci.service.cert.CertFilter;
import cci.web.controller.ViewManager;

@Controller
@SessionAttributes({ "certfilter", "vmanager" })
public class CertificateController {
	
	//public static Logger LOG=LogManager.getLogger(CertController.class);
	private static final Logger LOG=Logger.getLogger(CertificateController.class);
	
	@Autowired
	private CertService certService;

	
	// ---------------------------------------------------------------------------------------
	//  
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "cert.do")
	@ResponseBody
	public Certificate printcert() {
         
		Certificate certificate;
		certificate = new Certificate();
		return certificate;
	}

	// ---------------------------------------------------------------------------------------
	// View certs table config 
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "certconfig.do", method = RequestMethod.GET)
	public String openConfig(ModelMap model) {
		ViewManager vmanager = (ViewManager) model.get("vmanager");

		if (vmanager == null) {
			vmanager = initViewManager(model);
		}
       
		if (vmanager.getDownloadconfig() == null) {
			vmanager.setDownloadconfig(new ExportCertConfig());
		}
		
		model.addAttribute("downloadconfig", vmanager.getDownloadconfig());
		model.addAttribute("headermap", vmanager.getDownloadconfig().getHeadermap());
		return "fragments/config";
	}

	// ---------------------------------------------------------------------------------------
	//
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "certconfig.do", method = RequestMethod.POST)
	public String submitConfig(@ModelAttribute("downloadconfig") ExportCertConfig config,
			BindingResult result, SessionStatus status, ModelMap model) {
		
		ViewManager vmanager = (ViewManager) model.get("vmanager");
		vmanager.setDownloadconfig(config);
                
		model.addAttribute("downloadconfig", vmanager.getDownloadconfig());
		model.addAttribute("headermap", config.getHeadermap());
		return "fragments/config";
	}
	
	// ---------------------------------------------------------------------------------------
	//
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "certdownload.do", method = RequestMethod.GET)
	public void XSLFileDownload(HttpSession session,
			HttpServletResponse response, ModelMap model) {
		try {
			
            //LOG.debug("Download started...");   
			ViewManager vmanager = (ViewManager) model.get("vmanager");

			Filter filter = vmanager.getFilter();
			if (filter == null) {
				if (model.get("certfilter") != null) {
					filter = (Filter) model.get("certfilter");
				} else {
					filter = new CertFilter();
					model.addAttribute("certfilter", filter);
				}
				vmanager.setFilter(filter);
			}

			if (vmanager.getDownloadconfig() == null) {
				vmanager.setDownloadconfig(new ExportCertConfig());
			}

			SQLBuilder builder = new SQLBuilderCertificate();
			builder.setFilter(filter);
			List certs = certService.readCertificates(vmanager.getDownloadconfig().getFields(),
					vmanager.getOrderby(), vmanager.getOrder(), builder);
			
			//LOG.debug("Download. Certificates loaded from database..."); 
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			//response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					"attachment; filename=certificates.xlsx");
			(new XSLService()).makeWorkbook(certs,
					vmanager.getDownloadconfig().getHeaders(),
					vmanager.getDownloadconfig().getFields(), "Лист Сертификатов").write(
					response.getOutputStream());
			response.flushBuffer();
			//LOG.debug("Download finished...");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ---------------------------------------------------------------------------------------
	//  Get List of Certificates
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "certs.do", method = RequestMethod.GET)
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
		//LOG.info("=========================== GET CERT LIST =================================== >");

		// ViewManager vmanager = (ViewManager)
		// request.getSession().getAttribute("vmanager");
		ViewManager vmanager = (ViewManager) model.get("vmanager");

		if (vmanager == null) {
			vmanager = initViewManager(model);
		}

		if (orderby == null || orderby.isEmpty())
			orderby = "issuedate";
		if (order == null || order.isEmpty())
			order = ViewManager.ORDASC;
		if (onfilter == null)
			onfilter = false;

		vmanager.setPage(page == null ? 1 : page);
		vmanager.setPagesize(pagesize == null ? 10 : pagesize);
		vmanager.setOrderby(orderby);
		vmanager.setOrder(order);
		vmanager.setOnfilter(onfilter);
		vmanager.setUrl("certs.do");
		long step1 = System.currentTimeMillis();

		Filter filter = null;
		if (onfilter) {
			filter = vmanager.getFilter();

			if (filter == null) {
				if (model.get("certfilter") != null) {
					filter = (Filter) model.get("certfilter");
				} else {
					filter = new CertFilter();
					model.addAttribute("certfilter", filter);
				}
				vmanager.setFilter(filter);
			}
		} 
		
		// ACL needs
		if (filter == null) {
			filter = new CertFilter();
			//LOG.debug
			//		("New filterCertificate created bacause of ACL");
		}
		
		Iterator iterator = aut.getAuthorities().iterator();
	    String otdID = "";	
		while (iterator.hasNext()) {
			
		      String roleName = ((GrantedAuthority) iterator.next()).getAuthority();
		      
		      if  (certService.getACL().containsKey(roleName)) {      
		    	  otdID = certService.getACL().get(roleName);
			      filter.setConditionValue("OTD_ID", "OTD_ID", "=", otdID, FieldType.NUMBER);
		      }
		}
		// ACL needs
		
		SQLBuilder builder = new SQLBuilderCertificate();
		builder.setFilter(filter);
		
		long step2 = System.currentTimeMillis();
		vmanager.setPagecount(certService.getViewPageCount(builder));
		
		long step3 = System.currentTimeMillis();
		List<Certificate> certs = certService.readCertificatesPage(
				vmanager.getOrdnames(),
				vmanager.getPage(), vmanager.getPagesize(), vmanager.getPagecount(),  
				vmanager.getOrderby(), vmanager.getOrder(), builder);
		
		long step4 = System.currentTimeMillis();
		vmanager.setElements(certs);

		model.addAttribute("vmanager", vmanager);
		model.addAttribute("certs", certs);
		model.addAttribute("otd", otdID);
		model.addAttribute("next_page", vmanager.getNextPageLink());
		model.addAttribute("prev_page", vmanager.getPrevPageLink());
		model.addAttribute("last_page", vmanager.getLastPageLink());
		model.addAttribute("first_page", vmanager.getFirstPageLink());
		model.addAttribute("pages", vmanager.getPagesList());
		model.addAttribute("sizes", vmanager.getSizesList());
		model.addAttribute("timeduration", 
				" " + (step3 - step2) + " + " + (step4 - step3) + " = " + (System.currentTimeMillis() - start));

		return "listcertificates";
	}

	private ViewManager initViewManager(ModelMap model) {
		ViewManager vmanager = new ViewManager();
		vmanager.setHnames(new String[] { "Номер Сертификата", "Отделение",
				"Грузоотправитель/Экспортер", "Номер бланка", "Дата",
				"Доп. лист", "Замена для." });
		vmanager.setOrdnames(new String[] { "nomercert", "otd_id", "kontrp",
				"nblanka", "issuedate", "koldoplist", "parentnumber" });
		vmanager.setWidths(new int[] { 10, 20, 40, 8, 8, 6, 8 });
		model.addAttribute("vmanager", vmanager);
		// request.getSession().setAttribute("vmanager", vmanager);

		return vmanager;
	}

	
	// ---------------------------------------------------------------------------------------
	//
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "certfilter.do", method = RequestMethod.GET)
	public String openFilter(
			@ModelAttribute("certfilter") CertFilter fc, ModelMap model) {

		if (fc == null) {
			fc = new CertFilter();
			//LOG.debug("New filterCertificate created in GET method");
			model.addAttribute("certfilter", fc);
		} else {
			//LOG.debug("Found FilterCertificate in GET : ");
		}

		ViewCertFilter vf = new ViewCertFilter(
				((CertFilter) fc).getViewcertificate(),
				((CertFilter) fc).getCondition());
		model.addAttribute("viewfilter", vf);
		return "fragments/filter";
	}

	
	
	// ---------------------------------------------------------------------------------------
	//
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "certfilter.do", method = RequestMethod.POST)
	public String submitFilter(
			@ModelAttribute("viewfilter") ViewCertFilter viewfilter,
			@ModelAttribute("certfilter") CertFilter fc,
			BindingResult result, SessionStatus status, ModelMap model) {

		if (fc == null) {
			fc = new CertFilter();
			//LOG.debug
			//		("New filterCertificate created in the POST method");
		} else {
			//LOG.debug("Found FilterCertificate in POST");
		}

		fc.loadViewcertificate(viewfilter.getViewcertificate());
		fc.loadCondition(viewfilter.getCondition());

		model.addAttribute("certfilter", fc);
		return "fragments/filter";
	}

	// ---------------------------------------------------------------------------------------
	//
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "certgo.do")
	public String gocert(HttpServletRequest request, 
			@RequestParam(value = "certid", required = true) Integer certid,
			ModelMap model) {
		String relativeWebPath = "/resources";
		String  absoluteDiskPath= request.getSession().getServletContext().getRealPath(relativeWebPath);
		//LOG.debug("Absolute path: " + absoluteDiskPath);
		
		Certificate cert = certService.readCertificate(certid);
		makepdffile(absoluteDiskPath, cert);
		model.addAttribute("cert", cert);
		return "redirect:" + "/resources/out/" + cert.getCert_id() + ".pdf";
	}

	
	// ---------------------------------------------------------------------------------------
	//
	// ---------------------------------------------------------------------------------------
	private void makepdffile(String absoluteDiskPath, Certificate cert) {
		CertificatePDFBuilder builder = new CertificatePDFBuilder();
		String fout = absoluteDiskPath + "/out/" + cert.getCert_id() + ".pdf";
		String fconf = absoluteDiskPath + "/config/pages.xml";
		String fpath = absoluteDiskPath + "/fonts/";
		
		CountryConverter.setCountrymap(certService.getCountriesList("RU"));
		
		try {
		   builder.createPdf(fout, cert, fconf, fpath);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	
	// ---------------------------------------------------------------------------------------
	//  Check certificate 
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "check.do", method = RequestMethod.GET)
	public String check(ModelMap model) {
		String retpage = "check";
		Certificate cert = new Certificate();
		model.addAttribute("cert", cert);
		return retpage;
	}

	
	// ---------------------------------------------------------------------------------------
	//  Check certificate 
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "check.do", method = RequestMethod.POST)
	public String check(@ModelAttribute("cert") Certificate cert,
			BindingResult result, SessionStatus status, ModelMap model, HttpServletRequest request) {
		String retpage = "check";

		if (cert.getNomercert() != null && cert.getNblanka() != null
				&& cert.getDatacert() != null) {
			Certificate rcert = certService.checkCertificate(cert);

			if (rcert != null) {
				
				String relativeWebPath = "/resources";
				String  absoluteDiskPath= request.getSession().getServletContext().getRealPath(relativeWebPath);
				
				long start = System.currentTimeMillis();
				makepdffile(absoluteDiskPath, rcert);
				//LOG.info("Certificate check pdf making: " + (System.currentTimeMillis() - start));
				
				String msg = "<p>Найден сертификат номер " + cert.getNomercert()
						+ " на бланке с номером " + cert.getNblanka() + ", выданный "
						+ cert.getDatacert()
						+ ".</p> "+
				        "<p>Воспроизведение бумажной версии сертификата <a href=\"javascript:openCertificate(\'" +
						"resources/out/" + rcert.getCert_id() + ".pdf" +
						"')\">" + rcert.getNomercert() + "</a><p style=\"width:100%\">" + 
			            "<p>Результат воспроизведения может незначительно отличаться по форме и стилю отображения," +
			            "но полностью воспроизводит содержание документа.</p>";
				
				model.addAttribute("msg", msg);
				retpage = "fragments/message";
			} else {
				model.addAttribute("cert", cert);
				String msg = "Сертификат номер " + cert.getNomercert()
						+ " на бланке номер " + cert.getNblanka() + ", выданный "
						+ cert.getDatacert()
						+ ", не найден";
				model.addAttribute("msg", msg);
				retpage = "fragments/message";
			}
		}
		return retpage;
	}

	// ---------------------------------------------------------------------------------------
	//  Report Page making
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "certconfigreport.do", method = RequestMethod.GET)
	public String openPreprt(ModelMap model) {

		ReportCertConfig reportconfig = new ReportCertConfig();
		
		model.addAttribute("reportconfig", reportconfig);
		model.addAttribute("headermap", reportconfig.getHeadermap());
		return "fragments/configreport";
	}

	// ---------------------------------------------------------------------------------------
	// Generate report
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "certmakereport.do", method = RequestMethod.POST)
	public String submitReport(
			@ModelAttribute("reportconfig") ReportCertConfig reportconfig,
			BindingResult result, SessionStatus status, ModelMap model) {

		List reports = null;
		//LOG.debug("Report generation started...");
		ViewManager vmanager = (ViewManager) model.get("vmanager");

		try {

			Filter filter = vmanager.getFilter();
			if (filter == null) {
				if (model.get("certfilter") != null) {
					filter = (Filter) model.get("certfilter");
				} else {
					filter = new CertFilter();
					model.addAttribute("certfilter", filter);
				}
				vmanager.setFilter(filter);
			}

			SQLBuilder builder = new SQLBuilderCertificate();
			builder.setFilter(filter);

			reports = certService.makeReports(reportconfig.getFields(),	builder, vmanager.getOnfilter());

			//LOG.debug("Reporting finished...");

		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addAttribute("reports", reports);
		model.addAttribute("headers", reportconfig.getHeaders());
		return "fragments/viewreport";
	}		


	// ---------------------------------------------------------------------------------------
	// Fillin lists 
	// ---------------------------------------------------------------------------------------
	@ModelAttribute("countries")
	public Map<String, String> populateCompanyList() {
		return certService.getCountriesList("RU");
	}

	@ModelAttribute("departments")
	public Map<String, String> populateDepartmentssList() {
		return certService.getBranchesList();
	}

	@ModelAttribute("forms")
	public List<String> populateFormsList() {
		return certService.getFormsList();
	}

}
