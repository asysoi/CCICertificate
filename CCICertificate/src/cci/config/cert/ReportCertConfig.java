package cci.config.cert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportCertConfig {
	private String[] headernames = new String[] { "��������� ������", "���� �����������", "����� �����������", "�������",
			"������", "���������� ���.������",
			"��� ���������������",	"����������������.������ ������������", "����������������.������� ������������",
			"����� ����������������", "���������������", "����� ���������������",
			"���������", "�������� ����������", "�������",
			"��� ��������� �������", "������ ������", "������ ��������������",
			"������ �������������", "��� ����������",
			"���������. ������ ������������",
			"���������. ����������� ������������",
			"����� ����������", "��������",
			"����� ���������"};

	private String[] fieldnames = new String[] {"OTD_NAME", "ISSUEDATE", "FORMS", "EXPERT",
			"STATUS","KOLDOPLIST",
			"UNN", "KONTRP", "KONTRS", "ADRESS", "POLUCHAT", "ADRESSPOL", 
			"RUKOVOD", "TRANSPORT",	"MARSHRUT", "OTMETKA", "CODESTRANAV", 
			"CODESTRANAPR", "CODESTRANAP", "UNNEXP", "EXPP", "EXPS",
			"EXPADRESS","IMPORTER", "ADRESSIMP"};
	
	private Map<String, String> headermap = new LinkedHashMap<String, String>();
	
	private String[] headers = new String[]{};
	private String[] fields = new String[]{};
	
	public ReportCertConfig() {
		
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
}
