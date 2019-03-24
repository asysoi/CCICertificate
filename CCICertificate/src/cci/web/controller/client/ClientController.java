package cci.web.controller.client;

import java.util.LinkedHashMap;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import cci.config.client.ExportClientConfig;
import cci.model.Client;
import cci.model.ClientLocale;
import cci.model.fscert.FSCertificate;
import cci.model.fscert.FSProduct;
import cci.repository.SQLBuilder;
import cci.repository.client.SQLBuilderClient;
import cci.repository.fscert.SQLBuilderFSCertificate;
import cci.service.FieldType;
import cci.service.Filter;
import cci.service.XSLService;
import cci.service.cert.CertFilter;
import cci.service.client.ClientService;
import cci.service.fscert.FSFilter;
import cci.service.client.ClientFilter;
import cci.web.controller.ViewManager;
import cci.web.controller.fscert.ViewFSCertificate;
import cci.web.validator.ClientValidator;

@Controller
@SessionAttributes({ "clientfilter", "cmanager", "client"})
public class ClientController {

	private static final Logger LOG = Logger.getLogger(ClientController.class);
	private ClientValidator clientValidator;
	private String languages;

	@Autowired
	private ClientService clientService;

	@Autowired
	public ClientController(ClientValidator clientValidator) {
		this.clientValidator = clientValidator;
	}
	// ---------------------------------------------------------------------------------------
	//  Client grid    - Get List of Clients for Grid Rendering
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "sglients.do", method = RequestMethod.GET)
	public String sglients(
			@RequestParam(value = "clienttype", required = false) String clienttype,
			ModelMap model) {
		   model.addAttribute("clienttype", clienttype);
		return "client/clientgrid";
	}
	// ---------------------------------------------------------------------------------------
	//  Main Request - Get List of Clients for Grid Rendering 
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "clientgrid.do", method = RequestMethod.GET)
	public void listCertsForGrid (	
				@RequestParam(value = "page", required = false) int page,
				@RequestParam(value = "rows", required = false) int rows,
				@RequestParam(value = "sidx", defaultValue = "name", required = false) String sidx,
				@RequestParam(value = "sord", required = false) String sord,
				@RequestParam(value = "name", required = false) String name,
				@RequestParam(value = "address", required = false) String address,
				@RequestParam(value = "unp", required = false) String unp,
				@RequestParam(value = "phone", required = false) String phone,
				@RequestParam(value = "email", required = false) String email,
				Authentication aut,
				HttpSession session, HttpServletResponse response, HttpServletRequest request,
				ModelMap model) {
			
			LOG.info(request.getQueryString());
			
			Filter filter= new FSFilter();
			if (name != null) {
			      filter.setConditionValue("NAME", "NAME", "like", 
			    		  name, FieldType.STRING);
			}
			
			if (address != null) {
			      filter.setConditionValue("ADDRESS", "ADDRESS", "like", 
			    		  address, FieldType.STRING);
			}
			if (unp != null) {
			      filter.setConditionValue("UNP", "UNP", "like", 
			    		  unp, FieldType.STRING);
			}
			
			if (phone != null) {
			      filter.setConditionValue("PHONE", "PHONE", "like", 
			    		  phone, FieldType.STRING);
			}
			if (email != null) {
			      filter.setConditionValue("EMAIL", "EMAIL", "like", 
			    		  email, FieldType.STRING);
			}
						
			SQLBuilder builder = new SQLBuilderClient();
			builder.setFilter(filter);
			
			int itemscount = clientService.getViewPageCount(builder);
			int pagecount = itemscount/rows + (itemscount%rows > 0 ? 1 : 0);
			
			List<ViewClient> elements = clientService.readClientsPage(
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
	
	private String createXMLFromList(List<ViewClient> elements, int page, int pagecount, int itemscount) {
			String xml;
			xml = "<?xml version='1.0' encoding='utf-8'?>";
			xml +=  "<rows>";
			xml += "<page>"+ page + "</page>";
			xml += "<total>"+pagecount+"</total>";
			xml += "<records>"+itemscount+"</records>";

			if (elements != null) {
				for (ViewClient row : elements) {
					xml += "<row id='"+ row.getId() + "'>";            
					xml += "<cell><![CDATA["+getValue(row.getName())+"]]></cell>";
					xml += "<cell><![CDATA["+getValue(row.getAddress())+"]]></cell>";
					xml += "<cell><![CDATA["+getValue(row.getUnp())+"]]></cell>";
					xml += "<cell><![CDATA["+getValue(row.getPhone())+"]]></cell>";
					xml += "<cell><![CDATA["+getValue(row.getEmail())+"]]></cell>";
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
	// Get Clients List
	// ---------------------------------------------------------------
	@RequestMapping(value = "clients.do", method = RequestMethod.GET)
	public String listclients(
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "pagesize", required = false) Integer pagesize,
			@RequestParam(value = "orderby", required = false) String orderby,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "filter", required = false) Boolean onfilter,
			ModelMap model) {

		System.out
				.println("=========================== GET CLIENT LIST =================================== >");

		ViewManager cmanager = (ViewManager) model.get("cmanager");

		if (cmanager == null) {
			cmanager = initViewManager(model);
		}

		if (orderby == null || orderby.isEmpty())
			orderby = "name";
		if (order == null || order.isEmpty())
			order = ViewManager.ORDASC;
		if (onfilter == null)
			onfilter = false;

		cmanager.setPage(page == null ? 1 : page);
		cmanager.setPagesize(pagesize == null ? 10 : pagesize);
		cmanager.setOrderby(orderby);
		cmanager.setOrder(order);
		cmanager.setOnfilter(onfilter);
		cmanager.setUrl("clients.do");

		Filter filter = null;
		if (onfilter) {
			filter = cmanager.getFilter();

			if (filter == null) {
				if (model.get("clientfilter") != null) {
					filter = (Filter) model.get("clientfilter");
				} else {
					filter = new ClientFilter();
					model.addAttribute("clientfilter", filter);
				}
				cmanager.setFilter(filter);
			}
		}

		SQLBuilder builder = new SQLBuilderClient();
		builder.setFilter(filter);
		cmanager.setPagecount(clientService.getViewPageCount(builder));
		List<ViewClient> clients = clientService.readClientsPage(
				cmanager.getPage(), cmanager.getPagesize(),
				cmanager.getOrderby(), cmanager.getOrder(), builder);
		cmanager.setElements(clients);

		model.addAttribute("cmanager", cmanager);
		model.addAttribute("clients", clients);
		model.addAttribute("next_page", cmanager.getNextPageLink());
		model.addAttribute("prev_page", cmanager.getPrevPageLink());
		model.addAttribute("last_page", cmanager.getLastPageLink());
		model.addAttribute("first_page", cmanager.getFirstPageLink());
		model.addAttribute("pages", cmanager.getPagesList());
		model.addAttribute("sizes", cmanager.getSizesList());

		model.addAttribute("jspName", "client/clients_include.jsp");
		return "window";
	}

	  
	private ViewManager initViewManager(ModelMap model) {
		ViewManager cmanager = new ViewManager();
		cmanager.setHnames(new String[] { "Наименование компании", "Адрес",
				"УНП", "Банк", "Контактный телефон" });
		cmanager.setOrdnames(new String[] { "name", "address", "unp", "bname",
				"phone" });
		cmanager.setWidths(new int[] { 25, 20, 15, 20, 20 });
		model.addAttribute("cmanager", cmanager);
		return cmanager;
	}

	// ---------------------------------------------------------------
	// Get Clients List
	// ---------------------------------------------------------------
	@RequestMapping(value = "sclients.do", method = RequestMethod.GET)
	public String selectclients(
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "pagesize", required = false) Integer pagesize,
			@RequestParam(value = "orderby", required = false) String orderby,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "filter", required = false) Boolean onfilter,
			@RequestParam(value = "clienttype", defaultValue = "client", required = false) String clienttype,
			ModelMap model) {

		System.out
				.println("=========================== GET CLIENT SELECTION =================================== >");

		ViewManager cmanager = (ViewManager) model.get("cmanager");

		if (cmanager == null) {
			cmanager = initViewManager(model);
		}

		if (orderby == null || orderby.isEmpty())
			orderby = "name";
		if (order == null || order.isEmpty())
			order = ViewManager.ORDASC;
		if (onfilter == null)
			onfilter = false;

		cmanager.setPage(page == null ? 1 : page);
		cmanager.setPagesize(pagesize == null ? 10 : pagesize);
		cmanager.setOrderby(orderby);
		cmanager.setOrder(order);
		cmanager.setOnfilter(onfilter);
		cmanager.setUrl("sclients.do");

		Filter filter = null;
		if (onfilter) {
			filter = cmanager.getFilter();

			if (filter == null) {
				if (model.get("clientfilter") != null) {
					filter = (Filter) model.get("clientfilter");
				} else {
					filter = new ClientFilter();
					model.addAttribute("clientfilter", filter);
				}
				cmanager.setFilter(filter);
			}
		}

		SQLBuilder builder = new SQLBuilderClient();
		builder.setFilter(filter);
		cmanager.setPagecount(clientService.getViewPageCount(builder));
		List<ViewClient> clients = clientService.readClientsPage(
				cmanager.getPage(), cmanager.getPagesize(),
				cmanager.getOrderby(), cmanager.getOrder(), builder);
		cmanager.setElements(clients);

		model.addAttribute("cmanager", cmanager);
		model.addAttribute("clients", clients);
		model.addAttribute("next_page", cmanager.getNextPageLink());
		model.addAttribute("prev_page", cmanager.getPrevPageLink());
		model.addAttribute("last_page", cmanager.getLastPageLink());
		model.addAttribute("first_page", cmanager.getFirstPageLink());
		model.addAttribute("pages", cmanager.getPagesList());
		model.addAttribute("sizes", cmanager.getSizesList());
		model.addAttribute("clienttype", clienttype);

		return "client/clients";
	}


	
	
	// ---------------------------------------------------------------
	// Get Client Filter Window
	// ---------------------------------------------------------------
	@RequestMapping(value = "clientfilter.do", method = RequestMethod.GET)
	public String openFilter(@ModelAttribute("clientfilter") ClientFilter fc,
			ModelMap model) {

		if (fc == null) {
			fc = new ClientFilter();
			LOG.debug("New filterClient created in GET method");
			model.addAttribute("clientfilter", fc);
		} else {
			LOG.debug("Found FilterClient in GET : ");
		}

		ViewClientFilter vf = new ViewClientFilter(
				((ClientFilter) fc).getViewclient(),
				((ClientFilter) fc).getCondition());
		model.addAttribute("viewfilter", vf);
		return "client/cfilter";
	}

	// ---------------------------------------------------------------
	// Set Client Filter properties
	// ---------------------------------------------------------------
	@RequestMapping(value = "clientfilter.do", method = RequestMethod.POST)
	public String submitFilter(
			@ModelAttribute("viewfilter") ViewClientFilter viewfilter,
			@ModelAttribute("clientfilter") ClientFilter fc,
			BindingResult result, SessionStatus status, ModelMap model) {

		if (fc == null) {
			fc = new ClientFilter();
			System.out.println("New filterClient created in the POST method");
		} else {
			LOG.debug("Found FilterClient in POST");
		}

		fc.loadViewclient(viewfilter.getViewclient());
		fc.loadCondition(viewfilter.getCondition());
		
		model.addAttribute("clientfilter", fc);
		return "client/cfilter";
	}

	// ---------------------------------------------------------------
	// View Client
	// ---------------------------------------------------------------
	@RequestMapping(value = "clientview.do")
	public String clientView(
			@RequestParam(value = "id", required = true) Long id, ModelMap model) {
		Client client = clientService.readClientView(id);
		model.addAttribute("client", client);

		return "client/clientview";
	}

	// ---------------------------------------------------------------
	// Add Client POST
	// ---------------------------------------------------------------
	@RequestMapping(value = "clientadd.do", method = RequestMethod.POST)
	public String addClient(@ModelAttribute("client") Client client,
			BindingResult result, SessionStatus status, ModelMap model) {

		// clientValidator.validate(client, result);

		// status.setComplete();
		// generateAddressString(client);
		clientService.saveClient(client);
		return "client/clientform";
	}

	// ---------------------------------------------------------------
	// Add Client GET
	// ---------------------------------------------------------------
	@RequestMapping(value = "clientadd.do", method = RequestMethod.GET)
	public String addClientInit(ModelMap model) {

		Client client = new Client();
		model.addAttribute("client", client);
		return "client/clientform";
	}

	// ---------------------------------------------------------------
	// Update Client POST
	// ---------------------------------------------------------------
	@RequestMapping(value = "clientedit.do", method = RequestMethod.POST)
	public String updateClient(@ModelAttribute("client") Client client,
			BindingResult result, SessionStatus status, ModelMap model) {

		// status.setComplete();
		// generateAddressString(client);
		clientService.updateClient(client);
		return "client/clientform";
	}

	private void generateAddressString(Client client) {
        String address = "" ;
        
        address = client.getCindex() == null ? "" : client.getCindex();
        address +=  client.getCity() == null ? "" : address.isEmpty() ? client.getCity() : ", " + client.getCity();
        address +=  client.getStreet() == null ? "" : address.isEmpty() ? client.getStreet() : ", " + client.getStreet();
        address +=  client.getBuilding() == null ? "" : address.isEmpty() ? client.getBuilding() : ", " + client.getBuilding();
        address +=  client.getCodecountry() == null ? "" : address.isEmpty() ? getCountryNameBycode("RUF", client.getCodecountry()) : ", " + getCountryNameBycode("RUF", client.getCodecountry());
        
		client.setAddress(address);
		
		for (ClientLocale item : client.getLocales()) {
			address = "";
	        address = client.getCindex() == null ? "" : client.getCindex();
	        address +=  item.getCity() == null ? "" : address.isEmpty() ? item.getCity() : ", " + item.getCity();
	        address +=  item.getStreet() == null ? "" : address.isEmpty() ? item.getStreet() : ", " + item.getStreet();
	        address +=  client.getBuilding() == null ? "" : address.isEmpty() ? client.getBuilding() : ", " + client.getBuilding();
	        address +=  item.getLocale() == null ? "" : address.isEmpty() ? getCountryNameBycode("EN", client.getCodecountry()) : ", " + getCountryNameBycode("EN", client.getCodecountry());
	        item.setAddress(address);
		}
	}

	private Object getCountryNameBycode(String lang, String codecountry) {
		LOG.info("Lang = " + lang + " Code: " + codecountry);
		
		String ret = clientService.getCountriesList(lang).get(codecountry);
		return (ret == null) ? "" : ret; 
	}

	// ---------------------------------------------------------------
	// Update Client GET
	// ---------------------------------------------------------------
	@RequestMapping(value = "clientedit.do", method = RequestMethod.GET)
	public String updateClientInit(
			@RequestParam(value = "id", required = true) Long id, ModelMap model) {

		Client client = clientService.readClient(id);

		model.addAttribute("client", client);
		return "client/clientform";
	}

	// ---------------------------------------------------------------
	// Export Client List to XSL
	// ---------------------------------------------------------------
	@RequestMapping(value = "clientsexport.do", method = RequestMethod.GET)
	public void exportClientsToExcel(HttpSession session,
			HttpServletResponse response, ModelMap model) {
		try {

			LOG.debug("Download started...");
			ViewManager vmanager = (ViewManager) model.get("cmanager");

			Filter filter = vmanager.getFilter();
			if (filter == null) {
				if (model.get("clientfilter") != null) {
					filter = (Filter) model.get("clientfilter");
				} else {
					filter = new CertFilter();
					model.addAttribute("clientfilter", filter);
				}
				vmanager.setFilter(filter);
			}

			ExportClientConfig dconfig = new ExportClientConfig();

			SQLBuilder builder = new SQLBuilderClient();
			builder.setFilter(filter);
			List clients = clientService.readClients(vmanager.getOrderby(),
					vmanager.getOrder(), builder);

			LOG.debug("Download. Clients loaded from database...");
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition",
					"attachment; filename=companies.xlsx");

			(new XSLService()).makeWorkbook(clients, dconfig.getHeaders(),
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
	@RequestMapping(value = "clientlocales.do",  method = RequestMethod.GET)
	public void getgoods(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", defaultValue = "locales", required = false) String sidx,
			@RequestParam(value = "sord", required = false) String sord,
			HttpSession session, HttpServletResponse response, ModelMap model) {
			
			try {
					  Client item = (Client) model.get("client");
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
	
	private String localestoxml(Client item, int page, int rows ) {
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
				ClientLocale row = item.getLocales().get(index);
				xml += "<row id='"+ row.getId() + "'>";
				xml += "<cell><![CDATA["+(row.getLocale() == null ? "" : getLanguage(row.getLocale()))+"]]></cell>";
				xml += "<cell><![CDATA["+(row.getName() == null ? "" : row.getName())+"]]></cell>";
				xml += "<cell><![CDATA["+(row.getAddress() == null ? "" : row.getAddress())+"]]></cell>";
				//xml += "<cell><![CDATA["+(row.getCity() == null ? "" : row.getCity())+"]]></cell>";
				//xml += "<cell><![CDATA["+(row.getStreet() == null ? "" : row.getStreet())+"]]></cell>";
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
	@RequestMapping(value = "clientlocaleupdate.do",  method = RequestMethod.POST)
	public void updatePOSTgoods(
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "locale", required = false) String locale,
			@RequestParam(value = "lname", required = false) String name,
			@RequestParam(value = "lcity", required = false) String city,
			@RequestParam(value = "lstreet", required = false) String street,
			@RequestParam(value = "laddress", required = false) String address,
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		
			try {
				  Client item = (Client)model.get("client");
				  
				  for (ClientLocale element : item.getLocales()) {
					    if (element.getId() == Long.valueOf(id).longValue()) {
					       element.setLocale(locale);
					       element.setName(name);
					       element.setAddress(address);
					       //element.setCity(city);
					       //element.setStreet(street);
						   break;
					    }
				   }
			} catch (Exception ex) {
						LOG.info("Ошибка: " + ex.getMessage());
						model.addAttribute("error", ex.getMessage());
			}
	}


	private long getlastElementId(List<ClientLocale> elements) {
        long ret=0;
        for (ClientLocale element : elements) {
        	if (element.getId() != 0 && ret < element.getId()) {
        		ret = element.getId();
        	}
        }
		return ret;
	}
	
    //=====================================================================
	@RequestMapping(value = "clientlocaleadd.do",  method = RequestMethod.GET)
	public void addgoods(
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
			try {
				  Client item = (Client) model.get("client");
 				  ClientLocale element  = new ClientLocale();
				  element.setName("");
				  element.setLocale("");
				  element.setCity("");
				  element.setStreet("");
				  element.setId(getlastElementId(item.getLocales())+1);
				  item.getLocales().add(element);
			} catch (Exception ex) {
				  LOG.info("Ошибка: " + ex.getMessage());
				  model.addAttribute("error", ex.getMessage());
			}
	}
    //===================================================================== 
	@RequestMapping(value = "clientlocaledel.do",  method = RequestMethod.GET)
	public void delgoods(
			@RequestParam(value = "id", required = false) String id,
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
			try {
				  if (id != null) {
					 long iid = Long.valueOf(id).longValue(); 
				     Client item = (Client) model.get("client");
				     
				     for(int index = 0; index < item.getLocales().size(); index++) {
				    	 ClientLocale element = item.getLocales().get(index);
				    	 
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
	

	

	// ---------------------------------------------------------------
	// Get Countries List
	// ---------------------------------------------------------------
	@ModelAttribute("countries")
	public Map<String, String> populateCountryList() {
		return clientService.getCountriesList("RU");
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
