package cci.web.controller.owncert;

public class OwnFilter {

	public final String DATE_FORMAT = "%d.%m.%Y";
    private int id = 0;
	private String number = null;
	private String blanknumber = null;
	private String from = null;
	private String to = null;
	private String otd_id = null;

	public OwnFilter(String number, String blanknumber, String from, String to, String otd_id) {
		super();
		this.number = number;
		this.blanknumber = blanknumber;
		this.from = from;
		this.to = to;
		this.otd_id = otd_id;
	}

	public OwnFilter() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getBlanknumber() {
		return blanknumber;
	}

	public void setBlanknumber(String blanknumber) {
		this.blanknumber = blanknumber;
	}

	public String getOtd_id() {
		return otd_id;
	}

	public void setOtd_id(String otd_id) {
		this.otd_id = otd_id;
	}

	public String getWhereLikeClause() {
		
		String sqlwhere= "";
		
		if (id != 0) {
			if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
			sqlwhere += "id = " + id ;
		}
		
		if (number != null && !number.isEmpty()) {
			sqlwhere += " WHERE ";
			sqlwhere += "number LIKE '%" + number + "%'";   
		}
		
		if (blanknumber != null && !blanknumber.isEmpty()) {
			if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
			sqlwhere += "blanknumber LIKE '%" + blanknumber + "%'";   
		}
		
		if (from != null && !from.isEmpty()) {
			if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
			sqlwhere += "datecert > STR_TO_DATE('" + from + "','" +  DATE_FORMAT + "') ";   
		}
		
		if (to != null && !to.isEmpty()) {
			if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
			sqlwhere += "datecert < STR_TO_DATE('" + to + "','" +  DATE_FORMAT + "') ";   
		}
		
		if (otd_id != null && !otd_id.isEmpty()) {
			if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
			sqlwhere += "otd_id = " + otd_id;   
		}
		
		return sqlwhere;
	}
	
    public String getWhereEqualClause() {
		
		String sqlwhere= "";
		
		if (id != 0) {
			if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
			sqlwhere += "id = " + id ;
		}
		
		if (number != null && !number.isEmpty()) {
			if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
			sqlwhere += "number = '" + number + "'";   
		}
		
		if (blanknumber != null && !blanknumber.isEmpty()) {
			if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
			sqlwhere += "blanknumber = '" + blanknumber + "'";   
		}
		
		if (from != null && !from.isEmpty()) {
			if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
			sqlwhere += "datecert > STR_TO_DATE('" + from + "','" +  DATE_FORMAT + "') ";   
		}
		
		if (to != null && !to.isEmpty()) {
			if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
			sqlwhere += "datecert < STR_TO_DATE('" + to + "','" +  DATE_FORMAT + "') ";   
		}
		
		if (otd_id != null && !otd_id.isEmpty()) {
			if (sqlwhere.length() == 0) { 	sqlwhere += " WHERE ";	} else {sqlwhere += " AND ";}
			sqlwhere += "otd_id = " + otd_id;   
		}
		
		return sqlwhere;
	}

	@Override
	public String toString() {
		return "[number=" + nullypempty(number) + ", blanknumber=" + nullypempty(blanknumber)
				+ ", from=" + nullypempty(from) + ", to=" + nullypempty(to) + "]";
	}

	private String nullypempty(String var) {
		return (var == null) ? "" : var ;
	}
	
	
}
