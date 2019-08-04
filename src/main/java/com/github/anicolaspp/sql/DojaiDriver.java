package com.github.anicolaspp.sql;

import com.github.anicolaspp.db.connections.DirectConnection;
import com.github.anicolaspp.db.connections.MapRDBConnection;
import com.github.anicolaspp.db.connections.InMemoryConnection;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DojaiDriver implements Driver {

    public static final String JDCB_DOJAI_MAPR = "jdcb:dojai:mapr:";
    public static final String JDBC_DOJAI_MAPR_MEM = "jdbc:dojai:mapr:mem:";

    static {
        try {
            DriverManager.registerDriver(new DojaiDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DojaiConnection connection;

    public Connection connect(String url, Properties info) throws SQLException {
        if (acceptsURL(url)) {

            if (connection == null) {
                connection = new DojaiConnection(getConnectionTypeFrom(url));
            }

            return connection;

        } else {
            return null;
        }
    }

    public boolean acceptsURL(String url) throws SQLException {
        return url.equals(JDCB_DOJAI_MAPR) || url.equals(JDBC_DOJAI_MAPR_MEM);
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    public int getMajorVersion() {
        return 1;
    }

    public int getMinorVersion() {
        return 0;
    }

    public boolean jdbcCompliant() {
        return true;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    private DirectConnection getConnectionTypeFrom(String url) {
        if (url.equals(JDBC_DOJAI_MAPR_MEM)) {
            return new InMemoryConnection();
        } else if (url.equals(JDCB_DOJAI_MAPR)) {
            return new MapRDBConnection();
        } else {
            return null;
        }
    }
}
