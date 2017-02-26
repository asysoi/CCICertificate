package cci.web.controller.fscert;

import cci.model.fscert.FSCertificate;

public class ViewFSCertificate extends FSCertificate {
	
    private String branchname;
    private String branchaddress;
    private String exportername;
    private String exporteraddress;
    private String producername;
    private String produceraddress;
    private String expertname;
    private String signername;
    private String job;
    private String productname;
    private String blanknumber;
    private String datecertfrom;
    private String datecertto;
    private String countfrom;
    private String countto;
    private String str_otd_id;
    private String tovars;
    private String blanknumbers;
        
    public String getBlanknumbers() {
		return blanknumbers;
	}
	public void setBlanknumbers(String blanknumbers) {
		this.blanknumbers = blanknumbers;
	}
	public String getTovars() {
		return tovars;
	}
	public void setTovars(String tovars) {
		this.tovars = tovars;
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
	public String getExportername() {
		return exportername;
	}
	public void setExportername(String exportername) {
		this.exportername = exportername;
	}
	public String getExporteraddress() {
		return exporteraddress;
	}
	public void setExporteraddress(String exporteraddress) {
		this.exporteraddress = exporteraddress;
	}
	public String getProducername() {
		return producername;
	}
	public void setProducername(String producername) {
		this.producername = producername;
	}
	public String getProduceraddress() {
		return produceraddress;
	}
	public void setProduceraddress(String produceraddress) {
		this.produceraddress = produceraddress;
	}
	public String getExpertname() {
		return expertname;
	}
	public void setExpertname(String expertname) {
		this.expertname = expertname;
	}
	public String getSignername() {
		return signername;
	}
	public void setSignername(String signername) {
		this.signername = signername;
	}
	public String getProductname() {
		return productname;
	}
	public void setProductname(String productname) {
		this.productname = productname;
	}
	public String getBlanknumber() {
		return blanknumber;
	}
	public void setBlanknumber(String blanknumber) {
		this.blanknumber = blanknumber;
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
	public String getCountfrom() {
		return countfrom;
	}
	public void setCountfrom(String countfrom) {
		this.countfrom = countfrom;
	}
	public String getCountto() {
		return countto;
	}
	public void setCountto(String countto) {
		this.countto = countto;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getStr_otd_id() {
		return str_otd_id;
	}
	public void setStr_otd_id(String str_otd_id) {
		this.str_otd_id = str_otd_id;
	}
}