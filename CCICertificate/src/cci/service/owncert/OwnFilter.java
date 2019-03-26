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
	private ViewOwnCertificateCondition condition;
   	private ViewOwnCertificate viewcertificate;
   	
	public OwnFilter() { 
		// "ID","ID_BELTPP",
		String[] fields = new String[] { "TYPE", "NUMBER", "BLANKNUMBER", "CUSTOMERUNP",
				"CUSTOMERNAME","CUSTOMERADDRESS", "CUSTOMERUNP", "FACTORYADDRESS", "BRANCHNAME", "BRANCHADDRESS", 
				"ADDITIONALBLANKS", "DATESTART", "DATEEXPIRE","EXPERT","SIGNER", "SIGNERJOB", 
				"DATECERT", "DATELOAD", "BELTPPNAME", "BELTPPADDRESS", 
				"PRODUCTNAME", "PRODUCTCODE", "DATESTARTFROM", "DATESTARTTO", "DATEEXPIREFROM", "DATEEXPIRETO",
				"DATECERTFROM", "DATECERTTO", "OTD_ID",};
		
		// "ID","ID_BELTPP",
		String[] dbfields = new String[] { "TYPE","NUMBER","BLANKNUMBER", "CUSTOMERUNP",
				"CUSTOMERNAME","CUSTOMERADDRESS", "CUSTOMERUNP","FACTORIES","BRANCHES", "BRANCHES", 
				"ADDITIONALBLANKS","DATESTART","DATEEXPIRE","EXPERT","SIGNER", "SIGNERJOB", 
				"DATECERT","DATELOAD","BELTPPNAME","BELTPPADDRESS",
				"PRODUCTS", "CODES", "DATESTART", "DATESTART", "DATEEXPIRE", "DATEEXPIRE", 
				"DATECERT", "DATECERT", "OTD_ID"};
		
		// FieldType.ID, FieldType.ID, 
		FieldType[] types = new FieldType[] {
				FieldType.STRING,FieldType.STRING,FieldType.STRING, FieldType.STRING,
				FieldType.STRING, FieldType.STRING, FieldType.STRING, FieldType.STRING, FieldType.STRING, FieldType.STRING, 
				FieldType.STRING, FieldType.DATE, FieldType.DATE, FieldType.STRING, FieldType.STRING, FieldType.STRING,
				FieldType.DATE, FieldType.DATE, FieldType.STRING,FieldType.STRING,  
				FieldType.STRING, FieldType.STRING, FieldType.DATE, FieldType.DATE, FieldType.DATE, FieldType.DATE,
				FieldType.DATE, FieldType.DATE, FieldType.STRING };
		
		this.init(fields, dbfields, types);
   		condition = new ViewOwnCertificateCondition();
   	   	viewcertificate = new ViewOwnCertificate();

	}
		
	// -------------------------------------------------------------
	//  Return result of conversation list of conditions 
	//  to ViewCertificate in order using for View goals  
	// -------------------------------------------------------------
	public ViewOwnCertificate getViewcertificate() {
		
		if (viewcertificate == null) {
			viewcertificate = new ViewOwnCertificate();

			for (String field : getConditions().keySet()) {
				FilterCondition fcond = getConditions().get(field);
				String setter = convertFieldNameToSetter(field);

				if (fcond != null) {
					try {
						Method m = getMethod(viewcertificate, setter,
								new Class[] { fcond.getType() == FieldType.NUMBER ? Integer.class : String.class });
						if (m != null) {
							m.invoke(viewcertificate, new Object[] { fcond.getValue() });
						}
					} catch (Exception ex) {
						LOG.info("Error get view own certificate." + ex.getMessage());
					}
				}
			}
		}
		return viewcertificate;
	}
	

	// -----------------------------------------------------------------------
    //  Return list of condition operators filled in ViewCertCondition   
	// -----------------------------------------------------------------------
	public ViewOwnCertificateCondition getCondition() {
		
		if (condition == null) {
			condition = new ViewOwnCertificateCondition();

			for (String field : getConditions().keySet()) {
				FilterCondition fcond = getConditions().get(field);
				String setter = convertFieldNameToSetter(field);

				if (fcond != null) {
					try {
						Method m = getMethod(condition, setter,
								new Class[] { fcond.getType() == FieldType.NUMBER ? Integer.class : String.class });
						if (m != null) {
							m.invoke(condition, new Object[] { fcond.getOperator() });
						}
					} catch (Exception ex) {
						LOG.info("Error get own view condition." + ex.getMessage());
					}
				}
			}
		}
		return condition;
	}
	
	
	// -------------------------------------------------------------------------------------------
	// Convert viewCertificate into list of conditions by setting FilterCondition value property 
	// -------------------------------------------------------------------------------------------
	public void loadViewcertificate (ViewOwnCertificate cert) {
		if (cert != null) {
            viewcertificate = cert;
		}
        
		for (String field : getConditions().keySet()) {
			FilterCondition fcond = getConditions().get(field);
			
			String getter = convertFieldNameToGetter(field);
			
			if (fcond != null) {
				try {
					Method m = getMethod(viewcertificate, getter, new Class[] {});
					if (m != null) {
						fcond.setValue((String) m.invoke(viewcertificate, new Object[]{}));
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
	public void loadCondition(ViewOwnCertificateCondition cond) {

		if (cond != null) {
			condition = cond;
		}
		for (String field : getConditions().keySet()) {
			FilterCondition fcond = getConditions().get(field);
			String getter = convertFieldNameToGetter(field);

			if (fcond != null) {
				try {
					Method m = getMethod(condition, getter, new Class[] {});
					if (m != null) {
						fcond.setOperator((String) m.invoke(condition, new Object[] {}));
					}
				} catch (Exception ex) {
					LOG.info("Error view own condition load." + ex.getMessage());
				}
			}
		}
	}

}
