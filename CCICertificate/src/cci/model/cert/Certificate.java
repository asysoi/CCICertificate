package cci.model.cert;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;


@XmlRootElement(name = "cert")
@Component
@XmlType(propOrder = {"otd_id", "otd_name", "forms", "unn",  "kontrp",  "kontrs", "adress", "poluchat", "adresspol",  
		"datacert", "dataexpire", "nomercert", "expert", "nblanka", "rukovod", "transport", "marshrut", "otmetka",  
	    "stranav","codestranav", "stranapr","codestranapr", "stranap", "codestranap", "status", "koldoplist", "flexp", 
		"unnexp", "expp", "exps", "expadress", "flimp",  "importer","adressimp","flsez","sez", "flsezrez",  
		"parentnumber", "parentstatus", "category", "date_load", "products", "agree"})

public class Certificate {
	private static final Logger LOG=Logger.getLogger(Certificate.class);
	
	private Long cert_id;
	private String forms;
	private String unn;
	private String kontrp;
	private String kontrs;
	private String adress;
	private String poluchat;
	private String adresspol;
	private String datacert;
	private String issuedate;
	private String dataexpire;
	private String expiredate;
	private String nomercert;
	private String expert;
	private String nblanka;
	private String rukovod;
	private String transport;
	private String marshrut;
	private String otmetka;
	private String stranav;
	private String stranapr;
	private String status;
	private int koldoplist;
	private String flexp;
	private String unnexp;
	private String expp;
	private String exps;
	private String expadress;
	private String flimp;
	private String importer;
	private String adressimp;
	private String flsez;
	private String sez;
	private String flsezrez;
	private String stranap;
	private int otd_id;
	private String otd_name;
	private String otd_addr_index;
	private String otd_addr_city;
	private String otd_addr_line;
	private String otd_addr_building;
	private String parentnumber = "";
	private String parentstatus = "";
	private String tovar;
	private String codestranav;
	private String codestranapr;
	private String codestranap;
	private String category;
	private String eotd_name;
	private String eotd_addr_city;
	private String eotd_addr_line;
	private String agree="N";
	private List<Product> products;
	private String childnumber = "";
	private Integer child_id = 0;
	private Long parent_id;
	private ProductIterator iterator;
	private int cursor;
	private int currentlist;
	private String date_load;
	
	
	@XmlTransient	
	public String getExpiredate() {
		return expiredate;
	}

	public void setExpiredate(String expiredate) {
		this.expiredate = expiredate;
	}

	public String getDataexpire() {
		return dataexpire;
	}

	public void setDataexpire(String dataexpire) {
		this.dataexpire = dataexpire;
	}

	public String getAgree() {
		return agree;
	}

	public void setAgree(String agree) {
		this.agree = "Y".equals(agree) ? "Y" : "N";
	}

	@XmlTransient
	public String getIssuedate() {
		return issuedate;
	}

	public void setIssuedate(String issuedate) {
		this.issuedate = issuedate;
	}
	
	public String getDate_load() {
		if (date_load != null) {
		   int i = date_load.indexOf("00:00:00.0");
		   return i > 0 ? date_load.substring(0, i) : date_load;
		} else {
		   return null;	
		}
	}

	public void setDate_load(String date_load) {
		this.date_load = date_load;
	}

	@XmlTransient
	public int getCurrentlist() {
		return currentlist;
	}

	public void setCurrentlist(int currentlist) {
		this.currentlist = currentlist;
	}

	@XmlTransient
	public String getTovar() {
		return tovar;
	}

	public void setTovar(String tovar) {
		this.tovar = tovar;
	}

	@XmlTransient
	public String getChildnumber() {
		return childnumber;
	}

	public void setChildnumber(String childnumber) {
		this.childnumber = childnumber;
	}

	@XmlTransient 
	public Integer getChild_id() {
		return child_id;
	}

	public void setChild_id(Integer child_id) {
		this.child_id = child_id;
	}

	public String getParentnumber() {
		return parentnumber;
	}

	public void setParentnumber(String parentnumber) {
		this.parentnumber = parentnumber;
	}

	public String getParentstatus() {
		return parentstatus;
	}

	public void setParentstatus(String parentstatus) {
		this.parentstatus = parentstatus;
	}

	@XmlTransient
	public Long getCert_id() {
		return cert_id;
	}

	public void setCert_id(Long cert_id) {
		this.cert_id = cert_id;
	}

	public String getForms() {
		return forms;
	}

	public void setForms(String forms) {
		this.forms = forms;
	}

	public String getUnn() {
		return unn;
	}

	public void setUnn(String unn) {
		this.unn = unn;
	}

	public String getKontrp() {
		return kontrp;
	}

	public void setKontrp(String kontrp) {
		this.kontrp = kontrp;
	}

	public String getKontrs() {
		return kontrs;
	}

	public void setKontrs(String kontrs) {
		this.kontrs = kontrs;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getPoluchat() {
		return poluchat;
	}

	public void setPoluchat(String poluchat) {
		this.poluchat = poluchat;
	}

	public String getAdresspol() {
		return adresspol;
	}

	public void setAdresspol(String adresspol) {
		this.adresspol = adresspol;
	}

	public String getDatacert() {
		return datacert;
	}

	public void setDatacert(String datacert) {
		this.datacert = datacert;
	}

	public String getNomercert() {
		return nomercert;
	}

	public void setNomercert(String nomercert) {
		this.nomercert = nomercert;
	}

	public String getExpert() {
		return expert;
	}

	public void setExpert(String expert) {
		this.expert = expert;
	}

	public String getNblanka() {
		return nblanka;
	}

	public void setNblanka(String nblanka) {
		this.nblanka = nblanka;
	}

	public String getRukovod() {
		return rukovod;
	}

	public void setRukovod(String rukovod) {
		this.rukovod = rukovod;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getMarshrut() {
		return marshrut;
	}

	public void setMarshrut(String marshrut) {
		this.marshrut = marshrut;
	}

	public String getOtmetka() {
		return otmetka;
	}

	public void setOtmetka(String otmetka) {
		this.otmetka = otmetka;
	}

	public String getStranav() {
		return stranav;
	}

	public void setStranav(String stranav) {
		this.stranav = stranav;
	}

	public String getStranapr() {
		return stranapr;
	}

	public void setStranapr(String stranapr) {
		this.stranapr = stranapr;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getKoldoplist() {
		return koldoplist;
	}

	public void setKoldoplist(int koldoplist) {
		this.koldoplist = koldoplist;
	}

	public String getFlexp() {
		return flexp;
	}

	public void setFlexp(String flexp) {
		this.flexp = flexp;
	}

	public String getUnnexp() {
		return unnexp;
	}

	public void setUnnexp(String unnexp) {
		this.unnexp = unnexp;
	}

	public String getExpp() {
		return expp;
	}

	public void setExpp(String expp) {
		this.expp = expp;
	}

	public String getExps() {
		return exps;
	}

	public void setExps(String exps) {
		this.exps = exps;
	}

	public String getExpadress() {
		return expadress;
	}

	public void setExpadress(String expadress) {
		this.expadress = expadress;
	}

	public String getFlimp() {
		return flimp;
	}

	public void setFlimp(String flimp) {
		this.flimp = flimp;
	}

	public String getImporter() {
		return importer;
	}

	public void setImporter(String importer) {
		this.importer = importer;
	}

	public String getAdressimp() {
		return adressimp;
	}

	public void setAdressimp(String adressimp) {
		this.adressimp = adressimp;
	}

	public String getFlsez() {
		return flsez;
	}

	public void setFlsez(String flsez) {
		this.flsez = flsez;
	}

	public String getSez() {
		return sez;
	}

	public void setSez(String sez) {
		this.sez = sez;
	}

	public String getFlsezrez() {
		return flsezrez;
	}

	public void setFlsezrez(String flsezrez) {
		this.flsezrez = flsezrez;
	}

	public String getStranap() {
		return stranap;
	}

	public void setStranap(String stranap) {
		this.stranap = stranap;
	}

	@XmlElementWrapper(name = "products")
	@XmlElement(name = "row")
	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@XmlTransient
	public String getOtd_addr_index() {
		return otd_addr_index;
	}

	public void setOtd_addr_index(String otd_addr_index) {
		this.otd_addr_index = otd_addr_index;
	}

	@XmlTransient
	public String getOtd_addr_city() {
		return otd_addr_city;
	}

	public void setOtd_addr_city(String otd_addr_city) {
		this.otd_addr_city = otd_addr_city;
	}

	@XmlTransient
	public String getOtd_addr_line() {
		return otd_addr_line;
	}

	public void setOtd_addr_line(String otd_addr_line) {
		this.otd_addr_line = otd_addr_line;
	}

	@XmlTransient
	public String getOtd_addr_building() {
		return otd_addr_building;
	}

	public void setOtd_addr_building(String otd_addr_building) {
		this.otd_addr_building = otd_addr_building;
	}

	public String getOtd_name() {
		return otd_name;
	}

	public void setOtd_name(String otd_name) {
		this.otd_name = otd_name;
	}

	@XmlTransient
	public String getShort_kontrp() {
		// return kontrp.length() > 100 ? kontrp.substring(1, 100) + " ..." :
		// kontrp;
		return (kontrp != null && kontrp.length() > 100) ? kontrp.substring(1,
				100) + " ..." : kontrp;
	}
	
	
	@XmlTransient
	public String getEotd_name() {
		return eotd_name;
	}

	public void setEotd_name(String eotd_name) {
		this.eotd_name = eotd_name;
	}

	@XmlTransient
	public String getEotd_addr_city() {
		return eotd_addr_city;
	}

	public void setEotd_addr_city(String eotd_addr_city) {
		this.eotd_addr_city = eotd_addr_city;
	}

	@XmlTransient
	public String getEotd_addr_line() {
		return eotd_addr_line;
	}

	public void setEotd_addr_line(String eotd_addr_line) {
		this.eotd_addr_line = eotd_addr_line;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getTovar());
		builder.append(" ");
		builder.append(getParentnumber());
		builder.append(" ");
		builder.append(getParentstatus());
		builder.append(" ");
		builder.append(getCert_id());
		builder.append(" ");
		builder.append(getForms());
		builder.append(" ");
		builder.append(getUnn());
		builder.append(" ");
		builder.append(getKontrp());
		builder.append(" ");
		builder.append(getKontrs());
		builder.append(" ");
		builder.append(getAdress());
		builder.append(" ");
		builder.append(getPoluchat());
		builder.append(" ");
		builder.append(getAdresspol());
		builder.append(" ");
		builder.append(getDatacert());
		builder.append(" ");
		builder.append(getDataexpire());
		builder.append(" ");
		builder.append(getNomercert());
		builder.append(" ");
		builder.append(getExpert());
		builder.append(" ");
		builder.append(getNblanka());
		builder.append(" ");
		builder.append(getRukovod());
		builder.append(" ");
		builder.append(getTransport());
		builder.append(" ");
		builder.append(getMarshrut());
		builder.append(" ");
		builder.append(getOtmetka());
		builder.append(" ");
		builder.append(getStranav());
		builder.append(" ");
		builder.append(getStranapr());
		builder.append(" ");
		builder.append(getStatus());
		builder.append(" ");
		builder.append(getKoldoplist());
		builder.append(" ");
		builder.append(getFlexp());
		builder.append(" ");
		builder.append(getUnnexp());
		builder.append(" ");
		builder.append(getExpp());
		builder.append(" ");
		builder.append(getExps());
		builder.append(" ");
		builder.append(getExpadress());
		builder.append(" ");
		builder.append(getFlimp());
		builder.append(" ");
		builder.append(getImporter());
		builder.append(" ");
		builder.append(getAdressimp());
		builder.append(" ");
		builder.append(getFlsez());
		builder.append(" ");
		builder.append(getSez());
		builder.append(" ");
		builder.append(getFlsezrez());
		builder.append(" ");
		builder.append(getStranap());
		builder.append(" ");
		builder.append(getProducts());
		builder.append(" ");
		builder.append(getOtd_addr_index());
		builder.append(" ");
		builder.append(getOtd_addr_city());
		builder.append(" ");
		builder.append(getOtd_addr_line());
		builder.append(" ");
		builder.append(getOtd_addr_building());
		builder.append(" ");
		builder.append(getOtd_name());
		builder.append(" ");
		builder.append(getShort_kontrp());
		builder.append(" ");
		builder.append(getEotd_name());
		builder.append(" ");
		builder.append(getEotd_addr_city());
		builder.append(" ");
		builder.append(getEotd_addr_line());
		builder.append(" ");
		builder.append(getOtd_id());
		builder.append(" ");
		builder.append(getParent_id());
		builder.append(" ");
		builder.append(getCodestranav());
		builder.append(" ");
		builder.append(getCodestranapr());
		builder.append(" ");
		builder.append(getCodestranap());
		builder.append(" ");
		builder.append(getCategory());
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adress == null) ? 0 : adress.hashCode());
		result = prime * result
				+ ((adressimp == null) ? 0 : adressimp.hashCode());
		result = prime * result
				+ ((adresspol == null) ? 0 : adresspol.hashCode());
		result = prime * result
				+ ((datacert == null) ? 0 : datacert.hashCode());
		result = prime * result
				+ ((dataexpire == null) ? 0 : dataexpire.hashCode());
		result = prime * result
				+ ((expadress == null) ? 0 : expadress.hashCode());
		result = prime * result + ((expert == null) ? 0 : expert.hashCode());
		result = prime * result + ((expp == null) ? 0 : expp.hashCode());
		result = prime * result + ((exps == null) ? 0 : exps.hashCode());
		result = prime * result + ((flexp == null) ? 0 : flexp.hashCode());
		result = prime * result + ((flimp == null) ? 0 : flimp.hashCode());
		result = prime * result + ((flsez == null) ? 0 : flsez.hashCode());
		result = prime * result
				+ ((flsezrez == null) ? 0 : flsezrez.hashCode());
		result = prime * result + ((forms == null) ? 0 : forms.hashCode());
		result = prime * result
				+ ((importer == null) ? 0 : importer.hashCode());
		result = prime * result + ((kontrp == null) ? 0 : kontrp.hashCode());
		result = prime * result + ((kontrs == null) ? 0 : kontrs.hashCode());
		result = prime * result
				+ ((marshrut == null) ? 0 : marshrut.hashCode());
		result = prime * result + ((nblanka == null) ? 0 : nblanka.hashCode());
		result = prime * result
				+ ((nomercert == null) ? 0 : nomercert.hashCode());
		result = prime * result + otd_id;
		result = prime * result + ((otmetka == null) ? 0 : otmetka.hashCode());
		result = prime * result
				+ ((poluchat == null) ? 0 : poluchat.hashCode());
		result = prime * result + ((rukovod == null) ? 0 : rukovod.hashCode());
		result = prime * result + ((sez == null) ? 0 : sez.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((stranap == null) ? 0 : stranap.hashCode());
		result = prime * result
				+ ((stranapr == null) ? 0 : stranapr.hashCode());
		result = prime * result + ((stranav == null) ? 0 : stranav.hashCode());
		result = prime * result
				+ ((transport == null) ? 0 : transport.hashCode());
		result = prime * result + ((unn == null) ? 0 : unn.hashCode());
		result = prime * result + ((unnexp == null) ? 0 : unnexp.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			System.out.print(" |  " + 1);
			return false;
		}
		if (getClass() != obj.getClass()) {
			System.out.print(" |  " + 2);
			return false;
		}
		Certificate other = (Certificate) obj;
		if (adress == null) {
			if (other.adress != null && other.adress.length() > 0) {
				System.out.print(" |  " + 3);
				return false;
			}
		} else if (!adress.equals(other.adress)) {
			System.out.print(" |  " + 4);
			return false;
		}
		if (adressimp == null) {
			if (other.adressimp != null && other.adressimp.length() > 0) {
				System.out.print(" |  " + 5);
				return false;
			}
		} else if (!adressimp.equals(other.adressimp)) {
			System.out.print(" |  " + 6);
			return false;
		}
		if (adresspol == null) {
			if (other.adresspol != null && other.adresspol.length() > 0) {
				System.out.print(" |  " + 7);
				return false;
			}
		} else if (!adresspol.equals(other.adresspol)) {
			System.out.print(" |  " + 8);
			return false;
		}
		if (datacert == null) {
			if (other.datacert != null && other.datacert.length() > 0) {
				System.out.print(" |  " + 9);
				return false;
			}
		} else if (!datacert.equals(other.datacert)) {
			System.out.print(" |  " + 10);
			return false;
		}
		if (expadress == null) {
			if (other.expadress != null && other.expadress.length() > 0) {
				System.out.print(" |  " + 11);
				return false;
			}
		} else if (!expadress.equals(other.expadress)) {
			System.out.print(" |  " + 12);
			return false;
		}
		if (expert == null) {
			if (other.expert != null && other.expert.length() > 0) {
				System.out.print(" |  " + 13);
				return false;
			}
		} else if (!expert.equals(other.expert)) {
			System.out.print(" |  " + 14);
			return false;
		}
		if (expp == null) {
			if (other.expp != null && other.expp.length() > 0) {
				System.out.print(" |  " + 15);
				return false;
			}
		} else if (!expp.equals(other.expp)) {
			System.out.print(" |  " + 16);
			return false;
		}
		if (exps == null) {
			if (other.exps != null && other.exps.length() > 0) {
				System.out.print(" |  " + 17);
				return false;
			}
		} else if (!exps.equals(other.exps)) {
			System.out.print(" |  " + 18);
			return false;
		}
		if (flexp == null) {
			if (other.flexp != null && other.flexp.length() > 0) {
				System.out.print(" |  " + 19);
				return false;
			}
		} else if (!flexp.equals(other.flexp)) {
			System.out.print(" |  " + 20);
			return false;
		}
		if (flimp == null) {
			if (other.flimp != null && other.flimp.length() > 0) {
				System.out.print(" |  " + 21);
				return false;
			}
		} else if (!flimp.equals(other.flimp)) {
			System.out.print(" |  " + 22);
			return false;
		}
		if (flsez == null) {
			if (other.flsez != null && other.flsez.length() > 0) {
				System.out.print(" |  " + 23);
				return false;
			}
		} else if (!flsez.equals(other.flsez)) {
			System.out.print(" |  " + 24);
			return false;
		}
		if (flsezrez == null) {
			if (other.flsezrez != null && other.flsezrez.length() > 0) {
				System.out.print(" |  " + 25);
				return false;
			}
		} else if (!flsezrez.equals(other.flsezrez)) {
			System.out.print(" |  " + 26);
			return false;
		}
		if (forms == null) {
			if (other.forms != null && other.forms.length() > 0) {
				System.out.print(" |  " + 27);
				return false;
			}
		} else if (!forms.equals(other.forms)) {
			System.out.print(" |  " + 28);
			return false;
		}
		if (importer == null) {
			if (other.importer != null && other.importer.length() > 0) {
				System.out.print(" |  " + 29);
				return false;
			}
		} else if (!importer.equals(other.importer)) {
			System.out.print(" |  " + 30);
			return false;
		}
		
		//if (koldoplist == null) {
		//	if (other.koldoplist != null /*&& other.koldoplist.length() > 0*/) {
		//		System.out.print(" |  " + 31);
		//		return false;
		//	}
		//} else if (!koldoplist.equals(other.koldoplist)) {
		//	System.out.print(" |  " + 32);
		//	return false;
		//}
		
		if (kontrp == null) {
			if (other.kontrp != null && other.kontrp.length() > 0) {
				System.out.print(" |  " + 33);
				return false;
			}
		} else if (!kontrp.equals(other.kontrp)) {
			System.out.print(" |  " + 34);
			return false;
		}
		if (kontrs == null) {
			if (other.kontrs != null && other.kontrs.length() > 0) {
				System.out.print(" |  " + 36);
				return false;
			}
		} else if (!kontrs.equals(other.kontrs)) {
			System.out.print(" |  " + 37);
			return false;
		}
		if (marshrut == null) {
			if (other.marshrut != null && other.marshrut.length() > 0) {
				System.out.print(" |  " + 38);
				return false;
			}
		} else if (!marshrut.equals(other.marshrut)) {
			System.out.print(" |  " + 39 + " " + marshrut + "  |  "
					+ other.getMarshrut());
			return false;
		}
		if (nblanka == null) {
			if (other.nblanka != null && other.nblanka.length() > 0) {
				System.out.print(" |  " + 40);
				return false;
			}
		} else if (!nblanka.equals(other.nblanka)) {
			System.out.print(" |  " + 41);
			return false;
		}
		if (nomercert == null) {
			if (other.nomercert != null && other.nomercert.length() > 0) {
				System.out.print(" |  " + 42);
				return false;
			}
		} else if (!nomercert.equals(other.nomercert)) {
			System.out.print(" |  " + 43);
			return false;
		}
		if (otd_id != other.otd_id) {
			System.out.print(" |  " + 44);
			return false;
		}
		if (otmetka == null) {
			if (other.otmetka != null && other.otmetka.length() > 0) {
				System.out.print(" |  " + 45);
				return false;
			}
		} else if (!otmetka.equals(other.otmetka)) {
			System.out.print(" |  " + 46 + " " + otmetka + "  |  "
					+ other.getOtmetka());
			return false;
		}
		if (poluchat == null) {
			if (other.poluchat != null && other.poluchat.length() > 0) {
				System.out.print(" |  " + 47);
				return false;
			}
		} else if (!poluchat.equals(other.poluchat)) {
			System.out.print(" |  " + 48);
			return false;
		}
		if (rukovod == null) {
			if (other.rukovod != null && other.rukovod.length() > 0) {
				System.out.print(" |  " + 49);
				return false;
			}
		} else if (!rukovod.equals(other.rukovod)) {
			System.out.print(" |  " + 50);
			return false;
		}
		if (sez == null) {
			if (other.sez != null && other.sez.length() > 0) {
				System.out.print(" |  " + 51);
				return false;
			}
		} else if (!sez.equals(other.sez)) {
			System.out.print(" |  " + 52);
			return false;
		}
		if (status == null) {
			if (other.status != null && other.status.length() > 0) {
				System.out.print(" |  " + 53);
				return false;
			}
		} else if (!status.equals(other.status)) {
			System.out.print(" |  " + 54);
			return false;
		}
		if (stranap == null) {
			if (other.stranap != null && other.stranap.length() > 0) {
				System.out.print(" |  " + 55);
				return false;
			}
		} else if (!stranap.equals(other.stranap)) {
			System.out.print(" |  " + 56);
			return false;
		}
		if (stranapr == null) {
			if (other.stranapr != null && other.stranapr.length() > 0) {
				System.out.print(" |  " + 57);
				return false;
			}
		} else if (!stranapr.equals(other.stranapr)) {
			System.out.print(" |  " + 58);
			return false;
		}
		if (stranav == null) {
			if (other.stranav != null && other.stranav.length() > 0) {
				System.out.print(" |  " + 59);
				return false;
			}
		} else if (!stranav.equals(other.stranav)) {
			System.out.print(" |  " + 60);
			return false;
		}
		if (transport == null) {
			if (other.transport != null && other.transport.length() > 0) {
				System.out.print(" |  " + 61);
				return false;
			}
		} else if (!transport.equals(other.transport)) {
			System.out.print(" |  " + 62);
			return false;
		}
		if (unn == null) {
			if (other.unn != null && other.unn.length() > 0) {
				System.out.print(" |  " + 63);
				return false;
			}
		} else if (!unn.equals(other.unn)) {
			System.out.print(" |  " + 64);
			return false;
		}
		if (unnexp == null) {
			if (other.unnexp != null && other.unnexp.length() > 0) {
				System.out.print(" |  " + 65);
				return false;
			}
		} else if (!unnexp.equals(other.unnexp)) {
			System.out.print(" |  " + 66);
			return false;
		}
		
		if (dataexpire == null) {
			if (other.dataexpire != null && other.dataexpire.length() > 0) {
				System.out.print(" |  " + 67);
				return false;
			}
		} else if (!dataexpire.equals(other.dataexpire)) {
			System.out.print(" |  " + 67);
			return false;
		}		
		
		if (agree == null) {
			if (other.agree != null && other.agree.length() > 0) {
				System.out.print(" |  " + 68);
				return false;
			}
		} else if (!agree.equals(other.agree)) {
			System.out.print(" |  " + 68);
			return false;
		}		
		return true;
	}

	
	public int getOtd_id() {
		return otd_id;
	}

	public void setOtd_id(int otd_id) {
		this.otd_id = otd_id;
	}

	@XmlTransient
	public Long getParent_id() {
		return parent_id;
	}

	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}

	public String getCodestranav() {
		return codestranav;
	}

	public void setCodestranav(String codestranav) {
		this.codestranav = codestranav;
	}

	public String getCodestranapr() {
		return codestranapr;
	}

	public void setCodestranapr(String codestranapr) {
		this.codestranapr = codestranapr;
	}

	public String getCodestranap() {
		return codestranap;
	}

	public void setCodestranap(String codestranap) {
		this.codestranap = codestranap;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@XmlTransient
	public ProductIterator getIterator() {
		if (iterator == null) {
			iterator = new ProductIteratorImpl();
		}
		return iterator;
	}

	public class ProductIteratorImpl implements ProductIterator {

		public ProductIteratorImpl() {
			cursor = -1;
		}

		public Product first() {
			Product product = null;
			if (products != null && products.size() > 0) {
				cursor = 0;
				product = products.get(cursor);
			}
			return product;
		}

		public boolean hasNext() {
			return ((products != null) && (products.size() > 0) && (cursor < products
					.size() - 1));
		}

		public Product next() {
			Product product = null;
			if (products != null && products.size() > 0
					&& (cursor < products.size() - 1)) {
				cursor++;
				product = products.get(cursor);
			}
			return product;
		}

		public Product prev() {
			Product product = null;
			if (products != null && products.size() > 0 && (cursor > 0)) {
				cursor--;
				product = products.get(cursor);
			}
			return product;
		}

		@Override
		public void reset() {
			cursor = -1;
		}
		
		public void insertProductInNextPosition(Product iproduct) {
			products.add(cursor+1, iproduct);
		}
	}

}
