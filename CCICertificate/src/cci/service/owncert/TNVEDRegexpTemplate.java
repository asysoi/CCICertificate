package cci.service.owncert;

public class TNVEDRegexpTemplate {
       private String tnved;
       private String extnved;
       private String template_frst;
       private String template_scnd;
       private String template_thrd;
       private String extemplate_frst;
       private String extemplate_scnd;
       private String extemplate_thrd;
       private String min;
       private String max;
       private ExpRange exprange = null;
       
	public ExpRange getExprange() {
		return exprange;
	}
	public void setExprange(ExpRange exprange) {
		this.exprange = exprange;
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public String getMax() {
		return max;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getTnved() {
		return tnved;
	}
	public void setTnved(String tnved) {
		this.tnved = tnved;
	}
	public String getExtnved() {
		return extnved;
	}
	public void setExtnved(String extnved) {
		this.extnved = extnved;
	}
	public String getTemplate_frst() {
		return template_frst;
	}
	public void setTemplate_frst(String template_frst) {
		this.template_frst = template_frst;
	}
	public String getTemplate_scnd() {
		return template_scnd;
	}
	public void setTemplate_scnd(String template_scnd) {
		this.template_scnd = template_scnd;
	}
	public String getTemplate_thrd() {
		return template_thrd;
	}
	public void setTemplate_thrd(String template_thrd) {
		this.template_thrd = template_thrd;
	}
	public String getExtemplate_frst() {
		return extemplate_frst;
	}
	public void setExtemplate_frst(String extemplate_frst) {
		this.extemplate_frst = extemplate_frst;
	}
	public String getExtemplate_scnd() {
		return extemplate_scnd;
	}
	public void setExtemplate_scnd(String extemplate_scnd) {
		this.extemplate_scnd = extemplate_scnd;
	}
	public String getExtemplate_thrd() {
		return extemplate_thrd;
	}
	public void setExtemplate_thrd(String extemplate_thrd) {
		this.extemplate_thrd = extemplate_thrd;
	}
       
}
