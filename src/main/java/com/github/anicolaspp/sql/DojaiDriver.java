package com.github.anicolaspp.sql;

import com.github.anicolaspp.sql.connections.DirectConnection;
import com.github.anicolaspp.sql.connections.InMemoryConnection;
import com.github.anicolaspp.sql.connections.MapRDBConnection;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DojaiDriver implements Driver {

    static {
        try {
            DriverManager.registerDriver(new DojaiDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection connect(String url, Properties info) throws SQLException {
        if (acceptsURL(url)) {

            return new DojaiConnection(getConnectionTypeFrom(url));

        } else {
            return null;
        }
    }

    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith("dojai:mapr:") || url.startsWith("dojai:mapr:mem");
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
        if (url.startsWith("dojai:mapr:")) {
            return new MapRDBConnection();
        } else if (url.startsWith("dojai:mapr:mem:")) {
            return new InMemoryConnection();
        } else {
            return null;
        }
    }
}
