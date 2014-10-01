package cci.cert.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import cci.cert.model.Certificate;
import cci.cert.model.Product;
import cci.purchase.service.FilterCondition;
import cci.purchase.web.controller.PurchaseView;

@Repository
public class JDBCCertificateDAO implements CertificateDAO {

	private NamedParameterJdbcTemplate template;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.template = new NamedParameterJdbcTemplate(dataSource);
	}

	// ---------------------------------------------------------------
	// ����������� ������������ � ������
	// ---------------------------------------------------------------
	public int getViewPageCount(SQLBuilder builder) {
		String sql = "SELECT count(*) FROM CERT_VIEW " + builder.getWhereClause(); 
		System.out.println(sql);
		
	    return this.template.getJdbcOperations().queryForInt(sql);
	}
	
	// ---------------------------------------------------------------
	// ����� ����������� �� id
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
	// ����� ����������� �� id
	// ---------------------------------------------------------------
	public Certificate check(Certificate cert) {
		Certificate rcert = null;

		try {
			String sql = "select * from CERT_VIEW WHERE NOMERCERT = ? AND NBLANKA = ? AND DATACERT=? ";
			rcert = template.getJdbcOperations().queryForObject(sql,
					new Object[] { cert.getNomercert(), cert.getNblanka(), cert.getDatacert()},
					new BeanPropertyRowMapper<Certificate>(Certificate.class));
            if (rcert != null) {  
			   sql = "select * from C_PRODUCT WHERE cert_id = ?  ORDER BY product_id";
			   rcert.setProducts(template.getJdbcOperations().query(sql,
					new Object[] { rcert.getCert_id() },
					new BeanPropertyRowMapper<Product>(Product.class)));
            }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rcert;
	}



	// ---------------------------------------------------------------
	// ����� ����������� �� ������ ������
	// �������� ��������� ������������
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
	// ����� ����������� �� ������ �����������
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
	// ������� ��������� �������� ������ ������������
	// ---------------------------------------------------------------
	public List<Certificate> findViewNextPage(int page, int pagesize, String orderby, String order, SQLBuilder builder) {
		
		String sql = " SELECT cert.* " + 
				     " FROM (SELECT t.*, ROW_NUMBER() OVER " +  
				     " (ORDER BY t." + orderby + " " + order + ", t.CERT_ID " + order + ") rw " + 
				     " FROM CERT_VIEW t " +  builder.getWhereClause()  + " )" + 
				     " cert " + 
				     " WHERE cert.rw > "  + ((page - 1) *  pagesize) +
		             " AND cert.rw <= " + (page *  pagesize);
		
		System.out.println("SQL get next page : " + sql);
		return this.template.getJdbcOperations().query(sql,
				new BeanPropertyRowMapper<Certificate>(Certificate.class));
	}


	
	// ---------------------------------------------------------------
	//  ������� ��� �����������
	// ---------------------------------------------------------------
	public List<Certificate> findAll() {
		String sql = "select * from CERT_VIEW ORDER BY cert_id";

		return this.template.getJdbcOperations().query(sql,
				new BeanPropertyRowMapper<Certificate>(Certificate.class));
	}

	
	// ---------------------------------------------------------------
	// save certificate 
	// ---------------------------------------------------------------
	public int save(Certificate cert) {
		String sql_cert = "insert into c_cert values "
				+ "(beltpp.cert_id_seq.nextval, "
				+ ":forms, :unn, :kontrp, :kontrs, :adress, :poluchat, :adresspol, :datacert,"
				+ ":nomercert, :expert, :nblanka, :rukovod, :transport, :marshrut, :otmetka,"
				+ ":stranav, :stranapr, :status, :koldoplist, :flexp, :unnexp, :expp, "
				+ ":exps, :expadress, :flimp, :importer, :adressimp, :flsez, :sez,"
				+ ":flsezrez, :stranap, :otd_id, :parentnumber, :parentstatus, TO_DATE(:datacert,'DD.MM.YY'))";

		SqlParameterSource parameters = new BeanPropertySqlParameterSource(cert);
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		int cert_id = 0;

		try {
			int row = template.update(sql_cert, parameters, keyHolder,
					new String[] { "CERT_ID" });
			cert_id = keyHolder.getKey().intValue();

			String sql_product = "insert into C_PRODUCT values ("
					+ " beltpp.product_id_seq.nextval, " + cert_id
					+ ", "
					+ " :numerator, :tovar, :vidup, :kriter, :ves, :schet)";

			if (cert.getProducts() != null && cert.getProducts().size() > 0) {
				SqlParameterSource[] batch = SqlParameterSourceUtils
						.createBatch(cert.getProducts().toArray());
				int[] updateCounts = template.batchUpdate(sql_product, batch);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return cert_id;
	}

	
	// ---------------------------------------------------------------
	// update certificate
	// ---------------------------------------------------------------
	public void update(Certificate cert) {

		String sql_cert = "update c_cert SET "
				+ "forms = :forms, unn = :unn, kontrp = :kontrp, kontrs = :kontrs, adress = :adress, poluchat = :poluchat, adresspol = :adresspol, datacert = :datacert,"
				+ "nomercert = :nomercert, expert = :expert, nblanka = :nblanka, rukovod = :rukovod, transport = :transport, marshrut = :marshrut, otmetka = :otmetka,"
				+ "stranav = :stranav, stranapr = :stranapr, status = :status, koldoplist = :koldoplist, flexp = :flexp, unnexp = :unnexp, expp = :expp, "
				+ "exps = :exps, expadress = :expadress, flimp = :flimp, importer = :importer, adressimp = :adressimp, flsez = :flsez, sez = :sez,"
				+ "flsezrez = :flsezrez, stranap = :stranap, parentnumber = :parentnumber, parentstatus = : parentstatus "
				+ "WHERE cert_id = :cert_id";

		SqlParameterSource parameters = new BeanPropertySqlParameterSource(cert);

		try {

			int row = template.update(sql_cert, parameters);

			template.getJdbcOperations().update(
					"delete from C_PRODUCT where cert_id = ?",
					Long.valueOf(cert.getCert_id()));

			String sql_product = "insert into C_PRODUCT values ("
					+ " beltpp.product_id_seq.nextval, "
					+ cert.getCert_id() + ", "
					+ " :numerator, :tovar, :vidup, :kriter, :ves, :schet)";

			if (cert.getProducts() != null && cert.getProducts().size() > 0) {
				SqlParameterSource[] batch = SqlParameterSourceUtils
						.createBatch(cert.getProducts().toArray());
				int[] updateCounts = template.batchUpdate(sql_product, batch);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// ---------------------------------------------------------------
	// find certificate by query template
	// ---------------------------------------------------------------
	public List<Certificate> findByCertificate(Certificate qcert) {
        List<Certificate> certs = null;

		String sql_cert = "SELECT * from cert_view WHERE " 
				+ "datacert = :datacert AND nomercert = :nomercert AND nblanka = :nblanka";

		SqlParameterSource parameters = new BeanPropertySqlParameterSource(qcert);

		try {

			certs = template.query(sql_cert, parameters, new BeanPropertyRowMapper<Certificate>());
			
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
	// Get list of departments
	// ---------------------------------------------------------------
	public List<String> getDepartmentsList() {
    	 String sql = "SELECT name from C_OTD";
		 
 		 return (List<String>) template.getJdbcOperations().queryForList(sql, String.class);		 
	}
}
