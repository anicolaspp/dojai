package com.github.anicolaspp.sql;

import com.github.anicolaspp.parsers.select.SelectField;
import lombok.val;
import org.ojai.Document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DojaiResultSet implements ResultSet {

    private Document current;

    private Iterator<Document> documentStream;
    private List<SelectField> schema;

    private int rowNumber = 0;

    private boolean lastReadWasNull = false;

    public DojaiResultSet(Stream<Document> documentStream, List<SelectField> selectFields) {
        this.documentStream = documentStream.iterator();
        this.schema = selectFields;
    }

    @Override
    public boolean next() throws SQLException {
        boolean hasNext = documentStream.hasNext();

        if (hasNext) {
            current = documentStream.next();
            rowNumber++;
        }

        return hasNext;
    }

    @Override
    public void close() throws SQLException {
//        try {
//            store.close();
//        } catch (Exception e) {
//            throw new SQLException("Error closing store handler", e);
//        }
    }

    @Override
    public boolean wasNull() throws SQLException {
        return lastReadWasNull;
    }

    private void checkReadNull(String label) {
        if (current.getValue(toRowLabel(label)) == null) {
            this.lastReadWasNull = true;
        }
    }

    private void checkReadNull(int columnIndex) {
        checkReadNull(schema.get(columnIndex).getName());
    }

    private void checkIndex(int columnIndex) throws SQLException {

        if (schema.size() < columnIndex) {
            throw new SQLException("No available index: " + columnIndex);
        }
    }

    private void checkColumn(String label) throws SQLException {

        boolean exists = schema
                .stream()
                .anyMatch(field -> field.getName().equals(label) || field.getAlias().equals(label));

        if (!exists) {
            throw new SQLException("Invalid column label: " + label);
        }
    }

    private String toRowLabel(String label) {
        for (val field: schema) {
            if (field.getName().equals(label)) {
                return label;
            }

            if (field.getAlias().equals(label)){
                return field.getName();
            }
        }

        return label;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return current.getString(schema.get(columnIndex).getName());
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return current.getBoolean(schema.get(columnIndex).getName());
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return current.getByte(schema.get(columnIndex).getName());
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return current.getShort(schema.get(columnIndex).getName());
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return current.getInt(schema.get(columnIndex).getName());
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return current.getLong(schema.get(columnIndex).getName());
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return current.getFloat(schema.get(columnIndex).getName());
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return current.getDouble(schema.get(columnIndex).getName());
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return current.getDecimal(schema.get(columnIndex).getName());
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return current.getBinary(schema.get(columnIndex).getName()).array();
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return Date.valueOf(current.getDate(schema.get(columnIndex).getName()).toDateStr());
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return Time.valueOf(current.getTime(schema.get(columnIndex).getName()).toTimeStr());
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return Timestamp.valueOf(current.getTimestamp(schema.get(columnIndex).getName()).toString());
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return getBinaryStream(columnIndex);
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return getBinaryStream(columnIndex);
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        try {
            return new ByteArrayInputStream(current.getBinary(schema.get(columnIndex).getName()).array());
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return current.getString(toRowLabel(columnLabel));
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return current.getBoolean(toRowLabel(columnLabel));
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return current.getByte(toRowLabel(columnLabel));
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return current.getShort(toRowLabel(columnLabel));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return current.getInt(toRowLabel(columnLabel));
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return current.getLong(toRowLabel(columnLabel));
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return current.getFloat(toRowLabel(columnLabel));
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return current.getDouble(toRowLabel(columnLabel));
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return current.getDecimal(toRowLabel(columnLabel));
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return current.getBinary(toRowLabel(columnLabel)).array();
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return Date.valueOf(current.getDate(toRowLabel(columnLabel)).toDateStr());
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return Time.valueOf(current.getTime(toRowLabel(columnLabel)).toTimeStr());
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return Timestamp.valueOf(current.getTimestamp(toRowLabel(columnLabel)).toString());
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return getBinaryStream(toRowLabel(columnLabel));
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);


        try {
            return new ByteArrayInputStream(current.getBinary(toRowLabel(columnLabel)).array());
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public String getCursorName() throws SQLException {
        return "MapRDB Stream";
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return current.getValue(schema.get(columnIndex).getName());
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return current.getValue(columnLabel);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        checkColumn(columnLabel);

        for (int i = 0; i < schema.size(); i++) {
            if (schema.get(i).getValue().equals(columnLabel)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        checkIndex(columnIndex);
        checkReadNull(columnIndex);

        return current.getDecimal(schema.get(columnIndex).getName());
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        checkColumn(columnLabel);
        checkReadNull(columnLabel);

        return current.getDecimal(toRowLabel(columnLabel));
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return current == null;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return !documentStream.hasNext();
    }

    @Override
    public boolean isFirst() throws SQLException {
        return false;
    }

    @Override
    public boolean isLast() throws SQLException {
        return false;
    }

    @Override
    public void beforeFirst() throws SQLException {

    }

    @Override
    public void afterLast() throws SQLException {

    }

    @Override
    public boolean first() throws SQLException {
        return false;
    }

    @Override
    public boolean last() throws SQLException {
        return false;
    }

    @Override
    public int getRow() throws SQLException {
        return rowNumber;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        return false;
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        return false;
    }

    @Override
    public boolean previous() throws SQLException {
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
        return 1;
    }

    @Override
    public int getType() throws SQLException {
        return 0;
    }

    @Override
    public int getConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return false;
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {

    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {

    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {

    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {

    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {

    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {

    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {

    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {

    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {

    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {

    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {

    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {

    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {

    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {

    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {

    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {

    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {

    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {

    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {

    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {

    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {

    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {

    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {

    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {

    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {

    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {

    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {

    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {

    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {

    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {

    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {

    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {

    }

    @Override
    public void insertRow() throws SQLException {

    }

    @Override
    public void updateRow() throws SQLException {

    }

    @Override
    public void deleteRow() throws SQLException {

    }

    @Override
    public void refreshRow() throws SQLException {

    }

    @Override
    public void cancelRowUpdates() throws SQLException {

    }

    @Override
    public void moveToInsertRow() throws SQLException {

    }

    @Override
    public void moveToCurrentRow() throws SQLException {

    }

    @Override
    public Statement getStatement() throws SQLException {
        return null;
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return null;
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return null;
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return null;
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return null;
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return null;
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return null;
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {

    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {

    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {

    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {

    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {

    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {

    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {

    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {

    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {

    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {

    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {

    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {

    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {

    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {

    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {

    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {

    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {

    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {

    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {

    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        System.out.println("HERE");

        return null;
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        System.out.println("HERE");

        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        System.out.println("HERE");

        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {

        System.out.println("HERE");

        return false;
    }
}
