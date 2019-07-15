package com.github.anicolaspp.parsers.insert;

import com.github.anicolaspp.parsers.ChainParser;
import com.github.anicolaspp.parsers.ParserQueryResult;
import com.github.anicolaspp.parsers.ParserType;
import com.github.anicolaspp.parsers.QueryFunctions;
import com.github.anicolaspp.parsers.unknown.UnsupportedStatementParser;
import com.mapr.ojai.store.impl.Values;
import lombok.val;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import org.ojai.Value;
import org.ojai.store.Connection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InsertStatementParser implements ChainParser {

    private Connection connection;

    public InsertStatementParser(Connection connection) {

        this.connection = connection;
    }

    @Override
    public ChainParser next() {
        return new UnsupportedStatementParser(connection);
    }

    @Override
    public ParserQueryResult getQueryFrom(Statement statement) {
        if (!(statement instanceof Insert)) {
            return ParserQueryResult
                    .builder()
                    .successful(false)
                    .build();
        }

        val insert = (Insert) statement;

        val columns = insert.getColumns();
        val values = (ExpressionList )insert.getItemsList();

        if (columns.size() != values.getExpressions().size()) {
            return ParserQueryResult
                    .builder()
                    .successful(false)
                    .build();
        }

        Map<String, Object> doc = new HashMap<>();

        for (int i = 0; i < columns.size(); i++) {

            val col = columns.get(i);
            val value = fromExpression(values.getExpressions().get(i));

            System.out.println(col);
            System.out.println(value);


            doc.put(col.getColumnName(), value);
        }

        val document = connection.newDocument(doc);
        System.out.println(document);

        ParserQueryResult result = new InsertParserResult(
                null, QueryFunctions.getTableName(insert),
                null,
                true,
                ParserType.INSERT,
                Collections.singletonList(document)
        );

        return result;
    }

    private Value fromExpression(Expression expression) {
        if (expression instanceof Column) {
            return new Values.StringValue(((Column) expression).getColumnName());
        }

        if (expression instanceof LongValue) {
            return new Values.LongValue(((LongValue) expression).getValue());
        }

        return Values.NULL;
    }
}


