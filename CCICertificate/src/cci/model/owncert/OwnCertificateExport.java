package cci.model.owncert;

public class OwnCertificateExport extends OwnCertificate {

	public String getBeltppname() {
		return getBeltpp().getName();
	}

	public void setBeltppname(String beltppname) {
		getBeltpp().setName(beltppname);;
	}

	public String getBeltppaddress() {
		return getBeltpp().getAddress();
	}

	public void setBeltppaddress(String beltppaddress) {
		getBeltpp().setAddress(beltppaddress);;
	}
}
