package cci.model.owncert;

// *  --------------------------------
// *  Light OwnCertificate presentation
// * ---------------------------------  

public class OwnCertificateHeader {
	private String number;
	private String blanknumber;
	private String datecert;
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getBlanknumber() {
		return blanknumber;
	}
	public void setBlanknumber(String blanknumber) {
		this.blanknumber = blanknumber;
	}
	public String getDatecert() {
		return datecert;
	}
	public void setDatecert(String datecert) {
		this.datecert = datecert;
	}
}
