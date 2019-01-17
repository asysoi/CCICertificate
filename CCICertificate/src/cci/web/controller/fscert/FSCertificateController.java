package cci.web.controller.fscert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import cci.config.fscert.ExportFSCertConfig;
import cci.model.Client;
import cci.model.ClientLocale;
import cci.model.Employee;
import cci.model.EmployeeLocale;
import cci.model.cert.Certificate;
import cci.model.fscert.Branch;
import cci.model.fscert.Expert;
import cci.model.fscert.Exporter;
import cci.model.fscert.FSBlank;
import cci.model.fscert.FSCertificate;
import cci.model.fscert.FSProduct;
import cci.model.fscert.Producer;
import cci.model.fscert.Signer;
import cci.pdfbuilder.PDFBuilder;
import cci.pdfbuilder.cert.CertificatePDFBuilder;
import cci.pdfbuilder.fscert.FSPDFBuilder;
import cci.repository.SQLBuilder;
import cci.repository.fscert.SQLBuilderFSCertificate;
import cci.service.CountryConverter;
import cci.service.FieldType;
import cci.service.Filter;
import cci.service.cert.CertFilter;
import cci.service.cert.CertService;
import cci.service.cert.XSLWriter;
import cci.service.client.ClientService;
import cci.service.fscert.FSCertificateService;
import cci.service.fscert.FSFilter;
import cci.service.staff.EmployeeService;
import cci.web.controller.ViewManager;
import cci.web.controller.cert.CertificateController;


@Controller
@SessionAttributes({ "fscertfilter", "fsmanager","fscert", "activeemployee"})
public class FSCertificateController {
	
	private static final Logger LOG=Logger.getLogger(FSCertificateController.class);
	
	@Autowired
	private FSCertificateService fsCertService;
	
	@Autowired
	private CertService certService;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private EmployeeService employeeService;

	// ---------------------------------------------------------------------------------------
	//  Main Request - Get List of FS Certificates for Grid Rendering
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "fsgrid.do", method = RequestMethod.GET)
	public void listCertsForGrid (	
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", defaultValue = "datecert", required = false) String sidx,
			@RequestParam(value = "sord", required = false) String sord,
			@RequestParam(value = "certnumber", required = false) String certnumber,
			@RequestParam(value = "exportername", required = false) String exportername,
			@RequestParam(value = "exporteraddress", required = false) String exporteraddress,
			@RequestParam(value = "producername", required = false) String producername,
			@RequestParam(value = "produceraddress", required = false) String produceraddress,
			@RequestParam(value = "datecert", required = false) String datecert,
			Authentication aut,
			HttpSession session, HttpServletResponse response, HttpServletRequest request,
			ModelMap model) {
		
		LOG.info(request.getQueryString());
		
		Filter filter= new FSFilter();
		if (certnumber != null) {
		      filter.setConditionValue("CERTNUMBER", "CERTNUMBER", "like", 
		    		  certnumber, FieldType.STRING);
		}
		
		if (exportername != null) {
		      filter.setConditionValue("EXPORTERNAME", "NAME", "like", 
		    		  exportername, FieldType.STRING);
		}
		if (exporteraddress != null) {
		      filter.setConditionValue("EXPORTERADDRESS", "ADDRESS", "like", 
		    		  exporteraddress, FieldType.STRING);
		}
		
		if (producername != null) {
		      filter.setConditionValue("PRODUCERNAME", "NAME", "like", 
		    		  producername, FieldType.STRING);
		}
		if (produceraddress != null) {
		      filter.setConditionValue("PRODUCERADDRESS", "ADDRESS", "like", 
		    		  produceraddress, FieldType.STRING);
		}
		if (datecert != null) {
		      filter.setConditionValue("DATECERTFROM", "DATECERT", ">=", 
		    		  datecert, FieldType.DATE);
		}
		
		SQLBuilder builder = new SQLBuilderFSCertificate();
		builder.setFilter(filter);
		
		int itemscount = fsCertService.getViewPageCount(builder);
		int pagecount = itemscount/rows + (itemscount%rows > 0 ? 1 : 0);
		
		List<ViewFSCertificate> certs = fsCertService.readCertificatesPage(
				new String[] {"*"},
				page, rows, itemscount,  
				sidx, sord, builder);
		
		try {
    	   response.setContentType("text/xml;charset=utf-8"); 
		   response.setCharacterEncoding("UTF-8");
	       response.getWriter().println(createXMLFromList(certs, page,pagecount ,itemscount));
		   response.flushBuffer();
		} catch (Exception ex) {
		   ex.printStackTrace();
		   model.addAttribute("error", ex.getMessage());
		}
	}

	private String createXMLFromList(List<ViewFSCertificate> certs, int page, int pagecount, int itemscount) {
		String xml;
		xml = "<?xml version='1.0' encoding='utf-8'?>";
		xml +=  "<rows>";
		xml += "<page>"+ page + "</page>";
		xml += "<total>"+pagecount+"</total>";
		xml += "<records>"+itemscount+"</records>";

		if (certs != null) {
			for (ViewFSCertificate row : certs) {
				xml += "<row id='"+ row.getId() + "'>";            
				xml += "<cell>"+row.getCertnumber()+"</cell>";
				xml += "<cell><![CDATA["+row.getExportername()+"]]></cell>";
				xml += "<cell><![CDATA["+row.getExporteraddress()+"]]></cell>";
				xml += "<cell><![CDATA["+row.getProducername()+"]]></cell>";
				xml += "<cell><![CDATA["+row.getProduceraddress()+"]]></cell>";
				xml += "<cell>"+row.getDatecert()+"</cell>";
				xml += "</row>";
			}
		}
		xml +=  "</rows>";
		return xml;
	}


	// ---------------------------------------------------------------------------------------
	//  Main Request - Get List of FS Certificates
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "fscerts.do", method = RequestMethod.GET)
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
		LOG.info("< =========================== GET FS CERT LIST =================================== >");

		ViewManager vmanager = (ViewManager) model.get("fsmanager");

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
		vmanager.setUrl("fscerts.do");

		Filter filter = null;
		
		if (onfilter) {
			filter = vmanager.getFilter();

			if (filter == null) {
				if (model.get("fscertfilter") != null) {
					filter = (Filter) model.get("fscertfilter");
				} else {
					filter = new FSFilter();
					model.addAttribute("fscertfilter", filter);
				}
				vmanager.setFilter(filter);
			}
		} 
		
		// ACL needs: use filter to restrict access to documents  
		if (filter == null) {
			filter = new FSFilter();
		}
		
		
		Iterator iterator = aut.getAuthorities().iterator(); 
		while (iterator.hasNext()) {
			
		      String roleName = ((GrantedAuthority) iterator.next()).getAuthority();
		      
		      if  (certService.getACL().containsKey(roleName)) {      
			      filter.setConditionValue("OTD_ID", "OTD_ID", "=", 
			    		  certService.getACL().get(roleName), FieldType.NUMBER);
		      }
		}
		
		SQLBuilder builder = new SQLBuilderFSCertificate();
		builder.setFilter(filter);
		
		long step2 = System.currentTimeMillis();
		vmanager.setPagecount(fsCertService.getViewPageCount(builder));
		
		long step3 = System.currentTimeMillis();
		
		List<ViewFSCertificate> certs = fsCertService.readCertificatesPage(
				vmanager.getOrdnames(),
				vmanager.getPage(), vmanager.getPagesize(), vmanager.getPagecount(),  
				vmanager.getOrderby(), vmanager.getOrder(), builder);
		
		long step4 = System.currentTimeMillis();
		vmanager.setElements(certs);
        
		
		model.addAttribute("fsmanager", vmanager);
		model.addAttribute("certs", certs);
		model.addAttribute("next_page", vmanager.getNextPageLink());
		model.addAttribute("prev_page", vmanager.getPrevPageLink());
		model.addAttribute("last_page", vmanager.getLastPageLink());
		model.addAttribute("first_page", vmanager.getFirstPageLink());
		model.addAttribute("pages", vmanager.getPagesList());
		model.addAttribute("sizes", vmanager.getSizesList());
		model.addAttribute("timeduration", 
				" " + (step3 - step2) + " + " + (step4 - step3) + " = " + (System.currentTimeMillis() - start));

		return "listfscertificates";
	}
	
	//------------------------------------------------------------------------------
	//  Init viewmanager 
	//------------------------------------------------------------------------------
	private ViewManager initViewManager(ModelMap model) {
		ViewManager vmanager = new ViewManager();
		vmanager.setHnames(new String[] {"Номер Сертификата",  "Экспортер", 
				"Производитель", "Дата", "Кол. лист", "Выдан"});
		vmanager.setOrdnames(new String[] { "certnumber", "exportername", "producername",
				"datecert", "listscount", "locked" });
		vmanager.setWidths(new int[] { 10, 35, 30, 10, 10, 5});
		model.addAttribute("fsmanager", vmanager);

		return vmanager;
	}
	
  	// ---------------------------------------------------------------------------------------
	//    FS Certificate filter handling Method GET
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "fscertfilter.do", method = RequestMethod.GET)
	public String openFilter(
				@ModelAttribute("fscertfilter") FSFilter fc, ModelMap model) {

			if (fc == null) {
				fc = new FSFilter();
				LOG.debug("New filterFSCertificate created in GET method");
				model.addAttribute("fscertfilter", fc);
			} else {
				LOG.debug("Found FilterFSCertificate in GET : ");
			}

			ViewFSCertificateFilter vf = new ViewFSCertificateFilter(
					((FSFilter) fc).getViewcertificate(),
					((FSFilter) fc).getCondition());
			model.addAttribute("viewfsfilter", vf);
			return "fs/fsfilter";
	}
		
	// ---------------------------------------------------------------------------------------
	//   FS Certificate filter handling Method POST
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "fscertfilter.do", method = RequestMethod.POST)
	public String submitFilter(
				@ModelAttribute("viewfsfilter") ViewFSCertificateFilter viewfilter,
				@ModelAttribute("fscertfilter") FSFilter fc,
				BindingResult result, SessionStatus status, ModelMap model) {

			if (fc == null) {
				fc = new FSFilter();
				LOG.debug
						("New filterFSCertificate created in the POST method");
			} else {
				LOG.debug("Found FilterFSCertificate in POST");
			}

			fc.loadViewcertificate(viewfilter.getViewcertificate());
			fc.loadCondition(viewfilter.getCondition());

			model.addAttribute("fscertfilter", fc);
			return "fs/fsfilter";
	}
	
	// ---------------------------------------------------------------------------------------
	// View fs certs table config  for Export certificates to Excel format
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "fscertconfig.do", method = RequestMethod.GET)
	public String openConfig(ModelMap model) {
		ViewManager vmanager = (ViewManager) model.get("fsmanager");

		if (vmanager == null) {
			vmanager = initViewManager(model);
		}
       
		if (vmanager.getDownloadconfig() == null) {
			vmanager.setDownloadconfig(new ExportFSCertConfig());
		}
		
		model.addAttribute("downloadconfig", vmanager.getDownloadconfig());
		model.addAttribute("headermap", vmanager.getDownloadconfig().getHeadermap());
		return "fs/config";
	}

	// ---------------------------------------------------------------------------------------
	//   Post fs certs table config  for Export certificates to Excel format
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "fscertconfig.do", method = RequestMethod.POST)
	public String submitConfig(@ModelAttribute("downloadconfig") ExportFSCertConfig config,
			BindingResult result, SessionStatus status, ModelMap model) {
		
		ViewManager vmanager = (ViewManager) model.get("fsmanager");
		vmanager.setDownloadconfig(config);
                
		model.addAttribute("downloadconfig", vmanager.getDownloadconfig());
		model.addAttribute("headermap", config.getHeadermap());
		return "fs/config";
	}
	
	// ---------------------------------------------------------------------------------------
	//  Download list of FS Certificate as Excel file
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "fscertdownload.do", method = RequestMethod.GET)
	public void XSLFileDownload(HttpSession session,
				HttpServletResponse response, ModelMap model) {
			try {
				
	            LOG.debug("Download started...");   
				ViewManager vmanager = (ViewManager) model.get("fsmanager");

				Filter filter = vmanager.getFilter();
				if (filter == null) {
					if (model.get("fscertfilter") != null) {
						filter = (Filter) model.get("fscertfilter");
					} else {
						filter = new FSFilter();
						model.addAttribute("fscertfilter", filter);
					}
					vmanager.setFilter(filter);
				}

				if (vmanager.getDownloadconfig() == null) {
					vmanager.setDownloadconfig(new ExportFSCertConfig());
				}

				SQLBuilder builder = new SQLBuilderFSCertificate();
				builder.setFilter(filter);
				List certs = fsCertService.readCertificates(vmanager.getDownloadconfig().getFields(),
						vmanager.getOrderby(), vmanager.getOrder(), builder);
				
				LOG.debug("Download. Certificates loaded from database..."); 
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

				response.setHeader("Content-Disposition",
						"attachment; filename=fscertificates.xlsx");
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
	//   Create new FS Certificate/ Fill expert name and Number generate
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "fsadd.do",  method = RequestMethod.GET)
	public String createFSCertificate( Authentication aut, ModelMap model) {
		try {
			     FSCertificate cert = new FSCertificate();
			     cert.setLanguage("RU");
			     Employee employee = (Employee) model.get("activeemployee");
				     
			     if (employee == null) {
			    	 employee = fsCertService.getEmployeeByUserName(aut.getName());
			         model.addAttribute("activeemployee", employee);
			     }
				     
			     if (employee != null) {
			    	 Expert expert = new Expert();
			    	 expert.init(employee);
			    	 cert.setExpert(expert);
				     
			    	 Signer signer = new Signer();
			    	 signer.init(employee);
			    	 cert.setSigner(signer);
				    	 
			    	 cert.setOtd_id(employee.getDepartment().getId_otd());
			    	 cert.setDepartment(employee.getDepartment()); 
			    	 
			    	 Branch branch  = fsCertService.getBranchByCode(signer.getDepartment().getCode_otd());
			    	 cert.setBranch(branch);
			     }
			     
			     cert.setDatecert((new SimpleDateFormat("dd.MM.yyyy")).format(new Date()));
				     model.addAttribute("fscert", cert);

		} catch (Exception ex) {
				ex.printStackTrace();
				model.addAttribute("error", ex.getMessage());
				return "window";
		}
		return "editfscertificate";
	}
	
	
	@RequestMapping(value = "fsadd.do",  method = RequestMethod.POST)
	public String saveNewFSCertificate(FSCertificate fscert,
			BindingResult result, SessionStatus status, HttpServletRequest request, 
			HttpServletResponse response, ModelMap model) {
		    
		    FSCertificate storedCert = (FSCertificate)model.get("fscert");
		    
		    if (storedCert == null) {
			    model.addAttribute("error", "Информация о редактируемом сертификате потеряна. Перезегрузите сертификат.");		       	
		    } else {
		    	fscert.setBranch(storedCert.getBranch());
		    	fscert.setExporter(storedCert.getExporter());
		    	fscert.setProducer(storedCert.getProducer());
		    	fscert.setExpert(storedCert.getExpert());
		    	fscert.setSigner(storedCert.getSigner());
		    	fscert.setBlanks(storedCert.getBlanks());
		    	fscert.setProducts(storedCert.getProducts());
		    	fscert.setDepartment(storedCert.getDepartment());
		    	if (storedCert.getDepartment() != null) {
		    		fscert.setOtd_id(storedCert.getDepartment().getId_otd());
		    	}
		    	
		    	try {
		    		String relativeWebPath = "/resources";
		    		String  absoluteDiskPath= request.getSession().getServletContext().getRealPath(relativeWebPath);
    				fscert.setListscount(makepdffile(absoluteDiskPath, fscert, "org"));
		    		fsCertService.save(fscert);
					model.remove("error");		    		
		    	} catch (Exception ex) {
					model.addAttribute("error", ex.getMessage());
		    	}
		    	model.addAttribute("fscert", fscert);
		    	storedCert = null;
		    }
			return "  ";
	}

	// ---------------------------------------------------------------------------------------
	//   Create new FS Certificate/ Fill expert name and Number generate
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "rldfsnumber.do",  method = RequestMethod.GET)	
	private void generateNewCertNumberPost(
			@RequestParam(value = "codecountry", required = true) String codecountry, 
			HttpSession session, HttpServletResponse response, ModelMap model) {
		
		
		try {
			  FSCertificate storedCert = (FSCertificate)model.get("fscert");
			  String number = "BY";
			  
			   if (storedCert == null) {
					model.addAttribute("error", "Информация о редактируемом сертификате потеряна. Перезегрузите сертификат.");		       	
				} else {
					number += codecountry != null  && !codecountry.equals("null") ? codecountry : "";
					number += "7";
		    		    			
					if (storedCert.getSigner() != null && storedCert.getSigner().getDepartment() != null) {
						number += storedCert.getSigner().getDepartment().getId_otd();
						number += storedCert.getSigner().getDepartment().getCode();
					}
		      }
			  
			  response.setContentType("text/html; charset=UTF-8");
			  response.setCharacterEncoding("UTF-8");
		      response.getWriter().println(number);
			  response.flushBuffer();
				  
		} catch (Exception ex) {
			 ex.printStackTrace();
			 LOG.info("Ошибка: " + ex.getMessage());
			 model.addAttribute("error", ex.getMessage());
		}
	}
	
	// ---------------------------------------------------------------------------------------
	//   Reload Template of certification field
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "getlistcount.do",  method = RequestMethod.GET)
	public void getListCount(
			@RequestParam(value = "type", defaultValue="org", required = false) String type,
			HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model)  {

		    FSCertificate cert = (FSCertificate) model.get("fscert");
		    calculateListCount(cert, type, request, response, model);
	}

	private void calculateListCount(FSCertificate cert, String type, 
			    HttpServletRequest request,	HttpServletResponse response, ModelMap model ) {
		String relativeWebPath = "/resources";
		String  absoluteDiskPath= request.getSession().getServletContext().getRealPath(relativeWebPath);
				
		int pagecount = 1;
		
		try {
			if (cert != null) {
				pagecount = makepdffile(absoluteDiskPath, cert, type);
			}
			
 		    response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(pagecount);
		    response.flushBuffer();

		} catch (Exception ex) {
			 ex.printStackTrace();
			 LOG.info("Ошибка: " + ex.getMessage());
			 model.addAttribute("error", ex.getMessage());
		}

	}
	
	
	// ---------------------------------------------------------------------------------------
	//   View FS Certificate as HTML page 
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "fscert.do",  method = RequestMethod.GET)
	public String viewHTML(@RequestParam(value = "certid", required = true) Integer certid,
				ModelMap model) {
			try {
			     FSCertificate cert = fsCertService.getFSCertificateById(certid);
			     model.addAttribute("fscert", cert);
			     LOG.info(cert); 
			} catch (Exception ex) {
				model.addAttribute("error", ex.getMessage());
				return "error";
			}
			return "fs/viewfscertificate";
	}
	
	
	// ---------------------------------------------------------------------------------------
	//   EDIT FS Certificate as HTML page 
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "fsedit.do",  method = RequestMethod.GET)
	public String edit(@RequestParam(value = "certid", required = true) Integer certid,
				ModelMap model) {
			try {
			     FSCertificate cert = fsCertService.getFSCertificateById(certid);
			     model.addAttribute("fscert", cert);
			     LOG.info(cert); 
			} catch (Exception ex) {
				ex.printStackTrace();
				model.addAttribute("error", ex.getMessage());
			}
			return "editfscertificate";
	}

	// ---------------------------------------------------------------------------------------
	//   SAVE/ FS Certificate as HTML page 
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "fsedit.do",  method = RequestMethod.POST)
	public void save(FSCertificate fscert,
			BindingResult result, HttpSession session, 
			HttpServletResponse response, HttpServletRequest request, 
			SessionStatus status, ModelMap model) {
		    
		    FSCertificate storedCert = (FSCertificate)model.get("fscert");
		    
		    if (storedCert == null) {
			    model.addAttribute("error", "Информация о редактируемом сертификате потеряна. Перезегрузите сертификат.");		       	
		    } else {
		    	fscert.setId(storedCert.getId());
		    	// fscert.setCertnumber(storedCert.getCertnumber());
		    	fscert.setBranch(storedCert.getBranch());
		    	fscert.setExporter(storedCert.getExporter());
		    	fscert.setProducer(storedCert.getProducer());
		    	fscert.setExpert(storedCert.getExpert());
		    	fscert.setSigner(storedCert.getSigner());
		    	fscert.setBlanks(storedCert.getBlanks());
		    	fscert.setProducts(storedCert.getProducts());
		    	fscert.setDepartment(storedCert.getDepartment());
		    	fscert.setOtd_id(storedCert.getOtd_id());
		    	try {
		    		String relativeWebPath = "/resources";
		    		String  absoluteDiskPath= request.getSession().getServletContext().getRealPath(relativeWebPath);
    				fscert.setListscount(makepdffile(absoluteDiskPath, fscert, "org"));
    				
		    		fsCertService.save(fscert);
			    	model.addAttribute("fscert", fscert);
			    	storedCert = null;
		    	} catch (Exception ex) {
					model.addAttribute("error", ex.getMessage());
					String json = "{\"error\":\"" +  ex.getMessage()
					  		+ "\"}";
					try {		  
						response.setContentType("text/html; charset=UTF-8");
					    response.setStatus(400);
						response.setCharacterEncoding("UTF-8");
						response.getWriter().println(json);
						response.flushBuffer();
					} catch (Exception wex) {
						wex.printStackTrace();
						LOG.info("Ошибка: " + wex.getMessage());
					}
		    	}
		    }
	}

	// ---------------------------------------------------------------------------------------
	//   Link Exporter to FS certificate  
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "selclient.do",  method = RequestMethod.GET)
	public void linkClientToFSCertificate(
			@RequestParam(value = "id", required = true) Long clientid,
			@RequestParam(value = "clienttype", required = true) String clienttype,
			@RequestParam(value = "lang", required = true) String lang,
			HttpSession session, HttpServletResponse response, ModelMap model) {
		
			try {
				  LOG.info("Exporter ID: " + clientid);
				  FSCertificate cert = (FSCertificate)model.get("fscert");
				  Client client = clientService.readClient(clientid.longValue());
				 
				  if (client!=null && cert!=null) {
					  if ("exporter".equals(clienttype)) {
						Exporter exporter = new Exporter();
						exporter.init(client);
						cert.setExporter(exporter);
					  } else if ("producer".equals(clienttype)) {
						Producer producer = new Producer();
						producer.init(client);
						cert.setProducer(producer);
					  } 
				
					  response.setContentType("text/html; charset=UTF-8");
					  response.setCharacterEncoding("UTF-8");
					  
					  
					  if (!"RU".equals(lang)) {
						  ClientLocale locale = client.getLocale(lang);
						  if (locale != null) {
					        response.getWriter().println(getValue(locale.getName()) + "; " + getValue(locale.getAddress()));
 			        	  } else {
 			        		response.getWriter().println("Не определено для выбранного языка");
				          }
						  response.getWriter().println("("+getValue(client.getName()) + "; " + getValue(client.getAddress())+")");
					  } else {
						  response.getWriter().println(getValue(client.getName()) + "; " + getValue(client.getAddress()));
					  }
					  
					  response.flushBuffer();
					  LOG.info("Linked exporter to certificate: " + cert.getId());
					  
				  } else {	 
					  model.addAttribute("error", "Сертификат или экспортер не найдены");
				  }
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.info("Ошибка: " + ex.getMessage());
				model.addAttribute("error", ex.getMessage());
			}
	}
	

	// ---------------------------------------------------------------------------------------
	//   Link Employee to FS certificate  
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "selemployee.do",  method = RequestMethod.GET)
	public void linkEmployeeToFSCertificate(
			@RequestParam(value = "id", required = true) Long eid,
			@RequestParam(value = "employeetype", required = true) String etype,
			@RequestParam(value = "lang", required = true) String lang,
			HttpSession session, HttpServletResponse response, ModelMap model) {
		
			try {
				  FSCertificate cert = (FSCertificate)model.get("fscert");
				  Employee emp = employeeService.readEmployee(eid.longValue());
				 
				  if (emp!=null && cert!=null) {
					  if ("expert".equals(etype)) {
						Expert expert = new Expert();
						expert.init(emp);
						cert.setExpert(expert);
					  } else if ("signer".equals(etype)) {
						Signer signer = new Signer();
						signer.init(emp);
						cert.setSigner(signer);
				    	Branch branch  = fsCertService.getBranchByCode(signer.getDepartment().getCode_otd());
				    	cert.setBranch(branch);
					  } 
				
					  
					  String json = "{\"expert\":\"" + getEmployeeName(cert.getExpert(), lang) + "\","
							  		+ "\"signer\":\"" + getEmployeeName(cert.getSigner(), lang)+ "\""
							  		+ "}";
					  
					  response.setContentType("text/html; charset=UTF-8");
					  response.setCharacterEncoding("UTF-8");
					  response.getWriter().println(json);
					  response.flushBuffer();
				  } else {	 
					  model.addAttribute("error", "Сертификат или сотрудник не найдены");
				  }
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.info("Ошибка: " + ex.getMessage());
				model.addAttribute("error", ex.getMessage());
			}
	}

	
	// --------------------------------------------------------------------------------------
	//   Get empty or value string
	// --------------------------------------------------------------------------------------
	private String getValue(Object obj) {
        String ret;
		return (obj == null ? "" : obj.toString());
	}
	
	//---------------------------------------------------------------------------------------
	// Get list of Cert Numbers for autocomplete
	//---------------------------------------------------------------------------------------
	@RequestMapping(value = "parentlist.do",  method = RequestMethod.GET)
	public void getListOfCertNumber(
			@RequestParam(value = "term", required = true) String term,
			HttpSession session, HttpServletResponse response, ModelMap model) {
			
			try {
				  FSCertificate cert = (FSCertificate)model.get("fscert");
				  String json = fsCertService.getListCertNumber(term.toUpperCase());
				  response.setContentType("text/html; charset=UTF-8");
				  response.setCharacterEncoding("UTF-8");
  			      response.getWriter().println(json);
				  response.flushBuffer();
				  
			} catch (Exception ex) {
					ex.printStackTrace();
					LOG.info("Ошибка: " + ex.getMessage());
					model.addAttribute("error", ex.getMessage());
			}
	}

	// ---------------------------------------------------------------------------------------
	//   Reload Template of certification field
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "rldconfirm.do",  method = RequestMethod.GET)
	public void reloadConfirmationField(
			@RequestParam(value = "lang", required = true) String lang,
			HttpSession session, HttpServletResponse response, ModelMap model) {
			
			try {
				  FSCertificate cert = (FSCertificate)model.get("fscert");
				  
				  String template = fsCertService.getTemplate("confirmation", lang);
				  String replacement = cert.getProducer() != null ? getClientName(cert.getProducer(), lang) : "";
				  
				  if (template != null && replacement != null) {
				      template = template.replaceAll("\\[producer\\]", replacement);
				  }
				  cert.setConfirmation(template == null ? "Шаблон для выбранного языка не определен" : template);
				 
				  response.setContentType("text/html; charset=UTF-8");
				  response.setCharacterEncoding("UTF-8");
  			      response.getWriter().println(cert.getConfirmation());
				  response.flushBuffer();
				  
			} catch (Exception ex) {
					ex.printStackTrace();
					LOG.info("Ошибка: " + ex.getMessage());
					model.addAttribute("error", ex.getMessage());
			}
	}
	
	private String getClientName(Client client, String lang) {
        String ret = "";
        if (client != null) {
        	ret = (client.getName() != null ? client.getName() : "")   
         		  + (client.getName() != null && client.getAddress() != null ? ", " : "")
                    + (client.getAddress() != null ? client.getAddress() : "");
        
        	if (!"RU".equals(lang)) {
        		ClientLocale locale = client.getLocale(lang);
        		if (locale != null) {
        			ret = (locale.getName() != null ? locale.getName() : "")   
        					+  (locale.getName() != null && locale.getAddress() != null ? ", " : "") 
        					+  (locale.getAddress() != null ? locale.getAddress() : "")
        					+ "(" + ret + ")";
        		} else {
        			ret = "Не определено для выбранного языка" + "(" + ret + ")";
        		}
        	}
        }	
		return  ret;
	}
	
	private String getBranchName(Client client, String lang) {
        String ret = "";
        
        if (client != null) {
        	if ("RU".equals(lang)) {
            	ret = (client.getName() != null ? client.getName() : "");
        	} else {
        		ClientLocale locale = client.getLocale("EN");
        		if (locale != null) {
        			ret = (locale.getName() != null ? locale.getName() : "");
        		} 
        	}
        }	
		return  ret;
	}
	
	private String getBranchAddress(Client client, String lang) {
        String ret = "";
        if (client != null) {
        	if ("RU".equals(lang)) {
            	ret = (client.getAddress() != null ? client.getAddress() : "");
        	} else {
        		ClientLocale locale = client.getLocale("EN");
        		if (locale != null) {
        			ret = (locale.getAddress() != null ? locale.getAddress() : "");
        		} 
        	}
        }	
		return  ret;
	}
	
	private String getBranchContact(Client client, String lang) {
        String ret = "";
        if (client != null) {
        	if ("RU".equals(lang)) {
        		ret = "телефон: " + (client.getPhone() != null ? client.getPhone() : "") + ", " 	
            	      + "факс: " + (client.getFax() != null ? client.getFax() : "") + ", "
            	      + "e-mail: " + (client.getEmail() != null ? client.getEmail() : "") ;
        	} else {
        		ret = "phone: " + (client.getPhone() != null ? client.getPhone() : "") + ", " 	
              	      + "fax: " + (client.getFax() != null ? client.getFax() : "") + ", "
              	      + "e-mail: " + (client.getEmail() != null ? client.getEmail() : "") ;
        	}
        }	
        return  ret;
	}
	
	
	private String getEmployeeName(Employee employee, String lang) {
        String ret = "";
        
        if (employee != null) {
        	ret = (employee.getJob() != null ? employee.getJob() : "")  + " " 
                  + (employee.getName() != null ? employee.getName() : "");
        
        	if (!"RU".equals(lang)) {
        		EmployeeLocale locale = employee.getLocale(lang);
        		if (locale != null) {
        			ret = (locale.getJob() != null ? locale.getJob() : "") 
        	  		  + " " + (locale.getName() != null ? locale.getName() : "")
        			  + "(" + ret + ")";
        		} else {
        			ret = "Не определено для выбранного языка" + "(" + ret + ")";
        		}
        	}
        }
		return  ret;
	}
	
	// ---------------------------------------------------------------------------------------
	//   Reload Declaration from template
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "rlddecl.do",  method = RequestMethod.GET)
	public void reloadDeclarationField(
			@RequestParam(value = "lang", required = true) String lang,
			HttpSession session, HttpServletResponse response, ModelMap model) {
			
			try {
				  FSCertificate cert = (FSCertificate)model.get("fscert");
				  
				  String template = fsCertService.getTemplate("declaration", lang);
				  cert.setConfirmation(template == null ? "Шаблон для выбранного языка не определен" : template);
				 
				  response.setContentType("text/html; charset=UTF-8");
				  response.setCharacterEncoding("UTF-8");
  			      response.getWriter().println(cert.getConfirmation());
				  response.flushBuffer();
				  
			} catch (Exception ex) {
					ex.printStackTrace();
					LOG.info("Ошибка: " + ex.getMessage());
					model.addAttribute("error", ex.getMessage());
			}
	}

	
	// ---------------------------------------------------------------------------------------
	//   Handling Goods List: Adding, Deleting
	// ---------------------------------------------------------------------------------------
	@RequestMapping(value = "fsgoods.do",  method = RequestMethod.GET)
	public void getgoods(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", defaultValue = "datecert", required = false) String sidx,
			@RequestParam(value = "sord", required = false) String sord,
			HttpSession session, HttpServletResponse response, ModelMap model) {
			
			try {
					  FSCertificate cert = (FSCertificate)model.get("fscert");
					  
					  response.setContentType("text/html; charset=UTF-8");
					  response.setCharacterEncoding("UTF-8");
	  			      response.getWriter().println(goodstoxml(cert, page, rows));
					  response.flushBuffer();
					  
			} catch (Exception ex) {
						LOG.info("Ошибка: " + ex.getMessage());
						model.addAttribute("error", ex.getMessage());
			}
	}
	
	private String goodstoxml(FSCertificate cert, int page, int rows ) {
		String xml;
		xml = "<?xml version='1.0' encoding='utf-8'?>";
		xml +=  "<rows>";
		
		if (cert != null && cert.getProducts()!=null) {
			int itemscount = cert.getProducts().size();
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
				FSProduct row = cert.getProducts().get(index);
				xml += "<row id='"+ row.getId() + "'>";            
				xml += "<cell>"+(row.getNumerator() == null ? "" : row.getNumerator())+"</cell>";
				xml += "<cell><![CDATA["+(row.getTovar() == null ? "" : row.getTovar())+"]]></cell>";
				xml += "</row>";
			}
		}
		xml +=  "</rows>";
		return xml;
	}
	
    //=====================================================================
	@RequestMapping(value = "fsgoodsupdate.do",  method = RequestMethod.POST)
	public void updatePOSTgoods(
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "numerator", required = false) String numerator,
			@RequestParam(value = "tovar", required = false) String tovar,
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		
		    LOG.info("POST: " + "id: " + id + " numerator: " + numerator + " tovar: " + tovar);
		    
			try {
				  FSCertificate cert = (FSCertificate)model.get("fscert");
				  for (FSProduct product : cert.getProducts()) {
					    if (product.getId() == Long.valueOf(id).longValue()) {
						   if (numerator == null && tovar==null) {
							   cert.getProducts().remove(product);   
						   } else {
							  if (numerator != null) { 
								  product.setNumerator(Long.valueOf(numerator).longValue());
							  }
						      product.setTovar(tovar);
						   }   
						   break;
					    }
				   }
			} catch (Exception ex) {
						LOG.info("Ошибка: " + ex.getMessage());
						model.addAttribute("error", ex.getMessage());
			}
	}

	private long getlastNumerator(List<FSProduct> products) {
        long ret=0;
        for (FSProduct product : products) {
        	if (product.getNumerator() != null && ret < product.getNumerator()) {
        		ret = product.getNumerator();
        	}
        }
		return ret;
	}

	private long getlastProductId(List<FSProduct> products) {
        long ret=0;
        for (FSProduct product : products) {
        	if (product.getId() != null && ret < product.getId()) {
        		ret = product.getId();
        	}
        }
		return ret;
	}
    //=====================================================================
	@RequestMapping(value = "fsaddproduct.do",  method = RequestMethod.GET)
	public void addgoods(
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
			try {
				  FSCertificate cert = (FSCertificate)model.get("fscert");
 				  FSProduct product  = new FSProduct();
				  product.setTovar("");
				  product.setId_fscert(cert.getId());
				  product.setId(getlastProductId(cert.getProducts())+1);
				  product.setNumerator(getlastNumerator(cert.getProducts())+1);
				  cert.getProducts().add(product);
			} catch (Exception ex) {
				  LOG.info("Ошибка: " + ex.getMessage());
				  model.addAttribute("error", ex.getMessage());
			}
	}
    //===================================================================== 
	@RequestMapping(value = "fsdelproduct.do",  method = RequestMethod.GET)
	public void delgoods(
			@RequestParam(value = "id", required = false) String id,
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
			try {
				  if (id != null) {
					 int iid = Integer.valueOf(id).intValue(); 
				     FSCertificate cert = (FSCertificate)model.get("fscert");
				     
				     for(int index = 0; index < cert.getProducts().size(); index++) {
				    	 FSProduct product = cert.getProducts().get(index);
				    	 
				    	 if (product.getId() == iid) {
				    		 cert.getProducts().remove(index);
				    		 for(int i = index; i < cert.getProducts().size(); i++) {
				    			 product = cert.getProducts().get(i);
				    			 product.setNumerator(product.getNumerator() - 1);
				    		 }
				    		 break;
				    	 }
				     }
				  }
			} catch (Exception ex) {
				  LOG.info("Ошибка: " + ex.getMessage());
				  model.addAttribute("error", ex.getMessage());
			}
	}
	
	
	//=====================================================================
	@RequestMapping(value = "fsinsertproduct.do",  method = RequestMethod.GET)
	public void insertgoods(
			    @RequestParam(value = "id", required = false) String id,			
				HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
				try {
					  FSCertificate cert = (FSCertificate)model.get("fscert");
					  int iid = Integer.valueOf(id).intValue(); 
					  List<FSProduct> nproducts = new ArrayList<FSProduct>();
					  boolean beforeid = true;
					  
  				      for(int index = 0; index < cert.getProducts().size(); index++) {
					    	 FSProduct product = cert.getProducts().get(index);
					    	 
					    	 if (beforeid) {
					    		 if (product.getId() == iid) {
					    			 beforeid = false;
					 				 FSProduct nproduct  = new FSProduct();
									 nproduct.setTovar("");
									 nproduct.setId_fscert(cert.getId());
					    		     nproduct.setNumerator(product.getNumerator());
					    		     nproduct.setId(product.getId());
					    		     nproducts.add(nproduct);
						    		 product.setNumerator(product.getNumerator() + 1);
						    		 product.setId(product.getId() + 1);
						    		 nproducts.add(product);
					    		 } else {
					    			 nproducts.add(product);
					    		 }
					    	 } else {
					    		 product.setNumerator(product.getNumerator() + 1);
					    		 product.setId(product.getId() + 1);
					    		 nproducts.add(product);
					    	 }
  				      }
  				    cert.setProducts(nproducts);
				} catch (Exception ex) {
					  LOG.info("Ошибка: " + ex.getMessage());
					  model.addAttribute("error", ex.getMessage());
				}
	}

	
	    //=====================================================================
		@RequestMapping(value = "fsaddproducts.do",  method = RequestMethod.POST)
		public void addlistproducts(
				@RequestParam(value = "productlist", required = false) String ids,
				HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
				try {
					  FSCertificate cert = (FSCertificate)model.get("fscert");
	 				  
	 				  long lastid = getlastProductId(cert.getProducts());
	 				  long lastNumerator = getlastNumerator(cert.getProducts());
	 				  String products[] = ids.split("\\r\\n|\\n|\\r");
	 				  
	 				  for (String item : products) {
	 					 FSProduct product  = new FSProduct();
	 				     product.setTovar(item);
					     product.setId_fscert(cert.getId());
					     product.setId(++lastid);
					     product.setNumerator(++lastNumerator);
					     cert.getProducts().add(product);
	 				  }
				} catch (Exception ex) {
					  LOG.info("Ошибка: " + ex.getMessage());
					  model.addAttribute("error", ex.getMessage());
				}
		}
		
	    //===================================================================== 
		@RequestMapping(value = "fsdelallproducts.do",  method = RequestMethod.GET)
		public void delallproducts(
					HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
					try {
						FSCertificate cert = (FSCertificate)model.get("fscert");
						cert.getProducts().clear();
					} catch (Exception ex) {
						  LOG.info("Ошибка: " + ex.getMessage());
						  model.addAttribute("error", ex.getMessage());
					}
			}
			
	// ---------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------	
	//   Handling Blank List: Adding, Deleting
	// ---------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------	
	@RequestMapping(value = "fsblanks.do",  method = RequestMethod.GET)
	public void getblankss(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", defaultValue = "datecert", required = false) String sidx,
			@RequestParam(value = "sord", required = false) String sord,
			HttpSession session, HttpServletResponse response, ModelMap model) {
			
			try {
					  FSCertificate cert = (FSCertificate)model.get("fscert");
					  
					  response.setContentType("text/html; charset=UTF-8");
					  response.setCharacterEncoding("UTF-8");
	  			      response.getWriter().println(blankstoxml(cert, page, rows));
					  response.flushBuffer();
					  
			} catch (Exception ex) {
						LOG.info("Ошибка: " + ex.getMessage());
						model.addAttribute("error", ex.getMessage());
			}
	}
	
	private String blankstoxml(FSCertificate cert, int page, int rows ) {
		String xml;
		xml = "<?xml version='1.0' encoding='utf-8'?>";
		xml +=  "<rows>";
		
		if (cert != null && cert.getBlanks()!=null) {
			int itemscount = cert.getBlanks().size();
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
				FSBlank row = cert.getBlanks().get(index);
				xml += "<row id='"+ row.getPage() + "'>";            
				xml += "<cell>"+(row.getPage() == null ? "" : row.getPage())+"</cell>";
				xml += "<cell><![CDATA["+(row.getBlanknumber() == null ? "" : row.getBlanknumber())+"]]></cell>";
				xml += "</row>";
			}
		}
		xml +=  "</rows>";
		return xml;
	}
	
    //=====================================================================
	@RequestMapping(value = "fsblankupdate.do",  method = RequestMethod.POST)
	public void updatePOSTBlank(
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "blanknumber", required = false) String blanknumber,
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		
		    LOG.info("POST: " + "id: " + id + " page: " + page + " blank: " + blanknumber);
		    
			try {
				  FSCertificate cert = (FSCertificate)model.get("fscert");
				  for (FSBlank blank : cert.getBlanks()) {
					    if (blank.getPage() == Long.valueOf(id).longValue()) {
						   if (page == null && blanknumber==null) {
							   cert.getBlanks().remove(blank);   
						   } else {
							  if (page != null) { 
								  blank.setPage(Integer.valueOf(page).intValue());
							  }
							  blank.setBlanknumber(blanknumber);
						   }   
						   break;
					    }
				   }
			} catch (Exception ex) {
				   LOG.info("Ошибка: " + ex.getMessage());
				   model.addAttribute("error", ex.getMessage());
			}
	}

	private int getlastPageNumber(List<FSBlank> blanks) {
        int ret=0;
        for (FSBlank blank : blanks) {
        	if (blank.getPage() != null && ret < blank.getPage()) {
        		ret = blank.getPage();
        	}
        }
		return ret;
	}

    //=====================================================================
	@RequestMapping(value = "fsaddblank.do",  method = RequestMethod.GET)
	public void addblank(
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
			try {
				  FSCertificate cert = (FSCertificate)model.get("fscert");
 				  FSBlank blank  = new FSBlank();
				  blank.setBlanknumber("");
				  blank.setId_fscert(cert.getId());
				  blank.setPage(getlastPageNumber(cert.getBlanks())+1);
				  cert.getBlanks().add(blank);
			} catch (Exception ex) {
				  LOG.info("Ошибка: " + ex.getMessage());
				  model.addAttribute("error", ex.getMessage());
			}
	}
    //===================================================================== 
	@RequestMapping(value = "fsdelblank.do",  method = RequestMethod.GET)
	public void delblank(
			@RequestParam(value = "id", required = false) String id,
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
			try {
				  if (id != null) {
					 int iid = Integer.valueOf(id).intValue(); 
				     FSCertificate cert = (FSCertificate)model.get("fscert");
				     
				     for(int index = 0; index < cert.getBlanks().size(); index++) {
				    	 FSBlank blank = cert.getBlanks().get(index);
				    	 
				    	 if (blank.getPage() == iid) {
				    		 cert.getBlanks().remove(index);
				    		 for(int i = index; i < cert.getBlanks().size(); i++) {
				    			 blank = cert.getBlanks().get(i);
				    			 blank.setPage(blank.getPage() - 1);
				    		 }
				    		 break;
				    	 }
				     }
				  }
			} catch (Exception ex) {
				  LOG.info("Ошибка: " + ex.getMessage());
				  model.addAttribute("error", ex.getMessage());
			}
	}
	
	
	//=====================================================================
	@RequestMapping(value = "fsinsertblank.do",  method = RequestMethod.GET)
	public void insert(
			    @RequestParam(value = "id", required = false) String id,			
				HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
				try {
					  FSCertificate cert = (FSCertificate)model.get("fscert");
					  int iid = Integer.valueOf(id).intValue(); 
					  List<FSBlank> nblanks = new ArrayList<FSBlank>();
					  boolean beforeid = true;
					  
  				      for(int index = 0; index < cert.getBlanks().size(); index++) {
					    	 FSBlank blank = cert.getBlanks().get(index);
					    	 
					    	 if (beforeid) {
					    		 if (blank.getPage() == iid) {
					    			 beforeid = false;
					 				 FSBlank nblank  = new FSBlank();
									 nblank.setBlanknumber("");
									 nblank.setId_fscert(cert.getId());
					    		     nblank.setPage(blank.getPage());
					    		     nblank.setId(blank.getId());
					    		     nblanks.add(nblank);
						    		 blank.setPage(blank.getPage() + 1);
						    		 blank.setId(blank.getId() + 1);
						    		 nblanks.add(blank);
					    		 } else {
					    			 nblanks.add(blank);
					    		 }
					    	 } else {
					    		 blank.setPage(blank.getPage() + 1);
					    		 blank.setId(blank.getId() + 1);
					    		 nblanks.add(blank);
					    	 }
  				      }
  				    cert.setBlanks(nblanks);
				} catch (Exception ex) {
					  LOG.info("Ошибка: " + ex.getMessage());
					  model.addAttribute("error", ex.getMessage());
				}
	}

    //=====================================================================
	@RequestMapping(value = "fsaddblanks.do",  method = RequestMethod.POST)
	public void addlistblanks(
			@RequestParam(value = "blanklist", required = false) String ids,
			HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
			try {
				  FSCertificate cert = (FSCertificate)model.get("fscert");
 				  
 				  int lastpage = getlastPageNumber(cert.getBlanks());
 				  String items[] = ids.split("\\r\\n|\\n|\\r");
 				  
 				  for (String item : items) {
 					 FSBlank blank  = new FSBlank();
 					 blank.setBlanknumber(item);
 					 blank.setId_fscert(cert.getId());
				     blank.setPage(++lastpage);
				     cert.getBlanks().add(blank);
 				  }
			} catch (Exception ex) {
				  LOG.info("Ошибка: " + ex.getMessage());
				  model.addAttribute("error", ex.getMessage());
			}
	}
	
    //===================================================================== 
	@RequestMapping(value = "fsdelallblanks.do",  method = RequestMethod.GET)
	public void delallblankss(
				HttpSession session, HttpServletResponse response, HttpServletRequest request, ModelMap model) {
				try {
					FSCertificate cert = (FSCertificate)model.get("fscert");
					cert.getBlanks().clear();
				} catch (Exception ex) {
					  LOG.info("Ошибка: " + ex.getMessage());
					  model.addAttribute("error", ex.getMessage());
				}
		}
			
	// =======================================================================
	@RequestMapping(value = "rldlang.do",  method = RequestMethod.GET)
	public void reload(
			@RequestParam(value = "lang", required = true) String lang,
			HttpSession session, HttpServletResponse response, ModelMap model) {
			
			try {
				  FSCertificate cert = (FSCertificate)model.get("fscert");
				  String json = "{\"exporter\":\"" + getClientName(cert.getExporter(), lang) + "\"," 
				                + "\"producer\":\"" + getClientName(cert.getProducer(), lang) + "\","
				                + "\"expert\":\"" + getEmployeeName(cert.getExpert(), lang) + "\","
				                + "\"signer\":\"" + getEmployeeName(cert.getSigner(), lang) + "\","
						  		+ "\"branchname\":\"" + getBranchName(cert.getBranch(), lang) + "\","
						  		+ "\"branchaddress\":\"" + getBranchAddress(cert.getBranch(), lang)+ "\","
						  		+ "\"branchcontact\":\"" + getBranchContact(cert.getBranch(), lang)
				                + "\"}";
				  
				  response.setContentType("text/html; charset=UTF-8");
				  response.setCharacterEncoding("UTF-8");
  			      response.getWriter().println(json);
				  response.flushBuffer();
				  
			} catch (Exception ex) {
					ex.printStackTrace();
					LOG.info("Ошибка: " + ex.getMessage());
					model.addAttribute("error", ex.getMessage());
			}
	}
	
	// ========================================================================
	@RequestMapping(value = "fsprint.do",  method = RequestMethod.GET)
	public String printFSCertificate(
			@RequestParam(value = "certid", required = false) Integer id,
			@RequestParam(value = "type", defaultValue="org", required = true) String type,
			HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model)  {
		
			String relativeWebPath = "/resources";
			String  absoluteDiskPath= request.getSession().getServletContext().getRealPath(relativeWebPath);
			LOG.debug("Absolute path: " + absoluteDiskPath);
			
			FSCertificate cert = null;
			try {
				if (id == null) {
					cert = (FSCertificate) model.get("fscert");
				} else {
				    cert = fsCertService.getFSCertificateById(id.intValue());
				}
				if (cert != null) {
				   cert.setListscount(makepdffile(absoluteDiskPath, cert, type));	
				   makepdffile(absoluteDiskPath, cert, type);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "redirect:" + "/resources/out/" + cert.getCertnumber() + ".pdf";
	}
		
	// ========================================================================
	@RequestMapping(value = "fsprint.do",  method = RequestMethod.POST)
	public void printFSCertificate( 
			FSCertificate fscert, ModelMap model)  {
	    
	    FSCertificate storedCert = (FSCertificate) model.get("fscert");
	    
	    if (storedCert == null) {
		    model.addAttribute("error", "Информация о редактируемом сертификате потеряна. Перезегрузите сертификат.");		       	
	    } else {
	    	fscert.setId(storedCert.getId());
	    	//fscert.setCertnumber(storedCert.getCertnumber());
	    	fscert.setBranch(storedCert.getBranch());
	    	fscert.setExporter(storedCert.getExporter());
	    	fscert.setProducer(storedCert.getProducer());
	    	fscert.setExpert(storedCert.getExpert());
	    	fscert.setSigner(storedCert.getSigner());
	    	fscert.setBlanks(storedCert.getBlanks());
	    	fscert.setProducts(storedCert.getProducts());
	    	model.addAttribute("fscert", fscert);
	    	storedCert = null;
	    }
	}
	
	// ---------------------------------------------------------------------------------------
	//
	// ---------------------------------------------------------------------------------------
	private int makepdffile(String absoluteDiskPath, FSCertificate cert, String type) {
		FSPDFBuilder builder = new FSPDFBuilder();
		String fileout = absoluteDiskPath + "/out/" + cert.getCertnumber() + ".pdf";
		String fileconf = absoluteDiskPath + "/config/pages.xml";
		String fontpath = absoluteDiskPath + "/fonts/";
		
		CountryConverter.setCountrymap(certService.getCountriesList(cert.getLanguage()));
		String country = CountryConverter.getCountryNameByCode(cert.getCodecountrytarget());
		int pages = 1;  
		
		try {
		   FSCertificate parent=null;
		   if (cert.getParentnumber() != null && !cert.getParentnumber().isEmpty()) {
				parent =  fsCertService.getFSCertificateByNumber(cert.getParentnumber());
		   }
		   if (type != null && type.equals("org")) {	
			   pages = builder.genereatePdf(fileout, cert, fileconf, fontpath, country, parent, fsCertService, true); 
		   } else {
			   pages = builder.genereatePdf(fileout, cert, fileconf, fontpath, country, parent, fsCertService, false);
		   }
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return pages;
	}

	
	// ---------------------------------------------------------------------------------------
	// Fill in lists 
	// ---------------------------------------------------------------------------------------
	@ModelAttribute("countries")
	public Map<String, String> populateCountryRuList() {
		return certService.getCountriesList("RU");
	}

	@ModelAttribute("countriesen")
	public Map<String, String> populateCountryEnList() {
		return certService.getCountriesList("EN");
	}
	
	@ModelAttribute("departments")
	public Map<String, String> populateDepartmentssList() {
		return certService.getBranchesList();
	}
	
	@ModelAttribute("languages")
	public Map<String, String> populateLanguageList() {
		return certService.getLanguageList();
	}

}
