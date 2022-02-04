package cci.repository.cert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import cci.repository.cert.SequenceGenerator;
import cci.model.cert.Certificate;
import cci.model.cert.Country;
import cci.model.cert.Product;
import cci.model.cert.Report;
import cci.repository.SQLBuilder;
import cci.service.SQLQueryUnit;
import cci.web.controller.cert.CertFilter;
import cci.web.controller.cert.exception.CertificateDeleteException;
import cci.web.controller.cert.exception.CertificateGetException;
import cci.web.controller.cert.exception.CertificateUpdateException;
import cci.web.controller.cert.exception.NotFoundCertificateException;

@Repository
public class JDBCCertificateDAO implements CertificateDAO {
	
	private static final Logger LOG = Logger.getLogger(JDBCCertificateDAO.class);
	
	private NamedParameterJdbcTemplate template;
	private DataSource ds; 

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.template = new NamedParameterJdbcTemplate(dataSource);
		ds = dataSource;
	}
	
	// ---------------------------------------------------------------
	// количество сертификатов в списке -> PS
	// ---------------------------------------------------------------
	public int getViewPageCount(SQLBuilder builder) {
		long start = System.currentTimeMillis();
        SQLQueryUnit qunit = builder.getSQLUnitWhereClause();     
		String sql = "SELECT count(*) FROM CERT_VIEW "
				+ qunit.getClause();
	
		Integer count = this.template.queryForObject(sql, qunit.getParams(), Integer.class);
		
		//LOG.info(sql);
		//LOG.info("Query time: "
		//		+ (System.currentTimeMillis() - start));

		return count.intValue();
	}

	// ---------------------------------------------------------------
	// поиск сертификата по id -> PS
	// ---------------------------------------------------------------
	public Certificate findByID(Long id) {
		Certificate cert = null;

		try {
			String sql = "select * from CERT_VIEW WHERE cert_id = ?";
			cert = template.getJdbcOperations().queryForObject(sql,
					new Object[] { id },
					new BeanPropertyRowMapper<Certificate>(Certificate.class));

			sql = "select * from C_PRODUCT WHERE cert_id = ? ORDER BY product_id";
			cert.setProducts(template.getJdbcOperations().query(sql,
					new Object[] { cert.getCert_id() },
					new BeanPropertyRowMapper<Product>(Product.class)));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return cert;
	}

	// ---------------------------------------------------------------
	// поиск сертификата по number
	// ---------------------------------------------------------------
	public Certificate check(Certificate cert) {
		Certificate rcert = null;
		long start = System.currentTimeMillis();
		
		try {
			String sql = "select * from CERT_VIEW WHERE NOMERCERT = ? AND NBLANKA = ? AND (DATACERT=? OR ISSUEDATE=TO_DATE(?,'DD.MM.YY'))";
			rcert = template.getJdbcOperations().queryForObject(
					sql,
					new Object[] { cert.getNomercert(), cert.getNblanka(),
							cert.getDatacert(), cert.getDatacert() },
					new BeanPropertyRowMapper<Certificate>(Certificate.class));
			
			//LOG.info("Certificate check: " + (System.currentTimeMillis() - start));
			if (rcert != null) {
				sql = "select * from C_PRODUCT WHERE cert_id = ?  ORDER BY product_id";
				rcert.setProducts(template.getJdbcOperations().query(sql,
						new Object[] { rcert.getCert_id() },
						new BeanPropertyRowMapper<Product>(Product.class)));
			}
		} catch (Exception ex) {
			LOG.info("Certificate isn't found: " + ex.getMessage());
		}
		
		LOG.info("Certificate check load: " + (System.currentTimeMillis() - start));
		return rcert;
	}

	// ---------------------------------------------------------------
	// поиск сертификата по номеру бланка
	// возможно несколько сертификатов -> PS
	// ---------------------------------------------------------------
	public List<Certificate> findByNBlanka(String number) {
		List<Certificate> certs = null;

		try {
			String sql = "select * from CERT_VIEW WHERE nblanka = ?";
			certs = template.getJdbcOperations().query(sql,
					new Object[] { number },
					new BeanPropertyRowMapper<Certificate>(Certificate.class));

			for (Certificate cert : certs) {
				sql = "select * from C_PRODUCT WHERE cert_id = ?  ORDER BY product_id";
				cert.setProducts(template.getJdbcOperations().query(sql,
						new Object[] { cert.getCert_id() },
						new BeanPropertyRowMapper<Product>(Product.class)));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return certs;
	}

	// ---------------------------------------------------------------
	// поиск сертификата по номеру сертификата -> PS
	// ---------------------------------------------------------------
	public List<Certificate> findByNumberCert(String number) {
		List<Certificate> certs = null;

		try {
			String sql = "select * from CERT_VIEW WHERE nomercert = ?";
			certs = template.getJdbcOperations().query(sql,
					new Object[] { number },
					new BeanPropertyRowMapper<Certificate>(Certificate.class));

			for (Certificate cert : certs) {
				sql = "select * from C_PRODUCT WHERE cert_id = ?  ORDER BY product_id";
				cert.setProducts(template.getJdbcOperations().query(sql,
						new Object[] { cert.getCert_id() },
						new BeanPropertyRowMapper<Product>(Product.class)));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return certs;
	}

	// ---------------------------------------------------------------
	// вернуть очередную страницу списка сертификатов XXX
	// ---------------------------------------------------------------
	public List<Certificate> findViewNextPage(String[] dbfields, int page, int pagesize, int pagecount, 
			String orderby, String order, SQLBuilder builder) {
		String sql;
		long start = System.currentTimeMillis();
		
		/* 
		String flist = "cert.cert_id, cert.datacert";
		
		for (String field : dbfields) {
		    flist += ", cert." + field;  	
		}
		
		String sql = " SELECT " + flist
				+ " FROM (SELECT t.*, ROW_NUMBER() OVER " + " (ORDER BY t."
				+ orderby + " " + order + ", t.CERT_ID " + order + ") rw "
				+ " FROM CERT_VIEW t " + builder.getWhereClause() + " )"
				+ " cert " + " WHERE cert.rw > " + ((page - 1) * pagesize)
				+ " AND cert.rw <= " + (page * pagesize);
		*/

		String flist = "cert_id, datacert, otd_name";
		
		for (String field : dbfields) {
		    flist += ", " + field;  	
		}
        SQLQueryUnit filter = builder.getSQLUnitWhereClause();
        Map<String, Object> params = filter.getParams();
        //LOG.info("SQLQueryUnit : " + filter);
        
        
        if (pagesize < pagecount) {
        	sql = "select " + flist 
				+ " from cert_view where cert_id in "
				+ " (select  a.cert_id "
				+ " from (SELECT cert_id FROM (select cert_id from cert_view "
				+  filter.getClause()
				+ " ORDER by " +  orderby + " " + order + ", cert_id " + order  
				+ ") where rownum <= :highposition "    
				+ ") a left join (SELECT cert_id FROM (select cert_id from cert_view "
				+  filter.getClause()
				+ " ORDER by " +  orderby + " " + order + ", cert_id " + order
				+ ") where rownum <= :lowposition "   
				+ ") b on a.cert_id = b.cert_id where b.cert_id is null)" 
				+ " ORDER by " +  orderby + " " + order + ", cert_id " + order;
       		params.put("highposition", Integer.valueOf(page * pagesize));
    		params.put("lowposition", Integer.valueOf((page - 1) * pagesize));
        } else {
        	sql = "select " + flist 
    				+ " from cert_view "
    				+  filter.getClause()
    				+ " ORDER by " +  orderby + " " + order + ", cert_id " + order;  
        }
		
		//LOG.info("Next page : " + sql);
		
		if (pagecount != 0) {
		    return this.template.query(sql,	params, 
				new BeanPropertyRowMapper<Certificate>(Certificate.class));
		} else {
			return null;
		}
		
	}

	// ---------------------------------------------------------------
	// вернуть все сертификаты -> PS
	// ---------------------------------------------------------------
	public List<Certificate> findAll() {
		String sql = "select * from CERT_VIEW ORDER BY cert_id";

		return this.template.getJdbcOperations().query(sql,
				new BeanPropertyRowMapper<Certificate>(Certificate.class));
	}

	

	// ---------------------------------------------------------------
	// find certificate by query template -> PS
	// ---------------------------------------------------------------
	public List<Certificate> findByCertificate(Certificate qcert) {
		List<Certificate> certs = null;

		String sql_cert = "SELECT * from cert_view WHERE "
				+ "datacert = :datacert AND nomercert = :nomercert AND nblanka = :nblanka";

		SqlParameterSource parameters = new BeanPropertySqlParameterSource(
				qcert);

		try {

			certs = template.query(sql_cert, parameters,
					new BeanPropertyRowMapper<Certificate>());

			for (Certificate cert : certs) {
				String sql = "select * from C_PRODUCT WHERE cert_id = ?  ORDER BY product_id";
				cert.setProducts(template.getJdbcOperations().query(sql,
						new Object[] { cert.getCert_id() },
						new BeanPropertyRowMapper<Product>()));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return certs;
	}

	// ---------------------------------------------------------------
	// Get list of branches -> PS
	// ---------------------------------------------------------------
	public Map<String, String> getBranchesList() {
		String sql = "SELECT id, otd_name from C_OTD Order by otd_name";

        return template.query(sql, new ResultSetExtractor<Map<String, String>>(){
			
		    public Map<String, String> extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,String> mapRet= new HashMap<String,String>();
		        while(rs.next()){
		            mapRet.put(Integer.toString(rs.getInt("id")), rs.getString("otd_name"));
		        }
		        return mapRet;
		    }
		});
	}

	// ---------------------------------------------------------------
	// Get MAP of branch's by roles -> PS
	// ---------------------------------------------------------------
	public Map<String,String> getACL() {
		String sql = "SELECT acl_role, id from C_OTD";

		//LOG.debug("Got ACL map");
		
		return template.query(sql, new ResultSetExtractor<Map<String, String>>(){
			
		    public Map<String, String> extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,String> mapRet= new HashMap<String,String>();
		        while(rs.next()){
		            mapRet.put(rs.getString("acl_role"), Integer.toString(rs.getInt("id")));
		        }
		        return mapRet;
		    }
		});
	}
	
	// ---------------------------------------------------------------
	// Get list of countries -> PS
	// ---------------------------------------------------------------
	public Map<String, Map<String,String>> getCountriesList() {
		String sql = "SELECT * from C_COUNTRY ORDER BY NAME";

		Map<String, Map<String,String>> countries = new HashMap<String, Map<String,String>>();
		Map<String, String> countriesru = new LinkedHashMap<String, String>();
		Map<String, String> countriesen = new LinkedHashMap<String, String>();
		
		List<Country> list = template.getJdbcOperations().query(sql,
				new BeanPropertyRowMapper<Country>(Country.class));
		
		for (Country cntry:list) {
			countriesru.put(cntry.getCode(), cntry.getName());
			countriesen.put(cntry.getCode(), cntry.getEnname());
		}
		countries.put("RU",countriesru);
		countries.put("EN",countriesen);
		
		//LOG.debug("Got country list");		
		return countries;
	}

	// ---------------------------------------------------------------
	// Get list of forms  -> PS
	// ---------------------------------------------------------------
	public List<String> getFormsList() {
		String sql = "SELECT forms from c_cert group by forms having forms is not null ORDER BY forms ";

		//LOG.debug("Got forms list");
		return (List<String>) template.getJdbcOperations().queryForList(sql,
				String.class);
	}

	
	// ---------------------------------------------------------------
	// Get full list of certificates by filter with tovar 
	// ---------------------------------------------------------------
	public List<Certificate> getCertificates(String[] dbfields, String orderby, String order,
			SQLBuilder builder) {
		
		String flist = "cert_id, otd_name";
		
		for (String field : dbfields) {
		    flist += ", " + field;  	
		}
		
		SQLQueryUnit filter = builder.getSQLUnitWhereClause();
    	Map<String, Object> params = filter.getParams();

		String sql = " SELECT " + flist + " FROM CERT_VIEW_TOFILE " 
				+ filter.getClause() + " ORDER BY " +  orderby + " " + order;

		//LOG.info("Get certificates: " + sql);
		
		return this.template.query(sql,	params, 
				new BeanPropertyRowMapper<Certificate>(Certificate.class));
	}

	// ---------------------------------------------------------------
	// Get Analitic Report grouped by Fields 
	// ---------------------------------------------------------------
	public List<Report> getReport(String[] fields, SQLBuilder builder, Boolean onfilter) {
		String field = fields[0];   // берем только одно поле для группировки
		
		SQLQueryUnit filter = builder.getSQLUnitWhereClause(); 		
		
		String sql = "SELECT " + field + " as field, COUNT(*) as value FROM (SELECT * FROM CERT_VIEW " +  
					 (onfilter ? filter.getClause() : "") + ") group by " + field + " ORDER BY value DESC" ;		

		//LOG.debug("Make report: " + sql);
		
		Map<String, Object> params = filter.getParams();

		return this.template.query(sql,	params, 
				new BeanPropertyRowMapper<Report>(Report.class));
	}

	// ---------------------------------------------------------------
    // Get Pagination of Certificates rEPORT
	// ---------------------------------------------------------------
	public List<Certificate> findViewNextReportPage(String[] dbfields, int page, int pagesize,
			String orderby, String order, SQLBuilder builder) {
		
		/*
		String flist = "cert.cert_id, cert.datacert";

		for (String field : dbfields) {
		    flist += ", cert." + field;  	
		}
		
		String sql = " SELECT " + flist
				+ " FROM (SELECT t.*, ROW_NUMBER() OVER " + " (ORDER BY t."
				+ orderby + " " + order + ", t.FILE_IN_ID " + order + ") rw "
				+ " FROM CERT_REPORT t " +   builder.getWhereClause() + " )"
				+ " cert " + " WHERE cert.rw > " + ((page - 1) * pagesize)
				+ " AND cert.rw <= " + (page * pagesize);
		*/		

		String flist = "file_in_id, cert_id, datacert, otd_name";
		
		for (String field : dbfields) {
		    flist += ", " + field;  	
		}

		SQLQueryUnit filter = builder.getSQLUnitWhereClause();
	    //LOG.info("SQLQueryUnit : " + filter);
	        
		String sql = "select " + flist 
				+ " from cert_report where file_in_id in "
				+ " (select  /*+ materialize  no_merge */ a.file_in_id "
				+ " from (SELECT file_in_id FROM (select file_in_id from cert_report "
				+  filter.getClause()
				+ " ORDER by " +  orderby + " " + order + ", file_in_id " + order  
				+ ") where rownum <= :highposition "  
				+ ") a left join (SELECT file_in_id FROM (select file_in_id from cert_report "
				+  filter.getClause()
				+ " ORDER by " +  orderby + " " + order + ", file_in_id " + order
				+ ") where rownum <=  :lowposition"  
				+ ") b on a.file_in_id = b.file_in_id where b.file_in_id is null)" 
				+ " ORDER by " +  orderby + " " + order + ", file_in_id " + order;

		//LOG.info("Next loaded page : " + sql);
		
		Map<String, Object> params = filter.getParams();
		params.put("highposition", Integer.valueOf(page * pagesize));
		params.put("lowposition", Integer.valueOf((page - 1) * pagesize));
		
		//LOG.info("Next page : " + sql);
		return this.template.query(sql,	params, 
				new BeanPropertyRowMapper<Certificate>(Certificate.class));
		
	}

	// ---------------------------------------------------------------
    // Get Count Certificates  
	// ---------------------------------------------------------------
	public int getViewPageReportCount(SQLBuilder builder) {
		SQLQueryUnit filter = builder.getSQLUnitWhereClause();
		
		String sql = "select count(*) from CERT_REPORT " + filter.getClause();
		
		//LOG.info(sql);
		Integer count = this.template.queryForObject(sql, filter.getParams(), Integer.class);
		
		return count;
	}

	// ---------------------------------------------------------------
	// Get full list of certificates by filter with tovar for Load Report 
	// ---------------------------------------------------------------
	public List<Certificate> getReportCertificates(String[] dbfields, String orderby,
			String order, SQLBuilder builder) {
		
		SQLQueryUnit filter = builder.getSQLUnitWhereClause();
		
		String flist = "cert_id";
		
		for (String field : dbfields) {
		    flist += ", " + field;  	
		}
		
		String sql = " SELECT " + flist + " FROM CERT_VIEW_LOAD_REPORT " 
				+ filter.getClause() + " ORDER BY " +  orderby + " " + order;

		//LOG.info("Get certificates for Report: " + sql);

		return this.template.query(sql,	filter.getParams(), 
				new BeanPropertyRowMapper<Certificate>(Certificate.class));
		
	}
	
	// ------------------------------------------------------------------------------------------------------
	//
	//  Methods are applicable for CT-1 Certificate REST service 
	//
	// ------------------------------------------------------------------------------------------------------
	
	// ---------------------------------------------------------------
	// Save certificate / FOR REST SERVICE
	// ---------------------------------------------------------------
	public long save(Certificate cert) throws Exception {
		String sql_cert = "insert into c_cert "
				+ "(cert_id, "
				+ "forms, unn, kontrp, kontrs, adress, poluchat, adresspol, datacert, "
				+ "nomercert, expert, nblanka, rukovod, transport, marshrut, otmetka, "
				+ "stranav, stranapr, status, koldoplist, flexp, unnexp, expp, "
				+ "exps, expadress, flimp, importer, adressimp, flsez, sez, "
				+ "flsezrez, stranap, otd_id, parentnumber, parentstatus, issuedate,  "
				+ "codestranav, codestranapr, codestranap,  category) "
				+ "values (:cert_id, "
				+ "TRIM(:forms), :unn, :kontrp, :kontrs, :adress, :poluchat, :adresspol, :datacert, "
				+ ":nomercert, :expert, :nblanka, :rukovod, :transport, :marshrut, :otmetka, "
				+ ":stranav, :stranapr, :status, :koldoplist, :flexp, :unnexp, :expp, "
				+ ":exps, :expadress, :flimp, :importer, :adressimp, :flsez, :sez, "
				+ ":flsezrez, :stranap, :otd_id, :parentnumber, :parentstatus, TO_DATE(:datacert,'DD.MM.YY'),  "
				+ ":codestranav, :codestranapr, :codestranap,  :category )";

		long cert_id = 0;
		
		try {
			cert.setCert_id(SequenceGenerator.getNextValue("cert_id", this));
			SqlParameterSource parameters = new BeanPropertySqlParameterSource(cert);
			
			int row = template.update(sql_cert, parameters);
			
			if (row > 0) {
				cert_id = cert.getCert_id();
			
				if (cert_id > 0) { 
					String sql_product = "insert into c_PRODUCT values ("
							+ " beltpp.product_id_seq.nextval, " + cert_id
							+ ", "
							+ " :numerator, :tovar, :vidup, :kriter, :ves, :schet, :fobvalue)";

					if (cert.getProducts() != null && cert.getProducts().size() > 0) {
						SqlParameterSource[] batch = SqlParameterSourceUtils
								.createBatch(cert.getProducts().toArray());
						int[] updateCounts = template.batchUpdate(sql_product, batch);
				
						// 	very important -> create product  denorm record
						String tovar = "";
						for (Product product: cert.getProducts()) {
							tovar += product.getTovar() +  ", " + product.getKriter() + ", " + product.getVes() + "; "; 
						}
				     
						sql_product = "insert into C_PRODUCT_DENORM values (:cert_id, :tovar)";
						parameters = new MapSqlParameterSource().addValue("cert_id", cert_id).addValue("tovar",tovar);
						
						try {
						   template.update(sql_product, parameters);
				        } catch (Exception ex) {
				        	LOG.error("Certificate product add error " + cert.getNomercert() + ": " + ex.getMessage());
							template.getJdbcOperations().update(
									"DELETE FROM C_CERT WHERE cert_id = ?",	cert_id);
							cert_id = 0;
							throw new RuntimeException(ex);
				        }
					}
					
					saveSourceInfo(cert_id, "RESTFUL service");
				}
			}
		} catch (Exception ex) {
			LOG.error("Error of adding certificate " + cert.getNomercert() + ": " + ex.getMessage());
			throw new RuntimeException(ex);
		}
		return cert_id;
	}
    
	// ---------------------------------------------------------------
	// Save info about certificate load
	// ---------------------------------------------------------------
	public int saveSourceInfo(long cert_id, String source) {
			String sql = "insert into c_files_in(file_in_id, file_in_name, cert_id, date_load) values "
					     + "(beltpp.file_id_seq.nextval, :file_in_name, :cert_id, SYSDATE)";
			int row = 0;
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("file_in_name", source);
			parameters.put("cert_id", cert_id);
		
			try {
				row = template.update(sql, parameters);
			} catch (Exception ex) {
				LOG.error("Eroor source info saving during certificate adding: " + ex.getMessage());
			}
			return row;
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------
	//                     Certificate REST FULL Methods for certificate of origin
	// -----------------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------
	// Update certificate / FOR REST SERVICE
	// ---------------------------------------------------------------
	public Certificate update(Certificate cert, String otd_id) throws Exception {
	  Certificate rcert = getCertificateByNumber(cert.getNomercert(), cert.getNblanka(), cert.getDatacert());
	  
	  // дата сертификата не может меняться
	  // номер отделения не меняется
	  
      if (rcert != null) {
    	if (otd_id == null || rcert.getOtd_id() != Integer.parseInt(otd_id)) {
    		throw( new CertificateUpdateException("Пользователь не авторизирован для обновления сертификата."));	
    	}
    	
		cert.setCert_id(rcert.getCert_id());
		cert.setOtd_id(Integer.parseInt(otd_id));
		
		String sql_cert = "update c_cert SET "
				+ "forms = TRIM(:forms), unn = :unn, kontrp = :kontrp, kontrs = :kontrs, adress = :adress, poluchat = :poluchat, adresspol = :adresspol, datacert = :datacert,"
				+ " expert = :expert, rukovod = :rukovod, transport = :transport, marshrut = :marshrut, otmetka = :otmetka,"
				+ "stranav = :stranav, stranapr = :stranapr, status = :status, koldoplist = :koldoplist, flexp = :flexp, unnexp = :unnexp, expp = :expp, "
				+ "exps = :exps, expadress = :expadress, flimp = :flimp, importer = :importer, adressimp = :adressimp, flsez = :flsez, sez = :sez,"
				+ "flsezrez = :flsezrez, stranap = :stranap, otd_id = :otd_id, parentnumber = :parentnumber, parentstatus = :parentstatus, issuedate = TO_DATE(:datacert,'DD.MM.YY'), "
				+ "codestranav = :codestranav, codestranapr = :codestranapr, codestranap = :codestranap, category = :category "
				+ "WHERE nomercert = :nomercert AND nblanka = :nblanka";

		SqlParameterSource parameters = new BeanPropertySqlParameterSource(cert);

		try {

			int row = template.update(sql_cert, parameters);

			template.getJdbcOperations().update(
					"delete from c_PRODUCT where cert_id = ?",
					Long.valueOf(cert.getCert_id()));
			
			template.getJdbcOperations().update(
					"delete from c_PRODUCT_DENORM where cert_id = ?",
					Long.valueOf(cert.getCert_id()));


			String sql_product = "insert into c_PRODUCT values ("
					+ " beltpp.product_id_seq.nextval, "
					+ cert.getCert_id() + ", "
					+ " :numerator, :tovar, :vidup, :kriter, :ves, :schet, :fobvalue)";

			if (cert.getProducts() != null && cert.getProducts().size() > 0) {
				SqlParameterSource[] batch = SqlParameterSourceUtils
						.createBatch(cert.getProducts().toArray());
				int[] updateCounts = template.batchUpdate(sql_product, batch);
			}
			
			// create product  denorm record
			String tovar = "";
			for (Product product: cert.getProducts()) {
				tovar += product.getTovar() +  ", " + product.getKriter() + ", " + product.getVes() + "; "; 
			}
			
			sql_product = "insert into C_PRODUCT_DENORM values (:cert_id, :tovar)";
			parameters = new MapSqlParameterSource().addValue("cert_id", Long.valueOf(cert.getCert_id())).addValue("tovar",tovar);
			template.update(sql_product, parameters);

		} catch (Exception ex) {
			LOG.error("Eroor updating certificate " + cert.getNomercert() + ": " + ex.getMessage());
            throw new RuntimeException(ex);			
		}
		rcert = cert;
      }	
			
	  return rcert; 	
	}

	
	// ---------------------------------------------------------------
	// get pool. return start pooling / FOR REST SERVICE
	// ---------------------------------------------------------------
	public long getNextValuePool(String seq_name, int poolsize) throws Exception {

			String sql = "select value from c_sequence WHERE name = ? ";
			// long vl = template.getJdbcOperations().queryForInt(sql);
			long vl = template.getJdbcOperations().queryForObject(sql,
					new Object[] {seq_name}, Long.class);
			
			sql = "update c_sequence SET "
					+ " value = value + :poolsize"
					+ " WHERE name = :seq_name";
			
			SqlParameterSource parameters = 
						new MapSqlParameterSource().addValue("poolsize", Integer.valueOf(poolsize))
						                           .addValue("seq_name",seq_name);
		    template.update(sql, parameters);
		
		    return vl;
	}
	
	//--------------------------------------------------------------------
	// Get Certificate by certificate number / FOR REST SERVICE
	//--------------------------------------------------------------------
	public Certificate getCertificateByNumber(String number, String blanknumber, String date) throws Exception {
		
		Certificate rcert = null;
		long start = System.currentTimeMillis();
		
		try {
			String sql = "select * from CERT_VIEW WHERE NOMERCERT = ? AND NBLANKA = ? AND (DATACERT=? OR ISSUEDATE=TO_DATE(?,'DD.MM.YY'))";
			rcert = template.getJdbcOperations().queryForObject(
					sql,
					new Object[] { number, blanknumber, date, date},
					new BeanPropertyRowMapper<Certificate>(Certificate.class));
		
			if (rcert != null) {
				sql = "select * from C_PRODUCT WHERE cert_id = ?  ORDER BY product_id";
				rcert.setProducts(template.getJdbcOperations().query(sql,
						new Object[] { rcert.getCert_id() },
						new BeanPropertyRowMapper<Product>(Product.class)));
			}
		} catch(EmptyResultDataAccessException ex) {
			LOG.info("Certificate find error: " + ex.getMessage());
			throw (new NotFoundCertificateException("Cертификат с номером " + number + " на бланке с номером  " + blanknumber  
 					                                + " не найден в базе."));
		} catch (Exception ex) {
			LOG.info("Certificate loading error: " + ex.getMessage());
			throw (new CertificateGetException(ex.getMessage()));
		}
		
		LOG.info("Certificate loading: " + (System.currentTimeMillis() - start));
		return rcert;
	}
	
	
	
	//--------------------------------------------------------------------
	// Delete Certificate by certificate number / FOR REST SERVICE
	//--------------------------------------------------------------------
	public void deleteCertificate(String number, String blanknumber, String date, String otd_id) throws Exception  {
		
		  Certificate rcert = getCertificateByNumber(number, blanknumber, date);
		  
	      if (rcert != null ) {
	    	    if (otd_id == null || rcert.getOtd_id() != Integer.parseInt(otd_id)) {
	      		   throw( new CertificateDeleteException("Пользователь не авторизирован для удаления сертификата."));	
	      	    }
	      	
	    	  	template.getJdbcOperations().update(
						"delete from c_PRODUCT where cert_id = ?",
						Long.valueOf(rcert.getCert_id()));
				
				template.getJdbcOperations().update(
						"delete from c_PRODUCT_DENORM where cert_id = ?",
						Long.valueOf(rcert.getCert_id()));

				template.getJdbcOperations().update(
						"delete from c_cert WHERE cert_id = ?",
						Long.valueOf(rcert.getCert_id()));
	      } else {
	    	  throw (new NotFoundCertificateException("Не найдено сертификата с номером "  +  number + " на бланке " + blanknumber));
	      }
	}
		
	//--------------------------------------------------------------------
	// Get certificate's numbers delimited by comma / FOR REST SERVICE
	//--------------------------------------------------------------------
	public String getCertificates(CertFilter filter, boolean b)  throws Exception {
		String ret = null;
		StringBuffer str = new StringBuffer();
		
		String sql = "select nomercert, nblanka, datacert from c_cert " + filter.getWhereLikeClause() + " ORDER by issuedate";
		// LOG.info(sql);
		Connection conn = null;

		try {
			conn = ds.getConnection();
			Statement ps = conn.createStatement();
			ResultSet rs = ps.executeQuery(sql);
			str.append("Number;Blank;Date\n");
			
			while (rs.next()) {
				str.append(rs.getString("nomercert"));
				str.append(";");
				str.append(rs.getString("nblanka"));
				str.append(";");
				str.append(rs.getString("datacert"));
				str.append("\n");
			}
			rs.close();
			ps.close();
			ret = str.toString();
  	   } catch (SQLException e) {
  		    LOG.info("Error: " + e.getMessage());
			throw new RuntimeException(e);
	   } finally {
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException e) {}
			}
	   }
		
	   return ret;
	}

	//--------------------------------------------------------------------
	// Get set of certificates by page number 
	//--------------------------------------------------------------------
	public List<Certificate> getCertificatesPage(int page, int blocksize) throws Exception {
		
		String sql = "SELECT c.*, p.tovar tovar from " 
				+ "(select * from c_cert where cert_id in "
				+ " (select  a.cert_id " + " from (SELECT cert_id FROM (select cert_id from c_cert) "
				+ " where rownum <= :endposition " + ") a left join (SELECT cert_id FROM (select cert_id from c_cert )"
				+ " where rownum < :firstposition " + ") b on a.cert_id = b.cert_id where b.cert_id is null))   "
				+ " c left join C_PRODUCT_DENORM p on c.cert_id = p.cert_id order by issuedate";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("endposition",  Integer.valueOf(page * blocksize));
		params.put("firstposition", Integer.valueOf((page - 1) * blocksize   + 1));
		
		if (page != 0) {
		    return this.template.query(sql,	params, 
				new BeanPropertyRowMapper<Certificate>(Certificate.class));
		} else {
			return null;
		}
	}
	
}
