package cci.config.own;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

	public class ReportOwnCertConfig {
		private String[] headernames = new String[] {"Отделение БелТПП", "Производитель",
				"УНП производителя", "Адрес производства","Эксперт", "Кем выдан",
				"Должность выдавшего сертификат", "Дата загрузки","Код ТНВЭД ТС"};

		private String[] fieldnames = new String[] {"beltppname", "customername",
				"customerunp","factories","expert","signer",
				"signerjob","dateload","code"};
		
		// SELECT code, COUNT(*) as value FROM (SELECT DISTINCT ID_CERTIFICATE, CODE FROM OWNPRODUCT) own group by code 
		// ORDER BY value DESC;
		
		private Map<String, String> headermap = new LinkedHashMap<String, String>();
		
		private String[] headers = new String[]{};
		private String[] fields = new String[]{};

		/* -------------------------------------------------
		 * 
		 * ------------------------------------------------- */
		public ReportOwnCertConfig() {
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

