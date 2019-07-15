package com.github.anicolaspp.sql;

import com.github.anicolaspp.parsers.ChainParser;
import com.github.anicolaspp.parsers.insert.InsertParserResult;
import lombok.val;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.ojai.Document;
import org.ojai.store.DocumentStore;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class DojaiStatement implements Statement {

    private final org.ojai.store.Connection ojaiConnection;
    private DojaiConnection dojaiConnection;

    public DojaiStatement(org.ojai.store.Connection ojaiConnection, DojaiConnection dojaiConnection) {
        this.ojaiConnection = ojaiConnection;
        this.dojaiConnection = dojaiConnection;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        try {
            val statement = CCJSqlParserUtil.parse(sql);

            val query = ChainParser.build(ojaiConnection).parse(statement);

            val tableName = query.getTable();

            val store = ojaiConnection.getStore(tableName);

            val documents = store.find(query.getQuery());

            return new DojaiResultSet(documents, query.getSelectFields(), store);

        } catch (Exception e) {
            throw new SQLException("Error parsing SQL query", e);
        }
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        try {

            val statement = CCJSqlParserUtil.parse(sql);


            val query = (InsertParserResult) ChainParser.build(ojaiConnection).parse(statement);

            val tableName = query.getTable();

            val store = ojaiConnection.getStore(tableName);

            return runInserts(query.getDocuments(), store);
//
//            query.getDocuments().forEachRemaining(store::insert);
//
//            return query.getDocuments().size();

        } catch (Exception e) {
            throw new SQLException("Error Inserting", e);
        }
    }

    private int runInserts(Iterator<Document> documents, DocumentStore store) {
        AtomicInteger count = new AtomicInteger();

        documents.forEachRemaining(document -> {

            System.out.println(document);

            store.insert(document);

            count.addAndGet(1);
        });

        store.flush();

        return count.get();
    }

    @Override
    public void close() throws SQLException {
        try {
            ojaiConnection.close();
        } catch (Exception e) {
            throw new SQLException("Error closing connection", e);
        }
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
        return this.dojaiConnection;
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
