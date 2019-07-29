package com.github.anicolaspp.parsers.update;

import com.github.anicolaspp.db.QueryConditionBuilder;
import com.github.anicolaspp.db.QueryLimit;
import com.github.anicolaspp.db.Table;
import com.github.anicolaspp.parsers.ChainParser;
import com.github.anicolaspp.parsers.ParserQueryResult;
import com.github.anicolaspp.parsers.insert.InsertStatementParser;
import lombok.val;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import org.ojai.store.Connection;

public class UpdateStatementParser implements ChainParser {

    private Connection connection;

    public UpdateStatementParser(Connection connection) {
        this.connection = connection;
    }

    @Override
    public ChainParser next() {
        return new InsertStatementParser(connection);
    }

    @Override
    public ParserQueryResult getQueryFrom(Statement statement) {

        if (!(statement instanceof Update)) {
            return ParserQueryResult
                    .builder()
                    .successful(false)
                    .build();
        }

        val update = (Update) statement;
        val query = QueryLimit.limit(update.getLimit())
                .applyTo(connection
                        .newQuery()
                        .where(QueryConditionBuilder
                                .from(update.getWhere())
                                .buildWith(connection, null)));

        if (update.getColumns() != null && update.getColumns().size() > 0) {
            for (int i = 0; i < update.getColumns().size(); i++) {

                //TODO: set for different EXPRESION types.
//                connection.newMutation()
//                        .set(update.getColumns().get(i), update.getExpressions().get(i))

            }
        }

        String table = Table.from(update).getName();

        System.out.println("update");

        return null;
    }
}

