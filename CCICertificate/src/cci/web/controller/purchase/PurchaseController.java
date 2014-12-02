package cci.web.controller.purchase;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import cci.config.purchase.ExportPurchaseConfig;
import cci.model.purchase.Purchase;
import cci.repository.SQLBuilder;
import cci.repository.purchase.SQLBuilderPurchase;
import cci.service.Filter;
import cci.service.cert.CertFilter;
import cci.service.cert.XSLWriter;
import cci.service.purchase.PurchaseFilter;
import cci.service.purchase.PurchaseService;
import cci.web.controller.ViewManager;
import cci.web.validator.purchase.PurchaseValidator;

@Controller
@SessionAttributes({ "purchasefilter", "pmanager" })
public class PurchaseController {

	public static Logger LOG = LogManager.getLogger(PurchaseController.class);
	private PurchaseValidator purchaseValidator;

	@Autowired
	private PurchaseService purchaseService;

	@Autowired
	public PurchaseController(PurchaseValidator purchaseValidator) {
		this.purchaseValidator = purchaseValidator;
	}

	// ----------------------------------------------------------------------------
	// Purchase List Pagination
	// ----------------------------------------------------------------------------
	@RequestMapping(value = "purchases.do")
	public String listpurchases(
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "pagesize", required = false) Integer pagesize,
			@RequestParam(value = "orderby", required = false) String orderby,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "filter", required = false) Boolean onfilter,
			ModelMap model) {

		System.out
				.println("=========================== GET PURCHASES LIST =================================== >");

		ViewManager vmanager = (ViewManager) model.get("pmanager");

		if (vmanager == null) {
			vmanager = initViewManager(model);
		}

		if (orderby == null || orderby.isEmpty())
			orderby = "pchdate";
		if (order == null || order.isEmpty())
			order = ViewManager.ORDASC;
		if (onfilter == null)
			onfilter = false;

		vmanager.setPage(page == null ? 1 : page);
		vmanager.setPagesize(pagesize == null ? 10 : pagesize);
		vmanager.setOrderby(orderby);
		vmanager.setOrder(order);
		vmanager.setOnfilter(onfilter);
		vmanager.setUrl("purchases.do");

		Filter filter = null;
		if (onfilter) {
			filter = vmanager.getFilter();

			if (filter == null) {
				if (model.get("purchasefilter") != null) {
					filter = (Filter) model.get("purchasefilter");
				} else {
					filter = new PurchaseFilter();
					model.addAttribute("purchasefilter", filter);
				}
				vmanager.setFilter(filter);
			}
		}

		SQLBuilder builder = new SQLBuilderPurchase();
		builder.setFilter(filter);
		vmanager.setPagecount(purchaseService.getViewPurchasePageCount(builder));
		List<ViewPurchase> purchases = purchaseService.readViewPurchasePage(
				vmanager.getPage(), vmanager.getPagesize(),
				vmanager.getOrderby(), vmanager.getOrder(), builder);
		vmanager.setElements(purchases);

		model.addAttribute("pmanager", vmanager);
		model.addAttribute("purchases", purchases);
		model.addAttribute("next_page", vmanager.getNextPageLink());
		model.addAttribute("prev_page", vmanager.getPrevPageLink());
		model.addAttribute("last_page", vmanager.getLastPageLink());
		model.addAttribute("first_page", vmanager.getFirstPageLink());
		model.addAttribute("pages", vmanager.getPagesList());
		model.addAttribute("sizes", vmanager.getSizesList());

		model.addAttribute("jspName", "pch/purchaselist.jsp");
		return "window";
	}

	private ViewManager initViewManager(ModelMap model) {
		ViewManager vmanager = new ViewManager();
		vmanager.setHnames(new String[] { "Дата закупки", "Товар", "Продавец",
				"Цена", "Объем", "Ед. измер", "Покупатель" });
		vmanager.setOrdnames(new String[] { "pchdate", "product", "company",
				"price", "volume", "unit", "department" });
		vmanager.setWidths(new int[] { 15, 15, 20, 10, 10, 5, 25 });
		model.addAttribute("pmanager", vmanager);
		return vmanager;
	}

	// ---------------------------------------------------------------
	// Get Purchase Filter Window
	// ---------------------------------------------------------------
	@RequestMapping(value = "filterpurchase.do", method = RequestMethod.GET)
	public String openFilter(
			@ModelAttribute("purchasefilter") PurchaseFilter fc, ModelMap model) {

		if (fc == null) {
			fc = new PurchaseFilter();
			LOG.info("New filterPurchase created in GET method");
			model.addAttribute("purchasefilter", fc);
		} else {
			LOG.info("Found FilterClient in GET : ");
		}

		ViewPurchaseFilter vf = new ViewPurchaseFilter(
				((PurchaseFilter) fc).getViewpurchase(),
				((PurchaseFilter) fc).getCondition());
		model.addAttribute("viewfilter", vf);
		return "pch/purchasefilter";
	}

	// ---------------------------------------------------------------
	// Set Purchase Filter properties
	// ---------------------------------------------------------------
	@RequestMapping(value = "filterpurchase.do", method = RequestMethod.POST)
	public String submitFilter(
			@ModelAttribute("viewfilter") ViewPurchaseFilter viewfilter,
			@ModelAttribute("purchasefilter") PurchaseFilter fc,
			BindingResult result, SessionStatus status, ModelMap model) {

		if (fc == null) {
			fc = new PurchaseFilter();
			System.out.println("New filterPurchase created in the POST method");
		} else {
			LOG.info("Found FilterPurchase in POST");
		}

		fc.loadViewpurchase(viewfilter.getViewpurchase());
		fc.loadCondition(viewfilter.getCondition());

		model.addAttribute("purchasefilter", fc);
		return "pch/purchasefilter";
	}

	// ----------------------------------------------------------------------------
	// Purchase View Query
	// ----------------------------------------------------------------------------
	@RequestMapping(value = "viewpurchase.do")
	public String purchaseView(
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "popup", required = false) Boolean popup,
			ModelMap model) {

		ViewPurchase purchaseView = purchaseService.readViewPurchase(id);
		model.addAttribute("purchase", purchaseView);

		return "pch/purchaseview";
	}

	// ----------------------------------------------------------------------------
	// Purchase add POST query handler
	// ----------------------------------------------------------------------------
	@RequestMapping(value = "addpurchase.do", method = RequestMethod.POST)
	public String addPurchesSubmit(
			@ModelAttribute("purchase") ViewPurchase purchaseView,
			BindingResult result, SessionStatus status, ModelMap model) {

		// purchaseValidator.validate(purchaseView, result);

		purchaseService.savePurchase(purchaseView);
		return "pch/purchaseform";
	}

	// ----------------------------------------------------------------------------
	// Purchase add GET query handler
	// ----------------------------------------------------------------------------
	@RequestMapping(value = "addpurchase.do", method = RequestMethod.GET)
	public String addPurchaseForm(ModelMap model) {

		Purchase purchase = new Purchase();
		model.addAttribute("purchase", purchase);
		return "pch/purchaseform";
	}

	// ---------------------------------------------------------------
	// Update Purchase POST
	// ---------------------------------------------------------------
	@RequestMapping(value = "editpurchase.do", method = RequestMethod.POST)
	public String updatePurchase(@ModelAttribute("purchase") Purchase purchase,
			BindingResult result, SessionStatus status, ModelMap model) {

		// status.setComplete();
		purchaseService.updatePurchase(purchase);
		return "pch/purchaseform";
	}

	// ---------------------------------------------------------------
	// Update Purchase GET
	// ---------------------------------------------------------------
	@RequestMapping(value = "editpurchase.do", method = RequestMethod.GET)
	public String updatePurchaseInit(
			@RequestParam(value = "id", required = true) Long id, ModelMap model) {

		Purchase purchase = purchaseService.readPurchase(id);

		model.addAttribute("purchase", purchase);
		return "pch/purchaseform";
	}

	// ---------------------------------------------------------------
	// Export Purchase List to XSL
	// ---------------------------------------------------------------
	@RequestMapping(value = "/exportpurchases.do", method = RequestMethod.GET)
	public void exportPurchasesToExcel(HttpSession session,
			HttpServletResponse response, ModelMap model) {
		try {

			LOG.info("Download started...");
			ViewManager vmanager = (ViewManager) model.get("pmanager");

			Filter filter = vmanager.getFilter();
			if (filter == null) {
				if (model.get("purchasefilter") != null) {
					filter = (Filter) model.get("purchasefilter");
				} else {
					filter = new CertFilter();
					model.addAttribute("purchasefilter", filter);
				}
				vmanager.setFilter(filter);
			}

			ExportPurchaseConfig dconfig = new ExportPurchaseConfig();

			SQLBuilder builder = new SQLBuilderPurchase();
			builder.setFilter(filter);
			List purchases = purchaseService.readPurchases(vmanager.getOrderby(),
					vmanager.getOrder(), builder);

			LOG.info("Download. Purchases loaded from database...");
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition",
					"attachment; filename=certificates.xlsx");

			(new XSLWriter()).makeWorkbook(purchases, dconfig.getHeaders(),
					dconfig.getFields(), "Список сделок").write(
					response.getOutputStream());

			response.flushBuffer();
			LOG.info("Download finished...");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------------------------------
	// Initial Process
	// ----------------------------------------------------------------------------
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));
	}

	// ----------------------------------------------------------------------------
	// Get Department List
	// ----------------------------------------------------------------------------
	@ModelAttribute("departmentList")
	public Map<Long, String> populateDepartmentList() {

		return purchaseService.readDepartments();
	}

	// ----------------------------------------------------------------------------
	// Get ProductList
	// ----------------------------------------------------------------------------
	@ModelAttribute("productList")
	public Map<Long, String> populateProductList() {

		return purchaseService.readProducts();
	}

	// ----------------------------------------------------------------------------
	// Get Clients list
	// ----------------------------------------------------------------------------
	@ModelAttribute("companyList")
	public Map<Long, String> populateCompanyList() {

		return purchaseService.readCompanies();
	}

}