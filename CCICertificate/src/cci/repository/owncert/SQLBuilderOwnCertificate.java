package cci.repository.owncert;

import org.apache.log4j.Logger;

import cci.repository.SQLBuilder;
import cci.service.FilterCondition;
import cci.service.SQLQueryUnit;

public class SQLBuilderOwnCertificate extends SQLBuilder {
	private static final Logger LOG = Logger.getLogger(SQLBuilderOwnCertificate.class);
	
	public SQLQueryUnit getSQLUnitWhereClause() {
		SQLQueryUnit qunit = new SQLQueryUnit();
		String where = "";

		if (getFilter() != null) {
            
			for (String key : getFilter().getConditions().keySet()) {
				FilterCondition condition = getFilter().getConditions().get(key);
				condition.setDateconvertfunction("STR_TO_DATE");
				condition.setDateconvertformat("'%d.%m.%Y'");
				
				if (condition.getValue() != null && condition.getOperator() != null 
						&& !condition.getValue().trim().isEmpty() && !condition.getOperator().trim().isEmpty()) {
					
					SQLQueryUnit unit = condition.getWhereClause(); 
					if (key.equals("PRODUCTNAME") || key.equals("PRODUCTCODE") ) {
					     where += (where.trim().isEmpty() ? "" : " AND ") + 
									"id in ( SELECT id_certificate FROM ownproduct where " +
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
		LOG.debug("Where: " + where);
		LOG.debug("Params: " + qunit.getParams());
		qunit.setClause(where);
		return qunit;
	}
}