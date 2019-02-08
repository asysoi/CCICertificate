package cci.config.own;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import cci.config.ExportConfig;

public class ExportOwnCertConfig implements ExportConfig {
	
	private String[] headernames = new String[] { "Номер сертификата",	"Номер бланка",	"Производитель",
		"Адрес производителя","Регитсрационный номер производителя","Адрес производства","Обособленные подразделения",
		"Коды дополнительных бланков",	"Действителен с ","Действителен до ","Эксперт","Удостоверен",
		"Должность удостоверителя","Дата сертификата","Дата загрузки",
		"Отделение","Адрес отделения"};

	private String[] fieldnames = new String[] { "number",	"blanknumber",	"customername",
			"customeraddress","customerunp","factories","branches","additionalblanks","datestart",
			"dateexpire","expert","signer","signerjob","datecert","dateload","beltppname",
			"beltppaddress"};
	
	private Map<String, String> headermap = new LinkedHashMap<String, String>();
	private String[] headers = new String[]{};
	private String[] fields = new String[]{};
	
	public ExportOwnCertConfig() {
		 
		for(int i = 0; i < fieldnames.length; i++) {
			headermap.put(fieldnames[i], headernames[i]);
		}
	}

	public String[] getHeadernames() {
		return headernames;
	}


	public void setHeadernames(String[] headernames) {
		this.headernames = headernames;
	}


	public String[] getFieldnames() {
		return fieldnames;
	}


	public void setFieldnames(String[] fieldnames) {
		this.fieldnames = fieldnames;
	}


	public Map<String, String> getHeadermap() {
		return headermap;
	}


	public void setHeadermap(Map<String, String> headermap) {
		this.headermap = headermap;
	}


	public String[] getHeaders() {
		return headers;
	}


	public void setHeaders(String[] headers) {
		this.headers = headers;
	}


	public String[] getFields() {
		return fields;
	}


	public void setFields(String[] fields) {
		this.fields = fields;
		List<String> list = new ArrayList<String>();
		
		for (String key: fields) {
			list.add(headermap.get(key));
		}
		
		this.setHeaders((String[]) list.toArray(new String[list.size()]));
	}

	public String[] addField(String field) {
	      if (! ArrayUtils.contains(fields, field)) {
	    	  fields = (String[]) ArrayUtils.add(fields, field);
	      }
	      return fields;
	}

	public String[] addHeader(String header) {
	      if (! ArrayUtils.contains(headers, header)) {
	    	  headers = (String[]) ArrayUtils.add(headers, header);
	      }
	      return headers;
	}

	public String[] removeField(String field) {
	      if (ArrayUtils.contains(fields, field)) {
	    	  fields = (String[]) ArrayUtils.removeElement(fields, field);
	      }
	      return fields;
		
	}

	public String[] removeHeader(String header) {
	      if (ArrayUtils.contains(headers, header)) {
	    	  headers = (String[]) ArrayUtils.removeElement(headers, header);
	      }
	      return headers;
	}
}
