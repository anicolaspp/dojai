package com.github.anicolaspp.parsers.delete;

import com.github.anicolaspp.parsers.ChainParser;
import com.github.anicolaspp.parsers.ParserQueryResult;
import com.github.anicolaspp.parsers.ParserType;
import com.github.anicolaspp.parsers.QueryFunctions;
import com.github.anicolaspp.parsers.Table;
import com.github.anicolaspp.parsers.select.SelectField;
import com.github.anicolaspp.parsers.unknown.UnsupportedStatementParser;
import lombok.val;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import org.ojai.Document;
import org.ojai.store.Connection;

import java.util.Collections;
import java.util.stream.StreamSupport;

public class DeleteStatementParser implements ChainParser {

    private Connection connection;

    public DeleteStatementParser(Connection connection) {

        this.connection = connection;
    }

    @Override
    public ChainParser next() {
        return new UnsupportedStatementParser(connection);
    }

    @Override
    public ParserQueryResult getQueryFrom(Statement statement) {

        if (!(statement instanceof Delete)) {
            return ParserQueryResult
                    .builder()
                    .successful(false)
                    .build();
        }

        val delete = (Delete) statement;

        String table = Table.from(delete).getName();

        val query = connection
                .newQuery()
                .select("_id")
                .where(QueryFunctions.getQueryConditionFrom(delete.getWhere(), connection, Collections.singletonList(new SelectField("_id", "_id"))));

        val store = connection.getStore(table);

        val ids = StreamSupport
                .stream(store.find(query.build()).spliterator(), false)
                .map(Document::getId);

        return new DeleteParserResult(
                query,
                table,
                null,
                true, ParserType.DELETE,
                ids
        );
    }
}
