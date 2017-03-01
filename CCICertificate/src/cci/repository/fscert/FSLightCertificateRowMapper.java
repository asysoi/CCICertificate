package cci.repository.fscert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class FSLightCertificateRowMapper implements ResultSetExtractor {
    private String dateformat = "dd.MM.yyyy";
	@Override
	public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
		StringBuffer str = new StringBuffer();
		str.append("Certificate Number;Certificate Date\n");
		
		while (rs.next()) {
			str.append(rs.getString("certnumber"));
			str.append(";");
			str.append((new SimpleDateFormat(dateformat)).format(rs.getDate("datecert")));
			str.append("\n");
		}
		return str.toString();
	}

}
