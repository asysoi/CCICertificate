 package cci.repository.owncert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import cci.model.cert.Report;
import cci.model.owncert.Branch;
import cci.model.owncert.Factory;
import cci.model.owncert.OwnCertificate;
import cci.model.owncert.OwnCertificateExport;
import cci.model.owncert.OwnCertificateHeader;
import cci.model.owncert.OwnCertificateHeaders;
import cci.model.owncert.OwnCertificates;
import cci.model.owncert.Product;
import cci.repository.SQLBuilder;
import cci.service.SQLQueryUnit;
import cci.web.controller.owncert.OwnFilter;
import cci.web.controller.owncert.ViewWasteOwnCertificate;
import cci.web.controller.owncert.exception.NotDeleteOwnCertificateException;
import cci.web.controller.owncert.exception.NotUpdatedOwnCertificateFileNameException;
import cci.web.controller.cert.exception.NotFoundCertificateException;
import cci.web.controller.cert.exception.CertificateUpdateException;

@Repository
public class JDBCOwnCertificateDAO implements OwnCertificateDAO {

	private static final Logger LOG = Logger
			.getLogger(JDBCOwnCertificateDAO.class);
	
	private NamedParameterJdbcTemplate template;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.template = new NamedParameterJdbcTemplate(dataSource);
	}

	// ------------------------------------------------------------------------------
	//  This method returns count of pages in certificate list 
	// ------------------------------------------------------------------------------
	public int getViewPageCount(SQLBuilder builder) {
        SQLQueryUnit qunit = builder.getSQLUnitWhereClause();     
		String sql = "SELECT count(*) FROM owncertificate "
				+ qunit.getClause();
	
		Integer count = this.template.queryForObject(sql, qunit.getParams(), Integer.class);
		
		LOG.info(sql);
		return count.intValue();
	}

	// ------------------------------------------------------------------------------
	//  This method returns a current page of the certificate's list
	/*
	sql = "select " + flist 
		+ " from certview where id in "
		+ " (select  a.id "
		+ " from (SELECT id FROM (select id from certview "
		+  filter.getClause()
		+ " ORDER by " +  orderby + " " + order + ", id " + order  
		+ ") aa LIMIT :highposition "    
		+ ") a left join (SELECT id FROM (select id from certview "
		+  filter.getClause()
		+ " ORDER by " +  orderby + " " + order + ", id " + order
		+ ") bb LIMIT :lowposition "   
		+ ") b on a.id = b.id where b.id is null)" 
		+ " ORDER by " +  orderby + " " + order + ", id " + order;
	*/
	// ------------------------------------------------------------------------------
	public List<OwnCertificate> findViewNextPage(String[] dbfields, int page, int pagesize, int pagecount, String orderby,
			String order, SQLBuilder builder) {
		String sql;

		String flist = "*";
		
        SQLQueryUnit filter = builder.getSQLUnitWhereClause();
        Map<String, Object> params = filter.getParams();
        
        if (pagesize < pagecount) {
        	sql = "select " + flist
    				+ " from owncertificate "
    				+ filter.getClause()
    				+ " ORDER by " +  orderby + " " + order + ", id " + order  
    				+ " LIMIT :lowposition, :pagesize ";    

       		params.put("highposition", Integer.valueOf(page * pagesize));
    		params.put("lowposition", Integer.valueOf((page - 1) * pagesize));
    		params.put("pagesize", Integer.valueOf(pagesize));
    		
        } else {
        	sql = "select " + flist 
    				+ " from owncertificate "
    				+  filter.getClause()
    				+ " ORDER by " +  orderby + " " + order + ", id " + order;  
        }
		
		LOG.info("Next page : " + sql);
		
		if (pagecount != 0) {
		    return this.template.query(sql,	params, 
				new OwnCertificateMapper<OwnCertificate>());
		} else {
			return null;
		}
	}
	
	
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	//      Methods are applicable for RESTFUL service 
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------
	// Получить список сертификатов 
	// ---------------------------------------------------------------
	public OwnCertificates getOwnCertificates(OwnFilter filter, boolean isLike) {

		String sql = "select * from owncertificate "
				+ (isLike ? filter.getWhereLikeClause() : filter
						.getWhereEqualClause()) + " ORDER BY datecert";
         OwnCertificates certs = new  OwnCertificates();
		        
		 certs.setOwncertificates(this.template.getJdbcOperations()
				.query(sql, new OwnCertificateMapper<OwnCertificate>()));
		 return certs; 
	}

	// ---------------------------------------------------------------
	// Получить список заголовков сертификатов
	// ---------------------------------------------------------------
	public OwnCertificateHeaders getOwnCertificateHeaders(OwnFilter filter, boolean isLike) {

		String sql = "select number, blanknumber, datecert from owncertificate "
				+ (isLike ? filter.getWhereLikeClause() : filter
						.getWhereEqualClause()) + " ORDER BY datecert";
         OwnCertificateHeaders certs = new  OwnCertificateHeaders();
		        
		 certs.setOwncertificateheaders(this.template.getJdbcOperations()
				.query(sql,	new BeanPropertyRowMapper<OwnCertificateHeader>(OwnCertificateHeader.class)));
		 
		 return certs; 
	}
		
	/* ----------------------------------------------------------------
	* поиск единственного сертификата по id -> PS
	* --------------------------------------------------------------- */
	public OwnCertificate findOwnCertificateByID(OwnFilter filter) throws Exception {
		OwnCertificate cert = null;

		String sql = "select * from owncertificate " + filter.getWhereEqualClause();
		System.out.println("findOwnCertificateByID: " + sql);
		
		cert = template.getJdbcOperations()
				.queryForObject(
						sql,
						null,
						new OwnCertificateMapper<OwnCertificate>());
		
		loadReletedCertificateEntities(cert);
		return cert;
	}

	/* ---------------------------------------------------------------
	* 
	*   Сохранение сертификата в базе дданных
    *   Денормализованные данные хранятся
	*   factories -> ownCerificate.getFactoryList(), branches -> getBranchlist(), products -> getProductlist()
	* 
	* --------------------------------------------------------------- */
	public OwnCertificate saveOwnCertificate(OwnCertificate cert) throws Exception {

		cert.setId_beltpp(getBeltppID(cert));

		String sql_cert = "insert into owncertificate(id_beltpp, number, blanknumber, type, customername, customeraddress, "
				+ " customerunp, datecert, dateexpire, expert, signer, signerjob, datestart, additionalblanks, factories, branches, products, "
				+ " beltppname, beltppaddress, beltppphone, beltppunp, "
				+ " datestop, datechange, numberprev, numbernext, filename, status, productdescription, otd_id) "
				+ " values ("
				+ " :id_beltpp, :number, :blanknumber, :type, :customername, :customeraddress, :customerunp, "
				+ " STR_TO_DATE(:datecert,'%d.%m.%Y'), " + " STR_TO_DATE(:dateexpire,'%d.%m.%Y'), "
				+ " :expert, :signer, :signerjob, " + " STR_TO_DATE(:datestart,'%d.%m.%Y')"
				+ ", :additionalblanks, :factorylist, :branchlist, :productlist, "
				+ " :beltppname, :beltppaddress, :beltppphone, :beltppunp, " + " STR_TO_DATE(:datestop,'%d.%m.%Y'), "
				+ " STR_TO_DATE(:datechange,'%d.%m.%Y'), "
				+ " :numberprev, :numbernext, :filename, :status, :productdescription, :otd_id" 
				+ ")";

		SqlParameterSource parameters = new BeanPropertySqlParameterSource(cert);
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		int id = 0;

		int row = template.update(sql_cert, parameters, keyHolder, new String[] { "id" });
		cert.setId(keyHolder.getKey().intValue());

		if (row > 0 && cert.getId() > 0) {
			saveCertificateData(cert);
		} 
		return cert;
	}

	
	/* ---------------------------------------------------------------
	* 
	*  Сохранить сведения о продукции, производстве и обособленных подразделениях
    *    
	* --------------------------------------------------------------- */
	private void saveCertificateData(OwnCertificate cert) {
		
		int id  = cert.getId();
		String sql_branches = "insert into ownbranch(id_certificate, name, address) "
				+ "values ( :id, :name, :address)";
		
		if (cert.getBranches() != null && cert.getBranches().size() > 0) {
			for (Branch br : cert.getBranches()) {
				br.setId(id);
			}
			SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(cert.getBranches().toArray());
			int[] updateCounts = template.batchUpdate(sql_branches, batch);
			LOG.info("Добавлены обособленные подразделения: " + updateCounts.toString());
		}

		String sql_factories = "insert into ownfactory(id_certificate, address) " + "values (:id, :address)";

		if (cert.getFactories() != null && cert.getFactories().size() > 0) {
			for (Factory ft : cert.getFactories()) {
				ft.setId(id);
			}
			SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(cert.getFactories().toArray());
			int[] updateCounts = template.batchUpdate(sql_factories, batch);
			LOG.info("Добавлены адреса производств: " + updateCounts.toString());
		}

		String sql_product = "insert into ownproduct(id_certificate, number, name, code) "
				+ "values ( :id, :number, :name, :code)";

		if (cert.getProducts() != null && cert.getProducts().size() > 0) {
			for (Product pr : cert.getProducts()) {
				pr.setId(id);
			}
			SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(cert.getProducts().toArray());
			int[] updateCounts = template.batchUpdate(sql_product, batch);
			LOG.info("Добавлены продукты: " + updateCounts.toString());
		}

		String sql_xml = "insert into ownfiles (id_certificate, xml) values (?, ?)";
		template.getJdbcOperations().update(sql_xml, new Object[] { id, cert.getXml() });
	}

	/* -------------------------------------------
	*  Get id of beltpp branch
	* ------------------------------------------- */
	private int getBeltppID(OwnCertificate cert) {
		String sql = "SELECT id FROM beltpp WHERE name = '"
				+ cert.getBeltpp().getName() + "' Limit 1";
		int id = 0;
		try {
			id = this.template.getJdbcOperations().queryForObject(sql,
					Integer.class);
		} catch (Exception ex) {
			System.out
					.println(ex.getClass().getName() + ": " + ex.getMessage());
		}
		
		if (id == 0) {
			sql = "insert into beltpp(name, address, unp) values(:name, :address, :unp)";
			SqlParameterSource parameters = new BeanPropertySqlParameterSource(
					cert.getBeltpp());
			GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
			id = 0;

			int row = template.update(sql, parameters, keyHolder,
					new String[] { "id" });
			id = keyHolder.getKey().intValue();
		}
		return id;
	}

	/* ---------------------------------------------------------------
	 * 
	 * Update certificate
	 * 
	 * --------------------------------------------------------------- */
	public OwnCertificate updateOwnCertificate(OwnCertificate cert) {
		OwnCertificate oldcert = null;
		try {
			boolean active = TransactionSynchronizationManager.isActualTransactionActive();
			LOG.info("Start update transaction = " + active);

			String sql = "select * from owncertificate WHERE number = ? and blanknumber = ? and datecert = STR_TO_DATE(?,'%d.%m.%Y') and otd_id= ?";
			oldcert = template.getJdbcOperations().queryForObject(sql,
					new Object[] { cert.getNumber().trim(), cert.getBlanknumber().trim(),
							cert.getDatecert(), cert.getOtd_id() },
					new OwnCertificateMapper<OwnCertificate>());

		} catch (Exception ex) {
			throw (new NotFoundCertificateException(
					"Сертификат для обновления в базе не найден. Сертификат должен быть добавлен  другой командой: " + ex.getLocalizedMessage()));
		}
		
		if (oldcert == null) {
			throw (new NotFoundCertificateException(
					"Не удалось найти в базе cертификат " + cert.getNumber() + " на бланке " + cert.getBlanknumber()));
		}

		try {
			cert.setId(oldcert.getId());
			cert.setId_beltpp(getBeltppID(cert));

			String sql_cert = "update owncertificate SET "
					+ " id_beltpp = :id_beltpp, type=:type, customername=:customername,"
					+ " customeraddress=:customeraddress, customerunp=:customerunp, "
					+ " datecert = STR_TO_DATE(:datecert,'%d.%m.%Y'), "
					+ " dateexpire = STR_TO_DATE(:dateexpire,'%d.%m.%Y'),"
					+ " datestart=STR_TO_DATE(:datestart,'%d.%m.%Y'),"
					+ " datestop = STR_TO_DATE(:datestop,'%d.%m.%Y'), "
					+ " datechange = STR_TO_DATE(:datechange,'%d.%m.%Y'),"
					+ " expert=:expert, signer=:signer, signerjob=:signerjob," + " additionalblanks=:additionalblanks, "
					+ " factories=:factorylist, branches=:branchlist, products=:productlist,"
					+ " beltppname=:beltppname, beltppaddress=:beltppaddress, "
					+ " beltppphone=:beltppphone, beltppunp=:beltppunp,  "
					+ " numberprev = :numberprev, numbernext=:numbernext, status=:status,"
					+ " productdescription=:productdescription " + " WHERE id = :id ";

			SqlParameterSource parameters = new BeanPropertySqlParameterSource(cert);
			int row = template.update(sql_cert, parameters);
			LOG.info("Row updated = " + row);

			if (row > 0) {
				template.getJdbcOperations().update("delete from ownproduct where id_certificate = ?", cert.getId());
				template.getJdbcOperations().update("delete from ownbranch where id_certificate = ?", cert.getId());
				template.getJdbcOperations().update("delete from ownfactory where id_certificate = ?", cert.getId());
				saveCertificateData(cert);
			}
		} catch (Exception ex) {
			throw (new CertificateUpdateException(
					"Ошибка обновления сертификата. Сертификат был найден, но обновление выполнить не удалось: "
							+ ex.getLocalizedMessage()));
		}
		return cert;
		
	}

	/* ---------------------------------------------------------------
	 * 
	 * Find certificate by unique identification. Updated 07.02.2019
	 * 
	 *  --------------------------------------------------------------- */
	public OwnCertificate findOwnCertificateByNumber(String number, String blanknumber, String datecert, String otd_id) {

		String sql = "select * from owncertificate WHERE number = ? and blanknumber = ? and datecert =  STR_TO_DATE(?,'%d.%m.%Y') and otd_id = ? ";
		System.out.println("findOwnCertificateByNumber");
		OwnCertificate cert = template.getJdbcOperations()
				.queryForObject(
						sql,
						new Object[] { number.trim(), blanknumber.trim(), datecert.trim(), otd_id},
						new OwnCertificateMapper<OwnCertificate>());
		
		loadReletedCertificateEntities(cert);
		return cert;	
	}
	
	/* ---------------------------------------------------------------
	 * 
	 * Load all certificate's attributes 
	 * 
	 *  --------------------------------------------------------------- */
	private void loadReletedCertificateEntities(OwnCertificate cert) {
		String sql = "select * from ownproduct WHERE id_certificate = ? ORDER BY id";

		cert.setProducts(template.getJdbcOperations().query(sql, new Object[] { cert.getId() },
				new BeanPropertyRowMapper<Product>(Product.class)));

		sql = "select * from ownbranch WHERE id_certificate = ? ORDER BY id";

		cert.setBranches(template.getJdbcOperations().query(sql, new Object[] { cert.getId() },
				new BeanPropertyRowMapper<Branch>(Branch.class)));

		sql = "select * from ownfactory WHERE id_certificate = ? ORDER BY id";

		cert.setFactories(template.getJdbcOperations().query(sql, new Object[] { cert.getId() },
				new BeanPropertyRowMapper<Factory>(Factory.class)));
	}


	// ---------------------------------------------------------------
	// Update certificate's file name. If filename is empty there aren't original certificate file 
	// ---------------------------------------------------------------
	public boolean updateOwnCertificateFileName(String number, String blanknumber, String datecert, String filename) {
		boolean result = false;
		OwnCertificate cert = new OwnCertificate();
		cert.setNumber(number.trim());
		cert.setBlanknumber(blanknumber.trim());
		cert.setDatecert(datecert.trim());
		cert.setFilename(filename);

		String sql_cert = "update owncertificate SET " + " filename= :filename "
				+ " WHERE number = :number and blanknumber= :blanknumber and datecert =  STR_TO_DATE(:datecert,'%d.%m.%Y')";

		SqlParameterSource parameters = new BeanPropertySqlParameterSource(cert);

		try {

			int row = template.update(sql_cert, parameters);
			if (row > 0) {
				LOG.info("Row updated = " + row);
				result = true;
			} else {
				throw (new NotUpdatedOwnCertificateFileNameException(
						"В базе сертификатов отсутствует сертификат, " + "файл которого необходимо обновить"));
			}
		} catch (Exception ex) {
			LOG.info(ex.getMessage());
			throw (new NotUpdatedOwnCertificateFileNameException(
					"Ошибка обновления имени файла сертификата " + ex.getMessage()));
		}

		return result;
	}

	// ---------------------------------------------------------------
	// Delete certificate from database. Certificate will be moved to archive.There aren't real deletion    
	// ---------------------------------------------------------------
	public boolean deleteOwnCertificate(String number, String blanknumber, String datecert, String otd_id) {
		boolean ret = false;
		try {
			String sql = "select * from owncertificate WHERE number = ? and blanknumber = ? and datecert =  STR_TO_DATE(?,'%d.%m.%Y') and otd_id= ?";
            
			boolean active = TransactionSynchronizationManager.isActualTransactionActive();
			System.out.println("\n\nActive transaction = " + active + "\n\n");
			
			OwnCertificate cert = template.getJdbcOperations().queryForObject(sql,
					new Object[] { number.trim(), blanknumber.trim(), datecert.trim(), otd_id },
					new OwnCertificateMapper<OwnCertificate>());
            
			template.getJdbcOperations().update("delete from ownproduct where id_certificate = ?", cert.getId());
			template.getJdbcOperations().update("delete from ownbranch where id_certificate = ?", cert.getId());
			template.getJdbcOperations().update("delete from ownfactory where id_certificate = ?", cert.getId());
			template.getJdbcOperations().update("delete from ownfiles where id_certificate = ?", cert.getId());
			template.getJdbcOperations().update("delete from owncertificate where id = ?", cert.getId());
			ret = true;
			
		} catch (Exception ex) {
			LOG.info(ex.getMessage());
			throw (new NotDeleteOwnCertificateException("Сертификат не удален " + ex.getMessage()));
		}
		return ret;
	}

	// ------------------------------------------------------------------------------
	// This method returns list of own certificates for export to Excel file
	// ------------------------------------------------------------------------------
	public List<OwnCertificateExport> getCertificates(String[] dbfields, String orderby, String order,
			SQLBuilder builder) {

		String flist = "*";

		SQLQueryUnit filter = builder.getSQLUnitWhereClause();
		Map<String, Object> params = filter.getParams();

		String sql = " SELECT " + flist + " FROM owncertificate " + filter.getClause() + " ORDER BY " + orderby + " "
				+ order;

		LOG.info("Get certificates for export: " + sql);

		return this.template.query(sql, params, new OwnCertificateExportMapper<OwnCertificateExport>());
	}

	/* ---------------------------------------------------------------
	* Get Analytic Report grouped by Fields
	* --------------------------------------------------------------- */
	public List<Report> getReport(String[] fields, SQLBuilder builder) {
		String field = fields[0]; // берем только одно поле для группировки
		
        System.out.println("Report field: " + field);
		SQLQueryUnit filter = builder.getSQLUnitWhereClause();
		String sql;
		
        if (field.equals("code")) {
        	sql = "SELECT " + field + "  as field, COUNT(*) as value FROM (SELECT DISTINCT ID_CERTIFICATE," 
        			+ field + " FROM OWNPRODUCT where id_certificate in  (select id from owncertificate "
        			+ filter.getClause() 
        			+ ")) own group by " + field + " ORDER BY value DESC";
        } else {
        	
		    sql = "SELECT " + field + " as field, COUNT(*) as value FROM (select "
		    		+ field + " from owncertificate "
		    		+ filter.getClause() 
		    		+ " ) own group by " + field + " ORDER BY value DESC";
        }
		
		LOG.info("Make pivot report: " + sql);

		Map<String, Object> params = filter.getParams();

		return this.template.query(sql, params, new BeanPropertyRowMapper<Report>(Report.class));
	}

	/* ---------------------------------------------------------------
	* Get list orsha' factories for report
	* --------------------------------------------------------------- */
	public List<OwnCertificate> getOrshaCertificates(String reportdate, String query, String otd_id) {
        
    	/*String sql = "select * from owncertificate where " 
          + " ((UPPER(factories) like :orsha and UPPER(customeraddress) like :orsha and type= :typeproduct) "  
          + " OR  (UPPER(factories) like :orsha and UPPER(branches) like :orsha and type=:typeproduct) "
          + " OR  (UPPER(customeraddress) like :orsha and type=:typeservice) "  
          + " OR  (UPPER(branches) like :orsha and type=:typeservice) ) " 
          + " AND datestart <= STR_TO_DATE(:reportdate,'%d.%m.%Y')  and dateexpire >= STR_TO_DATE(:reportdate,'%d.%m.%Y') "
          + " and otd_id = :otd_id " 
          + " ORDER by customername, datecert ";
        */     
		String sql = "select * from owncertificate where " 
		          + " ((UPPER(factories) like :orsha and UPPER(customeraddress) like :orsha and type= :typeproduct) "  
		          + " OR  (UPPER(customeraddress) like :orsha and type=:typeservice) ) " 
		          + " AND datestart <= STR_TO_DATE(:reportdate,'%d.%m.%Y')  and dateexpire >= STR_TO_DATE(:reportdate,'%d.%m.%Y') "
		          + " and otd_id = :otd_id " 
		          + " ORDER by customername, datecert ";
		
        Map<String, Object> params = new HashMap<String,Object>();
        
     	params.put("orsha", "%" + query  +"%");
     	params.put("typeproduct", "с/п");
     	params.put("typeservice", "р/у");
     	params.put("reportdate", reportdate);
    	params.put("otd_id", Integer.valueOf(otd_id));
    	
		return this.template.query(sql,	params, 
				new OwnCertificateMapper<OwnCertificate>());
	}

	/* ---------------------------------------------------------------
	 * Get list waste certificates for report
	 * --------------------------------------------------------------- */
	public List<ViewWasteOwnCertificate> getWasteOwnCertificates(String reportdate, List<String> numbers) {
		String sql = "";
		
		LOG.info("Code numbers: " + numbers.toString());
		
		for (String code : numbers) {
		          sql += (sql.isEmpty() ? "" : " union ") + " select '" + code + "'" 
		        		  + " as productcode, customername, customerunp, customeraddress, datecert,"
		        		  + " number, datestart, dateexpire" 
	 	        		  + " from owncertificate where"
	 	        		  + " id in (select distinct id_certificate from ownproduct where code like "
	 	        		  + " '%" + code + "%')"; 
		}
		sql += " order by productcode, customername, datecert";
		LOG.info("SQL Waste Request: " + sql);
		
  	    return this.template.query(sql, 
  	    		new BeanPropertyRowMapper<ViewWasteOwnCertificate>(ViewWasteOwnCertificate.class));
	}
	
	
}
