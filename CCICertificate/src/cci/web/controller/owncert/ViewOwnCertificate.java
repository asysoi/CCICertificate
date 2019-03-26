package cci.web.controller.owncert;

import cci.model.owncert.OwnCertificate;

public class ViewOwnCertificate extends OwnCertificate {
	private String factoryaddress;	
	private String branchname;
	private String branchaddress;
	private String beltppname;
	private String beltppaddress;
	private String productname;
	private String productcode;
	private String viewcode;
	private String datecertfrom;
	private String datecertto;
	private String datestartfrom;
	private String datestartto;
	private String dateexpirefrom;
	private String dateexpireto;
	
	public String getViewcode() {
		return viewcode;
	}
	public void setViewcode(String viewcode) {
		this.viewcode = viewcode;
	}
	public String getDatestartfrom() {
		return datestartfrom;
	}
	public void setDatestartfrom(String datestartfrom) {
		this.datestartfrom = datestartfrom;
	}
	public String getDatestartto() {
		return datestartto;
	}
	public void setDatestartto(String datestartto) {
		this.datestartto = datestartto;
	}
	public String getDateexpirefrom() {
		return dateexpirefrom;
	}
	public void setDateexpirefrom(String dateexpirefrom) {
		this.dateexpirefrom = dateexpirefrom;
	}
	public String getDateexpireto() {
		return dateexpireto;
	}
	public void setDateexpireto(String dateexpireto) {
		this.dateexpireto = dateexpireto;
	}
	
	public String getFactoryaddress() {
		return factoryaddress;
	}
	public void setFactoryaddress(String factoryaddress) {
		this.factoryaddress = factoryaddress;
	}
	public String getBranchname() {
		return branchname;
	}
	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}
	public String getBranchaddress() {
		return branchaddress;
	}
	public void setBranchaddress(String branchaddress) {
		this.branchaddress = branchaddress;
	}
	public String getBeltppname() {
		return beltppname;
	}
	public void setBeltppname(String beltppname) {
		this.beltppname = beltppname;
	}
	public String getBeltppaddress() {
		return beltppaddress;
	}
	public void setBeltppaddress(String beltppaddress) {
		this.beltppaddress = beltppaddress;
	}

	public String getProductname() {
		return productname;
	}
	public void setProductname(String productname) {
		this.productname = productname;
	}
	public String getProductcode() {
		return productcode;
	}
	public void setProductcode(String productcode) {
		this.productcode = productcode;
	}
	public String getDatecertfrom() {
		return datecertfrom;
	}
	public void setDatecertfrom(String datecertfrom) {
		this.datecertfrom = datecertfrom;
	}
	public String getDatecertto() {
		return datecertto;
	}
	public void setDatecertto(String datecertto) {
		this.datecertto = datecertto;
	}
	@Override
	public String toString() {
		return "ViewOwnCertificate [factoryaddress=" + factoryaddress + ", branchname=" + branchname
				+ ", branchaddress=" + branchaddress + ", beltppname=" + beltppname + ", beltppaddress=" + beltppaddress
				+ ", productname=" + productname + ", productcode=" + productcode + ", datecertfrom=" + datecertfrom
				+ ", datecertto=" + datecertto + "]";
	}


}
