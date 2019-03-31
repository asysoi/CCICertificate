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

		if ("�/�".equals(cert.getType())) {
			ret = "��������� ������������ ������������";
		} else if ("�/�".equals(cert.getType())) {
			ret = "����� � ����� ������������ ������������";
		} else if ("�/�".equals(cert.getType())) {
			ret = "����� ������������ ������������";
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
		if ("�/�".equals(cert.getType())) {
			ret = "2. ��������������� ����� ������������� � ������ ��������������� �������� ����������� ��� � ��������������<br>����������������:";
		} else if ("�/�".equals(cert.getType())) {
			ret = "2. ��������������� ����� ������������� � ������ ��������������� �������� ����������� ��� � ��������������<br>����������������:";
		} else if ("�/�".equals(cert.getType())) {
			ret = "2. ��������������� ����� �����, ������������ ��������-����������, ��������� �����������, <br>"
				+ "������������ ��������������� ����������� � ������������� � ������ ��������������� <br>"
				+ "�������� ����������� ��� � ��������������<br>����������������:";
		}
		return ret;		
	}

	public String getPlaceproduction() {
		String ret = "";

		if ("�/�".equals(cert.getType())) {
			ret = "3. ����� ���������� ������������: <b>" + cert.getFactorylist() + "</b>";
		}
		return ret;
	}

	public String getTitlebrancheslist() {
		String ret = "";
		if ("�/�".equals(cert.getType())) {
			ret = "������������ ������������ ������������� ������������ ����, �������������� ������������ ���������,<br>����� ����������:";
		} else if ("�/�".equals(cert.getType())) {
			ret = "������������ ������������ ������������� ������������ ����, ����������� ������, ����������� ������,<br>����� ����������:";
		} else if ("�/�".equals(cert.getType())) {
			ret = "������������ ������������ ������������� �����, ������������ ��������-����������, ��������� �����������, <br>"
					+ "������������ ��������������� ����������� � �������������, ����������� ������,<br>����� ����������:";
		}
		return ret;
	}

	public String getTitleproductslist() {
		String ret = "";
		if ("�/�".equals(cert.getType())) {
			ret = "4. ������������ ���������, ��� ��������� � ������������ � ������ �������� �������������"
					+ "�������������������<br>������������ ����������� �����:";
		} else if ("�/�".equals(cert.getType())) {
			ret = "3. ������������ ����� � �����, ��� ����� � ����� �� �������������� ������������������� ������������";
		} else if ("�/�".equals(cert.getType())) {
			ret = "3. ������������ �����, ��� ������ �� �������������� ������������������� ������������";
		}
		return ret;
	}

	public String getValiddates() {
		String ret = "";
		if ("�/�".equals(cert.getType())) {
			ret = "5.";
		} else {
			ret = "4.";
		}
		ret += "���������� ������������ �  <b>" + cert.getDatestart() + "</b>  ��  <b>" + cert.getDateexpire() + "</b>";
		return ret;
	}

	public String getConfirmation() {
		String ret = "";
		if ("�/�".equals(cert.getType())) {
			ret = "6. �� ��������� ����������� ����������� ���������� ��������� �����������, ��� ���������, "
					+ "���������<br> � ������ 4 ���������� �����������, ��������� � ��������� ������������ ������������";
		} else if ("�/�".equals(cert.getType())) {
			ret = "5. �� ��������� ����������� ����������� ���������� ��������� �����������, ��� ������ � ������, "
					+ "���������<br> � ������ 3 ���������� �����������, �������� �������� � �������� ������������ ������������";
		} else if ("�/�".equals(cert.getType())) {
			ret = "5. �� ��������� ����������� ����������� ���������� ��������� �����������, ��� ������, ���������<br> "
					+ "� ������ 3 ���������� �����������, �������� �������� ������������ ������������";
		}
		return ret;
	}
}
