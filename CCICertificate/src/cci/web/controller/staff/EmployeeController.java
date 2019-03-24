package cci.web.controller.staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import cci.config.staff.ExportEmployeeConfig;
import cci.model.Client;
import cci.model.ClientLocale;
import cci.model.Department;
import cci.model.Employee;
import cci.model.EmployeeLocale;
import cci.repository.SQLBuilder;
import cci.repository.client.SQLBuilderClient;
import cci.repository.staff.SQLBuilderEmployee;
import cci.service.FieldType;
import cci.service.Filter;
import cci.service.XSLService;
import cci.service.cert.CertService;
import cci.service.fscert.FSFilter;
import cci.service.staff.EmployeeFilter;
import cci.service.staff.EmployeeService;
import cci.web.controller.ViewManager;
import cci.web.controller.client.ViewClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

@Controller
@SessionAttributes({ "employeemanager", "employeefilter", "employee", "editemployee"})
public class EmployeeController {
	
	private static final Logger LOG = Logger.getLogger(EmployeeController.class);
	private String languages;
	
	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private CertService certService;
	
	Map<String, Map<String, String>> deplist = null; 
	// ---------------------------------------------------------------------------------------
	//  Client grid    - Get List of Clients for Grid Rendering
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "sgemp.do", method = RequestMethod.GET)
	public String sgemployees(
		  @RequestParam(value = "employeetype", required = false) String employeetype,
		  ModelMap model) {
		  model.addAttribute("employeetype", employeetype);
	  	  return "staff/empgrid";
	}
	// ---------------------------------------------------------------------------------------
	//  Main Request - Get List of Employees for Grid Rendering 
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "empgrid.do", method = RequestMethod.GET)
	public void listEmployeesForGrid (	
				@RequestParam(value = "page", required = false) int page,
				@RequestParam(value = "rows", required = false) int rows,
				@RequestParam(value = "sidx", defaultValue = "name", required = false) String sidx,
				@RequestParam(value = "sord", required = false) String sord,
				@RequestParam(value = "name", required = false) String name,
				@RequestParam(value = "job", required = false) String job,
				@RequestParam(value = "otd_name", required = false) String otd_name,
				@RequestParam(value = "departmentname", required = false) String departmentname,				
				Authentication aut,
				HttpSession session, HttpServletResponse response, HttpServletRequest request,
				ModelMap model) {
			
			LOG.info(request.getQueryString());
			
			Filter filter= new FSFilter();
			if (name != null) {
			      filter.setConditionValue("NAME", "NAME", "like", 
			    		  name, FieldType.STRING);
			}
			
			if (job != null) {
			      filter.setConditionValue("JOB", "JOB", "like", 
			    		  job, FieldType.STRING);
			}
			if (otd_name != null) {
			      filter.setConditionValue("OTD_NAME", "OTD_NAME", "like", 
			    		  otd_name, FieldType.STRING);
			}
			if (departmentname != null) {
			      filter.setConditionValue("DEPARTMENTNAME", "DEPARTMENTNAME", "like", 
			    		  otd_name, FieldType.STRING);
			}

			
			SQLBuilder builder = new SQLBuilderClient();
			builder.setFilter(filter);
			
			int itemscount = employeeService.getViewPageCount(builder);
			int pagecount = itemscount/rows + (itemscount%rows > 0 ? 1 : 0);
			
			List<ViewEmployee> elements = employeeService.readEmployeesPage(
					page, rows,   
					sidx, sord, builder);
			
			try {
	    	   response.setContentType("text/xml;charset=utf-8"); 
			   response.setCharacterEncoding("UTF-8");
		       response.getWriter().println(createXMLFromList(elements, page,pagecount ,itemscount));
			   response.flushBuffer();
			} catch (Exception ex) {
			   ex.printStackTrace();
			   model.addAttribute("error", ex.getMessage());
			}
	}
	
	private String createXMLFromList(List<ViewEmployee> elements, int page, int pagecount, int itemscount) {
			String xml;
			xml = "<?xml version='1.0' encoding='utf-8'?>";
			xml +=  "<rows>";
			xml += "<page>"+ page + "</page>";
			xml += "<total>"+pagecount+"</total>";
			xml += "<records>"+itemscount+"</records>";

			if (elements != null) {
				for (ViewEmployee row : elements) {
					xml += "<row id='"+ row.getId() + "'>";            
					xml += "<cell><![CDATA["+getValue(row.getName())+"]]></cell>";
					xml += "<cell><![CDATA["+getValue(row.getJob())+"]]></cell>";
					xml += "<cell><![CDATA["+getValue(row.getDepartmentname())+"]]></cell>";
					xml += "<cell><![CDATA["+getValue(row.getOtd_name())+"]]></cell>";
					xml += "</row>";
				}
			}
			xml +=  "</rows>";
			return xml;
	}
	
	private String getValue(Object obj) {
		return (obj == null ? "" : obj.toString());
	}
	
	// ---------------------------------------------------------------
	// Get Employee List
	// ---------------------------------------------------------------
	@RequestMapping(value = "employees.do", method = RequestMethod.GET)
	public String listemployees(
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "pagesize", required = false) Integer pagesize,
			@RequestParam(value = "orderby", required = false) String orderby,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "filter", required = false) Boolean onfilter,
			ModelMap model) {

		LOG.info("=========================== GET STAFF LIST =================================== >");

		ViewManager viewmanager = (ViewManager) model.get("employeemanager");

		if (viewmanager == null) {
			viewmanager = initViewManager(model);
		}

		if (orderby == null || orderby.isEmpty())
			orderby = "name";
		if (order == null || order.isEmpty())
			order = ViewManager.ORDASC;
		if (onfilter == null)
			onfilter = false;

		viewmanager.setPage(page == null ? 1 : page);
		viewmanager.setPagesize(pagesize == null ? 10 : pagesize);
		viewmanager.setOrderby(orderby);
		viewmanager.setOrder(order);
		viewmanager.setOnfilter(onfilter);
		viewmanager.setUrl("employees.do");

		Filter filter = null;
		if (onfilter) {
			filter = viewmanager.getFilter();

			if (filter == null) {
				if (model.get("employeefilter") != null) {
					filter = (Filter) model.get("employeefilter");
				} else {
					filter = new EmployeeFilter();
					model.addAttribute("employeefilter", filter);
				}
				viewmanager.setFilter(filter);
			}
		}

		SQLBuilder builder = new cci.repository.staff.SQLBuilderEmployee();
		builder.setFilter(filter);
		viewmanager.setPagecount(employeeService.getViewPageCount(builder));
		List<ViewEmployee> employees = employeeService.readEmployeesPage(
				viewmanager.getPage(), viewmanager.getPagesize(),
				viewmanager.getOrderby(), viewmanager.getOrder(), builder);
		viewmanager.setElements(employees);

		model.addAttribute("employeemanager", viewmanager);
		model.addAttribute("employees", employees);
		model.addAttribute("next_page", viewmanager.getNextPageLink());
		model.addAttribute("prev_page", viewmanager.getPrevPageLink());
		model.addAttribute("last_page", viewmanager.getLastPageLink());
		model.addAttribute("first_page", viewmanager.getFirstPageLink());
		model.addAttribute("pages", viewmanager.getPagesList());
		model.addAttribute("sizes", viewmanager.getSizesList());

		model.addAttribute("jspName", "staff/employees_include.jsp");
		return "listemployees";
	}

	  
	private ViewManager initViewManager(ModelMap model) {
		ViewManager viewmanager = new ViewManager();
		viewmanager.setHnames(new String[] { "ФИО", "Должность",
				"Структурное подразделение", "Отделение", "Телефон" });
		viewmanager.setOrdnames(new String[] { "name", "job", "departmentname", "otd_name",
				"phone" });
		viewmanager.setWidths(new int[] { 25, 25, 25, 15, 10 });
		model.addAttribute("employeemanager", viewmanager);
		return viewmanager;
	}

	// ---------------------------------------------------------------
	// Get Employee List For Selection needs
	// ---------------------------------------------------------------
	@RequestMapping(value = "selemployees.do", method = RequestMethod.GET)
	public String selectemployees(
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "pagesize", required = false) Integer pagesize,
			@RequestParam(value = "orderby", required = false) String orderby,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "filter", required = false) Boolean onfilter,
			@RequestParam(value = "employeetype", defaultValue = "employee", required = false) String employeetype,
			ModelMap model) {

		LOG.info("=========================== GET STAFF LIST SELECTION =================================== >");
		
		ViewManager viewmanager = (ViewManager) model.get("employeemanager");

		if (viewmanager == null) {
			viewmanager = initViewManager(model);
		}

		if (orderby == null || orderby.isEmpty())
			orderby = "name";
		if (order == null || order.isEmpty())
			order = ViewManager.ORDASC;
		if (onfilter == null)
			onfilter = false;

		viewmanager.setPage(page == null ? 1 : page);
		viewmanager.setPagesize(pagesize == null ? 10 : pagesize);
		viewmanager.setOrderby(orderby);
		viewmanager.setOrder(order);
		viewmanager.setOnfilter(onfilter);
		viewmanager.setUrl("selemployees.do");

		Filter filter = null;
		if (onfilter) {
			filter = viewmanager.getFilter();

			if (filter == null) {
				if (model.get("employeefilter") != null) {
					filter = (Filter) model.get("employeefilter");
				} else {
					filter = new EmployeeFilter();
					model.addAttribute("employeefilter", filter);
				}
				viewmanager.setFilter(filter);
			}
		}

		SQLBuilder builder = new SQLBuilderEmployee();
		builder.setFilter(filter);
		viewmanager.setPagecount(employeeService.getViewPageCount(builder));
		List<ViewEmployee> employees = employeeService.readEmployeesPage(
				viewmanager.getPage(), viewmanager.getPagesize(),
				viewmanager.getOrderby(), viewmanager.getOrder(), builder);
		viewmanager.setElements(employees);

		model.addAttribute("employeemanager", viewmanager);
		model.addAttribute("employees", employees);
		model.addAttribute("next_page", viewmanager.getNextPageLink());
		model.addAttribute("prev_page", viewmanager.getPrevPageLink());
		model.addAttribute("last_page", viewmanager.getLastPageLink());
		model.addAttribute("first_page", viewmanager.getFirstPageLink());
		model.addAttribute("pages", viewmanager.getPagesList());
		model.addAttribute("sizes", viewmanager.getSizesList());
		model.addAttribute("employeetype", employeetype);

		return "staff/employees";
	}
	
	// ---------------------------------------------------------------
	// Get Employee Filter Window
	// ---------------------------------------------------------------
	@RequestMapping(value="employeefilter.do", method = RequestMethod.GET)
	public String openFilter(@ModelAttribute("employeefilter") EmployeeFilter fc,
			ModelMap model) {

		if (fc == null) {
			fc = new EmployeeFilter();
			LOG.debug("New filterEmployee created in GET method");
			model.addAttribute("employeefilter", fc);
		} else {
			LOG.debug("Found FilterEmployee in GET : ");
		}

		ViewEmployeeFilter vf = new ViewEmployeeFilter(
				((EmployeeFilter) fc).getViewemployee(),
				((EmployeeFilter) fc).getCondition());
		model.addAttribute("viewfilter", vf);
		return "staff/efilter";
	}

	// ---------------------------------------------------------------
	// POST Employee Filter properties
	// ---------------------------------------------------------------
	@RequestMapping(value = "employeefilter.do", method = RequestMethod.POST)
	public String submitFilter(
			@ModelAttribute("viewfilter") ViewEmployeeFilter viewfilter,
			@ModelAttribute("employeefilter") EmployeeFilter fc,
			BindingResult result, SessionStatus status, ModelMap model) {

		if (fc == null) {
			fc = new EmployeeFilter();
			System.out.println("New filterEmployee created in the POST method");
		} else {
			LOG.debug("Found FilterEmployee in POST");
		}

		fc.loadViewEmployee(viewfilter.getViewemployee());
		fc.loadCondition(viewfilter.getCondition());

		model.addAttribute("employeefilter", fc);
		return "staff/efilter";
	}
	
	// ---------------------------------------------------------------
	// Add Employee POST
	// ---------------------------------------------------------------
	@RequestMapping(value = "employeeadd.do", method = RequestMethod.POST)
	public String addEmployee(@ModelAttribute("editemployee") Employee employee,
				BindingResult result, SessionStatus status, ModelMap model) {
			try {
				LOG.info(employee);
				employeeService.saveEmployee(employee);
			} catch(Exception ex) {
				ex.printStackTrace();
				model.addAttribute("error", ex.getMessage());
			}
			return "staff/employeeform";
	}

	// ---------------------------------------------------------------
	// Add Employee GET
	// ---------------------------------------------------------------
	@RequestMapping(value = "employeeadd.do", method = RequestMethod.GET)
	public String addEmployeeInit(ModelMap model) {
		Employee employee = new Employee();
		model.addAttribute("editemployee", employee);
		return "staff/employeeform";
	}

	// ---------------------------------------------------------------
	// Edit Employee GET
	// ---------------------------------------------------------------
	@RequestMapping(value="employeeedit.do", method = RequestMethod.GET)		
	public String updateEmployeeForm(@RequestParam(value = "id", required = true) 
							Long idemployee, ModelMap model) {
		try {
		   Employee employee = employeeService.readEmployee(idemployee);
		   model.addAttribute("editemployee", employee);
		} catch (Exception ex) {
		   model.addAttribute("error", ex.getMessage());
		}
  	    return "staff/employeeform";
	}
	
	// ---------------------------------------------------------------
	// Edit Employee POST
	// ---------------------------------------------------------------
	@RequestMapping(value="employeeedit.do", method = RequestMethod.POST)
	public String updateEmployee(@ModelAttribute("editemployee") Employee employee,
			BindingResult result, SessionStatus status, ModelMap model) {

		employeeService.updateEmployee(employee);
		return "staff/employeeform";
	}
	
	
	
	// ---------------------------------------------------------------
	// View Employee
	// ---------------------------------------------------------------
	@RequestMapping(value = "employeeview.do")
	public String employeeView(
			@RequestParam(value = "id", required = true) Long id, ModelMap model) {
		Employee employee = employeeService.readEmployeeView(id);
		model.addAttribute("employee", employee);

		return "staff/employeeview";
	}

	// ---------------------------------------------------------------
	// Export Employee List to XSL
	// ---------------------------------------------------------------
	@RequestMapping(value = "employeesexport.do", method = RequestMethod.GET)
	public void exportEmployeesToExcel(HttpSession session,
			HttpServletResponse response, ModelMap model) {
		try {

			LOG.debug("Download started...");
			ViewManager vmanager = (ViewManager) model.get("employeemanager");

			Filter filter = vmanager.getFilter();
			if (filter == null) {
				if (model.get("employeefilter") != null) {
					filter = (Filter) model.get("employeefilter");
				} else {
					filter = new EmployeeFilter();
					model.addAttribute("employeefilter", filter);
				}
				vmanager.setFilter(filter);
			}

			ExportEmployeeConfig dconfig = new ExportEmployeeConfig();

			SQLBuilder builder = new SQLBuilderEmployee();
			builder.setFilter(filter);
			List employees = employeeService.readEmployees(vmanager.getOrderby(),
					vmanager.getOrder(), builder);

			LOG.debug("Download. Employees loaded from database...");
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition",
					"attachment; filename=companies.xlsx");

			(new XSLService()).makeWorkbook(employees, dconfig.getHeaders(),
					dconfig.getFields(), "Список контрагентов").write(
					response.getOutputStream());

			response.flushBuffer();
			LOG.debug("Download finished...");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// ---------------------------------------------------------------------------------------
	//   Handling Goods List: Adding, Deleting
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "emplocales.do",  method = RequestMethod.GET)
	public void getgoods(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", defaultValue = "locales", required = false) String sidx,
			@RequestParam(value = "sord", required = false) String sord,
			HttpSession session, HttpServletResponse response, ModelMap model) {
			
			try {
					  Employee item = (Employee) model.get("editemployee");
					  LOG.info(item);
					  response.setContentType("text/html; charset=UTF-8");
					  response.setCharacterEncoding("UTF-8");
	  			      response.getWriter().println(localestoxml(item, page, rows));
					  response.flushBuffer();
					  
			} catch (Exception ex) {
						LOG.info("Ошибка: " + ex.getMessage());
						model.addAttribute("error", ex.getMessage());
			}
	}
	
	private String localestoxml(Employee item, int page, int rows ) {
		String xml;
		xml = "<?xml version='1.0' encoding='utf-8'?>";
		xml +=  "<rows>";
		
		if (item != null && item.getLocales() != null) {
			int itemscount = item.getLocales().size();
			int pagecount = itemscount/rows + (itemscount%rows > 0 ? 1 : 0);
			if (page > pagecount) {
				page = pagecount;
			}
			xml += "<page>"+ page + "</page>";
			xml += "<total>"+pagecount+"</total>";
			xml += "<records>"+itemscount+"</records>";
			
			if (page == 0) { 
				page++;
			}
			
			for (int index = (page-1) * rows; 
					itemscount > 0 && index < page * rows && index < itemscount; index++ ) {
				EmployeeLocale row = item.getLocales().get(index);
				xml += "<row id='"+ row.getId() + "'>";
				xml += "<cell><![CDATA["+(row.getLocale() == null ? "" : getLanguage(row.getLocale()))+"]]></cell>";
				xml += "<cell><![CDATA["+(row.getName() == null ? "" : row.getName())+"]]></cell>";
				xml += "<cell><![CDATA["+(row.getJob() == null ? "" : row.getJob())+"]]></cell>";
				xml += "</row>";
			}
		}
		xml +=  "</rows>";
		LOG.info(xml);
		return xml;
	}
	
    private String getLanguage(String locale) {
    	String ret = "";
    	switch (locale) {
    	   case "EN": ret = "Английский"; break; 
    	   case "ES" :ret = "Испанский"; break;
    	   case "IT": ret = "Итальянский"; break; 
    	   case "CN": ret = "Китайский"; break; 
    	   case "DE": ret ="Немецкий"; break; 
    	   case "RU": ret ="Русский"; break;
    	   case "FR": ret ="Французкий";break;
    	   default:ret ="Русский";
    	}
 	    return ret;
	}

	//=====================================================================
	@RequestMapping(value = "emplocaleupdate.do",  method = RequestMethod.POST)
	public void updatePOSTgoods(
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "locale", required = false) String locale,
			@RequestParam(value = "lname", required = false) String name,
			@RequestParam(value = "ljob", required = false) String job,
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		
		    LOG.info("POST: " + "id: " + id + " locale: " + locale + " name: " + name);
		    
			try {
				Employee item = (Employee)model.get("editemployee");
				  
				  for (EmployeeLocale element : item.getLocales()) {
					    if (element.getId() == Long.valueOf(id).longValue()) {
					       element.setLocale(locale);
					       element.setName(name);
					       element.setJob(job);
						   break;
					    }
				   }
			} catch (Exception ex) {
						LOG.info("Ошибка: " + ex.getMessage());
						model.addAttribute("error", ex.getMessage());
			}
	}


	private long getlastElementId(List<EmployeeLocale> elements) {
        long ret=0;
        for (EmployeeLocale element : elements) {
        	if (element.getId() != 0 && ret < element.getId()) {
        		ret = element.getId();
        	}
        }
		return ret;
	}
	
    //=====================================================================
	@RequestMapping(value = "emplocaleadd.do",  method = RequestMethod.GET)
	public void addgoods(
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
			try {
				  Employee item = (Employee) model.get("editemployee");
				  EmployeeLocale element  = new EmployeeLocale();
				  element.setName("");
				  element.setLocale("");
				  element.setJob("");
				  element.setId(getlastElementId(item.getLocales())+1);
				  item.getLocales().add(element);
			} catch (Exception ex) {
				  LOG.info("Ошибка: " + ex.getMessage());
				  model.addAttribute("error", ex.getMessage());
			}
	}
    //===================================================================== 
	@RequestMapping(value = "emplocaledel.do",  method = RequestMethod.GET)
	public void delgoods(
			@RequestParam(value = "id", required = false) String id,
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
			try {
				  if (id != null) {
					 long iid = Long.valueOf(id).longValue(); 
					 Employee item = (Employee) model.get("editemployee");
				     
				     for(int index = 0; index < item.getLocales().size(); index++) {
				    	 EmployeeLocale element = item.getLocales().get(index);
				    	 
				    	 if (element.getId() == iid) {
				    		 item.getLocales().remove(index);
				    		 break;
				    	 }
				     }
				  }
			} catch (Exception ex) {
				  LOG.info("Ошибка: " + ex.getMessage());
				  model.addAttribute("error", ex.getMessage());
			}
	}

	// ---------------------------------------------------------------------------------------
	// Fill in lists 
	// ---------------------------------------------------------------------------------------
	@ModelAttribute("branches")
	public Map<String, String> populateBranchesList() {
		return certService.getBranchesList();
	}
	
	@ModelAttribute("departments")
	// -------------------------------------------------------
	// Create list of departments divided by otd_id
	// -------------------------------------------------------
	public Map<String, Map<String, String>> populateDepartmentsList() {
		if (deplist == null) {
			try {
				Map<String, Department> departments  = employeeService.getDepartmentsList();
				Map<String, String> otdlist = null;
				deplist= new HashMap();
		
				for (Map.Entry<String, Department> dep : departments.entrySet()) {
					String otd_id = dep.getValue().getId_otd().toString();
					if (deplist.containsKey(otd_id)) {
						otdlist = deplist.get(otd_id);
					} else {
						otdlist = new HashMap<String, String>();
						deplist.put(otd_id, otdlist);
					}
					otdlist.put(dep.getKey(), dep.getValue().getName());
				}
			} catch (Exception ex) {
				LOG.info("Departments List loading error: " + ex.getMessage());
			}
			LOG.info(deplist);
		}
		return  deplist;
	}
	
	@ModelAttribute("languages")
	public String getLanguages() {
		if (languages == null) {
			languages = 
			   "EN:Английский;ES:Испанский;IT:Итальянский;CN:Китайский;DE:Немецкий;RU:Русский;FR:Французкий";
		}
		return languages;
	}
}
