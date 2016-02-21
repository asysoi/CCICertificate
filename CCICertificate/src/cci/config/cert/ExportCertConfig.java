package cci.config.cert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public class ExportCertConfig {
	
	private String[] headernames = new String[] { "����� �����������", "���",
			"����������������.������ ������������", "����������������.������� ������������",
			"����� ����������������", "���������������", "����� ���������������",
			"���� �����������", "����� �����������", "�������", "����� ������",
			"���������", "�������� ����������", "�������",
			"��� ��������� �������", "������ ������", "������ ��������������",
			"������", "���������� ���.������", "��� ����������",
			"���������. ������ ������������",
			"���������. ����������� ������������",
			"����� ����������", "��������",
			"����� ���������", "���� ���", "���", "���� ��������� ���", "������ �������������",
			"��������� ������", "����� ����������� �����������",
			"������ ����������� �����������", "���� �������� �������"};

	private String[] fieldnames = new String[] { "FORMS", "UNN", "KONTRP",
			"KONTRS", "ADRESS", "POLUCHAT", "ADRESSPOL", "DATACERT",
			"NOMERCERT", "EXPERT", "NBLANKA", "RUKOVOD", "TRANSPORT",
			"MARSHRUT", "OTMETKA", "STRANAV", "STRANAPR", "STATUS",
			"KOLDOPLIST", "UNNEXP", "EXPP", "EXPS", "EXPADRESS",
			"IMPORTER", "ADRESSIMP", "FLSEZ", "SEZ", "FLSEZREZ",
			"STRANAP", "OTD_NAME", "PARENTNUMBER", "PARENTSTATUS", "TOVAR"};
	
	private Map<String, String> headermap = new LinkedHashMap<String, String>();
	private String[] headers = new String[]{};
	private String[] fields = new String[]{};
	
	public ExportCertConfig() {
		 
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
