package cci.repository;

import cci.service.Filter;
import cci.service.SQLQueryUnit;

public abstract class SQLBuilder {
	   private Filter filter = null;
	   
       public String getWhereClause() {
		   return null;
       };
       
       public SQLQueryUnit getSQLUnitWhereClause() {
           return null;
       }

       public void setFilter(Filter filter) {
    	   this.filter = filter;
       }

       public Filter getFilter() {
    	   return filter;
       }
}
