package cci.model.cert;

public class Report {
	
	private String field;
	private String value;
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getNormalfield() {
		if (field != null) {
			int pos = field.indexOf("00:00:00.0");
			
			if ( pos != -1 ) {
				return field.substring(0, pos-1);	
			}
		}
		return field; 	
	}

}
