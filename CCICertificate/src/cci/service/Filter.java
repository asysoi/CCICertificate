package cci.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cci.web.controller.fscert.FSCertificateController;

public abstract class Filter {
	
	private static final Logger LOG=Logger.getLogger(Filter.class);
	
	private Map<String, FilterCondition> conditions;
	private Boolean onfilter;

	public Map<String, FilterCondition> getConditions() {
		return conditions;
	}

	public void setFilters(Map<String, FilterCondition> conditions) {
		this.conditions = conditions;
	}

	public Boolean getOnfilter() {
		return onfilter;
	}

	public void setOnfilter(Boolean onfilter) {
		this.onfilter = onfilter;
	}

	public void init(String[] fields, String[] dbfields, FieldType[] types) {
		
        for(int i = 0; i < fields.length; i++) {
      	  this.setConditionValue(fields[i], dbfields[i], "", "", types[i]);
        }
	}

	public void setConditionValue(String field, String dbfield, String operator, String value, FieldType type) {
		if (conditions == null) {
			conditions = new HashMap<String, FilterCondition>();
		}
        String upkey = field.toUpperCase();
        
		if (conditions.containsKey(upkey)) {
			conditions.get(upkey).setOperator(operator);
			conditions.get(upkey).setValue(value);
		} else {
			FilterCondition filter = new FilterCondition();
			filter.setField(field);
			filter.setOperator(operator);
			filter.setValue(value);
			filter.setDbfield(dbfield);
			filter.setType(type);
			conditions.put(upkey, filter);
		}
	}

	@Override
	public String toString() {
		String ret = "";
		
		for (String field : getConditions().keySet()) {
            ret += "\n";  
            ret += getConditions().get(field).toString();	  
        }
		return ret;
	}

    protected Method getMethod(Object obj, String name, Class[] params) {
		Method m = null;
		try {
			 try {
			    m = obj.getClass().getMethod(name, params);
			 } catch (Exception ex) {
				// LOG.info("Error get method [" + name + "] for " + obj.getClass().getName() + ": " +  ex.getMessage() ); 
				m = obj.getClass().getSuperclass().getMethod(name, params); 
			 }
			 
		} catch (Exception ex) {
			LOG.info("Error get method: " + ex.getMessage());
		}
         
		return m;
	}

    
    protected String convertFieldNameToGetter(String field) {
		return "get" + field.substring(0, 1).toUpperCase() + field.substring(1).toLowerCase();
	}


    protected String convertFieldNameToSetter(String field) {
		return "set" + field.substring(0, 1).toUpperCase() + field.substring(1).toLowerCase();
	}
	
	
	
}
