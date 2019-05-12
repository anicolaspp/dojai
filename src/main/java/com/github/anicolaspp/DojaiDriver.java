package com.github.anicolaspp;

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
        
            return new DojaiConnection(url, info);
            
        } else {
            return null;
        }
    }
    
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith("dojai:mapr:");
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
        return false;
    }
    
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
