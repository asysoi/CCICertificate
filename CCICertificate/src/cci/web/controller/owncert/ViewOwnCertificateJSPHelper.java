package cci.web.controller.owncert;

import cci.model.owncert.OwnCertificate;

/* ---------------------------------
 * Helper class for JSP rendering
 * ---------------------------------*/
public class ViewOwnCertificateJSPHelper {
	private OwnCertificate cert;

	public ViewOwnCertificateJSPHelper(OwnCertificate cert) {
		this.cert = cert;
	}

	public OwnCertificate getCert() {
		return cert;
	}

	public void setCert(OwnCertificate cert) {
		this.cert = cert;
	}

	public String getBeltpp() {
		String beltpp = cert.getBeltpp().getName();
		if (cert.getBeltpp().getAddress() != null && !cert.getBeltpp().getAddress().trim().isEmpty())
			beltpp += ", " + cert.getBeltpp().getAddress();
		if (cert.getBeltpp().getPhone() != null && !cert.getBeltpp().getPhone().trim().isEmpty())
			beltpp += ", " + cert.getBeltpp().getPhone();
		return beltpp;
	}

	public String getCerttype() {
		String ret = "";

		if ("с/п".equals(cert.getType())) {
			ret = "продукции собственного производства";
		} else if ("р/у".equals(cert.getType())) {
			ret = "работ и услуг собственного производства";
		} else if ("б/у".equals(cert.getType())) {
			ret = "услуг собственного производства";
		}
		return ret;
	}

	public String getCustomer() {
		String customer = cert.getCustomername();
		if (cert.getCustomeraddress() != null && !cert.getCustomeraddress().trim().isEmpty())
			customer += ", " + cert.getCustomeraddress();
		return customer;
	}
	
	public String getTitleunp() {
		String ret = "";
		if ("с/п".equals(cert.getType())) {
			ret = "2. Регистрационный номер производителя в Едином государственном регистре юридических лиц и индивидуальных<br>предпринимателей:";
		} else if ("р/у".equals(cert.getType())) {
			ret = "2. Регистрационный номер производителя в Едином государственном регистре юридических лиц и индивидуальных<br>предпринимателей:";
		} else if ("б/у".equals(cert.getType())) {
			ret = "2. Регистрационный номер банка, небанковской кредитно-финансовой, страховой организации, <br>"
				+ "коммерческой микрофинансовой организации и лизингодателя в Едином государственном <br>"
				+ "регистре юридических лиц и индивидуальных<br>предпринимателей:";
		}
		return ret;		
	}

	public String getPlaceproduction() {
		String ret = "";

		if ("с/п".equals(cert.getType())) {
			ret = "3. Место нахождения производства: <b>" + cert.getFactorylist() + "</b>";
		}
		return ret;
	}

	public String getTitlebrancheslist() {
		String ret = "";
		if ("с/п".equals(cert.getType())) {
			ret = "Наименование обособленных подразделений юридического лица, осуществляющих производство продукции,<br>место нахождения:";
		} else if ("р/у".equals(cert.getType())) {
			ret = "Наименование обособленных подразделений юридического лица, выполняющих работы, оказывающие услуги,<br>место нахождения:";
		} else if ("б/у".equals(cert.getType())) {
			ret = "Наименование обособленных подразделений банка, небанковской кредитно-финансовой, страховой организации, <br>"
					+ "коммерческой микрофинансовой организации и лизингодателя, оказывающих услуги,<br>место нахождения:";
		}
		return ret;
	}

	public String getTitleproductslist() {
		String ret = "";
		if ("с/п".equals(cert.getType())) {
			ret = "4. Наименование продукции, код продукции в соответствии с единой Товарной номенклатурой "
				+ "внешнеэкономической деятельности Евразийского экономического союза";
		} else if ("р/у".equals(cert.getType())) {
			ret = "3. Наименование работ и услуг, код работ и услуг (в соответствии с общегосударственным "
					+ "классификатором Республики Беларусь ОКРБ 005-2011 «Виды экономической деятельности»)";
		} else if ("б/у".equals(cert.getType())) {
			ret = "3. Наименование работ и услуг, код работ и услуг (в соответствии с общегосударственным "
					+ "классификатором Республики Беларусь ОКРБ 005-2011 «Виды экономической деятельности»)";
		}
		return ret;
	}

	public String getValiddates() {
		String ret = "";
		if ("с/п".equals(cert.getType())) {
			ret = "5.";
		} else {
			ret = "4.";
		}
		ret += "Сертификат действителен с  <b>" + cert.getDatestart() + "</b>  до  <b>" + cert.getDateexpire() + "</b>";
		return ret;
	}

	public String getConfirmation() {
		String ret = "";
		if ("с/п".equals(cert.getType())) {
			ret = "6. На основании результатов проведенной экспертизы настоящим подтверждаю, что продукция, "
					+ "указанная<br> в пункте 4 настоящего сертификата, относится к продукции собственного производства";
		} else if ("р/у".equals(cert.getType())) {
			ret = "5. На основании результатов проведенной экспертизы настоящим подтверждаю, что работы и услуги, "
					+ "указанные<br> в пункте 3 настоящего сертификата, является работами и услугами собственного производства";
		} else if ("б/у".equals(cert.getType())) {
			ret = "5. На основании результатов проведенной экспертизы настоящим подтверждаю, что услуги, указанные<br> "
					+ "в пункте 3 настоящего сертификата, является услугами собственного производства";
		}
		return ret;
	}
}
