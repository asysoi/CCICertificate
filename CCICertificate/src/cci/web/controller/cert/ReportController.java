package cci.web.controller.cert;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import cci.config.cert.ExportCertConfig;
import cci.model.cert.Certificate;
import cci.repository.SQLBuilder;
import cci.repository.cert.SQLBuilderCertificate;
import cci.service.FieldType;
import cci.service.Filter;
import cci.service.XSLService;
import cci.service.cert.CertService;
import cci.service.cert.ReportFilter;
import cci.service.cert.ReportService;
import cci.web.controller.ViewManager;

@Controller
@SessionAttributes({ "reportfilter", "rmanager" })
public class ReportController {

	public static Logger LOG = Logger.getLogger(ReportController.class);
	// private static final Logger LOG=Logger.getLogger(CertController.class);

	@Autowired
	private ReportService reportService;

	@Autowired
	private CertService certService;

	// ---------------------------------------------------------------------------------------
	// View certs report config
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "reportconfig.do", method = RequestMethod.GET)
	public String openConfig(ModelMap model) {
		ViewManager rmanager = (ViewManager) model.get("rmanager");

		if (rmanager == null) {
			rmanager = initViewManager(model);
		}

		if (rmanager.getDownloadconfig() == null) {
			rmanager.setDownloadconfig(new ExportCertConfig());
		}

		model.addAttribute("downloadconfig", rmanager.getDownloadconfig());
		model.addAttribute("headermap", rmanager.getDownloadconfig()
				.getHeadermap());
		return "fragments/config";
	}

	// ---------------------------------------------------------------------------------------
	// Post download config
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "reportconfig.do", method = RequestMethod.POST)
	public String submitConfig(
			@ModelAttribute("downloadconfig") ExportCertConfig config,
			BindingResult result, SessionStatus status, ModelMap model) {

		ViewManager rmanager = (ViewManager) model.get("rmanager");
		rmanager.setDownloadconfig(config);

		model.addAttribute("downloadconfig", rmanager.getDownloadconfig());
		model.addAttribute("headermap", config.getHeadermap());
		return "fragments/config";
	}

	// ---------------------------------------------------------------------------------------
	// Report download
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "reportdownload.do", method = RequestMethod.GET)
	public void XSLFileDownload(HttpSession session,
			HttpServletResponse response, ModelMap model) {
		try {

			LOG.debug("Download started...");
			ViewManager rmanager = (ViewManager) model.get("rmanager");

			Filter filter = rmanager.getFilter();
			if (filter == null) {
				if (model.get("certfilter") != null) {
					filter = (Filter) model.get("reportfilter");
				} else {
					filter = new ReportFilter();
					model.addAttribute("reportfilter", filter);
				}
				rmanager.setFilter(filter);
			}

			if (rmanager.getDownloadconfig() == null) {
				rmanager.setDownloadconfig(new ExportCertConfig());
			}

			SQLBuilder builder = new SQLBuilderCertificate();
			builder.setFilter(filter);
			rmanager.getDownloadconfig().addField("DATE_LOAD");
			rmanager.getDownloadconfig().addHeader("Дата загрузки сертификата");
			
			List certs = reportService.readCertificates(rmanager.getDownloadconfig().getFields(), 
					rmanager.getOrderby(), rmanager.getOrder(),	builder);

			LOG.debug("Download. Certificates loaded from database...");
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			// response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					"attachment; filename=certificates.xlsx");
					
			(new XSLService()).makeWorkbook(certs,
					rmanager.getDownloadconfig().getHeaders(),
					rmanager.getDownloadconfig().getFields(),
					"Лист Сертификатов").write(response.getOutputStream());
			response.flushBuffer();
			
			rmanager.getDownloadconfig().removeField("DATE_LOAD");
			rmanager.getDownloadconfig().removeHeader("Дата загрузки сертификата");

			LOG.debug("Download finished...");

		} catch (Exception e) {
			LOG.debug(e.getMessage());
			e.printStackTrace();
		}
	}

	// ---------------------------------------------------------------------------------------
	// Get List of Certificates
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "reportcerts.do", method = RequestMethod.GET)
	public String reportcerts(
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "pagesize", required = false) Integer pagesize,
			@RequestParam(value = "orderby", required = false) String orderby,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "filter", required = false) Boolean onfilter,
			@RequestParam(value = "datefrom", required = false) String datefrom,
			@RequestParam(value = "dateto", required = false) String dateto,
			Authentication aut, ModelMap model) {

		ViewManager rmanager = (ViewManager) model.get("rmanager");
		// LOG.debug("step1");

		if (rmanager == null) {
			rmanager = initViewManager(model);
		}

		if (orderby == null || orderby.isEmpty())
			orderby = "date_load";
		if (order == null || order.isEmpty())
			order = ViewManager.ORDASC;
		if (onfilter == null)
			onfilter = false;

		rmanager.setPage(page == null ? 1 : page);
		rmanager.setPagesize(pagesize == null ? 10 : pagesize);
		rmanager.setOrderby(orderby);
		rmanager.setOrder(order);
		rmanager.setUrl("reportcerts.do");
		// LOG.debug("step2");

		Filter filter = null;
		filter = rmanager.getFilter();

		if (filter == null) {
			if (model.get("reportlter") != null) {
				filter = (Filter) model.get("reportfilter");
			} else {
				filter = new ReportFilter();
				model.addAttribute("reportfilter", filter);
			}
			rmanager.setFilter(filter);
		}

		LOG.debug("datefrom: " + datefrom);
		LOG.debug("dateto: " + dateto);
		filter.setConditionValue("DATEFROM", "DATE_LOAD", ">=",
				datefrom == null ? "" : datefrom, FieldType.DATE);
		filter.setConditionValue("DATEFTO", "DATE_LOAD", "<=",
				dateto == null ? "" : dateto, FieldType.DATE);
		filter.setConditionValue("OTD_NAME", "OTD_NAME", "=", "",
				FieldType.STRING);
		filter.setConditionValue("OTD_ID", "OTD_ID", "=", "",
				FieldType.NUMBER);

		Iterator iterator = (aut.getAuthorities()).iterator();
		while (iterator.hasNext()) {

			String roleName = ((GrantedAuthority) iterator.next())
					.getAuthority();

			if (certService.getACL().containsKey(roleName)) {
				filter.setConditionValue("OTD_ID", "OTD_ID", "=",
						certService.getACL().get(roleName), FieldType.NUMBER);
			}
		}

		SQLBuilder builder = new SQLBuilderCertificate();
		builder.setFilter(filter);

		rmanager.setPagecount(reportService.getViewPageCount(builder));
        //long start = System.currentTimeMillis(); 
		List<Certificate> certs = reportService.readCertificatesPage(
				rmanager.getOrdnames(),
				rmanager.getPage(), rmanager.getPagesize(),
				rmanager.getOrderby(), rmanager.getOrder(), builder);
		//LOG.info("Read Report duration: " + (System.currentTimeMillis() - start));
		
		rmanager.setElements(certs);

		model.addAttribute("rmanager", rmanager);
		model.addAttribute("certs", certs);
		model.addAttribute("next_page", rmanager.getNextPageLink());
		model.addAttribute("prev_page", rmanager.getPrevPageLink());
		model.addAttribute("last_page", rmanager.getLastPageLink());
		model.addAttribute("first_page", rmanager.getFirstPageLink());
		model.addAttribute("pages", rmanager.getPagesList());
		model.addAttribute("sizes", rmanager.getSizesList());
		model.addAttribute("datefrom", datefrom);
		model.addAttribute("dateto", dateto);

		model.addAttribute("jspName", "cert/report_include.jsp");
		return "window";
	}

	private ViewManager initViewManager(ModelMap model) {
		ViewManager rmanager = new ViewManager();
		rmanager.setHnames(new String[] { "Дата загрузки", "Номер Сертификата",
				"Отделение", "Эксперт", "Номер бланка", "Дата серт." });
		rmanager.setOrdnames(new String[] { "date_load", "nomercert",
				"otd_id", "expert", "nblanka", "issuedate" });
		rmanager.setWidths(new int[] { 18, 12, 22, 26, 10, 14 });
		model.addAttribute("rmanager", rmanager);

		return rmanager;
	}
}
