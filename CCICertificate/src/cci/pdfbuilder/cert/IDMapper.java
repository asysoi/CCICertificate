package cci.pdfbuilder.cert;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class IDMapper<T> implements RowMapper<Integer> {
    Integer id; 
	public Integer mapRow(ResultSet rs, int row) throws SQLException {
		return Integer.valueOf(rs.getInt("cert_id"));
	}
}



