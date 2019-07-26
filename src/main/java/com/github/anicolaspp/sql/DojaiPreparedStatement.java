package com.github.anicolaspp.sql;

import com.github.anicolaspp.db.RunnableQuery;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class DojaiPreparedStatement implements PreparedStatement {
    private final String sql;
    private String mutableSQL;

    private long numberOfQuestions;
    private int numberOfReplacements = 0;

    private org.ojai.store.Connection ojaiConnection;

    public DojaiPreparedStatement(String sql, org.ojai.store.Connection ojaiConnection) {
        this.sql = sql;
        this.mutableSQL = sql;
        this.numberOfQuestions = sql.chars().filter(n -> ((char) n) == '?').count();

        this.ojaiConnection = ojaiConnection;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        this.mutableSQL = fillWithNull(mutableSQL);

        System.out.println(String.format("executeQuery: %s", mutableSQL));

        return RunnableQuery.from(mutableSQL).executeQueryWith(ojaiConnection);
    }

    @Override
    public int executeUpdate() throws SQLException {
        this.mutableSQL = fillWithNull(mutableSQL);

        System.out.println(String.format("executeUpdate: %s", mutableSQL));

        return RunnableQuery.from(mutableSQL).executeUpdateWith(ojaiConnection);
    }

    private String fillWithNull(String sql) {
        char mark = '?';

        return sql.replace(String.valueOf(mark), "NULL");
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, "NULL");
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    private <A> String replaceWithValue(String sql, int parameterIndex, A value) {
        int index = findReplaceIndex(sql, parameterIndex - numberOfReplacements);

        if (index >= 0) {
            numberOfReplacements++;

            return sql.substring(0, index) + value + sql.substring(index + 1);
        } else {
            return sql;
        }
    }

    private int findReplaceIndex(String sql, int parameterIndex) {
        int count = -1;

        char[] chars = sql.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '?') {
                count++;
            }

            if (count == parameterIndex) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void clearParameters() throws SQLException {
        this.mutableSQL = sql;
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public boolean execute() throws SQLException {
        return false;
    }

    @Override
    public void addBatch() throws SQLException {

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {

    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        this.mutableSQL = replaceWithValue(mutableSQL, parameterIndex - 1, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {

    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {

    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return 0;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {

    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {

    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {

    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {

    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException {

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
