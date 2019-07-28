package com.github.anicolaspp.parsers.insert;

import com.github.anicolaspp.db.Table;
import com.github.anicolaspp.parsers.select.SelectStatementParser;
import com.github.anicolaspp.parsers.ChainParser;
import com.github.anicolaspp.parsers.ParserQueryResult;
import com.github.anicolaspp.parsers.ParserType;
import com.github.anicolaspp.db.Projection;
import com.github.anicolaspp.db.QueryFunctions;
import com.github.anicolaspp.parsers.delete.DeleteStatementParser;
import javafx.util.Pair;
import lombok.val;
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
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class InsertStatementParser implements ChainParser {

    private Connection connection;
    private static Random RND = new Random();

    public InsertStatementParser(Connection connection) {

        this.connection = connection;
    }

    @Override
    public ChainParser next() {
        return new DeleteStatementParser(connection);
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

        if (insert.getSelect() != null) {

            QueryResult documents = runSelect(insert.getSelect());

            Stream<Document> documentsToInsert = StreamSupport
                    .stream(documents.spliterator(), false)
                    .map(document -> Projection.project(columns, document).apply(Column::getColumnName, connection));

            return new InsertParserResult(
                    null,
                    Table.from(insert).getName(),
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
                    .mapToObj(createDocPair(columns, values))
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

            return new InsertParserResult(
                    null,
                    Table.from(insert).getName(),
                    null,
                    true,
                    ParserType.INSERT,
                    Stream.of(getDocumentWithValidId(doc))
            );
        }
    }

    private IntFunction<Pair<String, Value>> createDocPair(List<Column> columns, ExpressionList values) {
        return i -> new Pair<>(
                columns.get(i).getColumnName(),
                QueryFunctions.valueFromExpression(values.getExpressions().get(i)));
    }

    private Document getDocumentWithValidId(Map<String, Object> rawDocument) {
        validateId(rawDocument);

        return connection.newDocument(rawDocument);
    }

    private void validateId(Map<String, Object> doc) {
        if (doc.get("_id") == null) {
            doc.put("_id", String.valueOf(RND.nextGaussian()));
        }
    }

    private QueryResult runSelect(Select select) {
        ParserQueryResult query = new SelectStatementParser(connection).getQueryFrom(select);

        val tableName = query.getTable();

        val store = connection.getStore(tableName);

        return store.find(query.getQuery());
    }
}

