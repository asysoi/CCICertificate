package cci.repository.cert;

import org.apache.log4j.Logger;

import cci.repository.SQLBuilder;
import cci.service.FilterCondition;
import cci.service.SQLQueryUnit;

public class SQLBuilderCertificate extends SQLBuilder {
	private static final Logger LOG = Logger.getLogger(SQLBuilderCertificate.class);
	
	public SQLQueryUnit getSQLUnitWhereClause() {
		SQLQueryUnit qunit = new SQLQueryUnit();
		String where = "";

		if (getFilter() != null) {
            
			for (String key : getFilter().getConditions().keySet()) {
				FilterCondition condition = getFilter().getConditions().get(key);
				
				if (condition.getValue() != null && condition.getOperator() != null 
						&& !condition.getValue().trim().isEmpty() && !condition.getOperator().trim().isEmpty()) {
					
					SQLQueryUnit unit = condition.getWhereClause(); 
					if (key.equals("TOVAR") || key.equals("KRITER") || key.equals("SCHET")) {
					     where += (where.trim().isEmpty() ? "" : " AND ") + 
									"cert_id in ( SELECT cert_id FROM C_Product_Denorm where " +
									unit.getClause() + 
									") ";
					} else {
				       where += (where.trim().isEmpty() ? "" : " AND ")
					  	         +  unit.getClause();
					}
					qunit.getParams().putAll(unit.getParams());
			    }
			}
		    where = (where.trim().isEmpty() ? "" : " where " + where);
			
		}
		//LOG.info("Where: " + where);
		//LOG.info("Params: " + qunit.getParams());
		qunit.setClause(where);
		return qunit;
	}

}
