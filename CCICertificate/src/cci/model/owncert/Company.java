package cci.model.owncert;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlType(propOrder = { "name", "address", "unp", "phone"})
public class Company {
   
	@JsonIgnore
	private int id; 
	private String name;
	private String address;
	private String unp;
	private String phone;
	
   	public void init(int id, String name, String address, String unp, String phone) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.unp = unp;
		this.phone = phone;
	}
	
	@XmlTransient
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUnp() {
		return unp;
	}

	public void setUnp(String unp) {
		this.unp = unp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "Company [name=" + name + ", address=" + address + ", unp="
				+ unp + "]";
	}

	 @Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((address == null) ? 0 : address.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((unp == null) ? 0 : unp.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Company other = (Company) obj;
			if (address == null) {
				if (other.address != null)
					return false;
			} else if (!address.equals(other.address))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (unp == null) {
				if (other.unp != null)
					return false;
			} else if (!unp.equals(other.unp))
				return false;
			return true;
		}
}
