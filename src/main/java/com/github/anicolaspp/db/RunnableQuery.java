package com.github.anicolaspp.db;

import com.github.anicolaspp.parsers.ChainParser;
import com.github.anicolaspp.parsers.delete.DeleteParserResult;
import com.github.anicolaspp.parsers.insert.InsertParserResult;
import com.github.anicolaspp.sql.DojaiResultSet;
import lombok.val;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import org.ojai.Document;
import org.ojai.Value;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RunnableQuery {

    private static String sql;

    public RunnableQuery(String sql) {
        this.sql = sql;
    }

    public static RunnableQuery from(String sql) {
        return new RunnableQuery(sql);
    }

    public ResultSet executeQueryWith(org.ojai.store.Connection ojaiConnection) throws SQLException {
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

    public int executeUpdateWith(org.ojai.store.Connection ojaiConnection) throws SQLException {
        try {
            val statement = CCJSqlParserUtil.parse(sql);

            val query = ChainParser.build(ojaiConnection).parse(statement);

            val tableName = query.getTable();
            val store = ojaiConnection.getStore(tableName);

            if (query instanceof InsertParserResult) {
                return new StreamConsumer<Document>(store::insert).runOn(query.getDocuments());
            }

            if (query instanceof DeleteParserResult) {
                return new StreamConsumer<Value>(store::delete).runOn(query.getDocuments());
            }

            return 0;

        } catch (Exception e) {
            throw new SQLException("Error Inserting", e);
        }
    }
}

