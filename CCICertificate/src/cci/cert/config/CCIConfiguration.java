package cci.cert.config;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
 

public class CCIConfiguration {

   
    public DataSource dataSource() {
        final BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("");
        ds.setUrl("");
        ds.setUsername("");
        ds.setPassword("");
        return ds;
    }

}
