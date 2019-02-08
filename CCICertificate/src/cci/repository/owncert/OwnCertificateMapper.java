package cci.repository.owncert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import cci.model.owncert.Branch;
import cci.model.owncert.Company;
import cci.model.owncert.Factory;
import cci.model.owncert.OwnCertificate;


public class OwnCertificateMapper<T> implements RowMapper<OwnCertificate> {
	 Company beltpp;
   	 
 	 private static final Logger LOG = Logger
			.getLogger(JDBCOwnCertificateDAO.class);
   	 	
	 public OwnCertificate mapRow(ResultSet resultSet, int row) throws SQLException {
		 OwnCertificate cert = new OwnCertificate();
		 
		 try {
		       
		    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		    SimpleDateFormat time = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		    
		    cert.setId(resultSet.getInt("id")); 
		    cert.setId_beltpp(resultSet.getInt("id_beltpp")); 
		    cert.setType(resultSet.getString("type")); 
			cert.setNumber(resultSet.getString("number")); 
			cert.setBlanknumber(resultSet.getString("blanknumber"));
			cert.setCustomername(resultSet.getString("customername"));
			cert.setCustomeraddress(resultSet.getString("customeraddress"));
			cert.setCustomerunp(resultSet.getString("customerunp"));
			cert.setAdditionalblanks(resultSet.getString("additionalblanks"));
			cert.setDatestart(formatter.format(resultSet.getDate("datestart")));
			cert.setDateexpire(formatter.format(resultSet.getDate("dateexpire")));
			cert.setExpert(resultSet.getString("expert"));
			cert.setSigner(resultSet.getString("signer"));
			cert.setSignerjob(resultSet.getString("signerjob"));
			cert.setDatecert(formatter.format(resultSet.getDate("datecert")));
			cert.setDateload(time.format(resultSet.getTimestamp("dateload")));
			
			beltpp = new Company();
			beltpp.setName(resultSet.getString("beltppname")); 
			beltpp.setAddress(resultSet.getString("beltppaddress")); 
			beltpp.setUnp(resultSet.getString("beltppunp")); 
			beltpp.setPhone(resultSet.getString("beltppphone"));
			cert.setBeltpp(beltpp);
			
			Factory factory = new Factory();
			factory.setAddress(resultSet.getString("factories"));
			List<Factory> factories = new ArrayList<Factory>();
			factories.add(factory);
			cert.setFactories(factories);
			
			Branch branch = new Branch();
			branch.setName(resultSet.getString("branches"));
			List<Branch> branches = new ArrayList<Branch>();
			branches.add(branch);
			cert.setBranches(branches);
			
		 } catch (Exception ex ) {
		    throw new SQLException ("Mapping certificate error: " + ex.getMessage());  	 
		 }
			
	    return cert;
	  }
}


