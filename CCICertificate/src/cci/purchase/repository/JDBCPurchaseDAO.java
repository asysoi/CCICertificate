package cci.purchase.repository;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import cci.cert.model.Certificate;
import cci.purchase.model.Product;
import cci.purchase.model.Company;
import cci.purchase.model.Purchase;
import cci.purchase.service.Filter;
import cci.purchase.web.controller.PurchaseView;

public class JDBCPurchaseDAO implements PurchaseDAO { 
   
	
	private NamedParameterJdbcTemplate template;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.template = new NamedParameterJdbcTemplate(dataSource);
	}

	public List<Product> findProducts() {
		String sql = "select * from PCH_PRODUCT ORDER BY id";

		return this.template.getJdbcOperations().query(sql,
				new BeanPropertyRowMapper<Product>(Product.class));
	}

	public List<Company> findDepartments() {
		String sql = "select * from C_OTD ORDER BY id";

		return this.template.getJdbcOperations().query(sql,
				new BeanPropertyRowMapper<Company>(Company.class));
	}

	public List<Company> findCompanies() {
		String sql = "select * from PCH_COMPANY ORDER BY id";

		return this.template.getJdbcOperations().query(sql,
				new BeanPropertyRowMapper<Company>(Company.class));
	}

	public List<Purchase> findNextPage(int page, int pagesize) {
		String sql = " SELECT purchase.* " + 
			         " FROM (SELECT t.*, ROW_NUMBER() OVER (ORDER BY t.PCHDATE) rw FROM PCH_PURCHASE t) purchase " + 
			         " WHERE purchase.rw > "  + ((page - 1) *  pagesize) +
	                 " AND purchase.rw <= " + (page *  pagesize);
	
		System.out.println("SQL get next    page : " + sql);
		return this.template.getJdbcOperations().query(sql,
				new BeanPropertyRowMapper<Purchase>(Purchase.class));
	}
	
	public List<PurchaseView> findViewNextPage(int page, int pagesize, String orderby, String order, Filter filter) {
		
		String sql = " SELECT purchase.* " + 
			         " FROM (SELECT t.*, ROW_NUMBER() OVER " +
				     " (ORDER BY t." + orderby + " " + order + ") rw " +
				     " FROM PCH_PURCHASE_VIEW t " +  filter.makeWhereFilter() + " )" +
				     " purchase " + 
			         " WHERE purchase.rw > "  + ((page - 1) *  pagesize) +
	                 " AND purchase.rw <= " + (page *  pagesize);
	
		System.out.println("SQL get next    page : " + sql);
		return this.template.getJdbcOperations().query(sql,
				new BeanPropertyRowMapper<PurchaseView>(PurchaseView.class));
	}
	
	public int getViewPageCount(Filter filter) {
		String sql = "SELECT count(*) FROM PCH_PURCHASE_VIEW " + filter.makeWhereFilter(); 
		
	    return this.template.getJdbcOperations().queryForInt(sql);
	}
		

	public Purchase findPurchaseByID(long id) {
		Purchase item = null;

		try {
			String sql = "select * from PCH_PURCHASE WHERE id = ?";
			item = template.getJdbcOperations().queryForObject(sql,
					new Object[] { id },
					new BeanPropertyRowMapper<Purchase>(Purchase.class));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return item;
	}

	public PurchaseView findPurchaseViewByID(long id) {
		PurchaseView item = null;

		try {
			String sql = "select * from PCH_PURCHASE_VIEW WHERE id = ?";
			item = template.getJdbcOperations().queryForObject(sql,
					new Object[] { id },
					new BeanPropertyRowMapper<PurchaseView>(PurchaseView.class));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return item;
	}

	
	public void savePurchase(Purchase purchase) {
		String sql = "insert into pch_purchase (id,id_product,id_otd,id_company,price,volume,unit, pchdate, productproperty) values "
				+ "(beltpp.id_purchase_seq.nextval, "
				+ ":id_product, :id_otd, :id_company, :price, :volume, "
				+ ":unit, :pchDate, :productProperty)";
		
		SqlParameterSource parameters = new BeanPropertySqlParameterSource(purchase);
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		int idpurchase = 0;

		try {
			int row = template.update(sql, parameters, keyHolder,
					new String[] { "ID" });
			idpurchase = keyHolder.getKey().intValue();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
      
	@Override
	public void updatePurchase(Purchase purchase) {
		// TODO Auto-generated method stub
	}

	@Override
	public void saveProduct(Product product) {
		// TODO Auto-generated method stub
	}

	@Override
	public void saveCompany(Company company) {
		// TODO Auto-generated method stub
	}

}