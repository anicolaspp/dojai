package com.github.anicolaspp.parsers.select;

import com.github.anicolaspp.db.IdGenerator;
import com.github.anicolaspp.db.Projection;
import com.github.anicolaspp.db.QueryConditionBuilder;
import com.github.anicolaspp.db.QueryLimit;
import com.github.anicolaspp.db.StoreManager;
import com.github.anicolaspp.db.Table;
import com.github.anicolaspp.parsers.ChainParser;
import com.github.anicolaspp.parsers.ParserQueryResult;
import com.github.anicolaspp.parsers.ParserType;
import com.github.anicolaspp.parsers.update.UpdateStatementParser;
import lombok.val;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.ojai.Document;
import org.ojai.store.Connection;
import org.ojai.store.Query;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SelectStatementParser implements ChainParser {

    private Connection connection;

    public SelectStatementParser(Connection connection) {

        this.connection = connection;
    }

    @Override
    public ChainParser next() {
        return new UpdateStatementParser(connection);
    }

    @Override
    public ParserQueryResult getQueryFrom(Statement statement) {

        if (!(statement instanceof Select)) {
            return ParserQueryResult
                    .builder()
                    .successful(false)
                    .query(emptyQuery(connection))
                    .build();
        }

        return parseSelect((Select) statement);
    }

    private ParserQueryResult parseSelect(Select select) {

        if (!(select.getSelectBody() instanceof PlainSelect)) {
            return ParserQueryResult
                    .builder()
                    .query(emptyQuery(connection))
                    .build();
        }

        return runSelect((PlainSelect) select.getSelectBody());
    }

    private ParserQueryResult runSelect(PlainSelect plainSelectBody) {
        val from = plainSelectBody.getFromItem();

        if (from == null && plainSelectBody.getSelectItems().size() > 0 && plainSelectBody.getSelectItems().get(0).toString().equals("nextval('uuid')")) {

            val docs = Collections.singletonList(connection.newDocument().set("nextval", IdGenerator.getInstance().nextId()));

            return ParserQueryResult
                    .<Document>builder()
                    .type(ParserType.SELECT)
                    .table("IDS")
                    .selectFields(Collections.singletonList(SelectField.withName("nextval")))
                    .successful(true)
                    .documents(docs.stream())
                    .build();
        }

        if (from instanceof SubSelect) {

            val result = runSubSelect((SubSelect) from);

            Stream<Document> projectedDocs = result
                    .getDocuments()
                    .map(document -> Projection
                            .project(result.getSelectFields(), document)
                            .apply(SelectField::getValue, connection));

            return ParserQueryResult
                    .<Document>builder()
                    .type(ParserType.SELECT)
                    .query(result.getQuery())
                    .table(Table.from(from).getName())
                    .selectFields(getSchemaFrom(plainSelectBody))
                    .successful(true)
                    .documents(projectedDocs)
                    .build();

        } else {
            val schema = getSchemaFrom(plainSelectBody);

            val query = QueryLimit
                    .limit(plainSelectBody.getLimit())
                    .applyTo(getInitialQuery(plainSelectBody, schema))
                    .build();

            val tableName = Table.from(from).getName();

            val store = StoreManager.getStoreFor(tableName, connection);

            val documents = StreamSupport.stream(store.find(query).spliterator(), false);

            return ParserQueryResult
                    .<Document>builder()
                    .query(query)
                    .table(tableName)
                    .selectFields(schema)
                    .successful(true)
                    .type(ParserType.SELECT)
                    .documents(documents)
                    .build();
        }

    }

    private ParserQueryResult<Document> runSubSelect(SubSelect from) {

        val query = runSelect((PlainSelect) from.getSelectBody());

        val store = connection.getStore(query.getTable());

        Stream<Document> documents = StreamSupport.stream(store.find(query.getQuery()).spliterator(), false);

        return ParserQueryResult
                .<Document>builder()
                .query(query.getQuery())
                .table(query.getTable())
                .successful(true)
                .type(ParserType.SELECT)
                .selectFields(query.getSelectFields())
                .documents(documents)
                .build();
    }

    private Query getInitialQuery(PlainSelect plainSelectBody, List<SelectField> schema) {
        return connection
                .newQuery()
                .select(schema.stream().map(SelectField::getName).toArray(String[]::new))
                .where(QueryConditionBuilder
                        .from(plainSelectBody.getWhere())
                        .buildWith(connection, schema));


    }

    private List<SelectField> getSchemaFrom(PlainSelect plainSelectBody) {
        return plainSelectBody
                .getSelectItems()
                .stream()
                .map(selectItem -> {
                    if (selectItem instanceof SelectExpressionItem) {
                        String columnName = ((Column) ((SelectExpressionItem) selectItem).getExpression()).getColumnName();

                        return SelectField.withName(columnName).andAlias(((SelectExpressionItem) selectItem).getAlias());

                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

