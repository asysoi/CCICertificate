package cci.web.controller.owncert;

public class ViewWasteOwnCertificate {
	private String productcode;
	private String customername;
	private String customerunp;
	private String customeraddress;
	private String number;
	private String datecert;
	private String datestart;
	private String dateexpire;
	private String products = "";
		
	public String getProducts() {
		return products;
	}

	public void setProducts(String products) {
		this.products = products;
	}

	public String getCustomerunp() {
		return customerunp;
	}

	public void setCustomerunp(String customerunp) {
		this.customerunp = customerunp;
	}

	public String getCustomeraddress() {
		return customeraddress;
	}

	public void setCustomeraddress(String customeraddress) {
		this.customeraddress = customeraddress;
	}

	public String getProductcode() {
		return productcode;
	}

	public void setProductcode(String productcode) {
		this.productcode = productcode;
	}

	public String getCustomername() {
		return customername;
	}

	public void setCustomername(String customername) {
		this.customername = customername;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDatecert() {
		return datecert;
	}

	public void setDatecert(String datecert) {
		this.datecert = datecert;
	}

	public String getDatestart() {
		return datestart;
	}

	public void setDatestart(String datestart) {
		this.datestart = datestart;
	}

	public String getDateexpire() {
		return dateexpire;
	}

	public void setDateexpire(String dateexpire) {
		this.dateexpire = dateexpire;
	}
}
