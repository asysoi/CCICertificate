package cci.repository.fscert;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import cci.model.Client;
import cci.model.ClientLocale;
import cci.model.Department;
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
import cci.model.owncert.OwnCertificate;
import cci.model.owncert.Product;
import cci.repository.SQLBuilder;
import cci.repository.owncert.OwnCertificateMapper;
import cci.repository.staff.EmployeeRowMapper;
import cci.service.SQLQueryUnit;
import cci.web.controller.fscert.FSFilter;
import cci.web.controller.fscert.ViewFSCertificate;

@Repository
public class JDBCFSCertificateDAO implements FSCertificateDAO {

	private static final Logger LOG = Logger.getLogger(JDBCFSCertificateDAO.class);
	
	private NamedParameterJdbcTemplate template;
	private DataSource ds; 

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.template = new NamedParameterJdbcTemplate(dataSource);
		ds = dataSource;
	}

	
	// ------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------		
	//
	//   WEB  WEB  WEB WEB WEB WEB  
	//
	// ------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------
	//  This method returns count of pages in certificate list 
	// ------------------------------------------------------------------------------
	public int getViewPageCount(SQLBuilder builder) {
        SQLQueryUnit qunit = builder.getSQLUnitWhereClause();     
		String sql = "SELECT count(*) FROM FS_CERT "
				+ qunit.getClause();
	
		Integer count = this.template.queryForObject(sql, qunit.getParams(), Integer.class);
		
		LOG.info(sql);
		return count.intValue();
	}


	// ------------------------------------------------------------------------------
	//  This method returns a current page of the certificate's list
	// ------------------------------------------------------------------------------
	public List<ViewFSCertificate> findViewNextPage(String[] dbfields, int page, int pagesize, int pagecount,
			String orderby, String order, SQLBuilder builder) {
		String sql; 
		String flist = "id";
		
		for (String field : dbfields) {
		    flist += ", " + field;  	
		}
		flist = "*"; // all fields are needed 
		
        SQLQueryUnit filter = builder.getSQLUnitWhereClause();
        Map<String, Object> params = filter.getParams();
        LOG.info("SQLQueryUnit : " + filter);
        
        
        if (pagesize < pagecount) {
        	sql = "select " + flist 
				+ " from fscertview where id in "
				+ " (select  a.id "
				+ " from (SELECT id FROM (select id from fscertview "
				+  filter.getClause()
				+ " ORDER by " +  orderby + " " + order + ", id " + order  
				+ ") where rownum <= :highposition "    
				+ ") a left join (SELECT id FROM (select id from fscertview "
				+  filter.getClause()
				+ " ORDER by " +  orderby + " " + order + ", id " + order
				+ ") where rownum <= :lowposition "   
				+ ") b on a.id = b.id where b.id is null)" 
				+ " ORDER by " +  orderby + " " + order + ", id " + order;
       		params.put("highposition", Integer.valueOf(page * pagesize));
    		params.put("lowposition", Integer.valueOf((page - 1) * pagesize));
        } else {
        	sql = "select " + flist 
    				+ " from fscertview "
    				+  filter.getClause()
    				+ " ORDER by " +  orderby + " " + order + ", id " + order;  
        }
		
		LOG.info("Next page : " + sql);
		
		if (pagecount != 0) {
		    return this.template.query(sql,	params, 
				new BeanPropertyRowMapper<ViewFSCertificate>(ViewFSCertificate.class));
		} else {
			return null;
		}
	
	}

	// ------------------------------------------------------------------------------
	//  This method returns list of FS certificates for export to Excel file
	// ------------------------------------------------------------------------------
	public List<ViewFSCertificate> getCertificates(String[] dbfields, String orderby, String order,
			SQLBuilder builder) {
		
		String flist = "id";
		
		for (String field : dbfields) {
		    flist += ", " + field;  	
		} 
		
		flist = "*";
		
		SQLQueryUnit filter = builder.getSQLUnitWhereClause();
    	Map<String, Object> params = filter.getParams();

		String sql = " SELECT " + flist + " FROM FSCERTVIEWEXPORT " 
				// + " WHERE id in (SELECT id from fs_cert " + filter.getClause() + ") ORDER BY " +  orderby + " " + order;
				+ filter.getClause() + " ORDER BY " +  orderby + " " + order;

		LOG.info("Get EXPORT certificates: " + sql);
		
		return this.template.query(sql,	params, 
				new BeanPropertyRowMapper<ViewFSCertificate>(ViewFSCertificate.class));
	}


	// ---------------------------------------------------------------
	// поиск единственного сертификата по id -> PS
	// ---------------------------------------------------------------
	public FSCertificate findFSCertificateByID(int id) throws Exception {
		String sql = "select * from fs_cert WHERE id = ?";
		
		FSCertificate cert = template.getJdbcOperations()
				.queryForObject(
						sql,
						new Object[] { id },
						new FSCertificateRowMapper());
		
		loadAllLinkedObject(cert);
        
		return cert;

	}
	

	// ---------------------------------------------------------------
	// Загрузка шаблонов из базы данных
	// ---------------------------------------------------------------
	public FSTranslate loadTemplates() {
        FSTranslate fstranslate = new FSTranslate();
		String sql = 
			"select key, locale, fs_template_LOCALE.text from fs_template_LOCALE left join FS_TEMPLATE on idkey=id";
		
		fstranslate = template.query(sql, new  FSResultSetExtractor());
		
		return fstranslate;
	}

	
	// ---------------------------------------------------------------
	// Получить сотрудника палаты по имени пользователя
	// ---------------------------------------------------------------
	public Employee getEmployeeByUserName(String username) {
		String sql = "select * from EMPLOYEE_VIEW WHERE id = (SELECT IDEMPLOYEE from users where username=?)";

		Employee employee = template.getJdbcOperations()
				.queryForObject(
						sql,
						new Object[] { username },
						new EmployeeRowMapper<Employee>());
		
		if  (employee != null) {
			sql = "select * from CCI_EMPLOYEE_LOCALE WHERE IDEMPLOYEE = ? ORDER BY LOCALE";
			List<EmployeeLocale> locales = template.getJdbcOperations().query(sql, new Object[] { employee.getId() },
					new BeanPropertyRowMapper<EmployeeLocale>(EmployeeLocale.class));
			if (locales != null && locales.size() > 0) {
			   int lid = 0;	
			   for (EmployeeLocale locale : locales) {
				   locale.setId(lid++);
			   }
			   employee.setLocales(locales);
			}
		}
		return employee;
	}

	
	// ------------------------------------------------------------------------------------------------------
	//
	//  Methods are applicable for Free Sale Certificate database manipulation GRUD service 
	//
	// ------------------------------------------------------------------------------------------------------

	//--------------------------------------------------------------------
	//--------------------------------------------------------------------	
	//            ADD new FS Certificate for WEB or REST service request 
	//--------------------------------------------------------------------
	//--------------------------------------------------------------------
	public FSCertificate saveFSCertificate(FSCertificate cert) throws Exception {
			long id = 0;
	 		if (cert.getCertnumber().isEmpty()) {
	 			throw new RuntimeException("Добвляемый в базу сертификат должен иметь номер.");
	 		}
			
			if (cert.getBranch()!= null && cert.getBranch().getId() == 0 ) cert.getBranch().setId(findOrCreateBranchID(cert.getBranch()));
			if (cert.getExporter()!=null && cert.getExporter().getId() == 0 ) cert.getExporter().setId(findOrCreateClientID(cert.getExporter()));
			if (cert.getProducer()!=null && cert.getProducer().getId() == 0 ) cert.getProducer().setId(findOrCreateClientID(cert.getProducer()));
			if (cert.getExpert()!=null && cert.getExpert().getId() == 0 ) cert.getExpert().setId(findOrCreateEmployeeID(cert.getExpert()));
			if (cert.getSigner()!=null && cert.getSigner().getId() == 0 ) cert.getSigner().setId(findOrCreateEmployeeID(cert.getSigner()));
			if (cert.getDepartment()!=null && cert.getDepartment().getId() == 0 ) cert.getDepartment().setId(findOrCreateDepartmentID(cert.getDepartment()));
			
			LOG.info("Save FS Certificate: " + cert.getCertnumber());
			LOG.info("Save FS Certificate: " + cert.getOtd_id());
			
			String sql = "insert into fs_cert(certnumber, parentnumber, dateissue, dateexpiry, confirmation, declaration, codecountrytarget, "
					    + " datecert, listscount, language, otd_id, id_branch, id_exporter, id_producer, id_expert, id_signer, iddepartment) "
					    + " values (:certnumber, :parentnumber, "
					    + " TO_DATE(:dateissue,'DD.MM.YY'), "
					    + " TO_DATE(:dateexpiry,'DD.MM.YY'), "
					    + " :confirmation, :declaration, :codecountrytarget,  TO_DATE(:datecert,'DD.MM.YY'), :listscount, :language, :otd_id "
					    + ((cert.getBranch() != null) ?  ", :branch.id " : ", :branch")
					    + ((cert.getExporter() != null) ?  ", :exporter.id " : ", :exporter ")
					    + ((cert.getProducer() != null) ?  ", :producer.id " : ", :producer ")
					    + ((cert.getExpert() != null) ?  ", :expert.id " : ", :expert ")
					    + ((cert.getSigner() != null) ?  ", :signer.id " : ", :signer ")
					    + ((cert.getDepartment() != null) ?  ", :department.id " : ", :department ")
					    + ")";
			LOG.info(sql);
			
			SqlParameterSource parameters = new BeanPropertySqlParameterSource(cert);
			GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
			
			int row = template.update(sql, parameters, keyHolder,
					new String[] { "id" });
			id = keyHolder.getKey().intValue();

			if (row > 0) {
				sql = "insert into fs_product(id_fscert, numerator, tovar) values ("
						+ id + ", :numerator, :tovar)";
				if (cert.getProducts() != null && cert.getProducts().size() > 0) {
					SqlParameterSource[] batch = SqlParameterSourceUtils
							.createBatch(cert.getProducts().toArray());
					int[] updateCounts = template.batchUpdate(sql, batch);
				}
				
				sql = "insert into fs_blank(id_fscert, page, blanknumber) values ("
						+ id + ", :page, :blanknumber)";
				
				if (cert.getBlanks() != null && cert.getBlanks().size() > 0) {
					SqlParameterSource[] batch = SqlParameterSourceUtils
							.createBatch(cert.getBlanks().toArray());
					int[] updateCounts = template.batchUpdate(sql, batch);
				}

				cert.setId(id);
				insertValueIntoDenormTable(cert);
			}
			return cert;
	}

		
	// -------------------------------------------
	// Get id of branch
	// -------------------------------------------
	private long findOrCreateBranchID(Branch branch) throws Exception {
			    String where = createClientWhereClause(branch);
			    
				if (where.isEmpty()) {
					throw new RuntimeException("Отсутствует информация об отделении БелТПП");
				}
					
				long id = 0;
				String sql = "SELECT id FROM fs_branch " + where;
				
				try {
					SqlParameterSource parameters = new BeanPropertySqlParameterSource(branch);
					Branch client = this.template.queryForObject(sql, 
									parameters,
									new BeanPropertyRowMapper<Branch>(Branch.class));
					
					id = client.getId();
					
				} catch (Exception ex) {
					
	                LOG.info(ex.getMessage());
					if (id == 0) {
					  sql = "insert into fs_branch(name, codecountry, cindex, city, street, office, building," + 
							  	"phone, cell, email, unp, okpo, account, bname, bindex, bcodecountry," + 
			                    "bcity, bstreet, boffice, bbuilding, bemail, bunp) " +  
								"values(:name, :codecountry, :cindex, :city, :street, :office, :building," + 
							  	":phone, :cell, :email, :unp, :okpo, :account, :bname, :bindex, :bcodecountry," + 
			                    ":bcity, :bstreet, :boffice, :bbuilding,:bemail, :bunp) ";
					  
					  SqlParameterSource parameters = new BeanPropertySqlParameterSource(branch);
					  GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
					  
					  int row = template.update(sql, parameters, keyHolder,
							new String[] { "id" });
					  id = keyHolder.getKey().longValue();
				    }
				}
				return id;
		}
		
		// ----------------------------------------------------------------------------------------------
		//  make where clause for client 
		//-----------------------------------------------------------------------------------------------
		private String createClientWhereClause(Client client) {
			String sqlwhere = "";
			
			if (client.getName() != null && !client.getName().isEmpty()) {
				if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
				sqlwhere += " name = :name ";   
			}    
			if (client.getUnp() != null && !client.getUnp().isEmpty()) {
				if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
				sqlwhere += " unp = :unp ";   
			}
			if (client.getCity() != null && !client.getCity().isEmpty()) {
				if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
				sqlwhere += " city = :city ";   
			}
			if (client.getStreet() != null && !client.getStreet().isEmpty()) {
				if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
				sqlwhere += " street = :street ";   
			}
			if (client.getCindex() != null && !client.getCindex().isEmpty()) {
				if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
				sqlwhere += " cindex = :cindex ";   
			}
			if (client.getOffice() != null && !client.getOffice().isEmpty()) {
				if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
				sqlwhere += " office = :office ";   
			}
			if (client.getBuilding() != null && !client.getBuilding().isEmpty()) {
				if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
				sqlwhere += " building = :building ";   
			}
			if (sqlwhere.length() != 0)  sqlwhere += " AND ROWNUM = 1 ";
				
			return sqlwhere;
		}

		// ------------------------------------------------------------------------------
		// Get id of client / create client /Exporter / Producer
		// ------------------------------------------------------------------------------
		private long findOrCreateClientID(Client client) throws Exception{
		    String where = createClientWhereClause(client);
		    
			if (where.isEmpty()) {
				throw new RuntimeException("Нет никакой информации о предприятии");
			}
				
			long id = 0;
			String sql = "SELECT id FROM cci_client " + where;
			
			try {
				SqlParameterSource parameters = 
						new BeanPropertySqlParameterSource(client);
				Client company = this.template.queryForObject(sql, 
								parameters,
								new BeanPropertyRowMapper<Client>(Client.class));
				
				id = company.getId();
				
			} catch (Exception ex) {
				
				LOG.info(ex.getMessage());;
				if (id == 0) {
				  sql = "insert into cci_client(id, name, codecountry, cindex, city, street, office, building," + 
						  	"phone, cell, email, unp, okpo, account, bname, bindex, bcodecountry," + 
		                    "bcity, bstreet, boffice, bbuilding, bemail, bunp) " +  
							"values(id_client_seq.nextval, :name, :codecountry, :cindex, :city, :street, :office, :building," + 
						  	":phone, :cell, :email, :unp, :okpo, :account, :bname, :bindex, :bcodecountry," + 
		                    ":bcity, :bstreet, :boffice, :bbuilding, :bemail, :bunp) ";
				  
				  SqlParameterSource parameters = new BeanPropertySqlParameterSource(client);
				  GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
				  
				  int row = template.update(sql, parameters, keyHolder,
						new String[] { "id" });
				  id = keyHolder.getKey().longValue();
			    }
			}
			return id;
	     }

		
		// ----------------------------------------------------------------------------------------------------
		//  Get id of employee / create employee if it doesn't exist
		// ----------------------------------------------------------------------------------------------------
		private long findOrCreateEmployeeID(Employee emp) throws Exception{
			String where = createEmployeeWhereClause(emp);
			
			if (where.isEmpty()) {
				throw new RuntimeException("Отсутствует информация об эксперте");
			}
			
			String sql = "SELECT id FROM cci_employee " + where ;
			long id = 0;
			
			try {
				SqlParameterSource parameters = 
						new BeanPropertySqlParameterSource(emp);
				
				Employee expert = this.template.queryForObject(sql, 
								parameters,
								new BeanPropertyRowMapper<Employee>(Employee.class));
				id = expert.getId();
				
				//id = this.template.getJdbcOperations().queryForObject(sql, 
				//		new Object[] {emp.getName(), emp.getJob()},
				//		Long.class);
			} catch (Exception ex) {
			   LOG.info(ex.getMessage());	
			   if (id == 0) {
				  sql = "insert into cci_employee(name, job) " +  
							"values( :name, :job) ";
				  
				  SqlParameterSource parameters = new BeanPropertySqlParameterSource(emp);
				  GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
				  
				  template.update(sql, parameters, keyHolder,
						new String[] { "id" });
				  id = keyHolder.getKey().longValue();
			    }
			}
			return id;
		}

		// ------------------------------------------------------------------------
		//  make where clause for employee 
		//-------------------------------------------------------------------------
		private String createEmployeeWhereClause(Employee emp) {
	        String sqlwhere = "";
			
			if (emp.getName() != null && !emp.getName().isEmpty()) {
				if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
				sqlwhere += " name = :name ";   
			}    
			if (emp.getJob() != null && !emp.getJob().isEmpty()) {
				if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
				sqlwhere += " job = :job ";   
			}
			if (sqlwhere.length() != 0)  sqlwhere += " AND ROWNUM = 1 ";

			return sqlwhere;
		}
		
		// ----------------------------------------------------------------------------------------------------
  	    //  Get id of department / create department if it doesn't exist
		// ----------------------------------------------------------------------------------------------------
		private long findOrCreateDepartmentID(Department dep) throws Exception{
					String where = createDepartmentWhereClause(dep);
					
					if (where.isEmpty()) {
						throw new RuntimeException("Отсутствует информация о подразделении");
					}
					
					String sql = "SELECT id FROM cci_department " + where ;
					long id = 0;
					
					try {
						SqlParameterSource parameters = 
								new BeanPropertySqlParameterSource(dep);
						
						Department department = this.template.queryForObject(sql, 
										parameters,
										new BeanPropertyRowMapper<Department>(Department.class));
						id = department.getId();
						
					} catch (Exception ex) {
					   LOG.info(ex.getMessage());	
					   if (id == 0) {
						  sql = "insert into cci_department(name, code, id_otd)" +   
                                 " select :name, :code, id from c_otd where code = :code_otd ";
						  
						  SqlParameterSource parameters = new BeanPropertySqlParameterSource(dep);
						  GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
						  
						  template.update(sql, parameters, keyHolder,
								new String[] { "id" });
						  id = keyHolder.getKey().longValue();
					    }
					}
					return id;
		}

		// ------------------------------------------------------------------------
		//  make where clause for department 
		//-------------------------------------------------------------------------
		private String createDepartmentWhereClause(Department dep) {
		        String sqlwhere = "";
					
				if (dep.getName() != null && !dep.getName().isEmpty()) {
						if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
						sqlwhere += " name = :name ";   
				}    
				if (dep.getCode() != null && !dep.getCode().isEmpty()) {
						if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
						sqlwhere += " code = :code ";   
				}
				if (dep.getId_otd() != null && dep.getId_otd() > 0) {
					if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
					sqlwhere += " code_otd = :code_otd ";   
			    }
				if (sqlwhere.length() != 0)  sqlwhere += " AND ROWNUM = 1 ";
				return sqlwhere;
		}
 
		//--------------------------------------------------------------------
		//--------------------------------------------------------------------
		//            GET FS Certificate WEB or REST service 
		//--------------------------------------------------------------------
		//--------------------------------------------------------------------
		public FSCertificate getFSCertificateByNumber(String number) throws Exception {
	        FSCertificate rcert;
			
			String sql = "select * from FS_CERT WHERE CERTNUMBER = ?";
			rcert = template.getJdbcOperations().queryForObject(
						sql,
						new Object[] { number},
						new FSCertificateRowMapper());
			
	        loadAllLinkedObject(rcert);
	        
			return rcert;
		}
		
		// ------------------------------------------------------------------
		//   Fill in FS certificate
		// ------------------------------------------------------------------
		private void loadAllLinkedObject(FSCertificate rcert) throws Exception {

		  if (rcert != null) {
			if (rcert.getBranch() != null) {
				Branch branch = template.getJdbcOperations().queryForObject("select * from fs_branch where id = ? ",
						new Object[] { rcert.getBranch().getId() }, new BeanPropertyRowMapper<Branch>(Branch.class));
				loadClientLocales(branch); 
				rcert.setBranch(branch);
			}

			if (rcert.getExporter() != null) {
				Exporter obj = template.getJdbcOperations().queryForObject("select * from cci_client where id = ? ",
						new Object[] { rcert.getExporter().getId() },
						new BeanPropertyRowMapper<Exporter>(Exporter.class));
				loadClientLocales(obj);
				rcert.setExporter(obj);
			}

			if (rcert.getProducer() != null) {
				Producer obj = template.getJdbcOperations().queryForObject("select * from cci_client where id = ? ",
						new Object[] { rcert.getProducer().getId() },
						new BeanPropertyRowMapper<Producer>(Producer.class));
				loadClientLocales(obj);
				rcert.setProducer(obj);
			}

			if (rcert.getExpert() != null) {
				Expert obj = template.getJdbcOperations().queryForObject("select * from employee_view where id = ? ",
						new Object[] { rcert.getExpert().getId() }, new ExpertRowMapper<Expert>());
				loadEmployeeLocales(obj);
				rcert.setExpert(obj);
			}

			if (rcert.getSigner() != null) {
				Signer obj = template.getJdbcOperations().queryForObject("select * from employee_view where id = ? ",
						new Object[] { rcert.getSigner().getId() }, new SignerRowMapper<Signer>());
				loadEmployeeLocales(obj);
				rcert.setSigner(obj);
			}
			
			if (rcert.getDepartment() != null) {
				Department obj = template.getJdbcOperations().queryForObject("select * from cci_department where id = ? ",
						new Object[] { rcert.getDepartment().getId() }, new BeanPropertyRowMapper<Department>(Department.class));
				rcert.setDepartment(obj);
			}

			if (rcert != null) {
				String sql = "select * from FS_PRODUCT WHERE ID_FSCERT = ?  ORDER BY id";
				rcert.setProducts(template.getJdbcOperations().query(sql, new Object[] { rcert.getId() },
						new BeanPropertyRowMapper<FSProduct>(FSProduct.class)));

				sql = "select * from FS_BLANK WHERE ID_FSCERT = ?  ORDER BY PAGE";
				rcert.setBlanks(template.getJdbcOperations().query(sql, new Object[] { rcert.getId() },
						new BeanPropertyRowMapper<FSBlank>(FSBlank.class)));
			}
			
		  }
		  
		  

		}

        // load employee's locales   
		private void loadEmployeeLocales(Employee employee) {
			if  (employee != null) {
				String sql = "select * from CCI_EMPLOYEE_LOCALE WHERE IDEMPLOYEE = ?";
				List<EmployeeLocale> locales = template.getJdbcOperations().query(sql, new Object[] { employee.getId() },
						new BeanPropertyRowMapper<EmployeeLocale>(EmployeeLocale.class));
				if (locales != null && locales.size() > 0) {
				   employee.setLocales(locales);
				}
			}
		}

        // load client's locales		
		private void loadClientLocales(Client client) {
			if  (client != null) {
				String sql = "";
				if (client instanceof Branch) {
					sql = "select * from FS_BRANCH_LOCALE WHERE IDBRANCH = ? ";
				} else {
				    sql = "select * from CCI_CLIENT_LOCALE WHERE IDCLIENT = ? ";
				}
				
				List<ClientLocale> locales = template.getJdbcOperations().query(sql, new Object[] { client.getId() },
						new BeanPropertyRowMapper<ClientLocale>(ClientLocale.class));
				
				if (locales != null && locales.size() > 0) {
				   client.setLocales(locales);
				}
			}
		}


		//--------------------------------------------------------------------
		//--------------------------------------------------------------------	
		//            UPDATE FS Certificate WEB or REST service 
		//--------------------------------------------------------------------
		//--------------------------------------------------------------------
		public FSCertificate updateFSCertificate(FSCertificate cert) throws Exception {
			
	 		if (cert.getId() == 0) cert.setId(getFSCertificateIdByNUmber(cert.getCertnumber()));
			if (cert.getBranch()!= null && cert.getBranch().getId() == 0 ) cert.getBranch().setId(findOrCreateBranchID(cert.getBranch()));
			if (cert.getExporter()!=null && cert.getExporter().getId() == 0 ) cert.getExporter().setId(findOrCreateClientID(cert.getExporter()));
			if (cert.getProducer()!=null && cert.getProducer().getId() == 0 ) cert.getProducer().setId(findOrCreateClientID(cert.getProducer()));
			if (cert.getExpert()!=null && cert.getExpert().getId() == 0 ) cert.getExpert().setId(findOrCreateEmployeeID(cert.getExpert()));
			if (cert.getSigner()!=null && cert.getSigner().getId() == 0 ) cert.getSigner().setId(findOrCreateEmployeeID(cert.getSigner()));
			
			LOG.info(">>>>>>>>>>> Update FS Certificate: " + cert.getCertnumber());
			
			String sql = "UPDATE fs_cert SET certnumber=:certnumber, parentnumber = :parentnumber, dateissue = TO_DATE(:dateissue,'DD.MM.YY'), "
					    + " dateexpiry = TO_DATE(:dateexpiry,'DD.MM.YY'), confirmation = :confirmation, " 
					    + " declaration = :declaration, codecountrytarget=:codecountrytarget, datecert=TO_DATE(:datecert,'DD.MM.YY'), "
					    + " listscount = :listscount, language=:language, locked=:locked, version=version+1,  "
					    + " id_branch = " + ((cert.getBranch() != null) ?  ":branch.id, " : ":branch,")  
					    + " id_exporter = " + ((cert.getExporter() != null) ?  ":exporter.id, " : ":exporter, ") 
					    + " id_producer = " + ((cert.getProducer() != null) ?  ":producer.id, " : ":producer, ")
					    + " id_expert =  " + ((cert.getExpert() != null) ?  ":expert.id, " : ":expert, ")
					    + " id_signer = " + ((cert.getSigner() != null) ?  ":signer.id, " : ":signer, ")
					    + " iddepartment = " + ((cert.getDepartment() != null) ?  ":department.id " : " :department ")
					    + " WHERE id = :id and locked = 0"; 

			LOG.info(">>>>>>>>>>> Update FS Certificate: " + sql);
			
			SqlParameterSource parameters = new BeanPropertySqlParameterSource(cert);
			int row = template.update(sql, parameters);

			if (row > 0) {
				
				template.getJdbcOperations().update(
						"delete from FS_PRODUCT where id_fscert = ?",
						cert.getId());
				
				sql = "insert into fs_product(id_fscert, numerator, tovar) values ("
						+ cert.getId() + ", :numerator, :tovar)";
				if (cert.getProducts() != null && cert.getProducts().size() > 0) {
					SqlParameterSource[] batch = SqlParameterSourceUtils
							.createBatch(cert.getProducts().toArray());
					int[] updateCounts = template.batchUpdate(sql, batch);
					LOG.info("Added products : " +  updateCounts.toString());
				}
				
				template.getJdbcOperations().update(
						"delete from FS_BLANK where id_fscert = ?",
						cert.getId());
				
				sql = "insert into fs_blank(id_fscert, page, blanknumber) values ("
						+ cert.getId() + ", :page, :blanknumber)";
				
				if (cert.getBlanks() != null && cert.getBlanks().size() > 0) {
					SqlParameterSource[] batch = SqlParameterSourceUtils
							.createBatch(cert.getBlanks().toArray());
					int[] updateCounts = template.batchUpdate(sql, batch);
					LOG.info("Added blank : " +  updateCounts.toString());
				}
				
				insertValueIntoDenormTable(cert);
	 
			} else {
				if (cert.isLocked()) {
					throw new RuntimeException("Cертификат номер " + cert.getCertnumber() + " уже выдан и не может быть изменен.");
				} else {
				   throw new RuntimeException("Cертификат номер " + cert.getCertnumber() + " не может быть сохранен в базе. Внутрення ошибка. Обратитесь к администратору.");
				}
			}
			return cert;
		}

		
		// ---------------------------------------------------------------------
		//  Insert Values into denorm tables
		// ---------------------------------------------------------------------
		private void insertValueIntoDenormTable(FSCertificate cert) {

			String tovars = "";
			String sql = "insert into fs_product_denorm(id_fscert, tovars) values (:id_fscert, :tovars)";
			for (FSProduct product : cert.getProducts()) {
				tovars += product.getTovar() + "; ";
			}
			MapSqlParameterSource parameters = 
					new MapSqlParameterSource().addValue("id_fscert", cert.getId()).addValue("tovars",tovars);
			if (tovars != null && tovars.length() > 0 ) {
		        template.update(sql, parameters);
			}

			String blanks = "";
			sql = "insert into fs_blank_denorm(id_fscert, blanknumbers) values (:id_fscert, :blanknumbers)";
			for (FSBlank blank : cert.getBlanks()) {
				blanks += blank.getBlanknumber() + "; ";
			}
			parameters = 
					new MapSqlParameterSource().addValue("id_fscert", cert.getId()).addValue("blanknumbers",blanks);
			if (blanks != null && blanks.length() > 0 ) {			
					template.update(sql, parameters);
			}
		}


		//--------------------------------------------------------------------	
		//            GET ID certificate by its numbers 
		//--------------------------------------------------------------------
		private long getFSCertificateIdByNUmber(String certnumber) {
			String sql = "select id from FS_CERT WHERE CERTNUMBER = ?";
			
			long id = template.getJdbcOperations().queryForObject(
	                sql, new Object[] { certnumber }, Long.class);
			
			return id;
		}

		//--------------------------------------------------------------------
		//--------------------------------------------------------------------	
		//            GET list of certificate numbers in CVS string format 
		//--------------------------------------------------------------------
		//--------------------------------------------------------------------
		public String getFSCertificates(FSFilter filter) throws Exception {
			String ret = null;
			
			String sql = "select certnumber, datecert from fs_cert " 
			              + filter.getWhereLikeClause() 
			              + " ORDER by datecert";
			
			LOG.info(sql);
			SqlParameterSource parameters = new BeanPropertySqlParameterSource(filter);		
			
			return this.template.query(sql,	parameters, 
					new FSLightCertificateRowMapper());
		}

		//--------------------------------------------------------------------
		//--------------------------------------------------------------------	
		//            DELETE FS certificate by certificate number 
		//--------------------------------------------------------------------
		//--------------------------------------------------------------------
		public String deleteFSCertificate(String certnumber) throws Exception {
	        String rnumber = null;
	        long id = getFSCertificateIdByNUmber(certnumber);
	        
	    	template.getJdbcOperations().update(
					"delete from FS_PRODUCT where id_fscert = ?",
					Long.valueOf(id));
		
	    	template.getJdbcOperations().update(
					"delete from FS_BLANK where id_fscert = ?",
					Long.valueOf(id));
	    	
	    	template.getJdbcOperations().update(
					"delete from FS_CERT where id = ?",
					Long.valueOf(id));
	    	
	    	rnumber = certnumber;
	    	LOG.info("Certificate " + rnumber + " deleted !");
			return rnumber;
		}

		//--------------------------------------------------------------------
		//--------------------------------------------------------------------	
		//    Get Branch by branch code         
		//--------------------------------------------------------------------
		//--------------------------------------------------------------------
		public Branch getBranchByCode(String code_otd) {
			Branch branch = null;
			branch = template.getJdbcOperations().queryForObject("select * from fs_branch where code = ? ",
						new Object[] { code_otd }, new BeanPropertyRowMapper<Branch>(Branch.class));
			loadClientLocales(branch); 
			return branch;
		}


		//--------------------------------------------------------------------
		//--------------------------------------------------------------------	
		//   get JSON string for autocomplete         
		//--------------------------------------------------------------------
		//--------------------------------------------------------------------
		public String getListCertNumber(String term) {
			String sql = "select id, certnumber from FS_CERT WHERE UPPER(CERTNUMBER) LIKE :number AND rownum < 10 ";
			Map<String, Object> params = new HashMap();
			params.put("number", term +"%");

			List<ViewFSCertificate> certs = this.template.query(sql, params, 
					new BeanPropertyRowMapper<ViewFSCertificate>(ViewFSCertificate.class));
			
			String json = "[";
			String data = "";
			for (FSCertificate cert : certs) {
				if (! data.isEmpty()) { data += ", "; }
			    data += "{" + jdecor("id") + ":" + jdecor("" + cert.getId()) + ", " 
			                + jdecor("label") + ":" + jdecor(cert.getCertnumber()) + ", "
			                + jdecor("value") + ":" + jdecor(cert.getCertnumber()) +"}";
			}
			json += data + "]";
			return json;
		}

		private String jdecor(String str) {
			return "\"" + str + "\"";
		}
		
}
