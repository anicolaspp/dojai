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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
}
