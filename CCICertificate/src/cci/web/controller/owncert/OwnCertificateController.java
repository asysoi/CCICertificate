package cci.web.controller.owncert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import cci.config.cert.ExportCertConfig;
import cci.config.own.ExportOwnCertConfig;
import cci.model.cert.Certificate;
import cci.model.owncert.OwnCertificate;
import cci.repository.SQLBuilder;
import cci.repository.cert.SQLBuilderCertificate;
import cci.repository.owncert.SQLBuilderOwnCertificate;
import cci.service.FieldType;
import cci.service.Filter;
import cci.service.cert.CertFilter;
import cci.service.cert.CertService;
import cci.service.cert.XSLWriter;
import cci.service.owncert.OwnCertificateService;
import cci.service.owncert.OwnFilter;
import cci.web.controller.ViewManager;
import cci.web.controller.cert.CertificateController;
import cci.web.controller.cert.ViewCertFilter;


@Controller
@SessionAttributes({ "owncertfilter", "ownmanager"})
public class OwnCertificateController {
	
	private static final Logger LOG=Logger.getLogger(CertificateController.class);
	
	@Autowired
	private OwnCertificateService ownCertService;
	
	@Autowired
	private CertService certService;

	// ---------------------------------------------------------------------------------------
	//  Main Request - Get List of Own Certificates
	// ---------------------------------------------------------------------------------------
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
		LOG.info("< =========================== GET OWN CERT LIST =================================== >");

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
	
	//------------------------------------------------------------------------------
	//  Init viewmanager 
	//------------------------------------------------------------------------------
	private ViewManager initViewManager(ModelMap model) {
		ViewManager vmanager = new ViewManager();
		vmanager.setHnames(new String[] {"Номер Сертификата",  "Отделение", 
				"Предприятие", "Номер бланка", "Дата", "Доп. лист"});
		vmanager.setOrdnames(new String[] { "number", "beltppname", "customername",
				"blanknumber", "datecert", "additionalblanks"});
		vmanager.setWidths(new int[] { 15, 20, 40, 10, 10, 5 });
		model.addAttribute("ownmanager", vmanager);

		return vmanager;
	}
	
  	// ---------------------------------------------------------------------------------------
	//    Own Certificate filter handling Method GET
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "owncertfilter.do", method = RequestMethod.GET)
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
		
	// ---------------------------------------------------------------------------------------
	//   Own Certificate filter handling Method POST
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "owncertfilter.do", method = RequestMethod.POST)
	public String submitFilter(
				@ModelAttribute("viewownfilter") ViewOwnCertificateFilter viewfilter,
				@ModelAttribute("owncertfilter") OwnFilter fc,
				BindingResult result, SessionStatus status, ModelMap model) {

			if (fc == null) {
				fc = new OwnFilter();
				LOG.debug
						("New filterOwnCertificate created in the POST method");
			} else {
				LOG.debug("Found FilterOwnCertificate in POST");
			}

			fc.loadViewcertificate(viewfilter.getViewcertificate());
			fc.loadCondition(viewfilter.getCondition());

			model.addAttribute("owncertfilter", fc);
			return "own/ownfilter";
	}
	
	// ---------------------------------------------------------------------------------------
	// View own certs table config  for Export certificates to Excel format
	// ---------------------------------------------------------------------------------------
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
				HttpServletResponse response, ModelMap model) {
			try {
				
	            LOG.debug("Download started...");   
				ViewManager vmanager = (ViewManager) model.get("ownmanager");

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
					vmanager.setDownloadconfig(new ExportOwnCertConfig());
				}

				SQLBuilder builder = new SQLBuilderOwnCertificate();
				builder.setFilter(filter);
				List certs = ownCertService.readCertificates(vmanager.getDownloadconfig().getFields(),
						vmanager.getOrderby(), vmanager.getOrder(), builder);
				
				LOG.debug("Download. Certificates loaded from database..."); 
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

				response.setHeader("Content-Disposition",
						"attachment; filename=certificates.xlsx");
				(new XSLWriter()).makeWorkbook(certs,
						vmanager.getDownloadconfig().getHeaders(),
						vmanager.getDownloadconfig().getFields(), "Лист Сертификатов").write(
						response.getOutputStream());
				response.flushBuffer();
				LOG.debug("Download finished...");

			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	// ---------------------------------------------------------------------------------------
	//   View Own Certificate as HTML page 
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "owncert.do",  method = RequestMethod.GET)
	
	public String gocert(@RequestParam(value = "certid", required = true) Integer certid,
				ModelMap model) {
			try {
			     OwnCertificate cert = ownCertService.getOwnCertificateById(certid);
			     model.addAttribute("owncert", cert);
			     LOG.info(cert); 
			} catch (Exception ex) {
				model.addAttribute("error", ex.getMessage());
				return "error";
			}
			return "own/viewowncertificate";
		}

	
	// ---------------------------------------------------------------
	// Get LIst od Own Certificate Types
	// ---------------------------------------------------------------
	@ModelAttribute("types")
	public Map<String, String> populateOwnCertificateTypesList() {
		Map<String, String> types = new HashMap<String, String>();
		types.put("P", "Продукция");
		types.put("S", "Услуги");
		return types;
	}
		
	@ModelAttribute("departments")
	public Map<String, String> populateDepartmentssList() {
		return certService.getBranchesList();
	}

	
}
