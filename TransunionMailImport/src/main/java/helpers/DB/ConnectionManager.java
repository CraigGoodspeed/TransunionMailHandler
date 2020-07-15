package helpers.DB;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static NamedParameterJdbcTemplate jdbcTemplate;

    static {
        config.setJdbcUrl( buildJDBC() );
        config.setUsername( getDbConnectionUserName()  );
        config.setPassword( getDbConnectionPassword() );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        config.setAutoCommit(true);
        config.setMaximumPoolSize(20);
        ds = new HikariDataSource( config );
        jdbcTemplate = new NamedParameterJdbcTemplate(ds);
    }

    private static String buildJDBC() {
        return String.format(
                "jdbc:mysql://%s:3306/%s?useServerPrepStmts=false&rewriteBatchedStatements=true&allowMultiQueries=true"
                ,getDbHost()
                ,getDatabase()
        );
    }

    private static String getEnvironment(String key, String defaultVal){
        String toReturn = System.getenv(key);
        return toReturn == null ? defaultVal : toReturn;
    }

    private static String getDbHost(){
        return getEnvironment("DBConnectionHost", "goodspeed.co.za");
    }

    private static String getDatabase(){
        return getEnvironment("DBConnectionDatabase", "goodsaok_defaultDB");
    }

    private static String getDbConnectionUserName(){
        return getEnvironment("DBConnectionUsername", "goodsaok_admin");
    }

    private static String getDbConnectionPassword(){
        return getEnvironment("DBConnectionPassword", "$0m3B@d@$$P@$$w0rd!");
    }


    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static NamedParameterJdbcTemplate getNamedJdbcTemplate(){
        return jdbcTemplate;
    }
}
