package cci.web.controller.owncert;

public class ViewOwnCertificateFilter {
	private ViewOwnCertificateCondition condition;
   	private ViewOwnCertificate viewcertificate;
   
	public ViewOwnCertificateCondition getCondition() {
		return condition;
	}
	public void setCondition(ViewOwnCertificateCondition condition) {
		this.condition = condition;
	}

	public ViewOwnCertificate getViewcertificate() {
		return viewcertificate;
	}
	public void setViewcertificate(ViewOwnCertificate viewcertificate) {
		this.viewcertificate = viewcertificate;
	}
		
	public ViewOwnCertificateFilter() {
		super();		
	}

	public ViewOwnCertificateFilter(ViewOwnCertificate viewcertificate, ViewOwnCertificateCondition condition) {
		super();
		this.condition = condition;
		this.viewcertificate = viewcertificate;
	}
	
}
