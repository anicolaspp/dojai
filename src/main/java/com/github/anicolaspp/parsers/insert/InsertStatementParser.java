package com.github.anicolaspp.parsers.insert;

import com.github.anicolaspp.parsers.ChainParser;
import com.github.anicolaspp.parsers.ParserQueryResult;
import com.github.anicolaspp.parsers.ParserType;
import com.github.anicolaspp.parsers.QueryFunctions;
import com.github.anicolaspp.parsers.delete.DeleteStatementParser;
import com.github.anicolaspp.parsers.select.SelectStatementParser;
import com.mapr.ojai.store.impl.Values;
import javafx.util.Pair;
import lombok.val;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import org.ojai.Document;
import org.ojai.Value;
import org.ojai.store.Connection;
import org.ojai.store.QueryResult;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class InsertStatementParser implements ChainParser {

    private Connection connection;

    public InsertStatementParser(Connection connection) {

        this.connection = connection;
    }

    @Override
    public ChainParser next() {
        return new DeleteStatementParser(connection);
    }

    @Override
    public ParserQueryResult getQueryFrom(Statement statement) {
        Random random = new Random();

        if (!(statement instanceof Insert)) {
            return ParserQueryResult
                    .builder()
                    .successful(false)
                    .build();
        }

        val insert = (Insert) statement;

        val columns = insert.getColumns();

        if (insert.getSelect() != null) {

            QueryResult documents = runSelect(insert.getSelect());

            Stream<Document> documentsToInsert = StreamSupport
                    .stream(documents.spliterator(), false)
                    .map(document -> getDocumentToInsert(random, columns, document));

            return new InsertParserResult(
                    null,
                    QueryFunctions.getTableName(insert),
                    null,
                    true,
                    ParserType.INSERT,
                    documentsToInsert
            );
        } else {
            val values = (ExpressionList) insert.getItemsList();

            if (columns.size() != values.getExpressions().size()) {
                return ParserQueryResult
                        .builder()
                        .successful(false)
                        .build();
            }

            Map<String, Object> doc = IntStream
                    .range(0, columns.size())
                    .mapToObj(i -> new Pair<>(columns.get(i).getColumnName(), fromExpression(values.getExpressions().get(i))))
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

            validateId(random, doc);
            val document = connection.newDocument(doc);

            return new InsertParserResult(
                    null, QueryFunctions.getTableName(insert),
                    null,
                    true,
                    ParserType.INSERT,
                    Stream.of(document)
            );
        }
    }

    private Document getDocumentToInsert(Random random, List<Column> columns, Document document) {
        Map<String, Object> doc = getDoc(columns, document);

        validateId(random, doc);

        return connection.newDocument(doc);
    }

    private Map<String, Object> getDoc(List<Column> columns, Document document) {
        return columns
                .stream()
                .map(column -> {
                    val value = document.getValue(column.getColumnName());

                    return new Pair<>(column.getColumnName(), value);
                })
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private void validateId(Random random, Map<String, Object> doc) {
        if (doc.get("_id") == null) {
            doc.put("_id", String.valueOf(random.nextGaussian()));
        }
    }

    private QueryResult runSelect(Select select) {
        ParserQueryResult query = new SelectStatementParser(connection).getQueryFrom(select);

        val tableName = query.getTable();

        val store = connection.getStore(tableName);

        return store.find(query.getQuery());
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

