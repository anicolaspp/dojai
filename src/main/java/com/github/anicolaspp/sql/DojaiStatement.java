package com.github.anicolaspp.sql;

import com.github.anicolaspp.parsers.ChainParser;
import com.github.anicolaspp.parsers.delete.DeleteParserResult;
import com.github.anicolaspp.parsers.insert.InsertParserResult;
import lombok.val;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import org.ojai.Document;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

//import org.ojai.scala.Document;


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

            if (statement instanceof Insert || statement instanceof Delete) {
                throw new SQLException("Use **executeUpdate** for this type of query");
            }

            val query = ChainParser.build(ojaiConnection).parse(statement);

            return new DojaiResultSet(query.getDocuments(), query.getSelectFields());

        } catch (Exception e) {
            throw new SQLException("Error parsing SQL query", e);
        }
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        try {
            val statement = CCJSqlParserUtil.parse(sql);

            val query = ChainParser.build(ojaiConnection).parse(statement);

            if (query instanceof InsertParserResult) {
                val tableName = query.getTable();

                val store = ojaiConnection.getStore(tableName);

                Consumer<Document> consumer = store::insert;
                return runConsumerOn(query.getDocuments(), consumer);
            }

            if (query instanceof DeleteParserResult) {
                val tableName = query.getTable();

                val store = ojaiConnection.getStore(tableName);

                Consumer<Document> consumer = store::delete;
                return runConsumerOn(query.getDocuments(), consumer);
            }

            return 0;

        } catch (Exception e) {
            throw new SQLException("Error Inserting", e);
        }
    }

    private <A> int runConsumerOn(Stream<A> values, Consumer<A> consumer) {
        AtomicInteger count = new AtomicInteger();

        values.forEach(value -> {
            consumer.accept(value);

            count.addAndGet(1);
        });

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
