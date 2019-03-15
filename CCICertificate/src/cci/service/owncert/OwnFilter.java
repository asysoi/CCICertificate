package cci.service.owncert;

import java.lang.reflect.Method;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import cci.service.FieldType;
import cci.service.Filter;
import cci.service.FilterCondition;
import cci.web.controller.owncert.ViewOwnCertificate;
import cci.web.controller.owncert.ViewOwnCertificateCondition;

@Component
@Scope("session")
public class OwnFilter extends Filter {
	
	public static Logger LOG=Logger.getLogger(OwnFilter.class);
	
	public OwnFilter() { 
		String[] fields = new String[] {"ID","ID_BELTPP", "TYPE", "NUMBER", "BLANKNUMBER",
				"CUSTOMERNAME","CUSTOMERADDRESS", "CUSTOMERUNP", "FACTORYADDRESS", "BRANCHNAME", "BRANCHADDRESS", 
				"ADDITIONALBLANKS", "DATESTART", "DATEEXPIRE","EXPERT","SIGNER", "SIGNERJOB", 
				"DATECERT", "DATELOAD", "BELTPPNAME", "BELTPPADDRESS", 
				"PRODUCTNAME", "PRODUCTCODE", "DATESTARTFROM", "DATESTARTTO", "DATEEXPIREFROM", "DATEEXPIRETO",
				"DATECERTFROM", "DATECERTTO", "OTD_ID",};
		
		String[] dbfields = new String[] {"ID","ID_BELTPP", "TYPE","NUMBER","BLANKNUMBER",
				"CUSTOMERNAME","CUSTOMERADDRESS", "CUSTOMERUNP","FACTORIES","BRANCHES", "BRANCHES", 
				"ADDITIONALBLANKS","DATESTART","DATEEXPIRE","EXPERT","SIGNER", "SIGNERJOB", 
				"DATECERT","DATELOAD","BELTPPNAME","BELTPPADDRESS",
				"PRODUCTS", "PRODUCTS", "DATESTART", "DATESTART", "DATEEXPIRE", "DATEEXPIRE", 
				"DATECERT", "DATECERT", "OTD_ID"};
		
		FieldType[] types = new FieldType[] {
				FieldType.ID, FieldType.ID, FieldType.STRING,FieldType.STRING,FieldType.STRING,
				FieldType.STRING, FieldType.STRING, FieldType.STRING, FieldType.STRING, FieldType.STRING, FieldType.STRING, 
				FieldType.STRING, FieldType.DATE, FieldType.DATE, FieldType.STRING, FieldType.STRING, FieldType.STRING,
				FieldType.DATE, FieldType.DATE, FieldType.STRING,FieldType.STRING,  
				FieldType.STRING, FieldType.STRING, FieldType.DATE, FieldType.DATE, FieldType.DATE, FieldType.DATE,
				FieldType.DATE, FieldType.DATE, FieldType.NUMBER };
		
		this.init(fields, dbfields, types);
	}
		
	// -------------------------------------------------------------
	//  Return result of conversation list of conditions 
	//  to ViewCertificate in order using for View goals  
	// -------------------------------------------------------------
	public ViewOwnCertificate getViewcertificate() {
		ViewOwnCertificate cert = new ViewOwnCertificate();
		
		for (String field : getConditions().keySet()) {
			FilterCondition fcond = getConditions().get(field);
			String setter = convertFieldNameToSetter(field);
			
			if (fcond != null) {
				try {
					Method m = getMethod(cert, setter, new Class[] {fcond.getType() == FieldType.NUMBER ? Integer.class :  String.class});
					if (m != null) {
					    m.invoke(cert, new Object[]{fcond.getValue()});
					}
				} catch (Exception ex) {
					LOG.info("Error get view own certificate." + ex.getMessage());
				}
			}
		}
		return cert;
	}
	

	// -----------------------------------------------------------------------
    //  Return list of condition operators filled in ViewCertCondition   
	// -----------------------------------------------------------------------
	public ViewOwnCertificateCondition getCondition() {
		ViewOwnCertificateCondition cond = new ViewOwnCertificateCondition();
		
		for (String field : getConditions().keySet()) {
			FilterCondition fcond = getConditions().get(field);
			String setter = convertFieldNameToSetter(field);
			
			if (fcond != null) {
				try {
					Method m = getMethod(cond, setter, new Class[] {fcond.getType() == FieldType.NUMBER ? Integer.class :  String.class});
					if (m != null) {
					    m.invoke(cond, new Object[]{fcond.getOperator()});
					}
				} catch (Exception ex) {
    				LOG.info("Error get own view condition." + ex.getMessage());
				}
			}
		}
		return cond;
	}
	
	
	// -------------------------------------------------------------------------------------------
	// Convert viewCertificate into list of conditions by setting FilterCondition value property 
	// -------------------------------------------------------------------------------------------
	public void loadViewcertificate (ViewOwnCertificate cert) {
	
		for (String field : getConditions().keySet()) {
			FilterCondition fcond = getConditions().get(field);
			
			String getter = convertFieldNameToGetter(field);
			
			if (fcond != null) {
				try {
					Method m = getMethod(cert, getter, new Class[] {});
					if (m != null) {
						fcond.setValue((String) m.invoke(cert, new Object[]{}));
					}
				} catch (Exception ex) {
					LOG.info("Error view own certificate load." + ex.getMessage());
				}
			}
		}
	}

	
	// ------------------------------------------------------------
	// Set operators of conditions into storage variable of filter  
	// ------------------------------------------------------------
    public void loadCondition (ViewOwnCertificateCondition cond) {
    	
		for (String field : getConditions().keySet()) {
			FilterCondition fcond = getConditions().get(field);
			String getter = convertFieldNameToGetter(field);
			
			if (fcond != null) {
				try {
					Method m = getMethod(cond, getter, new Class[] {});
					if (m != null) {
						fcond.setOperator((String) m.invoke(cond, new Object[]{}));
					}
				} catch (Exception ex) {
					LOG.info("Error view own condition load." + ex.getMessage());
				}
			}
		}
	}
}
